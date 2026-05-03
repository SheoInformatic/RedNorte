package com.example.demo.service;

import com.example.demo.model.ListaEspera;
import com.example.demo.repository.ListaEsperaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListaEsperaService {

    private final ListaEsperaRepository repository;

    public ListaEsperaService(ListaEsperaRepository repository) {
        this.repository = repository;
    }

    // Crear con lógica de negocio
    public ListaEspera guardar(ListaEspera persona) {

        int posicion = repository.findAll().size() + 1;

        persona.setPosicion(posicion);
        persona.setEstado("EN_ESPERA");

        return repository.save(persona);
    }

    public List<ListaEspera> listar() {
        return repository.findAll();
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public List<ListaEspera> buscarPorEstado(String estado) {
        return repository.findByEstado(estado);
    }

    public ListaEspera atender(Long id) {
        ListaEspera persona = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));

        persona.setEstado("ATENDIDO");

        return repository.save(persona);
    }
}