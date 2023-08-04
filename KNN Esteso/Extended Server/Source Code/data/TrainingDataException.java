package data;

/**
 * Eccezione che viene lanciata quando ci sono problemi di lettura
 * oppure logici al Training Set
 */
public class TrainingDataException extends Exception
{
	/**
	 * ID che serve ad indentificare la serializzazzione e la deserializzazione
	 */
	private static final long serialVersionUID = -3122533967106886225L;
	
	/**
	 * Costruttore dell'eccezzione di default
	 */
	public TrainingDataException() {};
	
	/**
	 * Costruttore dell'eccezzione con messaggio definito
	 * 
	 * @param msg Messaggio da mostrare nell'eccezzione
	 */
	public TrainingDataException(String msg) { super(msg); };
}
