/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxexpendio.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafxexpendio.interfaz.Notificacion;
import javafxexpendio.modelo.dao.BebidaDAOImpl;
import javafxexpendio.modelo.pojo.Bebida;
import javafxexpendio.modelo.pojo.Usuario;
import javafxexpendio.utilidades.Utilidad;
import static javafxexpendio.utilidades.Utilidad.mostrarDialogoEntrada;

/**
 * FXML Controller class
 *
 * @author Dell
 */
public class FXMLFormularioBebidaController implements Initializable {

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
    @FXML
    private Button btnActualizarStock;
    private  Usuario usuarioSesion;
    //Variables necesarias para implementar el patron observable
    private Notificacion observador;
    private Bebida bebidaEdicion;
    private boolean isEdicion;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    } 
    
    //Función integrada para el observador
    public void inicializarInformacion(boolean isEdicion, Bebida bebidaEdicion, Notificacion observador, Usuario usuarioSesion) {
        this.bebidaEdicion = bebidaEdicion;
        this.isEdicion = isEdicion;
        this.observador = observador;
        this.usuarioSesion = usuarioSesion;
        if (isEdicion) {
            cargarInformacionEdicion();
            actualizarDisponibilidadComponentes();
        }
    }

    @FXML
    private void btnClicGuardar(ActionEvent event) {
        if (validarCampos()) {
            asigarTipoOperacion();
            Utilidad.getEscenarioComponente(tfNombre).close();
            limpiarCampos();
        }
    }

    @FXML
    private void btnClicCancelar(ActionEvent event) {
        Utilidad.getEscenarioComponente(tfNombre).close();
        limpiarCampos();
    }

    @FXML
    private void btnClicActualizarStock(ActionEvent event) {
        Optional<String> resultado = mostrarDialogoEntrada("Campo seguro", "Ingrese su contraseña:");
        if (validarPassword(resultado)) {
            tfStock.setEditable(true);
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Contraseña incorrecta", 
                    "La contraseña ingresada es incorrecta, no puede actualizar el stock.");
        }
        
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
        if (isEdicion && bebidaEdicion != null) {
            bebida.setIdBebida(bebidaEdicion.getIdBebida());
        }
        bebida.setBebida(tfNombre.getText());
        bebida.setStock(Integer.parseInt(tfStock.getText()));
        bebida.setStockMinimo(Integer.parseInt(tfStockMinimo.getText()));
        bebida.setPrecio(Double.parseDouble(tfPrecio.getText()));
        return bebida;
    }
    
    private void asigarTipoOperacion() {
        if (!isEdicion) {
            Bebida bebida = obtenerBebidaNueva();
            guardarBebida(bebida);
        } else {
            bebidaEdicion = obtenerBebidaNueva();
            actualizarBebida(bebidaEdicion);
        }
    }
    
    private void guardarBebida(Bebida bebida){
        try {
            BebidaDAOImpl bebidaDAOImpl = new BebidaDAOImpl();
            if (bebidaDAOImpl.crear(bebida)) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Bebida registrada", 
                        "La bebida fue registrada con exito");
                //Llamada al método de la interfaz para actualizar tabla
                observador.operacionExitosa();
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al registrar", 
                        "No se pudo registrar la bebida, intentalo más tarde.");
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error en la base de datos", ex.getMessage());
        }
    }
    
    private void actualizarBebida(Bebida bebida){
        try {
            BebidaDAOImpl bebidaDAOImpl = new BebidaDAOImpl();
            if (bebidaDAOImpl.actualizar(bebida)) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Bebida actualizada", 
                        "La bebida fue actualizada con exito");
                //Llamada al método de la interfaz para actualizar tabla
                observador.operacionExitosa();
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al actualizar", 
                        "No se pudo actualizar la bebida, intentalo más tarde.");
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error en la base de datos", ex.getMessage());
        }
    }
        
    private void cargarInformacionEdicion() {
        if (bebidaEdicion != null) {
            tfNombre.setText(bebidaEdicion.getBebida());
            tfStock.setText(String.valueOf(bebidaEdicion.getStock()));
            tfStockMinimo.setText(String.valueOf(bebidaEdicion.getStockMinimo()));
            tfPrecio.setText(String.valueOf(bebidaEdicion.getPrecio()));
        }
    }
    
    private boolean validarPassword(Optional<String> password) {
        return password
        .map(valor -> Utilidad.verificarPassword(valor, usuarioSesion.getPassword()))
        .orElse(false);
    }
    
    private void actualizarDisponibilidadComponentes() {
        tfStock.setEditable(false);
        btnActualizarStock.setDisable(false);
        btnActualizarStock.setVisible(true);
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
