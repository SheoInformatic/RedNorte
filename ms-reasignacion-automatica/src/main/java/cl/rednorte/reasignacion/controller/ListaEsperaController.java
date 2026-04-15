package cl.rednorte.reasignacion.controller;

import cl.rednorte.reasignacion.dto.ListaEsperaRequestDTO;
import cl.rednorte.reasignacion.dto.ListaEsperaResponseDTO;
import cl.rednorte.reasignacion.service.ListaEsperaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lista-espera")
@RequiredArgsConstructor
public class ListaEsperaController {

    private final ListaEsperaService listaService;

    @PostMapping
    public ResponseEntity<ListaEsperaResponseDTO> inscribir(@Valid @RequestBody ListaEsperaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(listaService.inscribirPaciente(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListaEsperaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(listaService.obtenerEntrada(id));
    }

    @GetMapping
    public ResponseEntity<List<ListaEsperaResponseDTO>> listarActivos() {
        return ResponseEntity.ok(listaService.listarActivos());
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<ListaEsperaResponseDTO>> porPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(listaService.listarPorPaciente(pacienteId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ListaEsperaResponseDTO> retirar(@PathVariable Long id) {
        return ResponseEntity.ok(listaService.retirarPaciente(id));
    }
}
