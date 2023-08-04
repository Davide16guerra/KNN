package server;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import data.TrainingDataException;

/**
 * Classe che crea la connessione con il client e apre una nuova connessione per
 * ogni client che viene connesso
 */
class MultiServer 
{
	/**
	 * Porta sulla quale collegare il client
	 */
	private static int PORT = 2026;
	
	/**
	 * Costruttore di MultiServer
	 * 
	 * @param port Porta di connessione
	 * @throws InterruptedException Lanciata se non è possibile collegarsi al client
	 * @throws IOException Lanciata se la comunicazione con il client si interrompe
	 */
	private MultiServer(int port) throws InterruptedException, IOException
	{
		PORT = port;
		run();
	}
	
	/**
	 * Metodo che viene chiamato per la crazione di tante instanze di server tanti client si collegano
	 * 
	 * @throws InterruptedException Lanciata se non è possibile collegarsi al client
	 * @throws IOException Lanciata se la comunicazione con il client si interrompe
	 */
	private void run() throws InterruptedException, IOException
	{
		try 
		{
			ServerSocket s = new ServerSocket(PORT);
			System.out.println("Server Avviato...");
			
			try 
			{
				while(true)
				{
					Socket socket = s.accept();
					try 
					{
						new ServerOneClient(socket);
					}
					catch(IOException e)
					{
						socket.close();
					}
				}
			} 
			finally
			{
				s.close();
			}
		}
		catch(BindException err)
		{
			System.out.println("Il Server è già online!");
			System.out.println("Uscita...");
		}
		
	}
	
	/**
	 * Main che serve ad essere lanciato per far instaziare il server
	 * 
	 * @param args Argomenti del main
	 * @throws TrainingDataException Lanciata se ci sono problemi con il trainingSet
	 * @throws InterruptedException Lanciata se la connessione viene interrotta
	 * @throws IOException Lanciata se la comunicazione con il client fallisce
	 */
	public static void main(String args[]) throws TrainingDataException, InterruptedException, IOException 
	{
		new MultiServer(PORT);
	}
}


