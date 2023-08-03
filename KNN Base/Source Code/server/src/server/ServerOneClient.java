package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import data.Data;
import data.TrainingDataException;
import database.DatabaseConnectionException;
import database.DbAccess;
import database.InsufficientColumnNumberException;
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
		String repeatScratch = "";
		do
		{
			try 
			{
				int r = (int) in.readObject();  
				KNN knn = null;
				String file = "";
				Data trainingSet = null;
				String risposta = "";
				
				if(r == 1) 
				{
					do
					{
						try 
						{
							risposta = "";
							file = ((String) in.readObject()) + ".dat";
							trainingSet = new Data(file);
							
						} catch(TrainingDataException exc)
						{
							risposta = "@ERROR";
							out.writeObject("@ERROR");
						}
					} while(risposta.contains("@ERROR"));
					
					out.writeObject("@OK");
					knn = new KNN(trainingSet);
					System.out.println(trainingSet.toString());
					
					try{knn.salva(file+".dmp");}
					catch(IOException exc) {System.out.println(exc.getMessage());}
				}
				else if(r == 2)
				{
					do
					{
						try
						{
							risposta = "";
							file = ((String) in.readObject()) + ".dat.dmp";
							knn=KNN.carica(file);
						}
						catch(FileNotFoundException exc)
						{
							risposta = "@ERROR";
							out.writeObject("@ERROR");
						}
						
					} while(risposta.contains("@ERROR"));
					
					out.writeObject("@OK");
					System.out.println(knn.toString());
				}
				else if(r == 3)
				{
					do
					{
						try 
						{
							risposta = "";
							file = ((String) in.readObject());
							DbAccess db=new DbAccess();
							trainingSet= new Data(db, file);
							
						} 
						catch (DatabaseConnectionException e) 
						{
							e.printStackTrace();
						} catch (TrainingDataException | InsufficientColumnNumberException e) {
							risposta = "@ERROR";
							out.writeObject("@ERROR");
						} catch (SQLException e) {
							e.printStackTrace();
						}
	
					} while(risposta.contains("@ERROR"));
					
					out.writeObject("@OK");
					knn = new KNN(trainingSet);
					System.out.println(trainingSet.toString());
				}
				
				String repeat = "";
				
				do
				{
					Double predictionResult;
					predictionResult = knn.predict(out, in);
					out.writeObject(predictionResult);
					repeat = (String) in.readObject();
					
				} while (repeat.equals("@REPEAT"));
					
				repeatScratch = (String) in.readObject();
				
			} 
			catch (ClassNotFoundException | IOException e) 
			{
				break;
			} 
			
		} while(repeatScratch.equals("@REPEATSCRATCH"));
	}
}
