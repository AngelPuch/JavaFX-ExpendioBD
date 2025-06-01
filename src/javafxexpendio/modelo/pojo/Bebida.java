/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxexpendio.modelo.pojo;

/**
 *
 * @author Dell
 */
public class Bebida {
    private int idBebida;
    private String bebida;
    private int stock;
    private int stockMinimo;
    private double precio;
    private String contenidoNeto;

    public Bebida() {
    }

    public Bebida(int idBebida, String bebida, int stock, int stockMinimo, double precio) {
        this.idBebida = idBebida;
        this.bebida = bebida;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.precio = precio;
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

    public String getContenidoNeto() {
        return contenidoNeto;
    }

    public void setContenidoNeto(String contenidoNeto) {
        this.contenidoNeto = contenidoNeto;
    }

    @Override
    public String toString() {
        return bebida + " " + contenidoNeto;
    }
    
    
    
}
