package cl.rednorte.reasignacion.service;

import cl.rednorte.reasignacion.dto.CitaRequestDTO;
import cl.rednorte.reasignacion.dto.CitaResponseDTO;
import cl.rednorte.reasignacion.dto.ReasignacionResultDTO;
import cl.rednorte.reasignacion.entity.Cita;
import cl.rednorte.reasignacion.entity.enums.EstadoCita;
import cl.rednorte.reasignacion.exception.CitaNotFoundException;
import cl.rednorte.reasignacion.exception.ReasignacionException;
import cl.rednorte.reasignacion.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepo;
    private final ReasignacionService reasignacionService;

    @Transactional
    public CitaResponseDTO crearCita(CitaRequestDTO dto) {
        Cita cita = Cita.builder()
                .pacienteId(dto.getPacienteId())
                .pacienteRut(dto.getPacienteRut())
                .pacienteNombre(dto.getPacienteNombre())
                .fechaAtencion(dto.getFechaAtencion())
                .centroAtencion(dto.getCentroAtencion())
                .especialidad(dto.getEspecialidad())
                .estado(dto.getEstado() != null ? dto.getEstado() : EstadoCita.RESERVADA)
                .build();
        return toDTO(citaRepo.save(cita));
    }

    @Transactional(readOnly = true)
    public CitaResponseDTO obtenerCita(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<CitaResponseDTO> listarPorPaciente(Long pacienteId) {
        return citaRepo.findByPacienteId(pacienteId).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<CitaResponseDTO> listarPorEstado(EstadoCita estado) {
        return citaRepo.findByEstado(estado).stream().map(this::toDTO).toList();
    }

    @Transactional
    public CitaResponseDTO actualizarCita(Long id, CitaRequestDTO dto) {
        Cita cita = findOrThrow(id);
        cita.setPacienteId(dto.getPacienteId());
        cita.setPacienteRut(dto.getPacienteRut());
        cita.setPacienteNombre(dto.getPacienteNombre());
        cita.setFechaAtencion(dto.getFechaAtencion());
        cita.setCentroAtencion(dto.getCentroAtencion());
        cita.setEspecialidad(dto.getEspecialidad());
        if (dto.getEstado() != null) cita.setEstado(dto.getEstado());
        return toDTO(citaRepo.save(cita));
    }

    /**
     * Cancela la cita y dispara reasignación automática al siguiente en lista de espera.
     */
    @Transactional
    public CitaResponseDTO cancelarCita(Long id) {
        Cita cita = findOrThrow(id);
        if (cita.getEstado() != EstadoCita.RESERVADA) {
            throw new ReasignacionException(
                    "Solo se pueden cancelar citas en estado RESERVADA. Estado actual: " + cita.getEstado());
        }
        cita.setEstado(EstadoCita.CANCELADA);
        citaRepo.save(cita);

        ReasignacionResultDTO resultado = reasignacionService.reasignarSlot(cita);
        log.info("Cancelación cita id={}: reasignadas {} citas", id, resultado.getAssignedCount());

        return toDTO(cita);
    }

    @Transactional
    public CitaResponseDTO completarCita(Long id) {
        Cita cita = findOrThrow(id);
        if (cita.getEstado() != EstadoCita.RESERVADA) {
            throw new ReasignacionException(
                    "Solo se pueden completar citas en estado RESERVADA. Estado actual: " + cita.getEstado());
        }
        cita.setEstado(EstadoCita.COMPLETADA);
        return toDTO(citaRepo.save(cita));
    }

    private Cita findOrThrow(Long id) {
        return citaRepo.findById(id)
                .orElseThrow(() -> new CitaNotFoundException("Cita id=" + id + " no encontrada"));
    }

    public CitaResponseDTO toDTO(Cita c) {
        return CitaResponseDTO.builder()
                .id(c.getId())
                .pacienteId(c.getPacienteId())
                .pacienteRut(c.getPacienteRut())
                .pacienteNombre(c.getPacienteNombre())
                .fechaAtencion(c.getFechaAtencion())
                .centroAtencion(c.getCentroAtencion())
                .especialidad(c.getEspecialidad())
                .estado(c.getEstado())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
