package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import utility.Keyboard;

/**
 * Classe che si interfaccia con l'utente e che comunica con il server le scelte dell'utente
 *
 */
@SuppressWarnings("removal")
public class Client 
{	
	/**
	 * Socket per la comunicazione con server
	 */
	private Socket socket=null;
	
	/**
	 * Output Stream dei dati verso il server
	 */
	private ObjectOutputStream out=null;
	
	/**
	 * Input Stream dei dati dal server
	 */
	private ObjectInputStream in=null;
	
	
	/**
	 * Costruttore di Client
	 * 
	 * @param address Indirizzo del server
	 * @param port Porta del server
	 * @throws IOException Lanciata quando occcorre un problema di comunicazione con il server
	 * @throws ClassNotFoundException Lanciata quando accade un problema alla serializzazione delle classi che estendono Serializable
	 */
	Client(String address, int port) throws IOException, ClassNotFoundException
	{
		try
		{
			socket = new Socket(address, port);
			System.out.println(socket);		
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());	 // stream con richieste del client
			talking();
		}
		catch (ConnectException err)
		{
			System.out.println("Impossibile aprire il Client di KNN, assicurarsi che il server sia aperto!");
		}
	}
	
	/**
	 * Metodo che si occupa di prendere in input le decisioni dell'utente, comunicarle al client, aspettare l'output del server e mostrarle all'utente
	 * 
	 * @throws IOException Lanciata quando occcorre un problema di comunicazione con il server
	 * @throws ClassNotFoundException Lanciata quando accade un problema alla serializzazione delle classi che estendono Serializable
	 */
	private void talking() throws IOException, ClassNotFoundException {
		
		int decision=0;
		String menu="";
		
		try
		{
			do {	
				do{
				
					System.out.println("Load KNN from file [1]");
					System.out.println("Load KNN from binary file  [2]");
					System.out.println("Load KNN from database  [3]");
					decision=Keyboard.readInt();
				}while(decision <1 || decision >3);
				
				String risposta = "";
				
					out.writeObject(decision);
					String tableName="";
					
				do {
					risposta = "";
					System.out.println("Table name (without estensione):");
					tableName=Keyboard.readString();
					out.writeObject(tableName);
					risposta=(String)in.readObject();
				
				}while(risposta.contains("@ERROR"));
				
				System.out.println("KNN loaded on the server");
				
				// predict
				String c;
				do {
					boolean flag=true; //reading example
					do {
						risposta = (String)(in.readObject());  
						if(!risposta.contains("@ENDEXAMPLE")) {
							// sto leggendo l'esempio
							String msg=(String)(in.readObject());
							if(risposta.equals("@READSTRING"))  //leggo una stringa
							{
								System.out.println(msg);
								out.writeObject(Keyboard.readString());
							}
							else //leggo un numero
							{
								double x=0.0;
								do {
									System.out.println(msg);								
									x=Keyboard.readDouble();
								}
								while(new Double(x).equals(Double.NaN));
								out.writeObject(x);
							}
						}
						else flag=false;
					}while( flag);
					
					//sto leggendo  k
					risposta=(String)(in.readObject());
					int k=0;
					do {
						System.out.print(risposta);
						k=Keyboard.readInt();
					}while (k<1);
					out.writeObject(k);
					//aspetto la predizione 
					
					System.out.println("Prediction:"+in.readObject());
		
						
			
					System.out.println("Vuoi ripetere predizione? Y/N");
					c=Keyboard.readString();
					if(c.toLowerCase().equals("y"))
					{
						out.writeObject("@REPEAT");
					}
					else
					{
						out.writeObject("@DONOTREPEAT");
					}
					
				}while (c.toLowerCase().equals("y"));	
				System.out.println("Vuoi ripetere una nuova esecuzione con un nuovo oggetto KNN? (Y/N)");
				menu=Keyboard.readString();
				
				if(menu.toLowerCase().equals("y"))
				{
					out.writeObject("@REPEATSCRATCH");
				}
				else
				{
					out.writeObject("@DONOTREPEATSCRATCH");
				}
			}
			while(menu.toLowerCase().equals("y"));
		}
		catch(SocketException err)
		{
			System.out.println("Non è più possibile stabilire una comunicazione con il server, perchè quest'ultimo è stato chiuso");
			System.out.println("Uscita dal client in corso...");
		}
		
	}

	/**
	 * Main che inizia la comunicazione con il server
	 * 
	 * @param args Argomenti del main
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args){
		
		InetAddress addr;
		try {
			addr = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			System.out.println(e.toString());
			return;
		}
		
		Client c;
		try {
			c=new Client("127.0.0.1", 2025);
			
		}  catch (IOException e) {
			System.out.println(e.toString());
			return;
		} catch (NumberFormatException e) {
			System.out.println(e.toString());
			return;
		} catch (ClassNotFoundException e) {
			System.out.println(e.toString());
			return;
		}
	}
}
