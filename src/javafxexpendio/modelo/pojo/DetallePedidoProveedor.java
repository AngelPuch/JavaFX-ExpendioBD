package javafxexpendio.modelo.pojo;

public class DetallePedidoProveedor {
    private int idPedidoProveedor;
    private int idBebida;
    private String bebida;
    private int cantidad;
    private double precioEstimado;
    private double subtotal;
    
    public DetallePedidoProveedor() {
    }
    
    public DetallePedidoProveedor(int idBebida, String bebida, int cantidad, double precioEstimado) {
        this.idBebida = idBebida;
        this.bebida = bebida;
        this.cantidad = cantidad;
        this.precioEstimado = precioEstimado;
        this.subtotal = cantidad * precioEstimado;
    }

    public int getIdPedidoProveedor() {
        return idPedidoProveedor;
    }

    public void setIdPedidoProveedor(int idPedidoProveedor) {
        this.idPedidoProveedor = idPedidoProveedor;
    }

    public int getIdBebida() {
        return idBebida;
    }

    public void setIdBebida(int idBebida) {
        this.idBebida = idBebida;
    }

    public String getBebida() {
        return bebida;
    }

    public void setBebida(String bebida) {
        this.bebida = bebida;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subtotal = cantidad * precioEstimado;
    }

    public double getPrecioEstimado() {
        return precioEstimado;
    }

    public void setPrecioEstimado(double precioEstimado) {
        this.precioEstimado = precioEstimado;
        this.subtotal = cantidad * precioEstimado;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}