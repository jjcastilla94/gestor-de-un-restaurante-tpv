package com.example.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "categoria")
public class Categoria {
    @GeneratedValue
    @Id
    private Long id;
    private String nombre;
    
    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos;
}
