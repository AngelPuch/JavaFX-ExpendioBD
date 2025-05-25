/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxexpendio.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafxexpendio.interfaz.Notificacion;
import javafxexpendio.modelo.dao.ProveedorDAOImpl;
import javafxexpendio.modelo.pojo.Proveedor;
import javafxexpendio.utilidades.Utilidad;

/**
 * FXML Controller class
 *
 * @author Dell
 */
public class FXMLFormularioProveedorController implements Initializable {

    @FXML
    private Label lbRazonSocialError;
    @FXML
    private Label lbTelefonoError;
    @FXML
    private Label lbCorreoError;
    @FXML
    private TextField tfRazonSocial;
    @FXML
    private TextField tfTelefono;
    @FXML
    private TextField tfCorreo;
    // Variables para el patrón Observer
    private Notificacion observador;
    private Proveedor proveedorEdicion;
    private boolean isEdicion;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void inicializarInformacion(boolean isEdicion, Proveedor proveedorEdicion, Notificacion observador) {
        this.proveedorEdicion = proveedorEdicion;
        this.isEdicion = isEdicion;
        this.observador = observador;
        if (isEdicion) {
            cargarInformacionEdicion();
        }
    }

    @FXML
    private void btnClicGuardar(ActionEvent event) {
        if (validarCampos()) {
            asignarTipoOperacion();
            Utilidad.getEscenarioComponente(tfRazonSocial).close();
            limpiarCampos();
        }
    }

    @FXML
    private void btnClicCancelar(ActionEvent event) {
        Utilidad.getEscenarioComponente(tfRazonSocial).close();
        limpiarCampos();
    }
    
    private boolean validarCampos() {
        boolean esValido = true;

        // Validar razon_social
        if (tfRazonSocial.getText().trim().isEmpty()) {
            lbRazonSocialError.setText("*Campo obligatorio");
            esValido = false;
        } else {
            lbRazonSocialError.setText("");
        }

        // Teléfono es opcional, pero si se escribe, debe tener 10 dígitos numéricos
        String telefono = tfTelefono.getText().trim();
        if (!telefono.isEmpty()) {
            if (!telefono.matches("\\d{10}")) {
                lbTelefonoError.setText("*Teléfono inválido");
                esValido = false;
            } else {
                lbTelefonoError.setText("");
            }
        } else {
            lbTelefonoError.setText("");
        }

        // Correo es opcional, pero si se escribe, validar formato básico
        String correo = tfCorreo.getText().trim();
        if (!correo.isEmpty()) {
            if (!correo.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
                lbCorreoError.setText("*Correo inválido");
                esValido = false;
            } else {
                lbCorreoError.setText("");
            }
        } else {
            lbCorreoError.setText("");
        }

        return esValido;
    }
     
    private Proveedor obtenerProveedorNuevo() {
        Proveedor proveedor = new Proveedor();
        if (isEdicion && proveedorEdicion != null) {
            proveedor.setIdProveedor(proveedorEdicion.getIdProveedor());
        }
        proveedor.setRazonSocial(tfRazonSocial.getText().trim());
        proveedor.setTelefono(tfTelefono.getText().trim().isEmpty() ? null : tfTelefono.getText().trim());
        proveedor.setCorreo(tfCorreo.getText().trim().isEmpty() ? null : tfCorreo.getText().trim());
        return proveedor;
    }
    
    private void asignarTipoOperacion() {
        if (!isEdicion) {
            Proveedor proveedor = obtenerProveedorNuevo();
            guardarProveedor(proveedor);
        } else {
            proveedorEdicion = obtenerProveedorNuevo();
            actualizarProveedor(proveedorEdicion);
        }
        
    }
    
    private void guardarProveedor(Proveedor proveedor) {
        try {
            ProveedorDAOImpl proveedorDAOImpl = new ProveedorDAOImpl();
            if (proveedorDAOImpl.crear(proveedor)) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Proveedor registrado", 
                        "El proveedor fue registrado con éxito.");
                observador.operacionExitosa();
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al registrar", 
                        "No se pudo registrar el proveedor, intentalo más tarde.");
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error en la base de datos", ex.getMessage());
        }
    }
    
    private void actualizarProveedor(Proveedor proveedor) {
        try {
            ProveedorDAOImpl proveedorDAOImpl = new ProveedorDAOImpl();
            if (proveedorDAOImpl.actualizar(proveedor)) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Proveedor actualizado", 
                        "El proveedor fue actualizado con éxito.");
                observador.operacionExitosa();
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al actualizar", 
                        "No se pudo actualizar el proveedor, intenta más tarde.");
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error en la base de datos", ex.getMessage());
        }
    }
    
    private void cargarInformacionEdicion() {
        if (proveedorEdicion != null) {
            tfRazonSocial.setText(proveedorEdicion.getRazonSocial());
            tfTelefono.setText(proveedorEdicion.getTelefono() != null ? proveedorEdicion.getTelefono() : "");
            tfCorreo.setText(proveedorEdicion.getCorreo() != null ? proveedorEdicion.getCorreo() : "");
        }
    }
    
    private void limpiarCampos() {
        tfRazonSocial.clear();
        tfTelefono.clear();
        tfCorreo.clear();

        lbRazonSocialError.setText("");
        lbTelefonoError.setText("");
        lbCorreoError.setText("");
    }
    
}
