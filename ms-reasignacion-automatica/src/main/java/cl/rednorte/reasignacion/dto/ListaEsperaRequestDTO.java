package cl.rednorte.reasignacion.dto;

import cl.rednorte.reasignacion.entity.enums.CentroAtencion;
import cl.rednorte.reasignacion.entity.enums.Especialidad;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ListaEsperaRequestDTO {

    @NotNull(message = "pacienteId es obligatorio")
    private Long pacienteId;

    private String pacienteRut;

    private String pacienteNombre;

    private String diagnostico;

    @NotNull(message = "centroAtencion es obligatorio")
    private CentroAtencion centroAtencion;

    @NotNull(message = "especialidad es obligatoria")
    private Especialidad especialidad;

    /** Fecha mínima preferida. Null = cualquier fecha disponible. */
    private LocalDate fechaPreferidaDesde;
}
