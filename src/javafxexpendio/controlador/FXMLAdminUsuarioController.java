/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxexpendio.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafxexpendio.modelo.dao.UsuarioDAOImpl;
import javafxexpendio.modelo.pojo.Usuario;
import javafxexpendio.utilidades.Utilidad;

/**
 * FXML Controller class
 *
 * @author Dell
 */
public class FXMLAdminUsuarioController implements Initializable {

    @FXML
    private TableView<Usuario> tblUsuario;
    @FXML
    private TableColumn colNombre;
    @FXML
    private TableColumn colApePaterno;
    @FXML
    private TableColumn colApeMaterno;
    @FXML
    private TableColumn colUsername;
    @FXML
    private TableColumn colPassword;
    @FXML
    private TableColumn colTipoUsuario;
    @FXML
    private TextField tfBuscarUsuario;
    private ObservableList<Usuario> usuarios;

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
    }

    @FXML
    private void btnClicEditar(ActionEvent event) {
    }

    @FXML
    private void btnClicEliminar(ActionEvent event) {
    }
    
    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        colApePaterno.setCellValueFactory(new PropertyValueFactory("apellidoPaterno"));
        colApeMaterno.setCellValueFactory(new PropertyValueFactory("apellidoMaterno"));
        colUsername.setCellValueFactory(new PropertyValueFactory("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory("password"));
        colTipoUsuario.setCellValueFactory(new PropertyValueFactory("tipoUsuario"));
    }
    
    private void cargarInformacionTabla() {
        try {
            UsuarioDAOImpl usuarioDAOImpl = new UsuarioDAOImpl();
            usuarios = FXCollections.observableArrayList();
            List<Usuario> usuariosDAO = usuarioDAOImpl.leerTodo();
            usuarios.addAll(usuariosDAO);
            tblUsuario.setItems(usuarios);
            configurarFiltroBusqueda();
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al cargar",
                    "Lo sentimos, por el momento no se puede mostrar la información de los usuarios. Por favor, inténtelo más tarde");
            cerrarVentana();
        }
    }
    
    private void configurarFiltroBusqueda() {
        Utilidad.activarFiltroBusqueda(tfBuscarUsuario, tblUsuario, usuarios, usuario ->
            usuario.getNombre()+ " " + usuario.getApellidoMaterno() + " " + usuario.getApellidoMaterno() + " " +
                    usuario.getUsername() + " " + usuario.getTipoUsuario());
    }
    
    private void cerrarVentana() {
        ((Stage)tblUsuario.getScene().getWindow()).close();
    }
    
    
}
