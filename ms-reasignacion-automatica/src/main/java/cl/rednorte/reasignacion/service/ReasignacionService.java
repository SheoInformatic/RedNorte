package cl.rednorte.reasignacion.service;

import cl.rednorte.reasignacion.dto.ReasignacionResultDTO;
import cl.rednorte.reasignacion.entity.Cita;
import cl.rednorte.reasignacion.entity.ListaEspera;
import cl.rednorte.reasignacion.entity.enums.CentroAtencion;
import cl.rednorte.reasignacion.entity.enums.Especialidad;
import cl.rednorte.reasignacion.entity.enums.EstadoCita;
import cl.rednorte.reasignacion.repository.CitaRepository;
import cl.rednorte.reasignacion.repository.ListaEsperaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReasignacionService {

    private final CitaRepository citaRepo;
    private final ListaEsperaRepository listaRepo;
    private final ListaEsperaService listaService;

    private static final List<EstadoCita> ESTADOS_DISPONIBLES =
            List.of(EstadoCita.CANCELADA, EstadoCita.LIBRE);

    /**
     * Reasigna inmediatamente el slot de una cita cancelada.
     * Busca el candidato de mayor prioridad en la lista de espera
     * para el mismo centro + especialidad + fecha (o la más próxima disponible).
     */
    @Transactional
    public ReasignacionResultDTO reasignarSlot(Cita citaCancelada) {
        log.info("Reasignando slot cita id={} ({} / {} / {})",
                citaCancelada.getId(),
                citaCancelada.getCentroAtencion(),
                citaCancelada.getEspecialidad(),
                citaCancelada.getFechaAtencion());

        List<ListaEspera> candidatos = listaRepo.findCandidatesForSlot(
                citaCancelada.getCentroAtencion(),
                citaCancelada.getEspecialidad(),
                citaCancelada.getFechaAtencion());

        List<ReasignacionResultDTO.AsignacionDetalle> detalles = new ArrayList<>();

        if (candidatos.isEmpty()) {
            log.info("Sin candidatos en lista de espera para slot id={}", citaCancelada.getId());
        } else {
            ListaEspera candidato = candidatos.get(0);
            Cita nuevaCita = asignarSlot(citaCancelada, candidato);
            detalles.add(buildDetalle(nuevaCita, candidato));
            log.info("Slot reasignado → nueva cita id={}, paciente id={}",
                    nuevaCita.getId(), candidato.getPacienteId());
        }

        return ReasignacionResultDTO.builder()
                .assignedCount(detalles.size())
                .timestamp(LocalDateTime.now())
                .assignments(detalles)
                .build();
    }

    /**
     * Escaneo proactivo: itera todos los pares (centro, especialidad) con entradas activas
     * y asigna slots LIBRE/CANCELADA a candidatos en orden de prioridad.
     */
    @Transactional
    public ReasignacionResultDTO runProactiveScan() {
        log.info("Iniciando escaneo proactivo de reasignación");
        listaService.recalcularPrioridades();

        List<Object[]> pares = listaRepo.findDistinctCentroEspecialidadActivos();
        List<ReasignacionResultDTO.AsignacionDetalle> detalles = new ArrayList<>();

        for (Object[] par : pares) {
            CentroAtencion centro = (CentroAtencion) par[0];
            Especialidad especialidad = (Especialidad) par[1];

            List<Cita> slotsDisponibles = citaRepo.findAvailableSlots(
                    centro, especialidad, LocalDate.now(), ESTADOS_DISPONIBLES);

            for (Cita slot : slotsDisponibles) {
                List<ListaEspera> candidatos = listaRepo.findCandidatesForSlot(
                        centro, especialidad, slot.getFechaAtencion());

                if (candidatos.isEmpty()) break;

                ListaEspera candidato = candidatos.get(0);
                Cita nuevaCita = asignarSlot(slot, candidato);
                detalles.add(buildDetalle(nuevaCita, candidato));
                log.info("Escaneo proactivo → nueva cita id={}, paciente id={}",
                        nuevaCita.getId(), candidato.getPacienteId());
            }
        }

        log.info("Escaneo proactivo completado. Asignaciones: {}", detalles.size());
        return ReasignacionResultDTO.builder()
                .assignedCount(detalles.size())
                .timestamp(LocalDateTime.now())
                .assignments(detalles)
                .build();
    }

    /** Crea la nueva Cita para el candidato usando los datos del slot disponible y marca la lista de espera. */
    private Cita asignarSlot(Cita slot, ListaEspera candidato) {
        // Marcar slot original como CANCELADA si era LIBRE (ya era CANCELADA si vino de cancelarCita)
        if (slot.getEstado() == EstadoCita.LIBRE) {
            slot.setEstado(EstadoCita.CANCELADA);
            citaRepo.save(slot);
        }

        Cita nuevaCita = Cita.builder()
                .pacienteId(candidato.getPacienteId())
                .pacienteRut(candidato.getPacienteRut())
                .pacienteNombre(candidato.getPacienteNombre())
                .fechaAtencion(slot.getFechaAtencion())
                .centroAtencion(slot.getCentroAtencion())
                .especialidad(slot.getEspecialidad())
                .estado(EstadoCita.RESERVADA)
                .build();
        nuevaCita = citaRepo.save(nuevaCita);

        candidato.setActivo(false);
        listaRepo.save(candidato);

        return nuevaCita;
    }

    private ReasignacionResultDTO.AsignacionDetalle buildDetalle(Cita cita, ListaEspera candidato) {
        return ReasignacionResultDTO.AsignacionDetalle.builder()
                .citaId(cita.getId())
                .pacienteId(candidato.getPacienteId())
                .pacienteNombre(candidato.getPacienteNombre())
                .centro(cita.getCentroAtencion())
                .especialidad(cita.getEspecialidad())
                .fecha(cita.getFechaAtencion())
                .build();
    }
}
