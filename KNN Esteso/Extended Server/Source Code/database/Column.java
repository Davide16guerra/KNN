package database;

/**
 * Classe che contiene le informazioni delle colonne di una tabella MySQL
 */
public class Column
{
	/**
	 * Nome della colonna
	 */
	private String name;
	
	/**
	 * Tipo della colonna, discreto o continuo
	 */
	private String type;
	
	/**
	 * Costruttore che crea una nuova colonna con i parametri in input
	 * 
	 * @param name Nome della colonna
	 * @param type Tipo della colonna
	 */
	Column(String name,String type)
	{
		this.name=name;
		this.type=type;
	}
	
	/**
	 * Metodo che resituisce il nome della colonna
	 * 
	 * @return Nome della colonna
	 */
	public String getColumnName()
	{
		return name;
	}
	
	/**
	 * Metodo che resituisce se la colonna in questione, sia o meno un continuo o discreto 
	 * (Controlla se sia un numero)
	 * 
	 * @return true se è discreto, false se è continuo
	 */
	public boolean isNumber()
	{
		return type.equals("number");
	}
	
	/**
	 * toString sovraccaricato per poter stampare la colonna con le informazioni
	 */
	@Override
	public String toString()
	{
		return name+":"+type;
	}
}