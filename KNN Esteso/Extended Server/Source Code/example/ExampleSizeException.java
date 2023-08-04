package example;

/**
 * Eccezione che viene lanciata quando c'è un problema con un esempio, in particolare la sua dimensione
 */
public class ExampleSizeException extends RuntimeException
{
	/**
	 * ID che serve ad indentificare la serializzazzione e la deserializzazione
	 */
	private static final long serialVersionUID = 199159425707693344L;
	
	/**
	 * Costruttore di default dell'eccezione 
	 */
	public ExampleSizeException() {};
	
	/**
	 * Costruttore custom dell'eccezzione con messaggio personalizzato
	 * 
	 * @param msg Messaggio da stampare
	 */
	public ExampleSizeException(String msg) 
	{
		super(msg);
	};
}
