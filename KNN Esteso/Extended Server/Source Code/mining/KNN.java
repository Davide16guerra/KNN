package mining;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import data.Data;
import example.Example;
import example.ExampleSizeException;

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
	 * Metodo che resituisce data
	 * 
	 * @return data
	 */
	public Data getData()
	{
		return data;
	}
	
	/**
	 * Metodo alternativo che esegue il predict su tutto il database da un'esempio "e" preso 
	 * in input dall'utente ed in particolare dal client
	 * 
	 * @param out Output stream
	 * @param in Input stream
	 * @return Risultato della predizione
	 * @throws Exception Lancia un'eccezione generica per situazioni particolari
	 */
	public double predict(ObjectOutputStream out, ObjectInputStream in) throws Exception
	{
		Example e = null;
		try
		{
			e = data.readExample(out,in);
		}
		catch (ExampleSizeException err)
		{
			throw new ExampleSizeException();
		}

		Boolean kError = false;
		int k = 0;
		
		do
		{
			Boolean goNext = (Boolean) in.readObject();
			
			if(goNext)
			{
				kError = (Boolean) in.readObject();
			}

			if(goNext && !kError)
			{
				k = (Integer)(in.readObject());
			}
			else if (!goNext)
			{
				throw new ExampleSizeException();
			}
			
		} while (kError);
		
		//Avvio e restituisco il risultato della predizione con il K ed l'esempio "e" dell'utente
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
	public static KNN carica(String nomeFile) throws IOException, ClassNotFoundException
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
