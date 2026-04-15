package cl.rednorte.reasignacion.repository;

import cl.rednorte.reasignacion.entity.ListaEspera;
import cl.rednorte.reasignacion.entity.enums.CentroAtencion;
import cl.rednorte.reasignacion.entity.enums.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ListaEsperaRepository extends JpaRepository<ListaEspera, Long> {

    List<ListaEspera> findByActivoTrue();

    List<ListaEspera> findByPacienteIdAndActivoTrue(Long pacienteId);

    boolean existsByPacienteIdAndCentroAtencionAndEspecialidadAndActivoTrue(
            Long pacienteId, CentroAtencion centro, Especialidad especialidad);

    /**
     * Candidatos activos para un slot (centro + especialidad + fecha).
     * Respeta fecha preferida mínima. Ordena por prioridad ASC, luego por createdAt ASC.
     */
    @Query("""
            SELECT l FROM ListaEspera l
            WHERE l.centroAtencion = :centro
              AND l.especialidad   = :especialidad
              AND l.activo         = true
              AND (l.fechaPreferidaDesde IS NULL OR l.fechaPreferidaDesde <= :fecha)
            ORDER BY l.prioridad ASC, l.createdAt ASC
            """)
    List<ListaEspera> findCandidatesForSlot(
            @Param("centro") CentroAtencion centro,
            @Param("especialidad") Especialidad especialidad,
            @Param("fecha") LocalDate fecha);

    /**
     * Pares (centro, especialidad) que tienen al menos una entrada activa.
     * Usados por el escaneo proactivo.
     */
    @Query("""
            SELECT DISTINCT l.centroAtencion, l.especialidad FROM ListaEspera l
            WHERE l.activo = true
            """)
    List<Object[]> findDistinctCentroEspecialidadActivos();
}
