package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

import data.Attribute;
import data.ContinuousAttribute;
import data.Data;
import data.DiscreteAttribute;
import data.TrainingDataException;
import database.DatabaseConnectionException;
import database.DbAccess;
import database.InsufficientColumnNumberException;
import example.Example;
import mining.KNN;

/**
 * Classe che si occupa in prima persona di comunicare con il client
 */
class ServerOneClient extends Thread
{
	/**
	 * Socket di connessione con il client
	 */
	private Socket socket;
	
	/**
	 * Input Stream per acquisire dal client
	 */
	private ObjectInputStream in;
	
	/**
	 * Output Stream per spedire al client
	 */
	private ObjectOutputStream out;
	
	/**
	 * Costruttore di ServerOneClient che instanzia tutti gli attributi di classe
	 * 
	 * @param s Socket di connessione
	 * @throws IOException Lanciata se la comunicazione con il client fallisce
	 */
	ServerOneClient(Socket s) throws IOException
	{
		socket = s;
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(socket.getInputStream());
		
		start();
	}
	
	/**
	 * Metodo lanciato da start() di Thread, che si occupa di comunicare con l'utente dal Client
	 */
	public void run()
	{
		Boolean error = false;
		Boolean goNext = false;
		Boolean exampleError = false;
		Boolean repeatInstance = false;
		
		do
		{
			try 
			{
				int r = (int) in.readObject();  
				KNN knn = null;
				Data trainingSet = null;
				String file = "";
				
				if(r == 1) 
				{
					try 
					{
						file = ((String) in.readObject()) + ".dat";
						trainingSet = new Data(file);
						out.writeObject(false);
						error = false;
						
						knn = new KNN(trainingSet);
						
						try
						{
							knn.salva(file + ".dmp");
						}
						catch(IOException exc) 
						{
							System.out.println(exc.getMessage());
						}
						
						serializeData(trainingSet);
						
						goNext = (Boolean) in.readObject();
					} 
					catch (TrainingDataException e) 
					{
						out.writeObject(true);
						error = true;
					}
				}
				else if(r == 2)
				{
					try
					{
						file = ((String) in.readObject()) + ".dat.dmp";
						knn = KNN.carica(file);
						
						out.writeObject(false);
						error = false;
						serializeData(knn.getData());
						
						goNext = (Boolean) in.readObject();
					}
					catch(FileNotFoundException exc)
					{
						out.writeObject(true);
						error = true;
					}
				}
				else if(r == 3)
				{
					try 
					{	
						file = ((String) in.readObject());
						DbAccess db = new DbAccess();
						
						trainingSet = new Data(db, file);
						knn = new KNN(trainingSet);
						
						out.writeObject("@OK");
						error = false;
						
						try
						{
							knn.salva(file + "DB" + ".dat.dmp");
						}
						catch(IOException exc) 
						{
							System.out.println(exc.getMessage());
						}
						
						serializeData(trainingSet);
						
						goNext = (Boolean) in.readObject();
					} 
					catch (TrainingDataException | InsufficientColumnNumberException e) 
					{
						out.writeObject("@WRONGNAME");
						error = true;
					} 
					catch (SQLException | DatabaseConnectionException e)
					{
						out.writeObject("@CONNECTIONERROR");
						error = true;
					}
				}
				
				//Controllo che non ci siano errori e che l'utente abbia espresso la volontà di andare avanti
				if(!error && goNext)  
				{
					do
					{
						try
						{
							Double predictionResult = knn.predict(out, in);
							out.writeObject(predictionResult);
							repeatInstance = (Boolean) in.readObject();
							exampleError = false;
						}
						catch(Exception err)  //Capto se o l'esempio o k sono stati acquisiti male oppure dal client sono tornato indietro
						{
							exampleError = true;
							break;
						}
						
					} while (repeatInstance == true);	
				}
			} 
			catch (ClassNotFoundException | IOException e) 
			{
				break;
			} 
			
		} while(error || !goNext || exampleError || !repeatInstance);
	}
	
	/**
	 * Metodo che si occupa di trasformare il trainingSet acquisito in uno più
	 * digeribile dal client per la visualizzazione a schermo della tabella
	 * 
	 * @param d trainingSet da trasformare
	 * @throws IOException Lanciata quando la comunicazione con il client fallisce
	 */
	private void serializeData(Data d) throws IOException
	{
		//Serializzo numberOfExamples
		int rows = d.getDataList().size();
		out.writeObject(rows);
		
		
		//Serializzo target
		out.writeObject(d.getTargetList());
		
		
		//Serializzo explanatorySet
		
		LinkedList<Attribute> oldExplanatorySet = d.getExplanatorySetList();
		LinkedList<String> explanatorySetNames = new LinkedList<String>();
		LinkedList<String> explanatorySetTypes = new LinkedList<String>();
		int columns = d.getExplanatorySetList().size();
		
		for(Attribute a : oldExplanatorySet)
		{
			if(a instanceof DiscreteAttribute)
			{
				explanatorySetTypes.add("@DISCRETE");
			}
			else if(a instanceof ContinuousAttribute)
			{
				explanatorySetTypes.add("@CONTINUOUS");
			}
			
			explanatorySetNames.add(a.getName());
		}
		
		out.writeObject(explanatorySetTypes);
		out.writeObject(explanatorySetNames);
		
		
		//Serializzo data
		

		ArrayList<LinkedList<String>> newData = new ArrayList<LinkedList<String>>();
		ArrayList<Example> oldData = d.getDataList();
		
		for(int i = 0; i < rows; i++)
		{
			LinkedList<String> temp = new LinkedList<String>();
			
			for(int j = 0; j < columns; j++)
			{
				if(oldData.get(i).get(j) instanceof String)
				{
					temp.add((String) oldData.get(i).get(j));
				}
				else if(oldData.get(i).get(j) instanceof Double)
				{
					temp.add(Double.toString((Double) oldData.get(i).get(j)));
				}
			}
			
			newData.add(i, temp);
		}
		
		out.writeObject(newData);
	}
}
