package javafxexpendio.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
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
    @FXML
    private TextField tfRfc;
    @FXML
    private Label lbRfcError;

    private Notificacion observador;
    private Cliente clienteEdicion;
    private boolean isEdicion;

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

    private void limpiarLabelsError() {
        lbNombreError.setText("");
        lbTelefonoError.setText("");
        lbCorreoError.setText("");
        lbRfcError.setText("");
        lbDireccionError.setText("");
    }

    private boolean validarCampos() {
        limpiarLabelsError();
        boolean esValido = true;

        if (tfNombre.getText().trim().isEmpty()) {
            lbNombreError.setText("*Campo obligatorio");
            esValido = false;
        }

        String telefono = tfTelefono.getText().trim();
        if (!telefono.isEmpty() && !telefono.matches("\\d{10}")) {
            lbTelefonoError.setText("*Teléfono inválido, deben ser 10 dígitos");
            esValido = false;
        }

        String correo = tfCorreo.getText().trim();
        if (!correo.isEmpty() && !correo.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            lbCorreoError.setText("*Correo inválido");
            esValido = false;
        }

        if (tfRfc.getText().trim().isEmpty()) {
            lbRfcError.setText("*Campo obligatorio");
            esValido = false;
        }
        
        if (tfDireccion.getText().trim().isEmpty()) {
            lbDireccionError.setText("*Campo obligatorio");
            esValido = false;
        }

        return esValido;
    }

    @FXML
    private void btnClicCancelar(ActionEvent event) {
        Utilidad.getEscenarioComponente(tfNombre).close();
    }

    @FXML
    private void btnClicGuardar(ActionEvent event) {
        if (validarCampos()) {
            asignarTipoOperacion();
            Utilidad.getEscenarioComponente(tfNombre).close();
        }
    }

    private Cliente obtenerClienteNuevo() {
        Cliente cliente = new Cliente();
        if (isEdicion && clienteEdicion != null) {
            cliente.setIdCliente(clienteEdicion.getIdCliente());
        }
        cliente.setNombre(tfNombre.getText());
        cliente.setTelefono(tfTelefono.getText());
        cliente.setCorreo(tfCorreo.getText());
        cliente.setRfc(tfRfc.getText());
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
                observador.operacionExitosa();
                limpiarCampos();
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al registrar",
                        "No se pudo registrar el cliente, inténtelo más tarde.");
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
                observador.operacionExitosa();
                limpiarCampos();
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al actualizar",
                        "No se pudo actualizar el cliente, inténtelo más tarde.");
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
            tfRfc.setText(clienteEdicion.getRfc());
            tfDireccion.setText(clienteEdicion.getDireccion());
        }
    }

    private void limpiarCampos() {
        tfNombre.clear();
        tfTelefono.clear();
        tfCorreo.clear();
        tfRfc.clear();
        tfDireccion.clear();
        limpiarLabelsError();
    }
}