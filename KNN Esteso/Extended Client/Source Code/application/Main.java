package application;
	
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import controllers.graphicController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.text.Font;

/**
 * Classe Main che lancia l'iterfaccia principale dell'appilicazione grafica
 * 
 * @author anton
 */
@SuppressWarnings("unused")
public class Main extends Application 
{
	@Override
	public void start(Stage primaryStage) throws UnknownHostException, IOException 
	{
		try
		{
			InetAddress addr;
			
			try 
			{
				addr = InetAddress.getByName("127.0.0.1");
			} 
			catch (UnknownHostException e) 
			{
				System.out.println(e.toString());
				return;
			}
			
			//Caricamento interfaccia grafica principale
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/graphic.fxml"));
			Parent root = loader.load();
			graphicController graphicController = loader.getController();
			graphicController.connection();
			
			//Carico i fonts
			Font.loadFont(getClass().getResourceAsStream("/fonts/Montserrat-Medium.ttf"), 40);
			Font.loadFont(getClass().getResourceAsStream("/fonts/Montserrat-Regular.ttf"), 40);
			Font.loadFont(getClass().getResourceAsStream("/fonts/Montserrat-Bold.ttf"), 40);
			Font.loadFont(getClass().getResourceAsStream("/fonts/Montserrat-Black.ttf"), 40);
			Font.loadFont(getClass().getResourceAsStream("/fonts/Montserrat-SemiBold.ttf"), 40);
		
			
			//Carico la finestra principale
			root = FXMLLoader.load(getClass().getResource("/FXMLs/graphic.fxml"));
			Scene scene = new Scene(root);

			//Carico l'icona dell'applicazione ed il titolo
			Image image = new Image("images/knnIcon.png");
			primaryStage.getIcons().add(image);
			primaryStage.setTitle("KNN");
			
			//Mostro a schermo la finestra
			primaryStage.setScene(scene);
			primaryStage.centerOnScreen();
			
			//Gestore del quando la finestra viene chiusa, oppure il processo arrestato
			primaryStage.setOnCloseRequest(evt -> 
			{
				try 
				{
					graphicController.disconnect();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			});
			
			//Mostro a schermo tutto lo stage creato
			primaryStage.show();
		} 
		catch(ConnectException err) 
		{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Errore!");
			alert.setHeaderText("Impossibile stabilire una connessione con il server KNN");
			alert.setContentText("Assicurati che il server sia avviato!");
			alert.showAndWait();
		}	
	}
	
	/**
	 * main principale che lancia la classe Main che estende application
	 * 
	 * @param args Argomenti del main
	 */
	public static void main(String[] args) 
	{
		launch(args);
	}
}
