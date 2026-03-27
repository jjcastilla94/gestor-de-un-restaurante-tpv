package com.example.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
@Table(name = "usuario_actual")
public class UsuarioActual {
    @GeneratedValue
    @Id
    private Long id;
    
    private String nombre, pass;
}
