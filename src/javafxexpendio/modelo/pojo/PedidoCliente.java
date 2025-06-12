package javafxexpendio.modelo.pojo;

import java.util.Date;

public class PedidoCliente {
    private int idPedidoCliente;
    private Date fechaCreacion;
    private Date fechaLimite;
    private Cliente cliente;
    private Venta venta;
    private int idEstadoPedido;

    public PedidoCliente() {
    }

    public PedidoCliente(int idPedidoCliente, Date fechaCreacion, Date fechaLimite, Cliente cliente, Venta venta, int idEstadoPedido) {
        this.idPedidoCliente = idPedidoCliente;
        this.fechaCreacion = fechaCreacion;
        this.fechaLimite = fechaLimite;
        this.cliente = cliente;
        this.venta = venta;
    }

    public int getIdEstadoPedido() {
        return idEstadoPedido;
    }

    public void setIdEstadoPedido(int idEstadoPedido) {
        this.idEstadoPedido = idEstadoPedido;
    }

    public int getIdPedidoCliente() {
        return idPedidoCliente;
    }

    public void setIdPedidoCliente(int idPedidoCliente) {
        this.idPedidoCliente = idPedidoCliente;
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
