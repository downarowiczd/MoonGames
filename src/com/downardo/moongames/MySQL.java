package com.downardo.moongames;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;

public class MySQL {
	
	
    public static Connection con;
    
    public static void connect(){
           
            try {
                    con = DriverManager.getConnection("jdbc:mysql://" + MoonGames.host + ":3306/"+ MoonGames.db + "?autoReconnect=true",MoonGames.user,MoonGames.pass);
                    System.out.println(MoonGames.moonGames + "Du hast erfolgreich die Verbindung zum Server hergestellt.");
                   
            } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println(MoonGames.moonGames + "Konnte keine Verbindung zur Datenbank herstellen.");
                    Bukkit.getServer().shutdown();
            }
           
    }
   
    public static void close(){
           
            if(con != null){
                    try {
                            con.close();
                    } catch (SQLException e) {
                            e.printStackTrace();
                    }
            }
           
    }
   
    public static void Update(String qry){  
    	try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(qry);
    	} catch (SQLException e) {
            e.printStackTrace();
    }
       
}
    
    
 
   
    public static ResultSet Query(String qry){
            ResultSet rs = null;
           
            try {
                    Statement stmt = con.createStatement();
                    rs = stmt.executeQuery(qry);
            } catch (SQLException e) {
                    e.printStackTrace();
            }
           
            return rs;
    }

    
    

}