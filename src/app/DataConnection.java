package app;

import virtuoso.jena.driver.VirtGraph;

public class DataConnection {

	static protected String connectionString = "jdbc:virtuoso://83.212.121.252:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
	static protected String username = "dba";
	static protected String password = "olv@psnet@openlink69";
	
	public static VirtGraph getConnection(){
		return new VirtGraph (connectionString, username, password );		
	}
	
	public static VirtGraph getConnection(String namedGraph){
		return new VirtGraph (namedGraph, connectionString, username, password );		
	}
}
