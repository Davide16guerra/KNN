package example;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe che incapsula un Arralyst di oggetti generici, oggetti che diventeranno attributi
 * continui oppure discreti, implementa l'interfaccia Serializable per poter essere serializzata
 * su file bianrio
 *
 */
public class Example implements Serializable
{
	/**
	 * ID che serve ad indentificare la serializzazzione e la deserializzazione
	 */
	private static final long serialVersionUID = 2600624355186591152L;
	
	/**
	 * Arralist di oggetti generici Object
	 */
	private ArrayList<Object> example;  
	

	/**
	 * Costruttore di Example che instanza l'arraylist con la dimensione fornita in input
	 * 
	 * @param size Dimensione dell'esempio da costruire
	 */
	public Example(int size)
	{
		example = new ArrayList<Object>(size);
	}
	
	/**
	 * Metodo che assegna ad example i valori
	 * 
	 * @param o Oggetto da inserire dell'Example
	 * @param index Posizione in cui inserire l'oggetto "o" in Example
	 */
	public void set(Object o, int index)
	{
		//Se l'indice che dò in input è più piccolo della dimensione allora lo assegno in
		//una posizione precisa, altrimenti semplicemente aggiungo l'oggetto.
		if(index < example.size())
		{
			try
			{	
				example.set(index, o);
			}
			catch(IndexOutOfBoundsException err)
			{
				err.getMessage();
			}
		}
		else
		{
			example.add(o);
		}
	}
	
	/**
	 * Metodo che restituisce l'oggetto in example in posizione index
	 * 
	 * @param index Posizione nella quale acquisire l'oggetto
	 * @return Oggetto in posizione "index"
	 */
	public Object get(int index)
	{
		return example.get(index);
	}
	
	/**
	 * Metodo che serve a scambiare le posizioni degli oggetti da example ad example "e" in input
	 * 
	 * @param e Esempio da swappare con quello implicito
	 */
	public void swap(Example e)
	{
		//Lancio un'eccezzione se le dimensioni dei due esempi non corrispondono
		if(example.size() != e.example.size())
		{
			throw new ExampleSizeException();
		}
		
		Object temp; //Oggetto generico d'appoggio per lo swap
		
		for(int i = 0; i < example.size(); i++)
		{
			temp = e.get(i);
			e.set(example.get(i), i);
			example.set(i, temp);
		}
	}
	
	/**
	 * Metodo che calcola la distanza tra due esempi, il primo è con il parametro implicito
	 * ed il secondo con il parametro "e" in input
	 * 
	 * @param e Esempio con la quale calcolare la distanza con quello implicito
	 * @return Distanza dei due oggetti
	 */
	public double distance(Example e)
	{
		//Lancio un'eccezzione se le dimensioni dei due esempi non corrispondono
		if(example.size() != e.example.size())
		{
			throw new ExampleSizeException();
		}
		
		double result = 0;
		
		//Fichè tutte le "colonne" sono finite
		for(int i = 0; i < example.size(); i++)
		{
			//Entro in quest'if se l'oggetto all'iesima posizione di example
			//e all'iesima posizione di e, è una instanza di String
			if(example.get(i) instanceof String && e.get(i) instanceof String)
			{
				//In quest'if basta solo che le stringhe siano diverse per fare
				//incrementare di 1 il risultato "result"
				if(!example.get(i).equals(e.get(i)))
				{
					result++;
				}
			}
			//Altrimenti entro in quest'if se l'oggetto all'iesima posizione di example
			//e all'iesima posizione di e, è una instanza di Double
			else if(example.get(i) instanceof Double && e.get(i) instanceof Double)
			{
				//Assegno al risultato il valore di sè stesso con l'aggiunta della distanza
				//dalle variabili indipendenti numeriche, inoltre eseguo due casting a Double
				//Per indicargli che, l'oggetto all'iesima posizione è proprio di tipo Double
				result = result + Math.abs((((Double) example.get(i)) - ((Double) e.get(i))));
			}
		}
		
		return result;
	}
	
	/**
	 * Sovraccarico del metodo toString che permette di stampare un Example
	 */
	@Override
	public String toString()
	{
		//Uso StringBuilder per concatenare dentro un StringBuilder 
		//tutto quello che vogliamo stampare
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");  //.append è il metodo per "aggiungere" stringhe all'interno di sb
		
		for(int i = 0; i < example.size(); i++)
		{
			sb.append(example.get(i));
			if(i < example.size() - 1)
			{
				sb.append(", ");
			}
		}
		
		sb.append("]");
		
		return sb.toString();
	}
}
