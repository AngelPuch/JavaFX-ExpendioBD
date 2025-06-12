package javafxexpendio.controlador;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Observable;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxexpendio.JavaFXAppExpendio;
import javafxexpendio.interfaz.Notificacion;
import javafxexpendio.modelo.dao.PromocionDAOImpl;
import javafxexpendio.modelo.pojo.Promocion;
import javafxexpendio.utilidades.Utilidad;

public class FXMLAdminPromocionController implements Initializable, Notificacion {

    @FXML
    private TextField tfBuscarPromocion;
    @FXML
    private TableView<Promocion> tblPromocion;
    @FXML
    private TableColumn colDescripcion;
    @FXML
    private TableColumn colBebida;
    @FXML
    private TableColumn colDescuento;
    @FXML
    private TableColumn colFechaInicio;
    @FXML
    private TableColumn colFechaFin;
    @FXML
    private TableColumn<Promocion, String> colEstado;
    private ObservableList<Promocion> promociones;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionTabla();
    }    

    @FXML
    private void btnClicAgregar(ActionEvent event) {
        irFormularioPromocion(false, null);
    }

    @FXML
    private void btnClicEditar(ActionEvent event) {
        Promocion promocionSeleccionada = getPromocionSeleccionada();
        if (promocionSeleccionada != null) {
            irFormularioPromocion(true, promocionSeleccionada);
        }
    }

    @Override
    public void operacionExitosa() {
        cargarInformacionTabla();
    }
    
    private void configurarTabla() {
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colBebida.setCellValueFactory(new PropertyValueFactory<>("bebida"));
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));

        colEstado.setCellValueFactory(cellData -> {
            Promocion promocion = cellData.getValue();
            String estado = "Vigente";
            if (promocion.getFechaFin() != null) {
                LocalDate fechaFin = new java.util.Date(promocion.getFechaFin().getTime())
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (fechaFin.isBefore(LocalDate.now())) {
                    estado = "Finalizado";
                }
            }
                return new SimpleStringProperty(estado);
            });
    }
    
    private void cargarInformacionTabla() {
        try {
            PromocionDAOImpl promocionDAOImpl = new PromocionDAOImpl();
            promociones = FXCollections.observableArrayList();
            promociones.addAll(promocionDAOImpl.leerTodo());
            tblPromocion.setItems(promociones);
            configurarFiltroBusqueda();
        } catch (Exception e) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar", 
                    "Lo sentimos, por el momento no se puede mostrar la información de las promociones. Por favor intentelo más tarde");
            cerrarVentana();
        }
    }
    
    private void irFormularioPromocion(boolean isEdicion, Promocion promocionEdicion) {
        try {
            Stage escenarioFormPromocion = new Stage();
            FXMLLoader loader = new FXMLLoader(JavaFXAppExpendio.class.getResource("vista/FXMLFormularioPromocion.fxml"));
            Parent vista = loader.load();
            FXMLFormularioPromocionController controlador = loader.getController();
            controlador.inicializarInformacion(isEdicion, promocionEdicion, this);
            Scene escena = new Scene(vista);
            escenarioFormPromocion.setScene(escena);
            escenarioFormPromocion.setTitle("Formulario promoción");
            escenarioFormPromocion.initModality(Modality.APPLICATION_MODAL);
            escenarioFormPromocion.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private Promocion getPromocionSeleccionada() {
        Promocion promocionSeleccionada = tblPromocion.getSelectionModel().getSelectedItem();
        if (promocionSeleccionada != null) {
            return promocionSeleccionada;
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Sin selección",
                "Por favor, selecciona un proveedor para eliminar.");
            return null;
        }
    }
    
        
    private void configurarFiltroBusqueda() {
        Utilidad.activarFiltroBusqueda(tfBuscarPromocion, tblPromocion, promociones, promocion ->
            promocion.getBebida().getBebida()+ " " + promocion.getDescuento());
    }
    
    private void cerrarVentana() {
        ((Stage)tblPromocion.getScene().getWindow()).close();
    }

    
}
