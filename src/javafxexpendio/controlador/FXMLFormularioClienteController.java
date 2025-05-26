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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafxexpendio.interfaz.Notificacion;
import javafxexpendio.modelo.dao.ClienteDAOImpl;
import javafxexpendio.modelo.pojo.Cliente;
import javafxexpendio.utilidades.Utilidad;

/**
 * FXML Controller class
 *
 * @author grill
 */
public class FXMLFormularioClienteController implements Initializable {

    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfTelefono;
    @FXML
    private TextField tfCorreo;
    @FXML
    private TextArea tfDireccion;
    @FXML
    private Label lbNombreError;
    @FXML
    private Label lbTelefonoError;
    @FXML
    private Label lbCorreoError;
    @FXML
    private Label lbDireccionError;
    //Variables necesarias para implementar el patron observable
    Notificacion observador;
    Cliente clienteEdicion;
    boolean isEdicion;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    public void inicializarInformacion(boolean isEdicion, Cliente clienteEdicion, Notificacion observador) {
        this.clienteEdicion = clienteEdicion;
        this.isEdicion = isEdicion;
        this.observador = observador;
        if (isEdicion) {
            cargarInformacionEdicion();
        }
    }
    
    private boolean validarCampos() {
        boolean esValido = true;
        boolean numeros = true;

        // Validar nombre
        if (tfNombre.getText().trim().isEmpty()) {
            lbNombreError.setText("*Campo obligatorio");
            esValido = false;
        } else {
            lbNombreError.setText("");
        }
        
        //Validar telefono
        try {
            double telefonoValido = Double.parseDouble(tfTelefono.getText());
            numeros = true;
        } catch (NumberFormatException ex) {
            numeros = false;
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al registrar", 
                        "No se pudo registrar el cliente, intentalo más tarde.");
        }
        if (tfTelefono.getText().trim().isEmpty()) {
            lbTelefonoError.setText("*Campo obligatorio");
            esValido = false;
        } else if (tfTelefono.getText().length() != 10 && numeros) {
            lbTelefonoError.setText("*Número de telefono inválido, debe contener 10 dígitos");
            esValido = false;
        } else if (!numeros){
            lbTelefonoError.setText("*Número de telefono inválido, solo deben ser dígitos");
            esValido = false;
        } else {
            lbNombreError.setText("");
        }
        
        //Validar correo
        if (tfCorreo.getText().trim().isEmpty()) {
            lbCorreoError.setText("*Campo obligatorio");
            esValido = false;
        } else {
            lbCorreoError.setText("");
        }
        
        //Validar direccion
        if (tfDireccion.getText().trim().isEmpty()) {
            lbDireccionError.setText("*Campo obligatorio");
            esValido = false;
        } else {
            lbDireccionError.setText("");
        }

        return esValido;
    }

    @FXML
    private void btnClicCancelar(ActionEvent event) {
        Utilidad.getEscenarioComponente(tfNombre).close();
        limpiarCampos();
    }

    @FXML
    private void btnClicGuardar(ActionEvent event) {
        if (validarCampos()) {
            asignarTipoOperacion();
            Utilidad.getEscenarioComponente(tfNombre).close();
            limpiarCampos();
        }
    }
    
    private Cliente obtenerClienteNuevo(){
        Cliente cliente = new Cliente();
        if (isEdicion && clienteEdicion != null) {
            cliente.setIdCliente(clienteEdicion.getIdCliente());
        }
        cliente.setNombre(tfNombre.getText());
        cliente.setTelefono(tfTelefono.getText());
        cliente.setCorreo(tfCorreo.getText());
        cliente.setDireccion(tfDireccion.getText());
        return cliente;
    }
    
    private void asignarTipoOperacion() {
        if (!isEdicion) {
            Cliente cliente = obtenerClienteNuevo();
            guardarCliente(cliente);
        } else {
            clienteEdicion = obtenerClienteNuevo();
            actualizarCliente(clienteEdicion);
        }
    }
    
    private void guardarCliente(Cliente cliente) {
        try {
            ClienteDAOImpl clienteDAOImpl = new ClienteDAOImpl();
            if (clienteDAOImpl.crear(cliente)) { 
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Cliente registrado",
                        "El cliente fue registrado con éxito");
                //Llamada al método de la interfaz para actualizar tabla
                observador.operacionExitosa();
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al registrar", 
                        "No se pudo registrar el cliente, intentalo más tarde.");
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error en la base de datos", ex.getMessage());
        }
    }
    
    private void actualizarCliente(Cliente clienteEdicion) {
        try {
            ClienteDAOImpl clienteDAOImpl = new ClienteDAOImpl();
            if (clienteDAOImpl.actualizar(clienteEdicion)) { 
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Cliente actualizado",
                        "El cliente fue actualizado con éxito");
                //Llamada al método de la interfaz para actualizar tabla
                observador.operacionExitosa();
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al registrar", 
                        "No se pudo actualizar el cliente, intentalo más tarde.");
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error en la base de datos", ex.getMessage());
        }
    }
    
    private void cargarInformacionEdicion() {
        if (clienteEdicion != null) {
            tfNombre.setText(clienteEdicion.getNombre());
            tfTelefono.setText(clienteEdicion.getTelefono());
            tfCorreo.setText(clienteEdicion.getCorreo());
            tfDireccion.setText(clienteEdicion.getDireccion());
        }
    }
    
    private void limpiarCampos() {
        tfNombre.clear();
        tfTelefono.clear();
        tfCorreo.clear();
        tfDireccion.clear();
        
        lbNombreError.setText("");
        lbTelefonoError.setText("");
        lbCorreoError.setText("");
        lbDireccionError.setText("");
    }
    
}
