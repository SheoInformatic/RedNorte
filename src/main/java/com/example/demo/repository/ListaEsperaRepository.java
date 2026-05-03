package com.example.demo.repository;

import com.example.demo.model.ListaEspera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListaEsperaRepository extends JpaRepository<ListaEspera, Long> {

    List<ListaEspera> findByEstado(String estado);

}