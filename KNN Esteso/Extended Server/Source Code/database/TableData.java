package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import example.Example;

/**
 * Classe che si occupa di acquisire i dati dal Database MySQL
 */
public class TableData 
{
	/**
	 * Accesso al database
	 */
	private DbAccess db;
	
	/**
	 * Nome della tabella
	 */
	private String table;
	
	/**
	 * Tabella di conversione MySQL
	 */
	private TableSchema tSchema;
	
	/**
	 * Lista che fa da tramite per l'acquisizione degli Example dal database
	 */
	private List<Example> transSet;
	
	/**
	 * Lista che fa da tramite per l'acquisizione del target dal database
	 */
	private List<Object> target;
	
	
	/**
	 * Costruttore di TableData
	 * 
	 * @param db Accesso al database
	 * @param tSchema Schema di conversione di MySQL
	 * @throws SQLException Lanciata quando ci sono errori logici in MySQL
	 * @throws InsufficientColumnNumberException Lanciata quando il nome della tabella non è corretto
	 */
	public TableData(DbAccess db, TableSchema tSchema) throws SQLException,InsufficientColumnNumberException
	{
		this.db = db;
		this.tSchema = tSchema;
		this.table = tSchema.getTableName();
		transSet = new ArrayList<Example>();
		target = new ArrayList<Object>();	
		init();
	}
	
	/**
	 * Metodo che popola gli attributi di classe tramite il database MySQL
	 * 
	 * @throws SQLException Lanciata quando ci sono errori logici in MySQL
	 */
	private void init() throws SQLException
	{		
		//Inizio la costrunzione della query con: "SELECT" 
		String query = "select ";
		
		//Iterator<Column> it=tSchema.iterator();
		
		//Ciclo per formare la stringa query: SELECT nomeColonna1, nomeColonna2, ..., nomeColonnaN;
		for(Column c:tSchema)
		{			
			//Equivalente di query = query + ... (In questo modo faccio il cast in automatico)
			query += c.getColumnName(); 
			query += ",";
		}
		
		//Continuo la formazione della query con la destinazione della query + 
		//il nome della colonna target: FROM nomeTabella
		query += tSchema.target().getColumnName();
		query += (" FROM "+ table);
		

		//Creo una variabile statement per ottenere la connessione
		Statement statement = db.getConnection().createStatement();
		//Tramite la connessione precedentemente creata interrogo il database con la query che
		//ho precedentemente creato
		ResultSet rs = statement.executeQuery(query);
		
		//Eseguo finchè nel database esiste una tupla successiva a quella attuale
		while (rs.next()) 
		{
			Example currentTuple = new Example(tSchema.getNumberOfAttributes());
			
			int i=0;
			for(Column c:tSchema) 
			{
				//Controllo se l'elemento in "rs", in posizione i + 1, sia un numero oppure no
				if(c.isNumber())
					//Se è un numero estrapolo tramite il metodo getDouble, un oggetto Double
					currentTuple.set(rs.getDouble(i+1),i);
				else
					//Altrimenti estrapolo tramite un altro metodo getString, 
					//la lettera o la stringa della colonna
					currentTuple.set(rs.getString(i+1),i);
				i++;
			}
			
			//Aggiungo l'Example currentTuple all'ArrayList transSet 
			transSet.add(currentTuple);
			
			//Controllo che tSchema.target(), cioè la colonna target, sia un numero
			if(tSchema.target().isNumber())
				target.add(rs.getDouble(tSchema.getNumberOfAttributes() +1));
			else
				target.add(rs.getString(tSchema.getNumberOfAttributes() +1));
			
			// Il +1, serve perchè, quando restituivo tSchema.getNumberOfAttributes(),
			// restituiva si, il numero totale degli attributi, ma tSchema non ha al suo interno anche target
			// quindi mancava letteralmente un +1 che rappresentava la posizione di target, dunque con quel "+1"
			// prendo proprio la posizione corretta del getDouble di rs.
			// P.S. Avrebbe funzionato se non fosse stato MySQL, ma proprio perchè in MySQL gli array
			// partono da 1, la modifica di quel +1 è essenziale
		}
		
		//Chiudo le risorse aperte
		rs.close();
		statement.close();
	}
	
	/**
	 * Metodo per calcolare, data una colonna in input il suo massimo oppure il suo minimo valore
	 * 
	 * @param column Colonna di cui trovare il minimo oppure il massimo
	 * @param aggregate Specificazione del se si tratta del minimo oppure del massimo
	 * @return Il minimo oppure il massimo della colonna in input
	 * @throws DatabaseConnectionException Lanciata se ci sono problemi di connessione con MySQL
	 */
	public Object getAggregateColumnValue(Column column, QUERY_TYPE aggregate) throws DatabaseConnectionException
	{
		//Credo un'pggetto Object temporaneo d'appoggio
		Object temp = null;
		
		try
		{
			// Costruzione della query tramite aggregate, in input a questo metodo 
			// passerò qualcosa del tipo QUERY_TYPE.MAX, ed in base al .MAX o al .MIN che farò seguire
			// al QUERY_TYPE in input, nella stringa cambierà quello che voglio calcolare, cioè o
			// il minimo oppure il massimo
			String query = "SELECT " + aggregate + "(" + column.getColumnName() + ")" + " FROM " + table + ";";
			
			//Come in init() creo la connessione tramite la quale sarò in grado di popolare rs
			//grazie all'esecuzione della query precedentemente creata
			Statement statement = db.getConnection().createStatement();
			ResultSet rs = statement.executeQuery(query); 
			
			//rs.next(), è l'indicatore ad andare alla riga 0 ed iniziare la scansione, anzichè alla riga -1
			rs.next(); 
			
			//Attribuisco a temp l'ggetto in prima posizione di rs
			// P.S. Gli array in MySQL partono da 1, quindi si, 1 è la prima posizione
			temp = rs.getObject(1);
			
			//Chiudo le risorse
			statement.close();
			rs.close();
		}
		catch(SQLException err)
		{
			throw new DatabaseConnectionException("Errore del calcolo del Min/Max");
		}

		return temp;
	}

	/**
	 * Metodo che resistituisce la lista transSet
	 * 
	 * @return Lista transSet
	 */
	public List<Example> getExamples()
	{
		return transSet; 
	}
	
	/**
	 * Metodo che resistituisce la lista target
	 * 
	 * @return Lista target
	 */
	public List<Object> getTargetValues()
	{
		return target; 
	}
	
	/**
	 * Main di testing delle corrette connessioni al database
	 * 
	 * @param args Argomenti del main
	 * @throws DatabaseConnectionException Lanciata se non è possibile stabilire una connessione con MySQL
	 * @throws SQLException Lanciata se ci sono errori di acquisizione di MySQL
	 * @throws InsufficientColumnNumberException Lanciata se il nome della tabella è errato
	 */
	public static void main(String[] args) throws DatabaseConnectionException, SQLException, InsufficientColumnNumberException
	{	
		DbAccess db = new DbAccess();
		TableSchema ts = new TableSchema("provac", db);
		TableData td = new TableData(db, ts);
		
		System.out.println(td.getExamples().toString());
		System.out.println(td.getTargetValues().toString());
	}
}
