/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxexpendio.modelo.pojo;

/**
 *
 * @author zenbook i5
 */
public class DetallePedido {
    private PedidoCliente pedidoCliente;
    private Bebida bebida;
    private int cantidad;
    private double subtotal;

    public DetallePedido() {
    }

    public DetallePedido(PedidoCliente pedidoCliente, Bebida bebida, int cantidad, double subtotal) {
        this.pedidoCliente = pedidoCliente;
        this.bebida = bebida;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public PedidoCliente getPedidoCliente() {
        return pedidoCliente;
    }

    public void setPedidoCliente(PedidoCliente pedidoCliente) {
        this.pedidoCliente = pedidoCliente;
    }

    public Bebida getBebida() {
        return bebida;
    }

    public void setBebida(Bebida bebida) {
        this.bebida = bebida;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    
    
}
