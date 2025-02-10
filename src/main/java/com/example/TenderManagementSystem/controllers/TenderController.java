package com.example.TenderManagementSystem.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TenderController {

    private static final Logger logger = LoggerFactory.getLogger(TenderController.class);
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/TenderManagementSystem";
    private static final String USER = "root";
    private static final String DB_PASSWORD = "PASSWORD";

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("username") != null;
    }

    private boolean isApproved(HttpSession session) {
        Boolean isApproved = (Boolean) session.getAttribute("isApproved");
        return isApproved != null && isApproved;
    }

    @GetMapping("/tender")
    public String tender(Model model, HttpSession session) {
        List<Map<String, Object>> tenders = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            String query = "SELECT * FROM tender";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Map<String, Object> tender = new HashMap<>();
                tender.put("id", rs.getInt("id"));
                tender.put("tender_id", rs.getString("tender_id"));
                tender.put("title", rs.getString("title"));
                tender.put("description", rs.getString("description"));
                tender.put("deadline", rs.getDate("deadline"));
                tender.put("status", rs.getString("status"));
                tenders.add(tender);
            }

            model.addAttribute("tenders", tenders);
        } catch (SQLException e) {
            logger.error("Database error: {}", e.getMessage());
            model.addAttribute("error", "Database error: " + e.getMessage());
        }

        model.addAttribute("isLoggedIn", isLoggedIn(session));
        model.addAttribute("isApproved", isApproved(session));

        return "tender"; // renders tender.html
    }

    @GetMapping("/bid")
    public String showBidPage(Model model, String tenderId) {
        model.addAttribute("tenderId", tenderId);
        return "bid"; // Returns bid.html
    }

    @PostMapping("/submitBid")
    public String submitBid(String tenderId, double biddingAmount, String solution, HttpSession session, Model model) {
        String vendorId = (String) session.getAttribute("username"); // Assume vendorId is stored in session

        if (vendorId == null) {
            model.addAttribute("errorMessage", "User is not logged in.");
            return "redirect:/login"; // Redirect to login if vendor is not logged in
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, DB_PASSWORD)) {
            String vendorQuery = "SELECT * FROM vendor WHERE vendor_id = ?";
            try (PreparedStatement vendorStmt = conn.prepareStatement(vendorQuery)) {
                vendorStmt.setString(1, vendorId);
                ResultSet rs = vendorStmt.executeQuery();

                if (rs.next()) {
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String mobileNumber = rs.getString("mobile_number");
                    String companyName = rs.getString("company_name");

                    String bidQuery = "INSERT INTO bidder (tender_id, vendor_id, bidding_amt, solution, name, email, mobile_number, company_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement bidStmt = conn.prepareStatement(bidQuery)) {
                        bidStmt.setString(1, tenderId);
                        bidStmt.setString(2, vendorId);
                        bidStmt.setDouble(3, biddingAmount);
                        bidStmt.setString(4, solution);
                        bidStmt.setString(5, name);
                        bidStmt.setString(6, email);
                        bidStmt.setString(7, mobileNumber);
                        bidStmt.setString(8, companyName);

                        bidStmt.executeUpdate();
                        model.addAttribute("successMessage", "Bid submitted successfully!");
                    }
                } else {
                    model.addAttribute("errorMessage", "Vendor information not found.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error submitting bid: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error submitting bid: " + e.getMessage());
        }

        return "redirect:/tender";
    }
}
