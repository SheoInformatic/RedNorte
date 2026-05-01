package cl.rednorte.reasignacion.dto;

import cl.rednorte.reasignacion.entity.enums.CentroAtencion;
import cl.rednorte.reasignacion.entity.enums.Especialidad;
import cl.rednorte.reasignacion.entity.enums.EstadoCita;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CitaResponseDTO {
    private Long id;
    private Long pacienteId;
    private String pacienteRut;
    private String pacienteNombre;
    private LocalDate fechaAtencion;
    private CentroAtencion centroAtencion;
    private Especialidad especialidad;
    private EstadoCita estado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
