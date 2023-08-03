package data;

/**
 * Classe che estende Attribute specializzandolo in attributo discreto, 
 * variabile indipendente testuale
 */
class DiscreteAttribute extends Attribute
{
	/**
	 * ID che serve ad indentificare la serializzazzione e la deserializzazione
	 */
	private static final long serialVersionUID = -5091749844520953832L;

	/**
	 * Costruttore di DiscreteAttribute
	 * 
	 * @param name Nome della colonna
	 * @param index Indice della colonna
	 */
	DiscreteAttribute(String name, int index)
	{
		super(name, index);
	}

}
