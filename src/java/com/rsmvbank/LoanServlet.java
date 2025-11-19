package com.rsmvbank;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.UUID;

public class LoanServlet extends HttpServlet {
    private String dbURL = "jdbc:mysql://localhost:3306/rsmv_bank";
    private String dbUser = "root";
    private String dbPass = "Activa2595"; 

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String name = request.getParameter("name");
        String amount = request.getParameter("amount");
        String purpose = request.getParameter("purpose");

        String reference = "LN-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass)) {
                String sql = "INSERT INTO loans(reference_no, name, amount, purpose, status, created_at) VALUES (?,?,?,?,?,NOW())";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, reference);
                    ps.setString(2, name);
                    ps.setString(3, amount);
                    ps.setString(4, purpose);
                    ps.setString(5, "SUBMITTED");
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h3>Dear " + name + ", your loan request of Rs " + amount + " for " + purpose + " has been submitted successfully.</h3>");
        out.println("<p>Your reference number is: <strong>" + reference + "</strong></p>");
        out.println("<a href='home.html'>Back to Home</a>");
        out.println("</body></html>");
    }
}