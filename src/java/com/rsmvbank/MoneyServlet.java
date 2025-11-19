package com.rsmvbank;

import java.io.IOException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class MoneyServlet extends HttpServlet {

    // <-- EDIT THESE to match your MySQL user/password if different -->
    private String dbURL  = "jdbc:mysql://127.0.0.1:3306/rsmv_bank?serverTimezone=UTC";
    private String dbUser = "root";
    private String dbPass = "Activa2595";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String amountString = request.getParameter("amount");
        String contact = request.getParameter("contact");       // for pay_contact
        String toAccount = request.getParameter("to_account");  // optional if you choose to use account number
        double amount = 0;

        if (amountString != null && !amountString.trim().isEmpty()) {
            try { amount = Double.parseDouble(amountString); } catch (Exception e) { amount = 0; }
        }

        // NOTE: for simplicity we treat "source" account as ADMIN account ACC0001
        // In real app you'd take the logged-in user's account id.
        String sourceAccountNo = "ACC0001";

        String message;

        // DB operations: check balance, update balances, insert transfer log (atomic transaction)
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                if ("self_transfer".equals(action)) {

                    // use toAccount parameter (if blank, error)
                    String dest = toAccount;
                    if (dest == null || dest.trim().isEmpty()) {
                        throw new IllegalArgumentException("Destination account required for self transfer.");
                    }

                    if (!hasSufficientBalance(conn, sourceAccountNo, amount)) {
                        message = "<div class='result-box error'>❌ Insufficient funds! Transfer failed.</div>";
                    } else {
                        // debit source
                        updateBalance(conn, sourceAccountNo, -amount);
                        // credit destination (create if not exists)
                        ensureAccountExists(conn, dest, "Other Account");
                        updateBalance(conn, dest, amount);

                        // log transfer
                        insertTransfer(conn, "SELF_TRANSFER", sourceAccountNo, dest, null, amount);

                        conn.commit();
                        double newBal = getBalance(conn, sourceAccountNo);
                        message = "<div class='result-box success'>✅ ₹" + amount + " transferred to account <b>" + dest + "</b>.<br>New Balance: ₹" + newBal + "</div>";
                    }

                } else if ("pay_contact".equals(action)) {

                    if (contact == null || contact.trim().isEmpty()) {
                        throw new IllegalArgumentException("Contact name required for Pay a Contact.");
                    }

                    if (!hasSufficientBalance(conn, sourceAccountNo, amount)) {
                        message = "<div class='result-box error'>❌ Insufficient funds! Payment failed.</div>";
                    } else {
                        // debit source
                        updateBalance(conn, sourceAccountNo, -amount);

                        // For this simple model, we do not credit a contact's account (just log payment)
                        insertTransfer(conn, "PAY_CONTACT", sourceAccountNo, null, contact, amount);

                        conn.commit();
                        double newBal = getBalance(conn, sourceAccountNo);
                        message = "<div class='result-box success'>✅ Paid ₹" + amount + " to <b>" + contact + "</b>.<br>New Balance: ₹" + newBal + "</div>";
                    }

                } else if ("balance".equals(action)) {
                    double bal = getBalance(conn, sourceAccountNo);
                    message = "<div class='result-box'>Your Current Balance: <b>₹" + bal + "</b></div>";
                    conn.commit();
                } else {
                    message = "<div class='result-box error'>Invalid action selected.</div>";
                    conn.commit();
                }
            } catch (Exception ex) {
                conn.rollback();
                // log (server log)
                ex.printStackTrace();
                message = "<div class='result-box error'>Error: " + escapeHtml(ex.getMessage()) + "</div>";
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "<div class='result-box error'>Database error: " + escapeHtml(e.getMessage()) + "</div>";
        }

        request.setAttribute("message", message);
        RequestDispatcher dispatcher = request.getRequestDispatcher("money.jsp");
        dispatcher.forward(request, response);
    }

    // --- DB helper methods ---

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(dbURL, dbUser, dbPass);
    }

    private boolean hasSufficientBalance(Connection conn, String accountNo, double amount) throws SQLException {
        double bal = getBalance(conn, accountNo);
        return bal >= amount;
    }

    private double getBalance(Connection conn, String accountNo) throws SQLException {
        String sql = "SELECT balance FROM accounts WHERE account_no = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("balance");
            }
        }
        // if not found, treat as zero
        return 0.0;
    }

    private void updateBalance(Connection conn, String accountNo, double delta) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_no = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, delta);
            ps.setString(2, accountNo);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                // account not present -> create then update
                ensureAccountExists(conn, accountNo, "Auto-created");
                try (PreparedStatement ps2 = conn.prepareStatement(sql)) {
                    ps2.setDouble(1, delta);
                    ps2.setString(2, accountNo);
                    ps2.executeUpdate();
                }
            }
        }
    }

    private void ensureAccountExists(Connection conn, String accountNo, String owner) throws SQLException {
        String sel = "SELECT id FROM accounts WHERE account_no = ?";
        try (PreparedStatement ps = conn.prepareStatement(sel)) {
            ps.setString(1, accountNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return; // exists
            }
        }
        String ins = "INSERT INTO accounts (account_no, owner, balance) VALUES (?,?,0)";
        try (PreparedStatement ps = conn.prepareStatement(ins)) {
            ps.setString(1, accountNo);
            ps.setString(2, owner);
            ps.executeUpdate();
        }
    }

    private void insertTransfer(Connection conn, String type, String fromAccount, String toAccount, String contact, double amount) throws SQLException {
        String sql = "INSERT INTO transfers (type, from_account, to_account, contact, amount, created_at) VALUES (?,?,?,?,?,NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setString(2, fromAccount);
            ps.setString(3, toAccount);
            ps.setString(4, contact);
            ps.setDouble(5, amount);
            ps.executeUpdate();
        }
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");
    }
}
