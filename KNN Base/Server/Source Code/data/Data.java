package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import database.Column;
import database.DatabaseConnectionException;
import database.DbAccess;
import database.InsufficientColumnNumberException;
import database.QUERY_TYPE;
import database.TableData;
import database.TableSchema;
import example.Example;
import example.ExampleSizeException;

/**
 * Classe principale del programma che si occupa di immagazzinare, gestire ed 
 * elaborare i dati presi in input da file
 */
public class Data implements Serializable
{
	/**
	 * ID che serve ad indentificare la serializzazzione e la deserializzazione
	 */
	private static final long serialVersionUID = -798233948391742730L;
	
	/**
	 * Arralist di Example, contiene in ogni posizione un Example, che
	 * a suo volta contiene le variabili indipendenti
	 */
	private ArrayList<Example> data;  
	
	/**
	 * Arralyst di Double, contiene le variablili dipendenti
	 */
	private ArrayList<Double> target; 
	
	/**
	 * Contiene il numero delle "tuple" sulla quale fare le operazioni
	 */
	private int numberOfExamples;	
	
	/**
	 * LinkedList contenente i dettagli su ogni variabile,
	 * se Continua o Discreta / Numerica o Testuale
	 */
	private LinkedList<Attribute> explanatorySet;
	
	/**
	 * Attributo che indica la presenza della colonna target
	 */
	@SuppressWarnings("unused")
	private ContinuousAttribute classAttribute;

	/**
	 * Costruttore di data, prendendo in input una stringa, cerca il file txt e lo carica in memoria
	 * 
	 * @param fileName  Nome del file di cui leggere i dati
	 * @throws FileNotFoundException Lanciata quando il file non è trovato
	 * @throws TrainingDataException Lanciata quando c'è un errore di calcolo nel database
	 */
	@SuppressWarnings({ "resource", "removal" })
	public Data(String fileName) throws FileNotFoundException, TrainingDataException 
	{
		try 
		{
			File inFile = new File(fileName);  //Tramite la stringa del nome file preso in input
											   //lo carico nel file
			Scanner sc = new Scanner(inFile);  //Apro lo scanner del file
			String line = sc.nextLine();  //Istruzione per andare alla riga del file successiva
			if (!line.contains("@schema"))
				throw new TrainingDataException("Schema Mancante");
			String s[] = line.split(" ");  //"Spezzo" la stringa, ogni volta che trovo una spazio,
										   //ed ogni volta la metto in una posizione dell'array s

			// Popolazione dell'explanatory Set
			
			explanatorySet = new LinkedList<Attribute>();
			short iAttribute = 0;
			line = sc.nextLine();
			while (!line.contains("@data")) 
			{
				s = line.split(" ");
				if (s[0].equals("@desc"))   // aggiungo l'attributo allo spazio descrittivo
				{ 							// @desc motor discrete	
					
					//Faccio una prima distinzione tra attributi Discreti ed Attributi Continui
					if(s[2].equals("discrete"))
					{
						explanatorySet.add(new DiscreteAttribute(s[1], iAttribute));
					}
					else if(s[2].equals("continuous"))
					{
						explanatorySet.add(new ContinuousAttribute(s[1], iAttribute));
					}
				} 
				else if (s[0].equals("@target"))
					classAttribute = new ContinuousAttribute(s[1], iAttribute);

				iAttribute++;
				line = sc.nextLine();

			}

			// Avvaloro il numero di "tuple"
			numberOfExamples = new Integer(line.split(" ")[1]);

			// Popolazione di data e target
			data = new ArrayList<Example>(numberOfExamples);
			target = new ArrayList<Double>(numberOfExamples);

			//Eseguo finchè esiste una linea successiva
			while (sc.hasNextLine()) 
			{
				Example e = new Example(explanatorySet.size());

				line = sc.nextLine();
				s = line.split(",");

				// Controllo che lo schema abbia senso, rispettando le colonne
				if (s.length - 1 != explanatorySet.size()) 
				{
					throw new TrainingDataException("Errore nello schema");
				}

				for (short jColumn = 0; jColumn < s.length - 1; jColumn++)
				{
					//Controllo che l'oggetto in posizione jColumn di explanatory set
					//sia instanza di DiscreteAttrobute
					if(explanatorySet.get(jColumn) instanceof DiscreteAttribute)
					{
						//Dal precendete controllo si appura che l'attributo è
						//Discreto / Testuale, quindi lo aggiungo semplicemente all'Example e
						e.set(s[jColumn], jColumn);
					}
					//Controllo che l'oggetto in posizione jColumn di explanatory set
					//sia instanza di ContinuousAttrobute
					else if(explanatorySet.get(jColumn) instanceof ContinuousAttribute)
					{
						//Dal precendete controllo si appura che l'attributo è
						//Continuo / Numerico, quindi lo aggiungo all'Example e, rendendolo double
						Double temp = Double.parseDouble(s[jColumn]);
						e.set(temp, jColumn);
						
						//Aggiorno Max, tramite setMax di ContinuosAttribute
						((ContinuousAttribute) explanatorySet.get(jColumn)).setMax(temp);  
						//Aggiorno Min, tramite setMin di ContinuosAttribute
						((ContinuousAttribute) explanatorySet.get(jColumn)).setMin(temp);  
					}
				}

				//Aggiungo finalmente a data, l'esempio e
				data.add(e);

				//Popolazione di Target, ovvero della variabile dipendente
				// Verifico che Y, ovvero Target sia di tipo numerico
				try 
				{
					//Aggiungo a target il valore della varaibile dipendente letta da file
					//Instanziando un nuovo double
					target.add(new Double(s[s.length - 1]));
				} 
				catch (NumberFormatException nfe) 
				{
					throw new TrainingDataException("Training Set privo di variabile target numerica");
				}
			}

			//Controllo che data non sia vuoto
			if (data.size() == 0) 
			{
				throw new TrainingDataException("Training Set Vuoto");
			}

			//Chiudo lo scanner del file
			sc.close();
		} 
		catch (FileNotFoundException fnfe) 
		{
			throw new TrainingDataException("File non Trovato");
		}
	}
	
	/**
	 * Costruttore di Data alternativo che si interfaccia con il Database MySQL
	 * 
	 * @param db Dati del database
	 * @param tableName Nome della tabella
	 * @throws TrainingDataException Lanciata quando c'è un errore nel database 
	 * @throws InsufficientColumnNumberException Lanciata quando il nome della tabella del database
	 * @throws SQLException Lanciata quando ci sono errori logici nel database
	 * @throws DatabaseConnectionException Lanciata quando non è possibile connettersi al database MySQL
	 */
	public Data(DbAccess db, String tableName) throws TrainingDataException, InsufficientColumnNumberException, SQLException, DatabaseConnectionException
	{

		TableSchema ts = new TableSchema(tableName, db);
		TableData td = new TableData(db, ts);
		
		// Popolazione dell'explanatory Set
		explanatorySet = new LinkedList<Attribute>();
		
		//Faccio una prima distinzione tra attributi Discreti ed Attributi Continui
		
		Iterator<Column> it = ts.iterator();
		
		short iAttribute = 0;
		while(it.hasNext())
		{
			Column tempIt = it.next();
			
			if(tempIt.isNumber())
			{
				explanatorySet.add(new ContinuousAttribute(tempIt.getColumnName(), iAttribute));
			}
			else 
			{
				explanatorySet.add(new DiscreteAttribute(tempIt.getColumnName(), iAttribute));
			}
			
			iAttribute++;
		}
		

		// Avvaloro il numero di "tuple"
		numberOfExamples = td.getExamples().size();

		// Popolazione di Data
		
		data = (ArrayList<Example>) td.getExamples();
		
		//Popolazione di Target
		target = new ArrayList<Double>(numberOfExamples);
		ArrayList<Object> tempAR = (ArrayList<Object>) td.getTargetValues();
		
		for(Object o : tempAR)
		{
			try
			{
				target.add((Double) o);
			}
			catch (ClassCastException err)
			{
				throw new TrainingDataException("Errore nel database, valore numerico non presente in target");
			}
		}
		
		//Aggiornamento di Minimo e Massimo per ogni colonna
		
		Iterator<Column> it2 = ts.iterator();
		
		int i = 0;
		while(it2.hasNext())
		{
			Column tempIt = it2.next();
			
			if(explanatorySet.get(i) instanceof ContinuousAttribute)
			{
				Double tempMax = (double) ((float) td.getAggregateColumnValue(tempIt, QUERY_TYPE.MAX));
				Double tempMin = (double) ((float) td.getAggregateColumnValue(tempIt, QUERY_TYPE.MIN));

				//Aggiorno Max, tramite setMax di ContinuosAttribute
				((ContinuousAttribute) explanatorySet.get(i)).setMax(tempMax);  
				//Aggiorno Min, tramite setMin di ContinuosAttribute
				((ContinuousAttribute) explanatorySet.get(i)).setMin(tempMin); 
			}

			i++;
		}
	}

	/**
	 * Metodo che restituisce la dimensione di Data e Target, nonchè il numero delle tuple
	 * 
	 * @return Numero degli esempi memorizzati, o meglio il numero delle "tuple"
	 */
	public int getNumberOfExplanatoryAttributes() 
	{
		return explanatorySet.size();
	}

	
	/**
	 * Partiziona data rispetto all'elemento x di key e restiutisce il punto di separazione
	 * 
	 * @param key ArrayList contenente le distanze calcolate in avgClosest
	 * @param inf Estremo inferiore
	 * @param sup Estremo superiore
	 * @return Posizione con la quale far girare la logica di QuickSort
	 * @throws ExampleSizeException Lanciata quando ci sono problemi nelle dimensioni dell'esempio acquisito, rispetto a quello già immagazzinato
	 */
	private int partition(ArrayList<Double> key, int inf, int sup) throws ExampleSizeException 
	{
		
		int i, j;

		i = inf;
		j = sup;
		int med = (inf + sup) / 2;

		Double x = key.get(med);

		try 
		{
			data.get(inf).swap(data.get(med));
		}
		catch(ExampleSizeException err)
		{
			throw new ExampleSizeException("Swap impossibile, dimensioni degli esempi differenti");
		}

		double temp = target.get(inf);
		target.set(inf, target.get(med));
		target.set(med, temp);

		temp = key.get(inf);
		key.set(inf, key.get(med));
		key.set(med, temp);

		while (true) 
		{
			while (i <= sup && key.get(i) <= x) 
			{
				i++;
			}

			while (key.get(j) > x) 
			{
				j--;
			}

			if (i < j) 
			{
				try
				{
					data.get(i).swap(data.get(j));
				}
				catch(ExampleSizeException err)
				{
					throw new ExampleSizeException("Swap impossibile, dimensioni degli esempi differenti");
				}
				
				temp = target.get(i);
				target.set(i, target.get(j));
				target.set(j, temp);

				temp = key.get(i);
				key.set(i, key.get(j));
				key.set(j, temp);
			} 
			else
			{
				break;
			}
		}
		try
		{
			data.get(inf).swap(data.get(j));
		}
		catch(ExampleSizeException err)
		{
			throw new ExampleSizeException("Swap impossibile, dimensioni degli esempi differenti");
		}

		temp = target.get(inf);
		target.set(inf, target.get(j));
		target.set(j, temp);

		temp = key.get(inf);
		key.set(inf, key.get(j));
		key.set(j, temp);

		return j;
	}

	
	/**
	 * Algoritmo quicksort per l'ordinamento di data usando come relazione d'ordine
	 * totale "minore uguale" definita su key
	 * 
	 * @param key ArrayList contenente le distanze calcolate in avgClosest
	 * @param inf Estremo inferiore
	 * @param sup Estremo superiore
	 * @throws ExampleSizeException Lanciata quando ci sono problemi nelle dimensioni dell'esempio acquisito, rispetto a quello già immagazzinato
	 */
	private void quicksort(ArrayList<Double> key, int inf, int sup) throws ExampleSizeException 
	{
		if (sup >= inf) 
		{
			int pos;

			try
			{
				pos = partition(key, inf, sup);
			}
			catch(ExampleSizeException err)
			{
				throw new ExampleSizeException("Partition impossibile, errore negli esempi");
			}

			if ((pos - inf) < (sup - pos + 1)) 
			{
				quicksort(key, inf, pos - 1);
				quicksort(key, pos + 1, sup);
			} 
			else 
			{
				quicksort(key, pos + 1, sup);
				quicksort(key, inf, pos - 1);
			}
		}
	}

	/**
	 * //Metodo che calcola la 1 Distanza sugli esempi scalati con il metodo Min/Max Scaler
	 * 
	 * @param e Example preso in input dall'utente di cui fare le operazioni
	 * @param k Parametro intero K, preso in input dell'utente
	 * @return Risultato del KNN dato dal databse caricato nella classe più l'example e il k dell'utente
	 * @throws ExampleSizeException Lanciata quando ci sono problemi nelle dimensioni dell'esempio acquisito, rispetto a quello già immagazzinato
	 */
	public double avgClosest(Example e, int k) throws ExampleSizeException 
	{
		//Key è un'ArrayList delle distanze
		ArrayList<Double> key = new ArrayList<Double>(numberOfExamples);  
		double sum = 0;
		int counter = 0;

		for (int i = 0; i < numberOfExamples; i++) 
		{
			//Calcolo la 1 Distanza di esempi scalati
			try
			{
				key.add(scaledExample(data.get(i)).distance(scaledExample(e)));
			}
			catch(ExampleSizeException ese)
			{
				throw new ExampleSizeException("Errore calcolo distanza, errore nel query point, oppure nel database acquisito");
			}
		}

		try
		{
			//Eseguo l'ordine dell'ArrayList key, e per conseguenza anche di Data e Target
			quicksort(key, 0, numberOfExamples - 1);
		}
		catch(ExampleSizeException err)
		{
			throw new ExampleSizeException("Quicksort impossibile, errore nello schema ");
		}
		
		System.out.println("sorted:");
		System.out.println(this.toString());  //In questo punto stampo data, ordinato 
											  //precedentemente dal quicksort

		
		//Inizio il calcolo della variabile dipendete dall'esempio dell'utente
		
		
		int stop = 0;
		for (int i = 0; i < numberOfExamples; i++) 
		{
			if (stop != k) 
			{
				//Faccio la somma dell'iesimo valore di target in previsione 
				//di fare la media successivamente
				sum = sum + target.get(i);
				counter++;

				//Controllo che, la prossima distanza sia effettivamente più piccola
				//di quella controllata attualmente, perchè quando K = 1, devo prendere
				//tutti k target di distanza più piccola, quindi se per esempio ci sono:
				//[0, 0, 0 ,0, 1] allora anche se k è 1, deve prendere tutti gli 1 (k) esempi 
				//più piccoli quindi in questo caso i primi 4
				if (i < numberOfExamples - 1 && key.get(i) < key.get(i + 1)) 
				{
					stop++;
				}
			} 
			else 
			{
				//Stampo a chermo tutte le distanze, ordinate
				System.out.println("distances:" + key.toString());
				//Restituisco la media, nonchè proprio la variabile incognita numerica
				return sum / counter;
			}
		}
		
		return sum / counter;
	}
	
	/**
	 * Metodo readExample alternativo per l'acquisizione del query point e spedirlo al client
	 * 
	 * @param out output stream
	 * @param in input stream
	 * @return Example o query point preso dall'utente
	 * @throws IOException Lanciata quando ci sono problemi con l'acquisizione e la ricezione di dati dal client
	 * @throws ClassNotFoundException Lanciata quando ci sono problemi di serializzazione dal client
	 * @throws ClassCastException Lanciata quando ci sono errori di casting di tipi
	 */
	public Example readExample(ObjectOutputStream out, ObjectInputStream in) throws IOException, ClassNotFoundException, ClassCastException
	{
		Example e = new Example(getNumberOfExplanatoryAttributes());
		int i = 0;
		
		for(Attribute a : explanatorySet)
		{
			if(a instanceof DiscreteAttribute) 
			{
				out.writeObject("@READSTRING");
				out.writeObject("Inserisci valore discreto X["+i+"]:");
				String temp = (String) in.readObject();
				e.set(temp, i);
			}
			else 
			{
				out.writeObject("@READDOUBLE");
				double x = 0.0;
				boolean res = false;
				do
				{
					out.writeObject("Inserisci valore continuo X["+i+"]:");
					try
					{
						x = (Double) in.readObject();
						res = true;
					}
					catch(ClassCastException err)
					{
						out.writeObject("Il valore inserito non è un valore continuo");
					}
				}
				while(!res);
				
				e.set(x, i);
			}
			i++;
		}
		
		out.writeObject("@ENDEXAMPLE");
		
		return e;
	}
	
	/**
	 * Metodo che, da un Example "e" in input, ne restituisce dove e se serve le sue 
	 * varibili indipendenti numeriche scalate rispettando il loro relativo Minimo e Massimo
	 * 
	 * @param e Esempio da scalare
	 * @return Esempio scalato
	 */
	private Example scaledExample(Example e)
	{
		Example temp = new Example(getNumberOfExplanatoryAttributes());
		Double scaledValue = 0.0;
		
		for(int i = 0; i < getNumberOfExplanatoryAttributes(); i++)
		{
			//Controllo che sia una variabile Discreta, in tal caso la aggiungo 
			//senza ulteriori istruzioni
			if(explanatorySet.get(i) instanceof DiscreteAttribute)
			{
				temp.set(e.get(i), i);
			}
			//Controllo che sia una variabile Continua, in questo caso invece
			//scalo il valore dell'esempio e in posizione i, e lo aggiungo all'example temp
			else if(explanatorySet.get(i) instanceof ContinuousAttribute)
			{
				scaledValue = ((ContinuousAttribute) explanatorySet.get(i)).scale((Double) e.get(i));
				temp.set(scaledValue, i);
			}
		}
		return temp;
	}

	/**
	 * Sovraccarico dell'operatore toString di Data per permetterci di stampare 
	 * a video le informazioni di data
	 */
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		Iterator<Example> dataIterator = data.iterator();
		Iterator<Double> targetIterator = target.iterator();

		for (int i = 0; i < data.size(); i++) 
		{
			sb.append("[" + (i + 1) + "]" + "	");
			sb.append(dataIterator.next().toString());
			sb.append("," + targetIterator.next());
			sb.append("\n");
		}

		return sb.toString();
	}

	/**
	 * Main "giocattolo" che ci permette di testare l'acquisizione di Data
	 * 
	 * @param args Argomenti del main
	 * @throws FileNotFoundException Lanciata quando il file non è trovato
	 * @throws TrainingDataException Lanciata quando c'è un errore di calcolo nel database
	 */
	public static void main(String args[]) throws FileNotFoundException, TrainingDataException 
	{
		Data trainingSet = new Data("provaC.dat");
		System.out.println(trainingSet);
	}

}
