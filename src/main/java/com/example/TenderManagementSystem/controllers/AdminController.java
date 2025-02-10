package com.example.TenderManagementSystem.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/TenderManagementSystem";
    private static final String USER = "root";
    private static final String DB_PASSWORD = "PASSWORD";

    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("tenders", getTenders());
        model.addAttribute("vendors", getVendors());
        model.addAttribute("bidders", getBidders());
        return "admin"; // returns the admin.html template
    }

    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalidate the session
        return "redirect:/login"; // Redirect to the login page
    }

    @GetMapping("/admin/createTender")
    public String createTenderForm(Model model) {
        model.addAttribute("tender", new Tender()); // Prepare a new Tender object
        return "createTender"; // returns the createTender.html template
    }

    @PostMapping("/admin/createTender")
    public String createTender(Tender tender) {
        addTenderToDatabase(tender); // Add the new tender to the database
        return "redirect:/admin"; // Redirect back to the admin page
    }

    @PostMapping("/admin/approveVendor")
    public String approveVendor(@RequestParam("id") Long vendorId) {
        updateVendorApprovalStatus(vendorId, true); // Approve the vendor in the database
        return "redirect:/admin"; // Redirect back to the admin page
    }

    @PostMapping("/admin/approveBidder")
    public String approveBidder(@RequestParam("id") Long bidderId) {
        updateBidderApprovalStatus(bidderId, true); // Approve the bidder in the database
        return "redirect:/admin"; // Redirect back to the admin page
    }

    private List<Tender> getTenders() {
        List<Tender> tenders = new ArrayList<>();
        String query = "SELECT * FROM tender";

        try (Connection conn = DriverManager.getConnection(URL, USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Tender tender = new Tender();
                tender.setId(rs.getLong("id"));
                tender.setTenderId(rs.getString("tender_id"));
                tender.setTitle(rs.getString("title"));
                tender.setDescription(rs.getString("description"));
                tender.setDeadline(rs.getDate("deadline"));
                tender.setStatus(rs.getString("status"));
                tenders.add(tender);
            }
        } catch (SQLException e) {
            logger.error("Error fetching tenders: {}", e.getMessage());
        }
        return tenders;
    }

    private List<Vendor> getVendors() {
        List<Vendor> vendors = new ArrayList<>();
        String query = "SELECT * FROM vendor";

        try (Connection conn = DriverManager.getConnection(URL, USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Vendor vendor = new Vendor();
                vendor.setId(rs.getLong("id"));
                vendor.setVendorId(rs.getString("vendor_id"));
                vendor.setName(rs.getString("name"));
                vendor.setEmail(rs.getString("email"));
                vendor.setMobileNumber(rs.getString("mobile_number"));
                vendor.setCompanyName(rs.getString("company_name"));
                vendor.setAddress(rs.getString("address"));
                vendor.setApproved(rs.getBoolean("approved")); // Get approved status
                vendors.add(vendor);
            }
        } catch (SQLException e) {
            logger.error("Error fetching vendors: {}", e.getMessage());
        }
        return vendors;
    }

    private List<Bidder> getBidders() {
        List<Bidder> bidders = new ArrayList<>();
        String query = "SELECT * FROM bidder";

        try (Connection conn = DriverManager.getConnection(URL, USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Bidder bidder = new Bidder();
                bidder.setId(rs.getLong("id"));
                bidder.setVendorId(rs.getString("vendor_id"));
                bidder.setName(rs.getString("name"));
                bidder.setEmail(rs.getString("email"));
                bidder.setMobileNumber(rs.getString("mobile_number"));
                bidder.setCompanyName(rs.getString("company_name"));
                bidder.setTenderId(rs.getString("tender_id"));
                bidder.setBiddingAmt(rs.getInt("bidding_amt"));
                bidder.setSolution(rs.getString("solution"));
                bidder.setApproved(rs.getBoolean("approved"));
                bidders.add(bidder);
            }
        } catch (SQLException e) {
            logger.error("Error fetching bidders: {}", e.getMessage());
        }
        return bidders;
    }

    private void addTenderToDatabase(Tender tender) {
        String query = "INSERT INTO tender (tender_id, title, description, deadline, status) VALUES (?, ?, ?, ?, 'Open')";

        try (Connection conn = DriverManager.getConnection(URL, USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, tender.getTenderId());
            pstmt.setString(2, tender.getTitle());
            pstmt.setString(3, tender.getDescription());
            pstmt.setDate(4, tender.getDeadline());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error adding tender to database: {}", e.getMessage());
        }
    }

    @PostMapping("/admin/closeTender")
    public String closeTender(@RequestParam("id") Long tenderId) {
        updateTenderApprovalStatus(tenderId, true);
        return "redirect:/admin"; // Redirect back to the admin page
    }

    private void updateTenderApprovalStatus(Long tenderId, boolean closed) {
        String query = "UPDATE tender SET status = 'Closed' WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, tenderId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating tender approval status: {}", e.getMessage());
        }
    }

    private void updateVendorApprovalStatus(Long vendorId, boolean approved) {
        String query = "UPDATE vendor SET approved = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setBoolean(1, approved);
            pstmt.setLong(2, vendorId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating vendor approval status: {}", e.getMessage());
        }
    }

    private void updateBidderApprovalStatus(Long vendorId, boolean approved) {
        String query = "UPDATE bidder SET approved = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setBoolean(1, approved);
            pstmt.setLong(2, vendorId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating bidder approval status: {}", e.getMessage());
        }
    }

    static class Tender {
        private Long id;
        private String tenderId;
        private String title;
        private String description;
        private Date deadline;
        private String status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getTenderId() { return tenderId; }
        public void setTenderId(String tender_id) { this.tenderId = tender_id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Date getDeadline() { return deadline; }
        public void setDeadline(Date deadline) { this.deadline = deadline; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    static class Vendor {
        private Long id;
        private String vendorId;
        private String name;
        private String email;
        private String mobileNumber;
        private String companyName;
        private String address;
        private Boolean approved; // Add this line

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getVendorId() { return vendorId; }
        public void setVendorId(String vendorId) { this.vendorId = vendorId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getMobileNumber() { return mobileNumber; }
        public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public Boolean getApproved() { return approved; } // Add getter for approved
        public void setApproved(Boolean approved) { this.approved = approved; } // Add setter for approved
    }

    static class Bidder {
        private Long id;
        private String vendorId;
        private String name;
        private String email;
        private String mobileNumber;
        private String companyName;
        private String tenderId;
        private Integer biddingAmt;
        private String solution;
        private Boolean approved;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getVendorId() { return vendorId; }
        public void setVendorId(String vendorId) { this.vendorId = vendorId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getMobileNumber() { return mobileNumber; }
        public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }

        public String getTenderId() { return tenderId; }
        public void setTenderId(String tenderId) { this.tenderId = tenderId; }

        public Integer getBiddingAmt() { return biddingAmt; }
        public void setBiddingAmt(Integer biddingAmt) { this.biddingAmt = biddingAmt; }

        public String getSolution() { return solution; }
        public void setSolution(String solution) { this.solution = solution; }

        public Boolean getApproved() { return approved; } // Add getter for approved
        public void setApproved(Boolean approved) { this.approved = approved; }


    }
}
