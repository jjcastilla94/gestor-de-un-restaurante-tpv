package com.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "descuento")
public class Descuento {
    
    @GeneratedValue
    @Id
    private Long id;
    private String nombre;
}
