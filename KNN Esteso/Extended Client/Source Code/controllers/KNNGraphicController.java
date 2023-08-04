package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import service.DATABASE_TYPE;

/**
 * Classe controller che si occupa di gestire tutto quello che avviene nella scena KNNGraphic.fxml
 * 
 * @author anton
 */
public class KNNGraphicController 
{
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
	
	//Pannelli
	
	/**
	 * Pannello di ancoraggio che ospita qualsiasi cosa sulla schermata di calcolo del KNN
	 */
	@FXML
	private AnchorPane knnPane;
	
	//Pulsanti
	
	/**
	 * Pulsante "binaryFileButton" sulla scena del calcolo del KNN
	 */
	@FXML
	private Button binaryFileButton;
	
	/**
	 * Pulsante "homeButton" sulla scena del calcolo del KNN
	 */
	@FXML
	private Button homeButton;
	
	/**
	 * Pulsante "infoButton" sulla scena del calcolo del KNN
	 */
	@FXML
	private Button infoButton;
	
	/**
	 * Pulsante "textFileButton" sulla scena del calcolo del KNN
	 */
	@FXML
	private Button textFileButton;
	
	/**
	 * Pulsante "databaseButton" sulla scena del calcolo del KNN
	 */
	@FXML
	private Button databaseButton;
	
	/**
	 * Pulsante "queryPointButton" sulla scena del calcolo del KNN
	 */
	@FXML
	private Button queryPointButton;
	
	/**
	 * Pulsante "retryButton" sulla scena del calcolo del KNN
	 */
	@FXML
	private Button retryButton;
	
	/**
	 * Pulsante "continueButton" sulla scena del calcolo del KNN
	 */
	@FXML
	private Button continueButton;

	
	//Campi di testo
	
	/**
	 * Campo di testo dove immettere il valore di k
	 */
	@FXML
	private TextField kTextField;
	
	/**
	 * Campo di testo dove inserire, una volta alla volta, i valori discreti e continui
	 */
	@FXML
	private TextField queryPointField;

	//Etichette a schermo

	/**
	 * Etichetta che indica il risultato del calcolo KNN
	 */
	@FXML
	private Label knnResultLabel;
	
	/**
	 * Etichetta che mostra il query point acquisito, insieme alla K acquisita
	 */
	@FXML
	private Label queryPointLabel;
	
	/**
	 * Etichetta che mostra che tipo di valore l'utente dovrà inserire, o discreto o continuo
	 */
	@FXML
	private Label qpLabel;
	
	/**
	 * Etichetta che mostra l'inserimento del query point
	 */
	@FXML
	private Label qpLabelZero;
	
	/**
	 * Etichetta che mostra l'inserimento di K
	 */
	@FXML
	private Label kLabel;
	
	/**
	 * Etichetta che mostra informazioni sul calcolo KNN
	 */
	@FXML
	private Label queryPointInfoLabel;
	
	//Tabelle
	
    /**
     * Tabella principale che mostra a schermo il database acquisito in uno dei tre modi dal server
     */
    @FXML
    private TableView<ArrayList<String>> knnTable;
    
    //Serie di strutture dati per immagazzinare i dati del database presi in input dal server
    
    /**
     * ArrayList di ArrayList di Stringhe che contiene le variabili indipendenti del database
     */
    static private ArrayList<LinkedList<String>> data;
    
    /**
     * ArrayList di Double che contiene le variabili dipendenti del database
     */
    static private ArrayList<Double> target;
    
    /**
     * ArrayList di Stringhe che contiene informazioni sulla tipologia di colonna, se discreta o continua
     */
    static private LinkedList<String> explanatorySetTypes;
    /**
     * ArrayList di Stringhe che contiene informazioni sui nomi delle colonne
     */
    static private LinkedList<String> explanatorySetNames;
    /**
     * Intero che contiene il numero di "tuple" del database
     */
    static private int numberOfExamples;
    
    //Comunicazione con il server
    
	/**
	 * Output Stream per dare dati al server
	 */
	static private ObjectOutputStream out = null;
	
	/**
	 * Input Stream per acquisire dati dal server
	 */
	static private ObjectInputStream in = null;
	
	//Attributi per l'aggiornamento grafico del Query Point
	
	
	/**
	 * Intero che contiene il numero delle occorrenze, dunque quante colonne ci sono
	 */
	private int occurrences = 0;
	
	/**
	 * Intero che conta quante volte il tasto continueButton è stato premuto
	 */
	private int counter = 0; 
	
	//Attributi per l'acquisizione dell'Example dell'utente
	
	
	/**
	 * ArrayList di Stringhe che contengono le scelte dei valori discreti e continui del query point,
	 * usato per stampare a schermo ciò che l'utente ha digitato
	 */
	private ArrayList<String> example = new ArrayList<String>(); 
	
	/**
	 * Booleano per controllare che non sia stato acquisito ancora alcun valore discreto o continuo
	 */
	private Boolean firstExample = true;
	
	/**
	 * Booleano che indica se c'è stato un errore qualsiasi per l'acquisizione del query point
	 */
	private Boolean exampleError = false;
	
	/**
	 * Booleano che indica se l'utente esprime la volontà di ripetere il calcolo KNN
	 * con lo stesso database, ma con un diverso query point
	 */
	private Boolean retried = false;
	
	/**
	 * Stringa che indica che tipo di colonna sto analizzando, o discreta o continua
	 */
	private String eType = "";
	
	//Metodi per quando si preme invio sulla tastiera
	
	/**
	 * Metodo che attiva il queryPointButton quando si preme invio
	 * 
	 * @param event Tipo di evento
	 * @throws ClassNotFoundException Lanciata quando avviene un problema di serializzazione 
	 * @throws IOException Lanciata quando c'è un problema di comunicazione con il server
	 */
	@FXML
	private void queryPointFieldEnter(ActionEvent event) throws ClassNotFoundException, IOException
	{
		clickedQueryPoinButton(event);
	}
	
	/**
	 * Metodo che attiva il continueButton quando si preme invio
	 * 
	 * @param event Tipo di evento
	 * @throws ClassNotFoundException Lanciata quando avviene un problema di serializzazione 
	 * @throws IOException Lanciata quando c'è un problema di comunicazione con il server
	 */
	@FXML
	private void kTextFieldEnter(ActionEvent event) throws IOException, ClassNotFoundException 
	{
		clickedContinueButton(event);
	}
	
	/**
	 * Metodo che attiva il binaryFileButton quando si preme invio
	 * 
	 * @param event Tipo di evento
	 * @throws IOException Lanciata quando c'è un problema di comunicazione con il server
	 */
	@FXML
	private void clickedBinaryFileButton(ActionEvent event) throws IOException 
	{
		try
		{
			out.writeObject(false);
			
			Parent root = FXMLLoader.load(getClass().getResource("/FXMLs/binaryFileGraphic.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = stage.getScene();  //Ottengo la scena corrente per ottenerne le dimensioni della finestra
			scene = new Scene(root, scene.getWidth(), scene.getHeight());
			stage.setScene(scene);
			stage.show();
		}
		catch(SocketException err)
		{
			ServerClosedError(event);
		}
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
		try
		{
			out.writeObject(false);
			
			Parent root = FXMLLoader.load(getClass().getResource("/FXMLs/graphic.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = stage.getScene();  //Ottengo la scena corrente per ottenerne le dimensioni della finestra
			scene = new Scene(root, scene.getWidth(), scene.getHeight());
			stage.setScene(scene);
			stage.show();
		}
		catch(SocketException err)
		{
			ServerClosedError(event);
		}
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
		try
		{
			out.writeObject(false);
			
			Parent root = FXMLLoader.load(getClass().getResource("/FXMLs/informationGraphic.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = stage.getScene();  //Ottengo la scena corrente per ottenerne le dimensioni della finestra
			scene = new Scene(root, scene.getWidth(), scene.getHeight());
			stage.setScene(scene);
			stage.show();
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
	 */
	@FXML
	private void clickedTextFileButton(ActionEvent event) throws IOException 
	{
		try
		{
			out.writeObject(false);
			
			Parent root = FXMLLoader.load(getClass().getResource("/FXMLs/textFileGraphic.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = stage.getScene();  //Ottengo la scena corrente per ottenerne le dimensioni della finestra
			scene = new Scene(root, scene.getWidth(), scene.getHeight());
			stage.setScene(scene);
			stage.show();
		}
		catch(SocketException err)
		{
			ServerClosedError(event);
		}
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
		try
		{
			out.writeObject(false);
			
			Parent root = FXMLLoader.load(getClass().getResource("/FXMLs/mysqlFileGraphic.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = stage.getScene();  //Ottengo la scena corrente per ottenerne le dimensioni della finestra
			scene = new Scene(root, scene.getWidth(), scene.getHeight());
			stage.setScene(scene);
			stage.show();
		}
		catch(SocketException err)
		{
			ServerClosedError(event);
		}
	}
	
	/**
	 * Metodo che viene attivato quando il queryPointButton viene premuto
	 * 
	 * @param event Tipo di evento
	 * @throws ClassNotFoundException Lanciata quando avviene un problema di serializzazione 
	 * @throws IOException Lanciata quando c'è un problema di comunicazione con il server
	 */
	@FXML
	private void clickedQueryPoinButton(ActionEvent event) throws IOException, ClassNotFoundException 
	{
		try
		{
			String e = queryPointField.getText();
			
			if(!exampleError && !e.isEmpty())
			{
				if(firstExample && !retried)
				{
					out.writeObject(true);  //Informo il server che voglio andare avanti al prediction
					out.writeObject(true);  //Informo il server, precisamente readExample() di Data, che voglio continaure
					firstExample = false;
				}
				else
				{
					out.writeObject(true);  
				}
			
				eType = (String) in.readObject();
			}
			
			if(!e.isEmpty())
			{
				try
				{
					if(eType.equals("@READSTRING"))
					{
						out.writeObject(e);
					}
					else if(eType.equals("@READDOUBLE"))
					{
						try
						{
							Double.parseDouble(e);
							out.writeObject(true);  //Vado avanti
							out.writeObject(false);  //Comunico al server che non ci sono problemi
							out.writeObject(Double.parseDouble(e));
							exampleError = false;
						}
						catch(NumberFormatException err)
						{
							out.writeObject(true);  //Vado avanti
							out.writeObject(true); //Comunico al server che ho avuto problemi
							exampleError = true;
							throw new NumberFormatException();
						}
					}
					
					example.add(e);
					
					updateView();  //Aggiorno la vista della schermata KNN
					
					queryPointField.clear();
				}
				catch (NumberFormatException err)
				{
					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setTitle("Attenzione!");
					alert.setHeaderText("Il valore immesso non è valido, riprova");
					alert.showAndWait();
					queryPointField.clear();
				}
			}
			else
			{
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("Attenzione!");
				alert.setHeaderText("Il campo non può essere lasciato vuoto!");	
				alert.showAndWait();
				queryPointField.clear();
			}
		}
		catch(SocketException err)
		{
			ServerClosedError(event);
		}
	}
	
	/**
	 * Metodo che viene attivato quando il retryButton viene premuto
	 * 
	 * @param event Tipo di evento
	 * @throws IOException Lanciata quando la comunicazione con il server viene meno
	 */
	@FXML
	private void clickedRetryButton(ActionEvent event) throws IOException 
	{
		try
		{
			out.writeObject(true);
			
			//Resetto le variabili temporanee
			example = new ArrayList<String>(); 
			firstExample = true;
			exampleError = false;
			occurrences = explanatorySetTypes.size();
			counter = 0;
			eType = "";
			
			//Cambio retried, perchè sto eseguendo una riprova
			retried = true;
			
			//Ritorno allo stato originale grafico di KNN
			kTextField.setDisable(true);
			continueButton.setDisable(true);
			retryButton.setDisable(true);
			queryPointLabel.setText("- Non ancora inserito! -");
			knnResultLabel.setText("");
			kLabel.setDisable(true);
			queryPointInfoLabel.setDisable(true);
			queryPointLabel.setDisable(true);
			knnResultLabel.setDisable(true);
			queryPointButton.setDisable(false);
			queryPointField.setDisable(false);
			qpLabelZero.setDisable(false);
			qpLabel.setDisable(false);
			
			updateView();
			
			//Mi permette di preselezionare il queryPointField, così non devo usare il mouse
			Platform.runLater(new Runnable() 
			{
	            @Override
	            public void run() 
	            {
	                queryPointField.requestFocus();
	            }
	        });
		}
		catch(SocketException err)
		{
			ServerClosedError(event);
		}
	}
	
	/**
	 * Metodo che viene attivato quando il continueButton viene premuto
	 * 
	 * @param event Tipo di evento
	 * @throws ClassNotFoundException Lanciata quando avviene un problema di serializzazione 
	 * @throws IOException Lanciata quando c'è un problema di comunicazione con il server
	 */
	@FXML
	private void clickedContinueButton(ActionEvent event) throws IOException, ClassNotFoundException 
	{
		try
		{
			out.writeObject(true); //Informo il client che voglio proseguire con la lettura di k
			
			String e = kTextField.getText();
			
			try
			{
				Integer.parseInt(e);
				out.writeObject(false); //Informo il server che non ci sono errori
				out.writeObject(Integer.parseInt(e));
				String temp = queryPointLabel.getText();
				queryPointLabel.setText(temp + " K=" + e);
				Double prediction = (Double) in.readObject();
				knnResultLabel.setText("É: " + prediction);
				kTextField.clear();
				kTextField.setDisable(true);
				continueButton.setDisable(true);
				retryButton.setDisable(false);
				kLabel.setDisable(true);
				queryPointInfoLabel.setDisable(false);
				queryPointLabel.setDisable(false);
				knnResultLabel.setDisable(false);
				
				//Mi permette di preselezionare il pulsante retryButton, così non devo usare il mouse
				Platform.runLater(new Runnable() 
				{
		            @Override
		            public void run() 
		            {
		            	retryButton.requestFocus();
		            }
		        });
			}
			catch (NumberFormatException err)
			{
				out.writeObject(true);
				
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("Attenzione!");
				alert.setHeaderText("Il valore immesso non è valido, riprova");
				alert.setContentText("Ricorda che K, deve essere un valore intero!");
				alert.showAndWait();
				kTextField.clear();
			}
		}
		catch(SocketException err)
		{
			ServerClosedError(event);
		}
	}
	
	/**
	 * Metodo che inizializza la scena KNNGraphic con alcune informazioni fornite in input
	 * 
	 * @param aggregate Ottengo il tipo di pulsante dalla quale sono venuto dalla scena precedente
	 * @param input Input Stream per ottenere informazioni dal server
	 * @param output Output Stream per dare informazioni al server
	 * @throws ClassNotFoundException Lanciata quando avviene un errore nella serializzazione 
	 * @throws IOException Lanciata quando c'è un problema di comunicazione con il server
	 */
	void iniziateView(DATABASE_TYPE aggregate, ObjectInputStream input, ObjectOutputStream output) throws ClassNotFoundException, IOException
	{
		in = input;
		out = output;
		
		if(aggregate.equals(DATABASE_TYPE.TXT))
		{
			textFileButton.setStyle("-fx-background-color : #00BFFF");
		}
		else if(aggregate.equals(DATABASE_TYPE.BIN))
		{
			binaryFileButton.setStyle("-fx-background-color : #00BFFF");
		}
		else if(aggregate.equals(DATABASE_TYPE.SQL))
		{
			databaseButton.setStyle("-fx-background-color : #00BFFF");
		}
		
		kTextField.setDisable(true);
		continueButton.setDisable(true);
		retryButton.setDisable(true);
		
		queryPointLabel.setText("- Non ancora inserito! -");
		knnResultLabel.setText("");
		kLabel.setDisable(true);
		queryPointInfoLabel.setDisable(true);
		queryPointLabel.setDisable(true);
		knnResultLabel.setDisable(true);
		
		//Acquisisco e costruisco la tabella
		tableAcquisition();
		iniziateKnnTable();
		
		//Continuo con l'intefaccia
		updateView();
		
		//Mi permette di preselezionare il queryPointField, così non devo usare il mouse
		Platform.runLater(new Runnable() 
		{
            @Override
            public void run() 
            {
                queryPointField.requestFocus();
            }
        });
	}
	
	/**
	 * Metodo che acquisisce dal server tutte le informazioni sulla tabella che si sta
	 * cercando di creare nella scena del calcolo KNN
	 * 
	 * @throws ClassNotFoundException Lanciata quando avviene un errore nella serializzazione 
	 * @throws IOException Lanciata quando c'è un problema di comunicazione con il server
	 */
	@SuppressWarnings("unchecked")
	private void tableAcquisition() throws ClassNotFoundException, IOException
	{
		numberOfExamples = (int) in.readObject();
		target = (ArrayList<Double>) in.readObject();
		explanatorySetTypes = (LinkedList<String>) in.readObject();
		explanatorySetNames = (LinkedList<String>) in.readObject();
		data = (ArrayList<LinkedList<String>>) in.readObject();
		occurrences = explanatorySetTypes.size();
	}
	
	/**
	 * Metodo che si occupa di costruire a schermo la tabella da tutte le informazioni 
	 * del database acquisite dal server
	 */
	private void iniziateKnnTable() 
	{
		TableColumn<ArrayList<String>, String> column = null;
		int columns = explanatorySetTypes.size() +1;
		int rows = numberOfExamples;
		
		//Colonna della numerazione delle righe
		column = new TableColumn<ArrayList<String>, String>("#");
		column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(0)));
		column.prefWidthProperty().bind(knnTable.widthProperty().multiply(0.07));
		column.setResizable(false);
		column.setSortable(false);
		knnTable.getColumns().add(column);
		
		//Creazione delle colonne
		
		for(int i = 0; i < columns; i++)
		{
			final int finalIdx = i;

			
			if(i < columns -1)
			{
				column = new TableColumn<ArrayList<String>, String>(explanatorySetNames.get(i));
				column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(finalIdx +1)));
			}
			else
			{
				column = new TableColumn<ArrayList<String>, String>("Y");
				column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(finalIdx +1)));
			}
			
			column.prefWidthProperty().bind(knnTable.widthProperty().multiply(0.17));
			column.setResizable(false);
			column.setSortable(false);
			
			knnTable.getColumns().add(column);
		}
		
		//Inserimento dei dati
		
		for(int i = 0; i < rows; i++)
		{
			ArrayList<String> temp = new ArrayList<String>();
			
			temp.add(Integer.toString(i +1));
			
			for(int j = 0; j < columns -1; j++)
			{
				temp.add(data.get(i).get(j));
			}

			temp.add(Double.toString(target.get(i)));
			
			knnTable.getItems().add(temp);
		}
	}	
	
	/**
	 * Metodo che aggiorna la scena in base alle informazioni e ai bottoni premuti dall'utente
	 * soprattutto al cambio di valore nell'acquisizione del query point
	 */
	private void updateView()
	{	
		if(occurrences > 0)
		{
			if(explanatorySetTypes.get(counter).equals("@DISCRETE"))
			{
				qpLabel.setText("valore discreto X[" + Integer.toString(counter) + "]");
			}
			else if(explanatorySetTypes.get(counter).equals("@CONTINUOUS"))
			{
				qpLabel.setText("valore continuo X[" + Integer.toString(counter) + "]");
			}
			
			if(occurrences > 1)
			{
				queryPointField.setPromptText("Inserisci X[" + Integer.toString(counter)+ "]...");
				queryPointButton.setText("Continua: " + Integer.toString(occurrences -1) + " rimanenti");
			}
			else if(occurrences == 1)
			{
				queryPointField.setPromptText("Insersci X[" + Integer.toString(counter)+ "]...");
				queryPointButton.setText("Fine");
			}
		
			counter++;
			occurrences--;	
		}
		else
		{
			queryPointButton.setDisable(true);
			queryPointField.setDisable(true);
			kTextField.setDisable(false);
			continueButton.setDisable(false);
			
			StringBuilder sb = new StringBuilder();
			
			sb.append("[");
			
			for(int i = 0; i < example.size(); i++)
			{
				sb.append(example.get(i));
				
				if(i < example.size() -1)
				{
					sb.append(", ");
				}
			}
			
			sb.append("]");
			
			queryPointLabel.setText(sb.toString());
			kLabel.setDisable(false);
			qpLabelZero.setDisable(true);
			qpLabel.setDisable(true);
			
			//Mi permette di preselezionare il kTextField, così non devo usare il mouse
			Platform.runLater(new Runnable() 
			{
	            @Override
	            public void run() 
	            {
	                kTextField.requestFocus();
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
