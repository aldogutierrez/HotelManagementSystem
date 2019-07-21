//STEP 1. Import required packages
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import com.mysql.cj.protocol.Resultset;

public class hotel {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:mysql://localhost/hotel";

   //  Database credentials
   static final String USER = "root";
   static final String PASS = "";
   
   public static String[] read(String filename) throws IOException
	{
		File file = new File(filename);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		StringBuffer stringBuffer = new StringBuffer();
		String line;
		String temp = "";
		while ((line = bufferedReader.readLine()) != null) {
			temp = temp + line;
			
		}
		fileReader.close();
		if (temp.indexOf(";") != -1)
			return temp.split(";");
		return temp.split("\n");
	}
   
   public static ArrayList<String> execute(String query, String type, String seperator)
   {
	   String[] types = type.split(",");
	   ArrayList<String> result = new ArrayList<String>();
	   Connection conn = null;
	   Statement stmt = null;
	   ResultSet rs = null;
	   try{
	      //STEP 2: Register JDBC driver (automatically done since JDBC 4.0)
	      Class.forName("com.mysql.cj.jdbc.Driver");

	      //STEP 3: Open a connection
	      conn = DriverManager.getConnection(DB_URL, USER, PASS);

	      //STEP 4: Execute a query
	      stmt = conn.createStatement();
	      rs = stmt.executeQuery(query);
//	      System.out.println(rs.getInt(0));
	      
//	      //STEP 5: Process the results
	      while(rs.next()){
	          String temp = "";
	          for (int i = 0; i < types.length; i++)
	          {
	        	  if (types[i].indexOf("int") != -1)
	        		  temp += rs.getInt(i+1);
	        	  else if (types[i].indexOf("str") != -1)
	        		  temp += rs.getString(i+1);
	        	  else
	        		  temp += rs.getDouble(i+1);
	        	  temp += seperator;
	          }
	          result.add(temp);
	      }
	      
	   }catch(SQLException se){
	      //Handle errors for JDBC
	      se.printStackTrace();
	   }catch(Exception e){
	      //Handle errors for Class.forName
	      e.printStackTrace();
	   }finally{
	      //finally block used to close resources
	      try{
	         if(stmt!=null)
	            stmt.close();
	      }catch(SQLException se2){
	      }// nothing we can do
	      try{
	         if(conn!=null)
	            conn.close();
	      }catch(SQLException se){
	         se.printStackTrace();
	      }//end finally try
	   }//end try
	   
	   return result;
   }
   
   public static void getQuery(String query, String attributes, String type, String seperator)
   {
	   String[] attrs = attributes.split(",");
	   ArrayList<String> result = execute(query, type, seperator);
	   for (String att: attrs)
		   System.out.print(att + " \t");
	   System.out.println();
	   for (String res: result)
	   {
		   String[] temp = res.split(seperator);
		   for (String t : temp)
			   System.out.print(t + "\t\t\t");
		   System.out.println();
	   }
   }
   
   public static void displayOption(String[] requests)
   {
	   int count = 1;
	   System.out.println("Please choose one from these options: ");
	   for (String r: requests)
	   {
		   System.out.println(count + ") " + requests[count - 1]);
		   count++;
	   }
   }
   
   public static void processOption(int option, String[] queries)
   {
	   if (option == 0)
		   return;
	   
	   String query = queries[option-1];
	   String seperator = ",";
	   String attr = "";
	   String type = "";
	   switch (option) {
	   case 1:
	   case 2:
		   attr = "roomnumber,bednumber";
		   type = "int,int";
		   break;
	   case 3:
		   attr = "customer";
		   type = "int";
		   break;
	   case 4:
		   attr = "average age";
		   type = "float";
		   break;
	   case 5:
		   attr = "max age";
		   type = "int";
		   break;
	   case 6:
		   attr = "min age";
		   type = "int";
		   break;
	   case 7:
		   attr = "average price";
		   type = "int";
		   break;
	   case 8:
	   case 9:
	   case 10:
	   case 15:
		   attr = "num rooms";
		   type = "int";
		   break;
	   case 11:
		   attr = "average price";
		   type = "float";
		   break;
	   case 12:
	   case 13:
		   attr = "price";
		   type = "float";
		   break;
	   case 14:
		   attr = "roomnumber";
		   type = "int";
		   break;
	   }
	   getQuery(query, attr, type, seperator);
	   
   }
   
   public static void main(String[] args) throws IOException, SQLException 
   {
	   String[] requests = read("requests.txt");
	   String[] queries = read("queries.txt");
	   
	   displayOption(requests);
	   System.out.println();
	   Scanner scan = new Scanner(System.in);
	   int option = 1;
	   while (option != 0)
	   {
		   System.out.print("Enter your option (from 1-" + queries.length + ", enter 0 to stop): ");
		   option = Integer.parseInt(scan.nextLine());
		   processOption(option, queries);
		   System.out.println();
	   }
	   System.out.println("Program ended");
   }
}