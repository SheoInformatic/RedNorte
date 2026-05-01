package cl.rednorte.reasignacion.repository;

import cl.rednorte.reasignacion.entity.Cita;
import cl.rednorte.reasignacion.entity.enums.CentroAtencion;
import cl.rednorte.reasignacion.entity.enums.Especialidad;
import cl.rednorte.reasignacion.entity.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPacienteId(Long pacienteId);

    List<Cita> findByEstado(EstadoCita estado);

    List<Cita> findByCentroAtencionAndEspecialidadAndEstado(
            CentroAtencion centro, Especialidad especialidad, EstadoCita estado);

    Optional<Cita> findFirstByCentroAtencionAndEspecialidadAndFechaAtencionAndEstado(
            CentroAtencion centro, Especialidad especialidad,
            LocalDate fecha, EstadoCita estado);

    /**
     * Slots disponibles (LIBRE o CANCELADA) en un centro+especialidad a partir de una fecha.
     * Ordenados por fecha ascendente para asignar el más próximo primero.
     */
    @Query("""
            SELECT c FROM Cita c
            WHERE c.centroAtencion = :centro
              AND c.especialidad   = :especialidad
              AND c.estado         IN :estados
              AND c.fechaAtencion  >= :desde
            ORDER BY c.fechaAtencion ASC
            """)
    List<Cita> findAvailableSlots(
            @Param("centro") CentroAtencion centro,
            @Param("especialidad") Especialidad especialidad,
            @Param("desde") LocalDate desde,
            @Param("estados") List<EstadoCita> estados);

    /**
     * Pares (centro, especialidad) distintos que tienen slots disponibles.
     * Usados por el escaneo proactivo para iterar combinaciones.
     */
    @Query("""
            SELECT DISTINCT c.centroAtencion, c.especialidad FROM Cita c
            WHERE c.estado IN :estados
              AND c.fechaAtencion >= :desde
            """)
    List<Object[]> findDistinctCentroEspecialidadWithAvailableSlots(
            @Param("estados") List<EstadoCita> estados,
            @Param("desde") LocalDate desde);
}
