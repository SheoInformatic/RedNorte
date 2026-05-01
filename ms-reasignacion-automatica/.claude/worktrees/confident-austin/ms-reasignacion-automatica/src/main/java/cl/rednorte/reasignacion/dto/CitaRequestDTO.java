package cl.rednorte.reasignacion.dto;

import cl.rednorte.reasignacion.entity.enums.CentroAtencion;
import cl.rednorte.reasignacion.entity.enums.Especialidad;
import cl.rednorte.reasignacion.entity.enums.EstadoCita;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CitaRequestDTO {

    @NotNull(message = "pacienteId es obligatorio")
    private Long pacienteId;

    private String pacienteRut;

    private String pacienteNombre;

    @NotNull(message = "fechaAtencion es obligatoria")
    private LocalDate fechaAtencion;

    @NotNull(message = "centroAtencion es obligatorio")
    private CentroAtencion centroAtencion;

    @NotNull(message = "especialidad es obligatoria")
    private Especialidad especialidad;

    /** Opcional al crear. Si null, se asigna RESERVADA por defecto. */
    private EstadoCita estado;
}
