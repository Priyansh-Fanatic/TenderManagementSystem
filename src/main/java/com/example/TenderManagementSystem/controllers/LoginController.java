package com.example.TenderManagementSystem.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/TenderManagementSystem";
    private static final String USER = "root";
    private static final String DB_PASSWORD = "1471";

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        return "login"; // returns the login.html template
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalidate the session to log out
        return "redirect:/"; // Redirect to homepage after logout
    }

    @PostMapping("/login/vendor")
    public String processVendorLogin(@RequestParam String username, @RequestParam String password, HttpSession session) {
        try (Connection conn = DriverManager.getConnection(URL, USER, DB_PASSWORD)) {
            String query = "SELECT username FROM Login WHERE username = ? AND password = ? AND role = 'Vendor'";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {

                    logger.info("Vendor logged in: {}", username);
                    String vendorId = rs.getString("username"); // Get vendor_id from result set
                    session.setAttribute("username", username);
                    session.setAttribute("isLoggedIn", username); // Set session attribute for login status

                    String approvalQuery = "SELECT approved FROM vendor WHERE vendor_id = ?";
                    try (PreparedStatement approvalStmt = conn.prepareStatement(approvalQuery)) {
                        approvalStmt.setString(1, vendorId); // Use vendorId from login query
                        ResultSet approvalRs = approvalStmt.executeQuery();

                        if (approvalRs.next()) {
                            boolean isApproved = approvalRs.getBoolean("approved");
                            session.setAttribute("isApproved", isApproved);
                            session.setAttribute("vendorId", vendorId);
                        } else {
                            logger.warn("No approval status found for vendor: {}", username);
                            session.setAttribute("isApproved", false); // Default to not approved if not found
                        }
                    }

                    return "redirect:/";
                } else {
                    return "redirect:/login?error=true";
                }
            }

        } catch (SQLException ex) {
            logger.error("Error during vendor login: {}", ex.getMessage());
            return "redirect:/login?error=true"; // Redirect back to login with error
        }
    }

    @PostMapping("/login/admin")
    public String processAdminLogin(@RequestParam String username, @RequestParam String password, HttpSession session) {
        if ("Admin".equals(username) && "1234".equals(password)) {
            // Successful admin login
            logger.info("Admin logged in: {}", username);
            session.setAttribute("username", username); // Set session attribute for admin login
            return "redirect:/admin"; // Redirect to admin dashboard on successful login
        } else {
            // Invalid credentials
            return "redirect:/login?error=true"; // Redirect back to login with error
        }
    }
}