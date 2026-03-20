package com.example;

import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Clase Ticket que representa un ticket de venta en el sistema TPV.
 * Contiene informaciÃ³n sobre la mesa, fecha y hora, precio, mÃ©todo de pago y empleado asociado.
 */
public class Ticket {
    
    /** Identificador Ãºnico del ticket */
    private IntegerProperty id;
    /** Identificador de la mesa asociada al ticket */
    private IntegerProperty id_mesa;
    /** Fecha y hora en la que se generÃ³ el ticket */
    private StringProperty fecha_hora;
    /** Precio total del ticket */
    private StringProperty precio;
    /** Identificador del mÃ©todo de pago utilizado */
    private IntegerProperty id_metodo_pago;
    /** Identificador del empleado que realizÃ³ el cobro */
    private IntegerProperty id_empleado;

    /**
     * Constructor de la clase Ticket.
     * 
     * @param id Identificador Ãºnico del ticket
     * @param id_mesa Identificador de la mesa
     * @param fecha_hora Fecha y hora del ticket
     * @param precio Precio total del ticket
     * @param id_metodo_pago Identificador del mÃ©todo de pago
     * @param id_empleado Identificador del empleado que realizÃ³ el cobro
     */
    public Ticket(int id, int id_mesa, String fecha_hora, String precio, int id_metodo_pago, int id_empleado) {
        this.id = new SimpleIntegerProperty(id);
        this.id_mesa = new SimpleIntegerProperty(id_mesa);
        this.fecha_hora = new SimpleStringProperty(fecha_hora);
        this.precio = new SimpleStringProperty(String.valueOf(precio));
        this.id_metodo_pago = new SimpleIntegerProperty(id_metodo_pago);
        this.id_empleado = new SimpleIntegerProperty(id_empleado);
    }

    /** @return propiedad id */
    public IntegerProperty idProperty() {
        return id;
    }

    /** @return propiedad id_mesa */
    public IntegerProperty id_mesaProperty() {
        return id_mesa;
    }

    /** @return propiedad fecha_hora */
    public StringProperty fecha_horaProperty() {
        return fecha_hora;
    }

    /** @return propiedad precio */
    public StringProperty precioProperty() {
        return precio;
    }

    /** @return propiedad id_metodo_pago */
    public IntegerProperty id_metodo_pagoProperty() {
        return id_metodo_pago;
    }

    /** @return propiedad id_empleado */
    public IntegerProperty id_empleadoProperty() {
        return id_empleado;
    }

    // Getters y Setters

    /** @return id del ticket */
    public int getId() {
        return id.get();
    }
    
    /** @param id Nuevo id del ticket */
    public void setId(int id) {
        this.id.set(id);
    }

    /** @return id de la mesa */
    public int getIdMesa() {
        return id_mesa.get();
    }

    /** @param id_mesa Nuevo id de la mesa */
    public void setIdMesa(int id_mesa) {
        this.id_mesa.set(id_mesa);
    }

    /** @return fecha y hora del ticket */
    public String getFechaHora() {
        return fecha_hora.get();
    }

    /** @param fecha_hora Nueva fecha y hora del ticket */
    public void setFechaHora(String fecha_hora) {
        this.fecha_hora.set(fecha_hora);
    }

    /** @return precio total del ticket */
    public String getPrecio() {
        return precio.get();
    }

    /** @param precio Nuevo precio total del ticket */
    public void setPrecio(String precio) {
        this.precio.set(precio);
    }

    /** @return id del mÃ©todo de pago */
    public int getIdMetodoPago() {
        return id_metodo_pago.get();
    }

    /** @param id_metodo_pago Nuevo id del mÃ©todo de pago */
    public void setIdMetodoPago(int id_metodo_pago) {
        this.id_metodo_pago.set(id_metodo_pago);
    }

    /** @return id del empleado que realizÃ³ el cobro */
    public int getIdEmpleado() {
        return id_empleado.get();
    }

    /** @param id_empleado Nuevo id del empleado */
    public void setIdEmpleado(int id_empleado) {
        this.id_empleado.set(id_empleado);
    }
}