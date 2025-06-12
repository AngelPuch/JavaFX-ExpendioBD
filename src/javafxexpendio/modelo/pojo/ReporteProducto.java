/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxexpendio.modelo.pojo;

/**
 *
 * @author Dell
 */
public class ReporteProducto {
    private int idBebida;
    private String nombreBebida;
    private String contenido_neto;
    private int cantidadVendida;
    private double totalRecaudado;
    
    // Getters y setters
    public int getIdBebida() {
        return idBebida;
    }
    
    public void setIdBebida(int idBebida) {
        this.idBebida = idBebida;
    }
    
    public String getNombreBebida() {
        return nombreBebida + " " + contenido_neto;
    }
    
    public void setNombreBebida(String nombreBebida) {
        this.nombreBebida = nombreBebida;
    }
    
    public int getCantidadVendida() {
        return cantidadVendida;
    }
    
    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }
    
    public double getTotalRecaudado() {
        return totalRecaudado;
    }
    
    public void setTotalRecaudado(double totalRecaudado) {
        this.totalRecaudado = totalRecaudado;
    }

    public String getContenido_neto() {
        return contenido_neto;
    }

    public void setContenido_neto(String contenido_neto) {
        this.contenido_neto = contenido_neto;
    }
    
    
}
