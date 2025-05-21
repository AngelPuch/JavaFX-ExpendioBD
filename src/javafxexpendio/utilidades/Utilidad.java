/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxexpendio.utilidades;

import javafx.scene.control.Alert;
import javafx.scene.control.Control;
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
    
    public static Stage getEscenarioComponente(Control componente) {
        return (Stage)componente.getScene().getWindow();
    }
    
}
