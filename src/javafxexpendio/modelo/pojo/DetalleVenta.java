package javafxexpendio.modelo.pojo;

public class DetalleVenta {
    private int idVenta;
    private int cantidad;
    private Bebida bebida;
    private double total;
    private double precioBebida;
    private double precioConDescuento;

    public DetalleVenta() {
    }

    public DetalleVenta(int idVenta, int cantidad, Bebida bebida, double total, double precioBebida) {
        this.idVenta = idVenta;
        this.cantidad = cantidad;
        this.bebida = bebida;
        this.total = total;
        this.precioBebida = precioBebida;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Bebida getBebida() {
        return bebida;
    }

    public void setBebida(Bebida bebida) {
        this.bebida = bebida;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getPrecioBebida() {
        return precioBebida;
    }

    public void setPrecioBebida(double precioBebida) {
        this.precioBebida = precioBebida;
    }

    public double getPrecioConDescuento() {
        return precioConDescuento;
    }

    public void setPrecioConDescuento(double precioConDescuento) {
        this.precioConDescuento = precioConDescuento;
    }
    
    
    
}
