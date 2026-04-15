package cl.rednorte.reasignacion.controller;

import cl.rednorte.reasignacion.dto.CitaRequestDTO;
import cl.rednorte.reasignacion.dto.CitaResponseDTO;
import cl.rednorte.reasignacion.entity.enums.EstadoCita;
import cl.rednorte.reasignacion.service.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    @PostMapping
    public ResponseEntity<CitaResponseDTO> crear(@Valid @RequestBody CitaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.crearCita(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtenerCita(id));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<CitaResponseDTO>> porPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(citaService.listarPorPaciente(pacienteId));
    }

    @GetMapping
    public ResponseEntity<List<CitaResponseDTO>> porEstado(
            @RequestParam(required = false) EstadoCita estado) {
        if (estado != null) {
            return ResponseEntity.ok(citaService.listarPorEstado(estado));
        }
        return ResponseEntity.ok(citaService.listarPorEstado(null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CitaRequestDTO dto) {
        return ResponseEntity.ok(citaService.actualizarCita(id, dto));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<CitaResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.cancelarCita(id));
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<CitaResponseDTO> completar(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.completarCita(id));
    }
}
