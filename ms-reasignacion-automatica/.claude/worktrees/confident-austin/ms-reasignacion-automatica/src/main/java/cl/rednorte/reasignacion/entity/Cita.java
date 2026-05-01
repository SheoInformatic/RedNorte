package cl.rednorte.reasignacion.entity;

import cl.rednorte.reasignacion.entity.enums.CentroAtencion;
import cl.rednorte.reasignacion.entity.enums.Especialidad;
import cl.rednorte.reasignacion.entity.enums.EstadoCita;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "citas",
    indexes = {
        @Index(name = "idx_citas_paciente",         columnList = "paciente_id"),
        @Index(name = "idx_citas_centro_esp_fecha",  columnList = "centro_atencion, especialidad, fecha_atencion"),
        @Index(name = "idx_citas_estado",            columnList = "estado"),
        @Index(name = "idx_citas_fecha",             columnList = "fecha_atencion")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paciente_id", nullable = false)
    private Long pacienteId;

    @Column(name = "paciente_rut", length = 20)
    private String pacienteRut;

    @Column(name = "paciente_nombre", length = 120)
    private String pacienteNombre;

    @Column(name = "fecha_atencion", nullable = false)
    private LocalDate fechaAtencion;

    @Enumerated(EnumType.STRING)
    @Column(name = "centro_atencion", nullable = false, length = 30)
    private CentroAtencion centroAtencion;

    @Enumerated(EnumType.STRING)
    @Column(name = "especialidad", nullable = false, length = 30)
    private Especialidad especialidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCita estado;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoCita.RESERVADA;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
