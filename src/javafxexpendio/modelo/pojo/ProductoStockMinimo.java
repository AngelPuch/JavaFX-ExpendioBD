/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxexpendio.modelo.pojo;

/**
 *
 * @author Dell
 */
public class ProductoStockMinimo {
    private int idBebida;
    private String nombreBebida;
    private int stock;
    private int stockMinimo;
    private double precio;
    private int diferencia;
    
    // Getters y setters
    public int getIdBebida() {
        return idBebida;
    }
    
    public void setIdBebida(int idBebida) {
        this.idBebida = idBebida;
    }
    
    public String getNombreBebida() {
        return nombreBebida;
    }
    
    public void setNombreBebida(String nombreBebida) {
        this.nombreBebida = nombreBebida;
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
    
    public int getStockMinimo() {
        return stockMinimo;
    }
    
    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }
    
    public double getPrecio() {
        return precio;
    }
    
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    
    public int getDiferencia() {
        return diferencia;
    }
    
    public void setDiferencia(int diferencia) {
        this.diferencia = diferencia;
    }
}
