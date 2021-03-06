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

public class Hotel
{
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/hotel";
	
	//static final String DB_URL = "jdbc:mysql://localhost/hotel?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "";

	/**
	 * Reads the passed-in file
	 * for analysis <which will be
	 * used for the requests and queries>
	 * 
	 * @param filename File to be read
	 * @return Array containing requests or queries
	 * @throws IOException
	 */
	public static String[] read(String filename) throws IOException
	{
		File file = new File(filename);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		String temp = "";
		
		while ((line = bufferedReader.readLine()) != null)
		{
			temp = temp + line;
		}

		fileReader.close();
		
		if (temp.indexOf(";") != -1)
		{
			return temp.split(";");
		}
		return temp.split("\n");
	}

	/**
	 * Establishes a connection to the database
	 * and executes the query to be able to get a result
	 * 
	 * @param query MySQL query to be executed
	 * @param type Return type of the executed query
	 * @param seperator Comma used as a default
	 * @return
	 */
	public static ArrayList<String> execute(String query, String type, String seperator)
	{
		String[] types = type.split(",");
		ArrayList<String> result = new ArrayList<String>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try
		{
			// STEP 2: Register JDBC driver (automatically done since JDBC 4.0)
			Class.forName("com.mysql.cj.jdbc.Driver");

			// STEP 3: Open a connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// STEP 4: Execute a query
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			// //STEP 5: Process the results
			while (rs.next())
			{
				String temp = "";
				for (int i = 0; i < types.length; i++)
				{
					if (types[i].indexOf("int") != -1)
						temp += rs.getInt(i + 1);
					else if (types[i].indexOf("str") != -1)
						temp += rs.getString(i + 1);
					else
						temp += rs.getDouble(i + 1);
					temp += seperator;
				}
				result.add(temp);
			}
		}
		catch (SQLException se)
		{
			// Handle errors for JDBC
			se.printStackTrace();
		}
		catch (Exception e)
		{
			// Handle errors for Class.forName
			e.printStackTrace();
		}
		finally
		{
			// finally block used to close resources
			try
			{
				if (stmt != null)
					stmt.close();
			}
			catch (SQLException se2)
			{
				// nothing we can do
			}
			try
			{
				if (conn != null)
					conn.close();
			}
			catch (SQLException se)
			{
				se.printStackTrace();
			} // end finally try
		} // end try

		return result;
	}

	/**
	 * Executes an update on the tables that
	 * contain a specific updating query that
	 * is passed-in
	 * 
	 * @param query Specified query that will be executed
	 * @param message System message that will be displayed
	 */
	public static void executeUpdate(String query, String message)
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			// STEP 2: Register JDBC driver (automatically done since JDBC 4.0)
			Class.forName("com.mysql.cj.jdbc.Driver");

			// STEP 3: Open a connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// STEP 4: Execute a query
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
			System.out.println(message);

		}
		catch (SQLException se)
		{
			// Handle errors for JDBC
			se.printStackTrace();
		}
		catch (Exception e)
		{
			// Handle errors for Class.forName
			e.printStackTrace();
		}
		finally
		{
			// finally block used to close resources
			try
			{
				if (stmt != null)
					stmt.close();
			}
			catch (SQLException se2)
			{
				// nothing we can do
			}
			try
			{
				if (conn != null)
					conn.close();
			}
			catch (SQLException se)
			{
				se.printStackTrace();
			} // end finally try
		} // End of try block
	}

	/**
	 * Retrieves the query to be executed
	 * and eventually, will executed
	 * 
	 * @param query Query to be executed
	 * @param attributes SQL statements
	 * @param type Return type of the executed query
	 * @param separator Comma used as a default
	 */
	public static void getQuery(String query, String attributes, String type, String separator)
	{
		String[] attrs = attributes.split(",");
		ArrayList<String> result = execute(query, type, separator);
		for (String att : attrs)
			System.out.print(att + " \t");
		System.out.println();
		for (String res : result)
		{
			String[] temp = res.split(separator);
			for (String t : temp)
				System.out.print(t + "\t\t");
			System.out.println();
		}
	}

	/**
	 * By default, in our options file
	 * we have options for display and
	 * at the disposal of the user
	 * This function displays them in the console
	 * 
	 * @param requests Array containing
	 * the requests for formatting and display
	 */
	public static void displayOption(String[] requests)
	{
		int count = 1;
		System.out.println("Please choose one from these options: \n");
		
		for (String r : requests)
		{
			System.out.println(count + ") " + requests[count - 1]);
			count++;
		}
	}

	/**
	 * Function to recognize the option to be processed
	 * in this case, there are updates and regular
	 * read queries, this function can separate the two
	 * according to the users request
	 * 
	 * @param option Query number in the console to
	 * be executed
	 * 
	 * @param queries List of queries available
	 */
	public static void processOption(int option, String[] queries)
	{
		if (option == 0)
			return;

		String query = queries[option - 1];
		String seperator = ",";
		String attr = "";
		String type = "";
		String message = "";
		boolean update = false;
		switch (option)
		{
		case 1:
			attr = "Available room";
			type = "int";
			break;
		case 2:
			attr = "Available single rooms";
			type = "int";
			break;
		case 3:
			attr = "Num of customer";
			type = "int";
			break;
		case 4:
			attr = "Average age";
			type = "float";
			break;
		case 5:
			attr = "Max age";
			type = "int";
			break;
		case 6:
			attr = "Min age";
			type = "int";
			break;
		case 7:
			attr = "Average price of rooms on 1st floor";
			type = "float";
			break;
		case 8:
		case 9:
		case 15:
			attr = "Num rooms";
			type = "int";
			break;
		case 10:
		case 17:
			update = true;
			message = "Successfully modified the databased";
			break;
		case 11:
			attr = "Room number,Penalty Fee";
			type = "int,float";
			break;
		case 12:
		case 13:
			attr = "price";
			type = "float";
			break;
		case 14:
		case 16:
			attr = "roomnumber";
			type = "int";
			break;
		case 18:
			attr = "Customer's Name";
			type = "str";
			break;
		case 19:
			attr = "uId,\tuName,     age,\tuId,room number,\tcheckin date, \tcheckout date";
			type = "str,str,str,str,int,str,str";
			break;
		}
		if (update == false)
			getQuery(query, attr, type, seperator);
		else
			executeUpdate(query, message);
	}

	public static void main(String[] args) throws IOException, SQLException
	{
		String[] requests = read("requests.txt");
		String[] queries = read("queries.txt");

		System.out.println("\t\t\t\t\t\t============================");
		System.out.println("\t\t\t\t\t\tWELCOME TO THE GRAND HOTEL");
		System.out.println("\t\t\t\t\t\t============================");
		
		displayOption(requests);
		System.out.println();
		Scanner scan = new Scanner(System.in);
		int option = 1;
		
		while (option != 0)
		{
			System.out.print("Enter your option (from 1 - " + queries.length + ", enter 0 to stop): ");
			option = Integer.parseInt(scan.nextLine());
			processOption(option, queries);
			System.out.println();
		}
		
		System.out.println("Goodbye!");
		scan.close();
	}
}