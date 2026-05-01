package cl.rednorte.reasignacion.service;

import cl.rednorte.reasignacion.dto.ListaEsperaRequestDTO;
import cl.rednorte.reasignacion.dto.ListaEsperaResponseDTO;
import cl.rednorte.reasignacion.entity.ListaEspera;
import cl.rednorte.reasignacion.entity.enums.Especialidad;
import cl.rednorte.reasignacion.exception.CitaNotFoundException;
import cl.rednorte.reasignacion.repository.ListaEsperaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListaEsperaService {

    private final ListaEsperaRepository repo;

    @Transactional
    public ListaEsperaResponseDTO inscribirPaciente(ListaEsperaRequestDTO dto) {
        ListaEspera entrada = ListaEspera.builder()
                .pacienteId(dto.getPacienteId())
                .pacienteRut(dto.getPacienteRut())
                .pacienteNombre(dto.getPacienteNombre())
                .diagnostico(dto.getDiagnostico())
                .centroAtencion(dto.getCentroAtencion())
                .especialidad(dto.getEspecialidad())
                .fechaPreferidaDesde(dto.getFechaPreferidaDesde())
                .prioridad(calcularPrioridad(dto.getDiagnostico(), dto.getEspecialidad(), LocalDateTime.now()))
                .activo(true)
                .build();
        return toDTO(repo.save(entrada));
    }

    @Transactional(readOnly = true)
    public ListaEsperaResponseDTO obtenerEntrada(Long id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<ListaEsperaResponseDTO> listarActivos() {
        return repo.findByActivoTrue().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ListaEsperaResponseDTO> listarPorPaciente(Long pacienteId) {
        return repo.findByPacienteIdAndActivoTrue(pacienteId).stream().map(this::toDTO).toList();
    }

    @Transactional
    public ListaEsperaResponseDTO retirarPaciente(Long id) {
        ListaEspera entrada = findOrThrow(id);
        entrada.setActivo(false);
        return toDTO(repo.save(entrada));
    }

    /**
     * Recalcula prioridades de todas las entradas activas.
     * Llamado antes del escaneo proactivo.
     */
    @Transactional
    public void recalcularPrioridades() {
        List<ListaEspera> activas = repo.findByActivoTrue();
        for (ListaEspera e : activas) {
            e.setPrioridad(calcularPrioridad(e.getDiagnostico(), e.getEspecialidad(), e.getCreatedAt()));
        }
        repo.saveAll(activas);
        log.debug("Prioridades recalculadas para {} entradas activas", activas.size());
    }

    /**
     * Fórmula de prioridad (menor = más urgente):
     * Base 100
     * -10 por semana de espera
     * -20 si tiene diagnóstico
     * -30 si el diagnóstico coincide con la especialidad
     */
    int calcularPrioridad(String diagnostico, Especialidad especialidad, LocalDateTime desde) {
        int prioridad = 100;

        long semanasEsperando = ChronoUnit.WEEKS.between(desde, LocalDateTime.now());
        prioridad -= (int) (semanasEsperando * 10);

        if (diagnostico != null && !diagnostico.isBlank()) {
            prioridad -= 20;
            if (diagnosticoCoincideEspecialidad(diagnostico, especialidad)) {
                prioridad -= 30;
            }
        }

        return prioridad;
    }

    private static final Map<Especialidad, List<String>> KEYWORDS = Map.of(
            Especialidad.TRAUMATOLOGIA, List.of("trauma", "fractura", "hueso", "articular", "ortop"),
            Especialidad.PEDIATRIA,     List.of("niño", "infant", "pediatr", "neonato"),
            Especialidad.DERMATOLOGIA,  List.of("piel", "dermat", "acne", "alergia", "eccema"),
            Especialidad.MEDICINA_GENERAL, List.of()
    );

    private boolean diagnosticoCoincideEspecialidad(String diagnostico, Especialidad especialidad) {
        String lower = diagnostico.toLowerCase();
        return KEYWORDS.getOrDefault(especialidad, List.of())
                .stream()
                .anyMatch(lower::contains);
    }

    private ListaEspera findOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new CitaNotFoundException("Lista espera id=" + id + " no encontrada"));
    }

    public ListaEsperaResponseDTO toDTO(ListaEspera e) {
        return ListaEsperaResponseDTO.builder()
                .id(e.getId())
                .pacienteId(e.getPacienteId())
                .pacienteRut(e.getPacienteRut())
                .pacienteNombre(e.getPacienteNombre())
                .diagnostico(e.getDiagnostico())
                .centroAtencion(e.getCentroAtencion())
                .especialidad(e.getEspecialidad())
                .fechaPreferidaDesde(e.getFechaPreferidaDesde())
                .prioridad(e.getPrioridad())
                .activo(e.getActivo())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
