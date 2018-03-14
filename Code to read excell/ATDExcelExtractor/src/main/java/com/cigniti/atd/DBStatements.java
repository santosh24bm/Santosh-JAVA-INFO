package com.cigniti.atd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;

import com.mysql.jdbc.DatabaseMetaData;

/**
 * @author Kumar Gaurav
 *
 */
public class DBStatements {

	public static final String TABLE_NAME = "DefectSummaryNew";

/**
 * This method will help to create table.
 * @param tablename
 * @param headerMap
 * @param headerTypeList
 * @return
 */
public static String getCreateTable(String tablename, LinkedHashMap<String, Integer> headerMap, List<Integer> headerTypeList){
	Iterator<String> iter = headerMap.keySet().iterator();
    String str="";
    String[] allFields = new String[headerMap.size()];
    int i = 0;
    while (iter.hasNext()){
        String fieldName = (String) iter.next();
        
        if(fieldName.matches(".*\\s+.*")){
        	fieldName = fieldName.replaceAll(" ", "_");
        }
        Integer fieldType = headerTypeList.get(i);

        switch (fieldType){
            case Cell.CELL_TYPE_NUMERIC:
                str=fieldName + " INTEGER";
                break;
            case Cell.CELL_TYPE_STRING:
                str= fieldName + " VARCHAR(255)";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                str=fieldName + " INTEGER";
                break;
        }
        allFields[i++]= str;
        System.out.println("all fields"+allFields);
    }
    try 
    {
    	Connection con = getDBConnection();
        Statement  stmt = con.createStatement();
        
        // create a schema if doesnt exists 
        int Result = stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS DEMONEW3");
     /*   System.out.println("db created "+Result);
        java.sql.DatabaseMetaData databaseMetaData = con.getMetaData();
        System.out.println("schema name >>>> "+databaseMetaData.getSchemas().getString(1));
         con = getNewDBConnection(databaseMetaData.getSchemas().getString(1));*/
         
         java.sql.DatabaseMetaData meta = con.getMetaData();
      
         ResultSet schemas = meta.getSchemas();
         while (schemas.next()) {
             String tableSchema = schemas.getString("TABLE_SCHEM");    // "TABLE_SCHEM"
             String tableCatalog = schemas.getString(2); //"TABLE_CATALOG"
             System.out.println("tableSchema"+tableSchema);
           }
         
        
        try
        {
            System.out.println( "Use the database..." );
            stmt.executeUpdate( "USE demo;" );
        }
        catch( SQLException e )
        {
            System.out.println( "SQLException: " + e.getMessage() );
            System.out.println( "SQLState:     " + e.getSQLState() );
            System.out.println( "VendorError:  " + e.getErrorCode() );
        }
        try
        {
            String all = org.apache.commons.lang3.StringUtils.join(allFields, ",");
            String createTableStr = "CREATE TABLE " + TABLE_NAME + " (" + all + ")";
            System.out.println(createTableStr);
            System.out.println( "Create a new table in the database" );
            
            if(!tableExist(con, TABLE_NAME)){
            	/*System.out.println("Dropping table "+TABLE_NAME);
            	stmt.executeUpdate( "DROP TABLE "+ TABLE_NAME);*/
            	System.out.println("Table "+TABLE_NAME+" is dropped and creating again");
            	stmt.executeUpdate( createTableStr );
            	System.out.println("Table "+TABLE_NAME+" is created successfully");
            }
        }
        catch( SQLException e )
        {
            System.out.println( "SQLException: " + e.getMessage() );
            System.out.println( "SQLState:     " + e.getSQLState() );
            System.out.println( "VendorError:  " + e.getErrorCode() );
        }
    }
    catch( Exception e )
    {
        System.out.println( ((SQLException) e).getSQLState() );
        System.out.println( e.getMessage() );
        e.printStackTrace();
    }
    return str; 
}
	

	
	/**
	 * This method will provide DB connection
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static Connection getNewDBConnection(String dbname) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/"+dbname+"", "root", "Ctl@1234");
		return con;
	}

	/**
	 * This method will provide DB connection
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static Connection getDBConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "Ctl@1234");
		return con;
	}

	public static void insertRowInDB(ArrayList<List<Object>> rowValueList) throws SQLException{
		Connection con;
		try {

		con = getDBConnection();
		Statement stmt = con.createStatement();
		con.setAutoCommit(false);
		
	    int rowSize = 0;
	    for(int i=0; i < rowValueList.size();i++){
	    	 List<Object> colValueList = rowValueList.get(i);
	    	 
	    	 if(colValueList.size() == 10){
	    		 
	    		 String insertQuery = "insert into "+TABLE_NAME+ " values ("+"'"+colValueList.get(0)+"','"+colValueList.get(1)+"','"+colValueList.get(2)
	    		 +"','"+colValueList.get(3)+"','"+colValueList.get(4)+"','"+colValueList.get(5)+"','"+colValueList.get(6)
	    		 +"','"+colValueList.get(7)+"','"
	    		 +colValueList.get(8)+"','"+colValueList.get(9)+"')";
	    		
	    		 System.out.println("statment is:-"+insertQuery);
	    		 stmt.executeUpdate(insertQuery);
		         con.commit();
	    		 
		         rowSize ++;
	    		 
		         System.out.println("Inserted record number is : "+rowSize);
	    	 }
	    }
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This method will help to check whether the table already exists or not
	 * @param conn
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static boolean tableExist(Connection conn, String tableName) throws SQLException {
		boolean tExists = false;
		try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null)) {
			while (rs.next()) {
				String tName = rs.getString("TABLE_NAME");
				if (tName != null && tName.equalsIgnoreCase(tableName)) {
					tExists = true;
					break;
				}
			}
		}
		return tExists;
	}
}
