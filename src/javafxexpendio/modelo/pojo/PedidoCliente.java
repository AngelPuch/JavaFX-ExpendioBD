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
    private Date fecha;
    private String estado;
    private Cliente cliente;

    public PedidoCliente() {
    }

    public PedidoCliente(int idPedidoCliente, double total, Date fecha, String estado, Cliente cliente) {
        this.idPedidoCliente = idPedidoCliente;
        this.total = total;
        this.fecha = fecha;
        this.estado = estado;
        this.cliente = cliente;
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    
}
