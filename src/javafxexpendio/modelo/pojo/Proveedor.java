package javafxexpendio.modelo.pojo;

public class Proveedor {
    private int idProveedor;
    private String razonSocial;
    private String telefono;
    private String correo;

    public Proveedor() {
    }

    public Proveedor(int idProveedor, String razonSocial, String telefono, String correo) {
        this.idProveedor = idProveedor;
        this.razonSocial = razonSocial;
        this.telefono = telefono;
        this.correo = correo;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Override
    public String toString() {
        return razonSocial;
    }
    
    
    
    
}
