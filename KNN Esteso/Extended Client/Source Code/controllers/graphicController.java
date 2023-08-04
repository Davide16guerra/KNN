package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.DATABASE_TYPE;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import javafx.application.Platform;
import javafx.event.ActionEvent;

/**
 * Classe controller che si occupa di gestire tutto quello che accade nelle scene
 * graphic.fxml, textFilegraphic.fxml, binaryFilegraphic.fxml, mysqlFilegraphic.fxml e
 * informationGraphic.fxml
 * 
 * @author anton
 */
public class graphicController 
{
	//Bottoni
	
	/**
	 * Tasto "File di Testo" che compare in tutti i menù
	 */
	@FXML
	private Button textFileButton;
	
	/**
	 * Tasto "File Binario" che compare in tutti i menù
	 */
	@FXML
	private Button binaryFileButton;
	
	/**
	 * Tasto "Database MySQL" che compare in tutti i menù
	 */
	@FXML
	private Button databaseButton;
	
	/**
	 * Tasto "Home" che compare in tutti i menù
	 */
	@FXML
	private Button homeButton;
	
	/**
	 * Tasto "Informazioni" che compare in tutti i menù
	 */
	@FXML
	private Button infoButton;
	
	/**
	 * Tasto "Inoltra richiesta come File di Testo"
	 */
	@FXML
	private Button sumbitTextFileButton;

	/**
	 * Tasto "Inoltra richiesta come File Binario"
	 */
	@FXML
	private Button sumbitBinaryFileButton;

	/**
	 * Tasto "Inoltra richiesta con il database MySQL"
	 */
	@FXML
	private Button sumbitSQLButton;
	
	//Scena

	/**
	 * Palcoscenico
	 */
	@FXML
	private Stage stage;
	
	/**
	 * Scena attuale
	 */
	@FXML
	private Scene scene;
	
	/**
	 * Radice attuale delle risorse caricate da file FXML
	 */
	@FXML
	private Parent root;
	
	//Campi di testo
	
	/**
	 * Campo di testo della sezione File di Testo
	 */
	@FXML
	private TextField textFileField;
	
	/**
	 * Campo di testo della sezione File Binario
	 */
	@FXML
	private TextField binaryFileField;
	
	/**
	 * Campo di testo della sezione database MySQL
	 */
	@FXML
	private TextField SQLFileField;

	//Attributi per la comunicazione con il server esteso
	
	
	/**
	 * Socket per la comunicazione con il server, pre inizializzata a null
	 */
	static private Socket socket = null;
	
	/**
	 * Output Stream per inviare dati al server, pre inizializzata a null
	 */
	static private ObjectOutputStream out = null;
	
	/**
	 * Input Stream per ricevere dati dal server, pre inizializzata a null
	 */
	static private ObjectInputStream in = null;


	/**
	 * Metodo che viene attivato quando il textFileButton viene premuto
	 * 
	 * @param event Tipo di evento
	 * @throws IOException Lanciata quando la comunicazione con il server viene meno
	 */
	@FXML
	private void clickedTextFileButton(ActionEvent event) throws IOException 
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/textFileGraphic.fxml"));
		root = loader.load();
		
		graphicController graphicController = loader.getController();
		graphicController.focusField(DATABASE_TYPE.TXT);
		
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = stage.getScene();  //Ottengo la scena corrente per ottenerne le dimensioni della finestra
		scene = new Scene(root, scene.getWidth(), scene.getHeight());
		stage.setScene(scene);
		stage.show();
	}
	
	/**
	 * Metodo che viene attivato quando il binaryFileButton viene premuto
	 * 
	 * @param event Tipo di evento
	 * @throws IOException Lanciata quando la comunicazione con il server viene meno
	 */
	@FXML
	private void clickedBinaryFileButton(ActionEvent event) throws IOException 
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/binaryFileGraphic.fxml"));
		root = loader.load();
		
		graphicController graphicController = loader.getController();
		graphicController.focusField(DATABASE_TYPE.BIN);
		
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = stage.getScene();  //Ottengo la scena corrente per ottenerne le dimensioni della finestra
		scene = new Scene(root, scene.getWidth(), scene.getHeight());
		stage.setScene(scene);
		stage.show();
	}
	
	/**
	 * Metodo che viene attivato quando il databaseButton viene premuto
	 * 
	 * @param event Tipo di evento
	 * @throws IOException Lanciata quando la comunicazione con il server viene meno
	 */
	@FXML
	private void clickedDatabaseButton(ActionEvent event) throws IOException
	{	
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/mysqlFileGraphic.fxml"));
		root = loader.load();
		
		graphicController graphicController = loader.getController();
		graphicController.focusField(DATABASE_TYPE.SQL);
		
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = stage.getScene();  //Ottengo la scena corrente per ottenerne le dimensioni della finestra
		scene = new Scene(root, scene.getWidth(), scene.getHeight());
		stage.setScene(scene);
		stage.show();
	}
	
	/**
	 * Metodo che viene attivato quando l'infoButton viene premuto
	 * 
	 * @param event Tipo di evento
	 * @throws IOException Lanciata quando la comunicazione con il server viene meno
	 */
	@FXML
	private void clickedInfoButton(ActionEvent event) throws IOException 
	{
		//Metodo legacy per chiamare una nuova scena
		Parent root = FXMLLoader.load(getClass().getResource("/FXMLs/informationGraphic.fxml"));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = stage.getScene();  //Ottengo la scena corrente per ottenerne le dimensioni della finestra
		scene = new Scene(root, scene.getWidth(), scene.getHeight());
		stage.setScene(scene);
		stage.show();
	}
	
	/**
	 * Metodo che viene attivato quando l'homeButton viene premuto
	 * 
	 * @param event Tipo di evento
	 * @throws IOException Lanciata quando la comunicazione con il server viene meno
	 */
	@FXML
	private void clickedHomeButton(ActionEvent event) throws IOException 
	{
		//Metodo legacy per chiamare una nuova scena
		Parent root = FXMLLoader.load(getClass().getResource("/FXMLs/graphic.fxml"));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = stage.getScene();  //Ottengo la scena corrente per ottenerne le dimensioni della finestra
		scene = new Scene(root, scene.getWidth(), scene.getHeight());
		stage.setScene(scene);
		stage.show();
	}
	
	/**
	 * Metodo che viene attivato quando il textFileButton viene premuto
	 * 
	 * @param event Tipo di evento
	 * @throws IOException Lanciata quando la comunicazione con il server viene meno
	 * @throws ClassNotFoundException Lanciata quando avviene un problema con la serializzazione
	 */
	@FXML
	private void clickedSumbitTextFileButton(ActionEvent event) throws IOException, ClassNotFoundException
	{
		try
		{
			Boolean error = false;
			out.writeObject(1);
			String fileName = textFileField.getText();
			out.writeObject(fileName);
			error = (Boolean) in.readObject();
	
			if(error == false)
			{
				//Avvio nuova finestra
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/KNNGraphic.fxml"));
				root = loader.load();
				KNNGraphicController knnGraphicController = loader.getController();
				knnGraphicController.iniziateView(DATABASE_TYPE.TXT, in, out);
				
				stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				scene = stage.getScene();  //Ottengo la scena corrente per ottenerne le dimensioni della finestra
				scene = new Scene(root, scene.getWidth(), scene.getHeight());
	
				stage.setScene(scene);
				stage.show();
			}
			else
			{
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("Attenzione!");
				alert.setHeaderText("Il nome immesso non è valido, oppure non è stato scritto correttamente");
				alert.setContentText("Ricorda che non è necessario inserire anche l'estensione!");
				alert.showAndWait();
				textFileField.clear();
			}
		}
		catch(SocketException err)
		{
			ServerClosedError(event);
		}
	}
	
	/**
	 * Metodo che viene attivato quando il binaryFileButton viene premuto
	 * 
	 * @param event Tipo di evento
	 * @throws IOException Lanciata quando la comunicazione con il server viene meno
	 * @throws ClassNotFoundException Lanciata quando avviene un problema con la serializzazione
	 */
	@FXML
	private void clickedSumbitBinaryFileButton(ActionEvent event) throws IOException, ClassNotFoundException
	{	
		try
		{
			Boolean error = false;
			out.writeObject(2);
			String fileName = binaryFileField.getText();
			out.writeObject(fileName);
			error = (Boolean) in.readObject();
			
			if(error == false)
			{
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/KNNGraphic.fxml"));
				root = loader.load();
				KNNGraphicController knnGraphicController = loader.getController();
				knnGraphicController.iniziateView(DATABASE_TYPE.BIN, in, out);
				
				stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				scene = stage.getScene();  //Ottengo la scena corrente per ottenerne le dimensioni della finestra
				scene = new Scene(root, scene.getWidth(), scene.getHeight());
	
				stage.setScene(scene);
				stage.show();
			}
			else
			{
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("Attenzione!");
				alert.setHeaderText("Il nome immesso non è valido, oppure non è stato scritto correttamente");
				alert.setContentText("Ricorda che non è necessario inserire anche l'estensione!");
				alert.showAndWait();
				binaryFileField.clear();
			}
		}
		catch(SocketException err)
		{
			ServerClosedError(event);
		}
	}

	/**
	 * Metodo che viene attivato quando il textFileButton viene premuto
	 * 
	 * @param event Tipo di evento
	 * @throws IOException Lanciata quando la comunicazione con il server viene meno
	 * @throws ClassNotFoundException Lanciata quando avviene un problema con la serializzazione
	 */
	@FXML
	private void clickedSumbitSQLButton(ActionEvent event) throws IOException, ClassNotFoundException 
	{
		try
		{
			out.writeObject(3);
			String fileName = SQLFileField.getText();
			out.writeObject(fileName);
			String errorType = (String) in.readObject();
	
	
			if(errorType.equals("@OK"))
			{
				//Avviso l'utente dell'avvenuta connessione con MySQL e dell'acquisione corretta del database
				Alert alertC = new Alert(Alert.AlertType.INFORMATION);
				alertC.setTitle("Informazioni sulla connessione");
				alertC.setHeaderText("Connessione al database MySQL stabilita");
				alertC.setContentText("L'acquisizione dei dati dal databse è riuscita correttamente");
				alertC.showAndWait();
				
				//Avvio della nuova finestra
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/KNNGraphic.fxml"));
				root = loader.load();
				KNNGraphicController knnGraphicController = loader.getController();
				knnGraphicController.iniziateView(DATABASE_TYPE.SQL, in, out);
				
				stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				scene = stage.getScene();  //Ottengo la scena corrente per ottenerne le dimensioni della finestra
				scene = new Scene(root, scene.getWidth(), scene.getHeight());
	
				stage.setScene(scene);
				stage.show();
			}
			else if(errorType.equals("@WRONGNAME"))
			{
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("Attenzione!");
				alert.setHeaderText("Il nome immesso non è valido, oppure non è stato scritto correttamente");
				alert.setContentText("Ricorda che, trattandosi di MySQL non è necessario inserire l'estensione!");
				alert.showAndWait();
				SQLFileField.clear();
			}
			else if(errorType.equals("@CONNECTIONERROR"))
			{
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Errore!");
				alert.setHeaderText("Impossibile stabilire una connessione con il database MySQL");
				alert.setContentText("Assicurati che il server MySQL sia avviato, prima di proseguire");
				alert.showAndWait();
				SQLFileField.clear();
			}
		}
		catch(SocketException err)
		{
			ServerClosedError(event);
		}
	}	
	
	//Metodi per quando si preme invio sulla tastiera
	
	/**
	 * Metodo che attiva il textFileButton quando si preme invio
	 * 
	 * @param event Tipo di evento
	 * @throws ClassNotFoundException Lanciata quando avviene un problema di serializzazione 
	 * @throws IOException Lanciata quando c'è un problema di comunicazione con il server
	 */
    @FXML
    private void enterTextFileField(ActionEvent event) throws  IOException, ClassNotFoundException 
    {
    	clickedSumbitTextFileButton(event);
    }
    
	/**
	 * Metodo che attiva il binaryFileButton quando si preme invio
	 * 
	 * @param event Tipo di evento
	 * @throws ClassNotFoundException Lanciata quando avviene un problema di serializzazione 
	 * @throws IOException Lanciata quando c'è un problema di comunicazione con il server
	 */
    @FXML
    private void enterBinaryFileField(ActionEvent event) throws ClassNotFoundException, IOException
    {
    	clickedSumbitBinaryFileButton(event);
    }
    
	/**
	 * Metodo che attiva l'SQLFileButton quando si preme invio
	 * 
	 * @param event Tipo di evento
	 * @throws ClassNotFoundException Lanciata quando avviene un problema di serializzazione 
	 * @throws IOException Lanciata quando c'è un problema di comunicazione con il server
	 */
    @FXML
    private void enterSQLField(ActionEvent event) throws IOException, ClassNotFoundException
    {
    	clickedSumbitSQLButton(event);
    }
    
	/**
	 * Metodo per connettere il Client al Server
	 * 
	 * @throws UnknownHostException Lanciata quando non è possibile connettersi al server
	 * @throws IOException Lanciata quando c'è un problema con la comunicazione con il server
	 */
	public void connection() throws UnknownHostException, IOException 
	{
		socket = new Socket("127.0.0.1", 2026);
		System.out.println(socket);		
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}
	
	/**
	 * Metodo per chiudere la connessione con il Server
	 * 
	 * @throws UnknownHostException Lanciata quando non è possibile connettersi al server
	 * @throws IOException Lanciata quando c'è un problema con la comunicazione con il server
	 */
	public void disconnect() throws UnknownHostException, IOException 
	{
		socket.close();
		in = null;
		out = null;
	}
	
	/**
	 * Metodo per auto selezionare i vari campi di testo, così da non dover usare il
	 * mouse ogni volta per selezionarlo
	 * 
	 * @param aggregation Tipo di campo di testo da pre selezionare
	 */
	private void focusField(DATABASE_TYPE aggregation)
	{
		if(aggregation.equals(DATABASE_TYPE.TXT))
		{
			Platform.runLater(new Runnable() 
			{
	            @Override
	            public void run() 
	            {
	                textFileField.requestFocus();
	            }
	        });
		}
		else if(aggregation.equals(DATABASE_TYPE.BIN))
		{
			Platform.runLater(new Runnable() 
			{
	            @Override
	            public void run() 
	            {
	                binaryFileField.requestFocus();
	            }
	        });
		}
		else if(aggregation.equals(DATABASE_TYPE.SQL))
		{
			Platform.runLater(new Runnable() 
			{
	            @Override
	            public void run() 
	            {
	                SQLFileField.requestFocus();
	            }
	        });
		}
	}
	
	/**
	 * Metodoc che viene chiamato quando il server si chiude all'improvviso mentre si usa l'applicazione
	 * 
	 * @param event Tipo di Evento
	 * @throws IOException Lanciata quando c'è un problema di comunicazione con il server
	 */
	private void ServerClosedError(ActionEvent event) throws IOException 
	{
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Errore!");
		alert.setHeaderText("Impossibile stabilire una connessione con il server KNN");
		alert.setContentText("Il server non è più Online");
		alert.showAndWait();
		
		//Procedo alla chiusura del client
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLs/graphic.fxml"));
		root = loader.load();
		
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = stage.getScene();  //Ottengo la scena corrente per ottenerne le dimensioni della finestra
		scene = new Scene(root, scene.getWidth(), scene.getHeight());

		stage.setScene(scene);
		stage.close();
	}
}


