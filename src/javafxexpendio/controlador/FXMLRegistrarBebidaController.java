/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxexpendio.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafxexpendio.modelo.dao.BebidaDAOImpl;
import javafxexpendio.modelo.pojo.Bebida;
import javafxexpendio.utilidades.Utilidad;

/**
 * FXML Controller class
 *
 * @author Dell
 */
public class FXMLRegistrarBebidaController implements Initializable {

    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfStock;
    @FXML
    private TextField tfStockMinimo;
    @FXML
    private TextField tfPrecio;
    @FXML
    private Label lbNombreError;
    @FXML
    private Label lbStockError;
    @FXML
    private Label lbStockMinimoError;
    @FXML
    private Label lbPrecioError;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void btnClicGuardar(ActionEvent event) {
        if (validarCampos()) {
            Bebida bebida = obtenerBebidaNueva();
            guardarBebida(bebida);
            Utilidad.getEscenarioComponente(tfNombre).close();
            limpiarCampos();
        }
    }

    @FXML
    private void btnClicCancelar(ActionEvent event) {
        Utilidad.getEscenarioComponente(tfNombre).close();
        limpiarCampos();
    }
    
    
    private boolean validarCampos() {
        boolean esValido = true;

        // Validar nombre
        if (tfNombre.getText().trim().isEmpty()) {
            lbNombreError.setText("*Campo obligatorio");
            esValido = false;
        } else {
            lbNombreError.setText("");
        }

        // Validar stock
        String textoStock = tfStock.getText().trim();
        if (textoStock.isEmpty()) {
            lbStockError.setText("*Campo obligatorio");
            esValido = false;
        } else {
            try {
                int stock = Integer.parseInt(textoStock);
                if (stock < 0) {
                    lbStockError.setText("*Número inválido");
                    esValido = false;
                } else {
                    lbStockError.setText("");
                }
            } catch (NumberFormatException e) {
                lbStockError.setText("*Número inválido");
                esValido = false;
            }
        }

        // Validar stock mínimo
        String textoStockMinimo = tfStockMinimo.getText().trim();
        if (textoStockMinimo.isEmpty()) {
            lbStockMinimoError.setText("*Campo obligatorio");
            esValido = false;
        } else {
            try {
                int stockMinimo = Integer.parseInt(textoStockMinimo);
                if (stockMinimo < 0) {
                    lbStockMinimoError.setText("*Número inválido");
                    esValido = false;
                } else {
                    lbStockMinimoError.setText("");
                }
            } catch (NumberFormatException e) {
                lbStockMinimoError.setText("*Número inválido");
                esValido = false;
            }
        }

        // Validar precio
        String textoPrecio = tfPrecio.getText().trim();
        if (textoPrecio.isEmpty()) {
            lbPrecioError.setText("*Campo obligatorio");
            esValido = false;
        } else {
            try {
                double precio = Double.parseDouble(textoPrecio);
                if (precio < 0) {
                    lbPrecioError.setText("*Número inválido");
                    esValido = false;
                } else {
                    lbPrecioError.setText("");
                }
            } catch (NumberFormatException e) {
                lbPrecioError.setText("*Número inválido");
                esValido = false;
            }
        }

        return esValido;
    }
    
    private Bebida obtenerBebidaNueva() {
        Bebida bebida = new Bebida();
        bebida.setBebida(tfNombre.getText());
        bebida.setStock(Integer.parseInt(tfStock.getText()));
        bebida.setStockMinimo(Integer.parseInt(tfStockMinimo.getText()));
        bebida.setPrecio(Double.parseDouble(tfPrecio.getText()));
        return bebida;
    }
    
    private void guardarBebida(Bebida bebida){
        try {
            BebidaDAOImpl bebidaDAOImpl = new BebidaDAOImpl();
            if (bebidaDAOImpl.crear(bebida)) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Bebida registrada", 
                        "La bebida fue registrada con exito");
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al registrar", 
                        "No se pudo registrar la bebida, intentalo más tarde.");
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error en la base de datos", ex.getMessage());
        }
    }
    
    private void limpiarCampos() {
        tfNombre.clear();
        tfStock.clear();
        tfStockMinimo.clear();
        tfPrecio.clear();

        lbNombreError.setText("");
        lbStockError.setText("");
        lbStockMinimoError.setText("");
        lbPrecioError.setText("");
    }

    
}
