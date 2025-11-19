**RSMV Bank – Java Web Application**

RSMV Bank is a simple banking web application built using Java Servlets, JSP, JDBC, and MySQL.
It provides essential banking features like login, money transfer, loan requests, and a contact support system.

**Workflow** 

1. User opens the RSMV Bank web app in the browser (home page).
2. User logs in using their credentials; app validates details from the `users` table in MySQL.
3. After successful login, user is redirected to the dashboard (money transfer, loan, contact options).
4. For **Money Transfer**, user enters receiver details and amount; app stores the transaction with a unique reference number in the `transactions` table.
5. For **Loan Request**, user fills loan form (amount, purpose, etc.); app saves the request with status in the `loans` table.
6. For **Contact/Support**, user submits a query; the message is stored in the `contact_messages` table for admin review.
7. All database operations use a common `DBConnection` class with JDBC for connecting to MySQL.
8. On each action, the user sees a success or error message based on the database response.

**Tech Stack**

Backend- Java Servlets, JDBC <br>
Frontend- HTML, CSS, JS<br>
Server- Apache Tomcat<br>
IDE- Apache NetBeans<br>
Database- MySQL

