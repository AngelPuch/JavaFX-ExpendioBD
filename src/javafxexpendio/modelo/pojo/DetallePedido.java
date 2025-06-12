package javafxexpendio.modelo.pojo;

public class DetallePedido {
    private PedidoCliente pedidoCliente;
    private Bebida bebida;
    private int cantidad;
    private double precioBebida;

    public DetallePedido() {
    }

    public DetallePedido(PedidoCliente pedidoCliente, Bebida bebida, int cantidad, double precioBebida) {
        this.pedidoCliente = pedidoCliente;
        this.bebida = bebida;
        this.cantidad = cantidad;
        this.precioBebida = precioBebida;
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

    public double getPrecioBebida() {
        return precioBebida;
    }

    public void setPrecioBebida(double precioBebida) {
        this.precioBebida = precioBebida;
    }
    
}
