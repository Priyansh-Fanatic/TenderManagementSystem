package com.example.TenderManagementSystem.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.sql.*;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        return "index"; // renders index.html
    }


}
