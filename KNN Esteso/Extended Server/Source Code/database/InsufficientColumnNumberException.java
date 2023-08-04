package database;

/**
 * Eccezione che viene lanciata quando il nome della tabella non è contenuto in MySQL
 */
public class InsufficientColumnNumberException extends Exception 
{
	/**
	 * ID che serve ad indentificare la serializzazzione e la deserializzazione
	 */
	private static final long serialVersionUID = -5659728327270775670L;

	/**
	 * Costruttore dell'eccezione con messaggio personalizzato
	 * 
	 * @param msg Messaggio da stampare
	 */
	public InsufficientColumnNumberException(String msg) {super(msg);}
}
