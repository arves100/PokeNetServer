package org.pokenet.server.network;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.pokenet.server.GameServer;

/**
 * A class for handling MySQL Instance (why does the server need to make 99 new connections?)
 * @author Arves100
 *
 */
public class MySqlInstance {
	private static MySqlManager manager = new MySqlManager();
	private static boolean isConnected = false;
	
	public static boolean connect() {
		if (isConnected)
			return false; // -_-
		
		if (!manager.connect(GameServer.getDatabaseHost(), GameServer.getDatabasePort(), GameServer.getDatabaseUsername(), GameServer.getDatabasePassword()))
			return  false;
		
		if (!manager.selectDatabase(GameServer.getDatabaseName()))
			return false;
		
		isConnected = true;
		
		return true;
	}
	
	public static boolean isConnected() {
		return isConnected;
	}
	
	public static boolean close() {
		if (!isConnected)
			return false; // -_-
		
		if (!manager.close())
			return false;
		
		isConnected = false;
		return true;
	}
	
	public static ResultSet query(String query) throws SQLException {
		if (!isConnected)
			return null;
		
		return manager.query(query);
	}
	
	public static String parseSQL(String text) {
		return MySqlManager.parseSQL(text);
	}
}
