package javafxexpendio.modelo.pojo;

public class EstadoPedido {
    private int idEstadoPedido;
    private String estado;
    
    public EstadoPedido() {
    }
    
    public EstadoPedido(int idEstadoPedido, String estado) {
        this.idEstadoPedido = idEstadoPedido;
        this.estado = estado;
    }

    public int getIdEstadoPedido() {
        return idEstadoPedido;
    }

    public void setIdEstadoPedido(int idEstadoPedido) {
        this.idEstadoPedido = idEstadoPedido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
