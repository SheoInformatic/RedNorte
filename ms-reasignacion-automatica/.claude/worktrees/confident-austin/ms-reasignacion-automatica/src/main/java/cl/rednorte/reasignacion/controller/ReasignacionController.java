package cl.rednorte.reasignacion.controller;

import cl.rednorte.reasignacion.dto.ReasignacionResultDTO;
import cl.rednorte.reasignacion.service.ReasignacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reasignacion")
@RequiredArgsConstructor
public class ReasignacionController {

    private final ReasignacionService reasignacionService;

    /** Dispara manualmente el escaneo proactivo de slots disponibles. */
    @PostMapping("/escanear")
    public ResponseEntity<ReasignacionResultDTO> escanear() {
        return ResponseEntity.ok(reasignacionService.runProactiveScan());
    }
}
