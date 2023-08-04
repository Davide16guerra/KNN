package data;
import java.io.Serializable;

/**
 * Classe attributo generico che implementa l'interfaccia Serializable 
 * per poter esere serializzato su file binario
 *
 */
public abstract class Attribute implements Serializable  //Abstract perchè verrà successivamente specializzata
{
	/**
	 * ID che serve ad indentificare la serializzazzione e la deserializzazione
	 */
	private static final long serialVersionUID = -5673306298276305493L;
	
	/**
	 * Nome della "colonna" delle variabili indipendenti
	 */
	private String name; 
	
	/**
	 * Indice della colonna
	 */
	private int index;	
		
	/**
	 * Costruttore di Attribute
	 * 
	 * @param name Nome della colonna
	 * @param index Indice della colonna
	 */
	Attribute(String name, int index)
	{
		this.name = name;
		this.index = index;
	}
	
	/**
	 * Metodo che resistituisce il nome della colonna
	 * 
	 * @return Il nome della colonna
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Metodo che resistituisce l'indice della colonna
	 * 
	 * @return Numero della colonna
	 */
	int getIndex()
	{
		return index;
	}
}
