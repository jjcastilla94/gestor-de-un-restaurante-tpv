package com.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

import com.example.entity.enums.EstadoMesa;

@Entity
@Table(name = "mesa")
public class Mesa {
    
    @GeneratedValue
    @Id
    private Long id;
    private String nombre;
    private int numero, capacidad;

    private EstadoMesa estado;

    @OneToMany(mappedBy = "mesa")
    private List<Comanda> comandas;

    @OneToMany(mappedBy = "mesa")
    private List<Ticket> tickets;

}
