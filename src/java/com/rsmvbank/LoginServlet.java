package com.rsmvbank;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class LoginServlet extends HttpServlet {
    private String dbURL = "jdbc:mysql://localhost:3306/rsmv_bank";
    private String dbUser = "root";
    private String dbPass = "Activa2595"; 

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String user = request.getParameter("username");
        String pass = request.getParameter("password");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass)) {
                String sql = "SELECT password FROM users WHERE username = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, user);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String realPass = rs.getString("password");
                            if (realPass.equals(pass)) {
                                response.sendRedirect("home.html");
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        PrintWriter out = response.getWriter();
        out.println("<html><body><h3>Invalid Credentials. Try Again!</h3>");
        out.println("<a href='index.html'>Back</a></body></html>");
    }
}