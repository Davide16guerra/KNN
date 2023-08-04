package database;

/**
 * Eccezione lanciata quando ci sono problemi di connessione con il database MySQL
 */
public class DatabaseConnectionException extends Exception 
{
	/**
	 * ID che serve ad indentificare la serializzazzione e la deserializzazione
	 */
	private static final long serialVersionUID = -545896439327900355L;

	/**
	 * Costruttore dell'eccezione che mostra a schermo un messaggio definito
	 * 
	 * @param msg Messaggio da stampare
	 */
	DatabaseConnectionException(String msg)
	{
		super(msg);
	}
}
