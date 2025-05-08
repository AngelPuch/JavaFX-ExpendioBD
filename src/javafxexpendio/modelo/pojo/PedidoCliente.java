/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxexpendio.modelo.pojo;

import java.util.Date;

/**
 *
 * @author zenbook i5
 */
public class PedidoCliente {
    private int idPedidoCliente;
    private double total;
    private Date fechaCreacion;
    private Date fechaLimite;
    private Cliente cliente;
    private Venta venta;

    public PedidoCliente() {
    }

    public PedidoCliente(int idPedidoCliente, double total, Date fechaCreacion, Date fechaLimite, Cliente cliente, Venta venta) {
        this.idPedidoCliente = idPedidoCliente;
        this.total = total;
        this.fechaCreacion = fechaCreacion;
        this.fechaLimite = fechaLimite;
        this.cliente = cliente;
        this.venta = venta;
    }

    public int getIdPedidoCliente() {
        return idPedidoCliente;
    }

    public void setIdPedidoCliente(int idPedidoCliente) {
        this.idPedidoCliente = idPedidoCliente;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(Date fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    
}
