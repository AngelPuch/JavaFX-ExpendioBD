/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxexpendio.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafxexpendio.interfaz.Notificacion;
import javafxexpendio.modelo.dao.BebidaDAOImpl;
import javafxexpendio.modelo.dao.PromocionDAOImpl;
import javafxexpendio.modelo.dao.UsuarioDAOImpl;
import javafxexpendio.modelo.pojo.Bebida;
import javafxexpendio.modelo.pojo.Promocion;
import javafxexpendio.modelo.pojo.Proveedor;
import javafxexpendio.modelo.pojo.Usuario;
import javafxexpendio.utilidades.Utilidad;

/**
 * FXML Controller class
 *
 * @author Dell
 */
public class FXMLFormularioPromocionController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private ComboBox<Bebida> cbBebida;
    @FXML
    private TextField tfDescripcion;
    @FXML
    private TextField tfDescuento;
    @FXML
    private DatePicker dpFechaInicio;
    @FXML
    private DatePicker dpFechaFin;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnGuardar;
    @FXML
    private Label lbBebidaError;
    @FXML
    private Label lbDescripcionError;
    @FXML
    private Label lbDescuentoError;
    @FXML
    private Label lbFechaInicioError;
    @FXML
    private Label lbFechaFinError;
    private ObservableList<Bebida> bebidas;
    // Variables para el patrón Observer
    private Notificacion observador;
    private Promocion promocionEdicion;
    private boolean isEdicion;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarBebidas();
    }    

    public void inicializarInformacion(boolean isEdicion, Promocion promocionEdicion, Notificacion observador) {
        this.isEdicion = isEdicion;
        this.promocionEdicion = promocionEdicion;
        this.observador = observador;
        if (isEdicion) {
            cargarInformacionEdicion();
        }
    }
    
    @FXML
    private void btnClicGuardar(ActionEvent event) {
        if (validarCampos()) {
            asignarTipoOperacion();
            Utilidad.getEscenarioComponente(tfDescuento).close();
            limpiarCampos();
        }
    }
    
    @FXML
    private void btnClicCancelar(ActionEvent event) {
        Utilidad.getEscenarioComponente(tfDescuento).close();
        limpiarCampos();
    }
    
    private void cargarBebidas() {
        try {
            bebidas = FXCollections.observableArrayList();
            BebidaDAOImpl bebidaDAOImpl = new BebidaDAOImpl();
            bebidas.setAll(bebidaDAOImpl.leerTodo());
            cbBebida.setItems(bebidas);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void cargarInformacionEdicion() {
        if (promocionEdicion != null) {
            tfDescripcion.setText(promocionEdicion.getDescripcion());
            tfDescuento.setText(String.valueOf(promocionEdicion.getDescuento()));
            
            if (promocionEdicion.getFechaInicio() != null) {
                LocalDate fechaInicio = LocalDate.parse(promocionEdicion.getFechaInicio().toString());
                dpFechaInicio.setValue(fechaInicio);
            }

            // Convertir la fecha de fin
            if (promocionEdicion.getFechaFin() != null) {
                LocalDate fechaFin = LocalDate.parse(promocionEdicion.getFechaFin().toString());
                dpFechaFin.setValue(fechaFin);
            }
            
            for (Bebida bebida : bebidas) {
                if (bebida.getIdBebida() == promocionEdicion.getBebida().getIdBebida()) {
                    cbBebida.setValue(bebida);
                    break;
                }
            }
        }
    }
    
    private boolean validarCampos() {
        boolean esValido = true;

        // Validar bebida seleccionada
        if (cbBebida.getValue() == null) {
            // Asegúrate de tener un Label llamado lbBebidaError
            lbBebidaError.setText("*Campo obligatorio");
            esValido = false;
        } else {
            lbBebidaError.setText("");
        }

        // Validar descripción
        if (tfDescripcion.getText().trim().isEmpty()) {
            lbDescripcionError.setText("*Campo obligatorio");
            esValido = false;
        } else {
            lbDescripcionError.setText("");
        }

        // Validar descuento (obligatorio y debe ser número entre 0 y 100)
        String descuentoTexto = tfDescuento.getText().trim();
        if (descuentoTexto.isEmpty()) {
            lbDescuentoError.setText("*Campo obligatorio");
            esValido = false;
        } else {
            try {
                double descuento = Double.parseDouble(descuentoTexto);
                if (descuento < 0 || descuento > 100) {
                    lbDescuentoError.setText("*Debe estar entre 0 y 100");
                    esValido = false;
                } else {
                    lbDescuentoError.setText("");
                }
            } catch (NumberFormatException e) {
                lbDescuentoError.setText("*Debe ser un número válido");
                esValido = false;
            }
        }

        // Validar fecha inicio
        if (dpFechaInicio.getValue() == null) {
            lbFechaInicioError.setText("*Campo obligatorio");
            esValido = false;
        } else {
            lbFechaInicioError.setText("");
        }

        // Validar fecha fin
        if (dpFechaFin.getValue() == null) {
            lbFechaFinError.setText("*Campo obligatorio");
            esValido = false;
        } else if (dpFechaInicio.getValue() != null && dpFechaFin.getValue().isBefore(dpFechaInicio.getValue())) {
            lbFechaFinError.setText("*La fecha fin debe ser posterior a la fecha inicio");
            esValido = false;
        } else {
            lbFechaFinError.setText("");
        }

        return esValido;
    }
    
    private void asignarTipoOperacion() {
        if (!isEdicion) {
            Promocion promocion = obtenerPromocionNueva();
            guardarPromocion(promocion);
        } else {
            promocionEdicion = obtenerPromocionNueva();
            actualizarPromocion(promocionEdicion);
        }
    }
    
    private void guardarPromocion(Promocion promocion) {
        try {
            PromocionDAOImpl promocionDAOImpl = new PromocionDAOImpl();
            if (promocionDAOImpl.crear(promocion)) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Promoción registrada", 
                        "La promoción fue registrada con éxito.");
                observador.operacionExitosa();
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al registrar", 
                        "No se pudo registrar la promoción, intentalo más tarde.");
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error en la base de datos", ex.getMessage());
        }
    }
    
    private void actualizarPromocion(Promocion promocion) {
        try {
            PromocionDAOImpl promocionDAOImpl = new PromocionDAOImpl();
            if (promocionDAOImpl.actualizar(promocion)) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Promoción actualizada", 
                        "La promoción fue actualizada con éxito.");
                observador.operacionExitosa();
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al actualizar", 
                        "No se pudo actualizar la promoción, intentalo más tarde.");
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error en la base de datos", ex.getMessage());
        }
    }
    
    private Promocion obtenerPromocionNueva() {
        Promocion promocion = new Promocion();

        if (isEdicion && promocionEdicion != null) {
            promocion.setIdPromocion(promocionEdicion.getIdPromocion());
        }

        promocion.setBebida(cbBebida.getValue());
        promocion.setDescripcion(tfDescripcion.getText().trim());

        String descuentoTexto = tfDescuento.getText().trim();
        promocion.setDescuento(descuentoTexto.isEmpty() ? 0 : Double.parseDouble(descuentoTexto));

        LocalDate fechaInicio = dpFechaInicio.getValue();
        LocalDate fechaFin = dpFechaFin.getValue();

        Date fechaInicioDate = Date.from(fechaInicio.atStartOfDay(ZoneId.systemDefault()).toInstant());
        promocion.setFechaInicio(fechaInicioDate);
        Date fechaFinDate = Date.from(fechaFin.atStartOfDay(ZoneId.systemDefault()).toInstant());
        promocion.setFechaFin(fechaFinDate);

        return promocion;
    }

    private void limpiarCampos() {
        tfDescripcion.clear();
        tfDescuento.clear();
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
        cbBebida.getSelectionModel().clearSelection();

        lbBebidaError.setText("");
        lbDescripcionError.setText("");
        lbDescuentoError.setText("");
        lbFechaInicioError.setText("");
        lbFechaFinError.setText("");
    }
    
    


    
}
