package com.example.TenderManagementSystem.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Controller
public class RegistrationController {

    // Display the registration page
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/vendorRegister")
    public String registerVendor(@RequestParam String vid,
                                 @RequestParam String vname,
                                 @RequestParam String vemail,
                                 @RequestParam String vmobile,
                                 @RequestParam String vcompany_name,
                                 @RequestParam String vaddress,
                                 @RequestParam String vpassword) {

        String url = "jdbc:mysql://127.0.0.1:3306/TenderManagementSystem";
        String user = "root";
        String dbPassword = "1471";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword)) {

            String vendorInsertQuery = "INSERT INTO Vendor (vendor_id, name, email, mobile_number, company_name, address) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(vendorInsertQuery)) {
                pstmt.setString(1, vid);
                pstmt.setString(2, vname);
                pstmt.setString(3, vemail);
                pstmt.setString(4, vmobile);
                pstmt.setString(5, vcompany_name);
                pstmt.setString(6, vaddress);
                pstmt.executeUpdate();
                System.out.println("Vendor registered: " + vname + ", Address: " + vaddress);
            }

            String loginInsertQuery = "INSERT INTO Login (username, password, role) VALUES (?, ?, 'Vendor')";
            try (PreparedStatement pstmt = conn.prepareStatement(loginInsertQuery)) {
                pstmt.setString(1, vid);
                pstmt.setString(2, vpassword);
                pstmt.executeUpdate();
            }

        } catch (SQLException ex) {
            System.out.println("Error inserting the data: " + ex.getMessage());
        }
        return "redirect:/";
    }
}
