package cl.rednorte.reasignacion.dto;

import cl.rednorte.reasignacion.entity.enums.CentroAtencion;
import cl.rednorte.reasignacion.entity.enums.Especialidad;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReasignacionResultDTO {

    private int assignedCount;
    private LocalDateTime timestamp;
    private List<AsignacionDetalle> assignments;

    @Data
    @Builder
    public static class AsignacionDetalle {
        private Long citaId;
        private Long pacienteId;
        private String pacienteNombre;
        private CentroAtencion centro;
        private Especialidad especialidad;
        private LocalDate fecha;
    }
}
