/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxexpendio.utilidades;

import java.util.Optional;
import java.util.function.Function;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

/**
 *
 * @author Dell
 */
public class Utilidad {
    
    public static void mostrarAlertaSimple(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
    
    public static boolean mostrarAlertaConfirmacion(String titulo, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);

        ButtonType botonAceptar = new ButtonType("Aceptar");
        ButtonType botonCancelar = new ButtonType("Cancelar");
        alerta.getButtonTypes().setAll(botonAceptar, botonCancelar);

        Optional<ButtonType> resultado = alerta.showAndWait();
        return resultado.isPresent() && resultado.get() == botonAceptar;
    }
    
    public static Optional<String> mostrarDialogoEntrada(String titulo, String contenido) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(titulo);
        dialog.setHeaderText(null);
        dialog.setContentText(contenido);

        return dialog.showAndWait();
    }
    
    public static Stage getEscenarioComponente(Control componente) {
        return (Stage)componente.getScene().getWindow();
    }
    
    public static <T> void activarFiltroBusqueda(TextField textField, TableView<T> tabla, 
            ObservableList<T> listaOriginal, Function<T, String> extractor) {

        FilteredList<T> filtrada = new FilteredList<>(listaOriginal, p -> true);
        tabla.setItems(filtrada);

        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtro = (newVal == null) ? "" : newVal.toLowerCase().trim();
            filtrada.setPredicate(item -> extractor.apply(item).toLowerCase().contains(filtro));
        });
    }
    
}
