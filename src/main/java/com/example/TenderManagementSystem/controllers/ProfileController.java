package com.example.TenderManagementSystem.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/TenderManagementSystem";
    private static final String USER = "root";
    private static final String DB_PASSWORD = "PASSWORD";

    @GetMapping("/profile")
    public String vendorProfile(HttpSession session, Model model) {
        String vendorId = (String) session.getAttribute("vendorId");

        if (vendorId == null) {
            return "redirect:/login";
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, DB_PASSWORD)) {
            String vendorQuery = "SELECT * FROM vendor WHERE vendor_id = ?";
            try (PreparedStatement vendorStmt = conn.prepareStatement(vendorQuery)) {
                vendorStmt.setString(1, vendorId);
                ResultSet vendorRs = vendorStmt.executeQuery();

                if (vendorRs.next()) {
                    Map<String, Object> vendorDetails = new HashMap<>();
                    vendorDetails.put("name", vendorRs.getString("name"));
                    vendorDetails.put("email", vendorRs.getString("email"));
                    vendorDetails.put("mobile_number", vendorRs.getString("mobile_number"));
                    vendorDetails.put("company_name", vendorRs.getString("company_name"));
                    vendorDetails.put("address", vendorRs.getString("address"));
                    vendorDetails.put("approved", vendorRs.getBoolean("approved") ? "Yes" : "No");
                    model.addAttribute("vendor", vendorDetails);
                }
            }

            List<Map<String, Object>> pendingBids = new ArrayList<>();
            String pendingBidsQuery = "SELECT * FROM bidder WHERE vendor_id = ? AND approved = 0";
            try (PreparedStatement pendingStmt = conn.prepareStatement(pendingBidsQuery)) {
                pendingStmt.setString(1, vendorId);
                ResultSet pendingRs = pendingStmt.executeQuery();

                while (pendingRs.next()) {
                    Map<String, Object> bid = new HashMap<>();
                    bid.put("tender_id", pendingRs.getString("tender_id"));
                    bid.put("bidding_amt", pendingRs.getDouble("bidding_amt"));
                    bid.put("solution", pendingRs.getString("solution"));
                    bid.put("status", "Pending");
                    pendingBids.add(bid);
                }
                model.addAttribute("pendingBids", pendingBids);
            }

            List<Map<String, Object>> approvedBids = new ArrayList<>();
            String approvedBidsQuery = "SELECT * FROM bidder WHERE vendor_id = ? AND approved = 1";
            try (PreparedStatement approvedStmt = conn.prepareStatement(approvedBidsQuery)) {
                approvedStmt.setString(1, vendorId);
                ResultSet approvedRs = approvedStmt.executeQuery();

                while (approvedRs.next()) {
                    Map<String, Object> bid = new HashMap<>();
                    bid.put("tender_id", approvedRs.getString("tender_id"));
                    bid.put("bidding_amt", approvedRs.getDouble("bidding_amt"));
                    bid.put("solution", approvedRs.getString("solution"));
                    bid.put("status", "Approved");
                    approvedBids.add(bid);
                }
                model.addAttribute("approvedBids", approvedBids);
            }
        } catch (SQLException e) {
            logger.error("Error fetching profile data: {}", e.getMessage());
            model.addAttribute("errorMessage", "Error fetching profile data: " + e.getMessage());
        }

        return "profile";
    }
}
