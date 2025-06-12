package javafxexpendio;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFXAppExpendio extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Parent vista  = FXMLLoader.load(getClass().getResource("vista/FXMLInicioSesion.fxml"));
            Scene escenaInicioSesion = new Scene(vista);
            
            primaryStage.setScene(escenaInicioSesion);
            primaryStage.setTitle("Inicio de sesión");
            primaryStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
