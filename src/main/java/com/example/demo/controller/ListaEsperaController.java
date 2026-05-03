package com.example.demo.controller;

import com.example.demo.model.ListaEspera;
import com.example.demo.service.ListaEsperaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lista-espera")
public class ListaEsperaController {

    private final ListaEsperaService service;

    public ListaEsperaController(ListaEsperaService service) {
        this.service = service;
    }

    // Crear
    @PostMapping
    public ListaEspera crear(@RequestBody ListaEspera persona) {
        return service.guardar(persona);
    }

    // Listar todo
    @GetMapping
    public List<ListaEspera> listar() {
        return service.listar();
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    // Buscar por estado
    @GetMapping("/buscar")
    public List<ListaEspera> buscarPorEstado(@RequestParam String estado) {
        return service.buscarPorEstado(estado);
    }

    @PutMapping("/atender/{id}")
    public ListaEspera atender(@PathVariable Long id) {
        return service.atender(id);
    }

}