package mining;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import data.Data;
import example.Example;

/**
 * Classe KNN che si occupa del mining vero e proprio del training set e 
 * dell'acquisizione dei dati dall'utente, implementa l'interfaccia
 * Serializable per poter salvare su file binario l'oggetto KNN
 * 
 */
public class KNN implements Serializable
{
	/**
	 * ID che serve ad indentificare la serializzazzione e la deserializzazione
	 */
	private static final long serialVersionUID = 3172731936606540257L;
	
	/**
	 * Attributo data nella quale inserire i dati del database
	 */
	private Data data;
	
	/**
	 * Costruttore di KNN, prendendo in input un trainingSet di tipo Data
	 * 
	 * @param trainingSet Oggetto di tipo data da attribuire all'instanza di classe
	 */
	public KNN(Data trainingSet)
	{
		data = trainingSet;
	}
	
	/**
	 * Metodo alternativo che esegue il predict su tutto il database da un'esempio "e" preso 
	 * in input dall'utente ed in particolare dal client
	 * 
	 * @param out Output stream
	 * @param in Input stream
	 * @return Risultato della predizione
	 * @throws IOException Lanciata quando c'è un errore di comunicazione con il client
	 * @throws ClassNotFoundException Lanciata quando ci sono problemi di serializzazione dal client
	 * @throws ClassCastException Lanciata quando ci sono errori di casting di tipi
	 */
	public double predict(ObjectOutputStream out, ObjectInputStream in) throws IOException, ClassNotFoundException, ClassCastException
	{
		Example e = data.readExample(out,in);
		int k=0;
		
		out.writeObject("Inserisci valore k>=1:");
		k=(Integer)(in.readObject());
		
		return data.avgClosest(e, k);
	}
	
	/**
	 * Metodo che tramite la serializzazione salva su file binario l'oggetto instanza di KNN
	 * 
	 * @param nomeFile Nome del file binario da salvare
	 * @throws IOException Lanciata quando non è possibile proseguire con il salvataggio
	 */
	public void salva(String nomeFile) throws IOException
	{
		//Tramite la stringa in input cerco e trovo nella root 
		//della cartella il file che creerò come output binario
		FileOutputStream outFile = new FileOutputStream(nomeFile);
		ObjectOutputStream outStream = new ObjectOutputStream(outFile);
		KNN temp = new KNN(data);
		
		outStream.writeObject(temp);
		outStream.close();
	}
	
	/**
	 * Metodo che tramite la serializzazione carica su file binario l'oggetto instanza di KNN
	 * 
	 * @param nomeFile Nome del file binario da caricare
	 * @return Instanza di KNN pre-popolata
	 * @throws IOException Lanciata quando non trova il file binario da caricare
	 * @throws ClassNotFoundException Lanciata quando non trova la classe da serializzare
	 */
	public static KNN carica(String nomeFile) throws IOException,ClassNotFoundException
	{
		//Tramite la stringa in input cerco e trovo nella root 
		//della cartella il file bianario che caricherò nell'oggetto temp di tipo KNN
		FileInputStream inFile = new FileInputStream(nomeFile);
		ObjectInputStream inStream = new ObjectInputStream(inFile);
		KNN temp = (KNN) inStream.readObject();
		inStream.close();
		
		return temp;
	}
	
	/**
	 * Sovraccarico dell'operatore toString per stampare un KNN,
	 * che a sua volta usa il toString per stampare data
	 */
	@Override
	public String toString() 
	{
		return data.toString();
	}
	
}
