package com.example.TenderManagementSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.sql.*;

@SpringBootApplication
public class TenderManagementSystemApplication {
	public static void create_db() {
		String url = "jdbc:mysql://127.0.0.1:3306/";
		String user = "root";
		String password = "PASSWORD";

		try (Connection conn = DriverManager.getConnection(url, user, password);
			 Statement stmt = conn.createStatement()) {

			String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS TenderManagementSystem";
			stmt.executeUpdate(createDatabaseSQL);
			System.out.println("Database created.");

			String createTableSQL_1 = "CREATE TABLE IF NOT EXISTS TenderManagementSystem.login " +
					"(id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(255) NOT NULL UNIQUE, password VARCHAR(255) NOT NULL, role VARCHAR(100) NOT NULL)";
			stmt.executeUpdate(createTableSQL_1);
			System.out.println("Table 'login' created.");

			String createTableSQL_2 = "CREATE TABLE IF NOT EXISTS TenderManagementSystem.Vendor " +
					"(id INT AUTO_INCREMENT PRIMARY KEY, vendor_id VARCHAR(255) NOT NULL UNIQUE, name VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL, mobile_number VARCHAR(15) NOT NULL,company_name VARCHAR(100) NOT NULL ,address TEXT,approved BOOLEAN DEFAULT FALSE)";
			stmt.executeUpdate(createTableSQL_2);
			System.out.println("Table 'Vendor' created.");

			String createTableSQL_3 = "CREATE TABLE IF NOT EXISTS TenderManagementSystem.Bidder " +
					"(id INT AUTO_INCREMENT PRIMARY KEY, vendor_id VARCHAR(255) NOT NULL UNIQUE, name VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL, mobile_number VARCHAR(15) NOT NULL,company_name VARCHAR(100) NOT NULL,tender_id VARCHAR(255) NOT NULL,bidding_amt int,solution TEXT,approved BOOLEAN DEFAULT FALSE)";
			stmt.executeUpdate(createTableSQL_3);
			System.out.println("Table 'Bidder' created.");

			String createTableSQL_4 = "CREATE TABLE IF NOT EXISTS TenderManagementSystem.Tender " +
					"(id INT AUTO_INCREMENT PRIMARY KEY, tender_id VARCHAR(255) NOT NULL UNIQUE, title VARCHAR(255) NOT NULL, description TEXT, deadline DATE, status VARCHAR(100))";
			stmt.executeUpdate(createTableSQL_4);
			System.out.println("Table 'Tender' created.");

		} catch (SQLException ex) {
			System.out.println("Error creating database or tables: " + ex.getMessage());
		}
	}

	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Fail_1");
			return;
		}

		try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/TenderManagementSystem", "root", "1471");
			 Statement stmt = conn.createStatement();
			 ResultSet rset = stmt.executeQuery("SELECT * FROM bidder")) {
		} catch (SQLException ex) {
			create_db();
		}
		SpringApplication.run(TenderManagementSystemApplication.class, args);
	}

}
