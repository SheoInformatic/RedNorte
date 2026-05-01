package cl.rednorte.reasignacion.entity;

import cl.rednorte.reasignacion.entity.enums.CentroAtencion;
import cl.rednorte.reasignacion.entity.enums.Especialidad;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "lista_espera",
    uniqueConstraints = @UniqueConstraint(
        name = "uc_paciente_centro_especialidad",
        columnNames = {"paciente_id", "centro_atencion", "especialidad"}
    ),
    indexes = {
        @Index(name = "idx_le_centro_esp_activo", columnList = "centro_atencion, especialidad, activo"),
        @Index(name = "idx_le_prioridad",          columnList = "prioridad"),
        @Index(name = "idx_le_paciente",            columnList = "paciente_id")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListaEspera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paciente_id", nullable = false)
    private Long pacienteId;

    @Column(name = "paciente_rut", length = 20)
    private String pacienteRut;

    @Column(name = "paciente_nombre", length = 120)
    private String pacienteNombre;

    @Column(name = "diagnostico", length = 255)
    private String diagnostico;

    @Enumerated(EnumType.STRING)
    @Column(name = "centro_atencion", nullable = false, length = 30)
    private CentroAtencion centroAtencion;

    @Enumerated(EnumType.STRING)
    @Column(name = "especialidad", nullable = false, length = 30)
    private Especialidad especialidad;

    /** Fecha mínima preferida. NULL = cualquier fecha disponible. */
    @Column(name = "fecha_preferida_desde")
    private LocalDate fechaPreferidaDesde;

    /** Menor número = mayor prioridad */
    @Column(name = "prioridad", nullable = false)
    private Integer prioridad;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (prioridad == null) prioridad = 100;
        if (activo == null) activo = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
