-- SQL schema for RSMV Bank web application
CREATE DATABASE IF NOT EXISTS rsmv_bank;
USE rsmv_bank;

-- Users table (for login)
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(100) NOT NULL,
  fullname VARCHAR(200),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Insert default admin (username: admin, password: 1234)
INSERT INTO users (username, password, fullname) VALUES ('admin', '1234', 'Administrator');

-- Loans table
CREATE TABLE IF NOT EXISTS loans (
  id INT AUTO_INCREMENT PRIMARY KEY,
  reference_no VARCHAR(50) UNIQUE NOT NULL,
  name VARCHAR(200),
  amount VARCHAR(100),
  purpose VARCHAR(500),
  status VARCHAR(50),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Contacts table
CREATE TABLE IF NOT EXISTS contacts (
  id INT AUTO_INCREMENT PRIMARY KEY,
  reference_no VARCHAR(50) UNIQUE NOT NULL,
  name VARCHAR(200),
  message TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);