package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Gestisce l'accesso al DB per la lettura dei dati di training
 * @author Map Tutor
 *
 */
public class DbAccess {

	/**
	 * Nome del driver jdbc
	 */
	@SuppressWarnings("unused")
	private final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
	
	/**
	 * Tipologia di database SQL
	 */
	private final String DBMS = "jdbc:mysql";
	
	/**
	 * Nome del server
	 */
	private final String SERVER = "localhost";
	
	/**
	 * Numero della porta
	 */
	private final int PORT = 3306;
	
	/**
	 * Nome del Database
	 */
	private final String DATABASE = "Map";
	
	/**
	 * Nome utente di MySQL
	 */
	private final String USER_ID = "Student";
	
	/**
	 * Password utente di MySQL
	 */
	private final String PASSWORD = "map";

	/**
	 * Connessione al Database
	 */
	private Connection conn;

	/**
	 * Metodo che inizializza una connessione al DB
	 * 
	 * @throws DatabaseConnectionException Lanciata quando ci sono problemi di connessione con MySQL
	 */
	public DbAccess() throws DatabaseConnectionException{
		String connectionString =  DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE
				+ "?user=" + USER_ID + "&password=" + PASSWORD + "&serverTimezone=UTC";
	
		try {
			conn = DriverManager.getConnection(connectionString, USER_ID, PASSWORD);
			
		} catch (SQLException e) {
			System.out.println("Impossibile connettersi al DB");
			e.printStackTrace();
			throw new DatabaseConnectionException(e.toString());
		}
		
	}
	
	/**
	 * Metodo che restituisce la connessione del database
	 * 
	 * @return Connessione al database
	 */
	public  Connection getConnection(){
		return conn;
	}

	/**
	 * Metodo che chiude la connessione al database
	 */
	public  void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println("Impossibile chiudere la connessione");
		}
	}
}
