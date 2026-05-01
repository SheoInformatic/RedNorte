package cl.rednorte.reasignacion.dto;

import cl.rednorte.reasignacion.entity.enums.CentroAtencion;
import cl.rednorte.reasignacion.entity.enums.Especialidad;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ListaEsperaResponseDTO {
    private Long id;
    private Long pacienteId;
    private String pacienteRut;
    private String pacienteNombre;
    private String diagnostico;
    private CentroAtencion centroAtencion;
    private Especialidad especialidad;
    private LocalDate fechaPreferidaDesde;
    private Integer prioridad;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
