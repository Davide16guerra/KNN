package data;

/**
 * Classe che estende Attribute specializzandolo in attributo continuo, 
 * variabile indipendente numerica
 */
public class ContinuousAttribute extends Attribute
{
	/**
	 * ID che serve ad indentificare la serializzazzione e la deserializzazione
	 */
	private static final long serialVersionUID = -2203590574786776089L;
	
	//Mi salvo il minimo ed il massimo della variabile dipendete attuale, 
	//cos� da poter scalare anche il query point (esempio fornito dall'utente)
	/**
	 * Attributo del valore pi� piccolo della variabile indipendente attuale
	 */
	private double min;	
	/**
	 * Attributo del valore pi� grande della variabile indipendente attuale 
	 */
	private double max;
	
	/**
	 * Costruttore di ContinuousAttribute
	 * 
	 * @param name Nome della colonna
	 * @param index Indice della colonna
	 */
	ContinuousAttribute(String name, int index) 
	{
		super(name, index);
		
		min = Double.POSITIVE_INFINITY;  //Assegno a min il valore pi� grande possibile
		max = Double.NEGATIVE_INFINITY;  //Assegno a max il valore pi� piccolo possibile
	}
	
	/**
	 * Metodo che, ad ogni chiamata verifica se il valore passato in input sia pi� piccolo di quello
	 * gi� "immagazzinato" in ContinuousAttribute, ed in caso affermativo, lo aggiorna 
	 * 
	 * @param v Valore di cui verificare il minimo
	 */
	void setMin(Double v)
	{
		if(v < min)
		{
			min = v;
		}
	}
	
	/**
	 * Metodo che, ad ogni chiamata verifica se il valore passato in input sia pi� grande di quello
	 * gi� "immagazzinato" in ContinuousAttribute, ed in caso affermativo, lo aggiorna 
	 * 
	 * @param v Valore di cui verificare il massimo
	 */
	void setMax(Double v)
	{
		if(v > max)
		{
			max = v;
		}
	}
	
	/**
	 * Metodo che scala il valore double in input tenendo conto del massimo e del minimo della variabile
	 * indipendete attuale, in sostanza si tratta del Min/Max Scaler
	 * 
	 * @param value Valore da scalare
	 * @return Valore scalato
	 */
	double scale(Double value)
	{
		return (value - min) / (max - min);
	}

}
