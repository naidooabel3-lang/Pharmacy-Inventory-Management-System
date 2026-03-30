-- Create Database and tables for HealthFirst Pharmacy (MySQL)
CREATE DATABASE IF NOT EXISTS healthfirst;
USE healthfirst;

DROP TABLE IF EXISTS sale_items;
DROP TABLE IF EXISTS sales;
DROP TABLE IF EXISTS medicines;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Cashier') DEFAULT 'Cashier',
    full_name VARCHAR(100)
);

CREATE TABLE suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT
);

CREATE TABLE medicines (
    medicine_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    company VARCHAR(150),
    medicine_type VARCHAR(50),
    price DECIMAL(10,2) NOT NULL,
    quantity_in_stock INT DEFAULT 0,
    reorder_level INT DEFAULT 10,
    expiry_date DATE,
    supplier_id INT,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE SET NULL
);

CREATE TABLE sales (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2),
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE TABLE sale_items (
    sale_item_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT,
    medicine_id INT,
    quantity_sold INT,
    price_at_sale DECIMAL(10,2),
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id) ON DELETE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id) ON DELETE SET NULL
);

-- Sample Users
INSERT INTO users (username, password, role, full_name) VALUES 
('admin', 'password', 'Admin', 'Dr. Cain Miller'),
('cashier', 'password', 'Cashier', 'Caitline Miller');

-- Sample Suppliers
INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES 
('PharmaCorp Ltd', 'Mr. Naidoo', '+27849193428', 'info@pharmacorp.co.za', '70 Jobart Street, Johannesburg'),
('MediHealth Distributors', 'Ms. Mashinini', '+27956495829', 'info@medihealth.co.za', '20 Durbanville Lake, Cape Town');

-- Sample Medicines
INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) VALUES 
('Paracetamol 500mg', 'Clicks', 'Tablet', 35.00, 150, 20, '2027-12-31', 1),
('Rucintix 150mg', 'MediHealth', 'Capsule', 195.00, 5, 15, '2026-06-30', 2),
('Cough Syrup 200ml', 'HealthLife', 'Syrup', 105.00, 50, 10, '2026-12-31', 2),
('Sinutab Spray 10ml', 'BioMed', 'Spray', 125.00, 20, 5, '2026-04-20', 2),
('Skinoren Cream', 'SkinCare ltd', 'Cream', 95.00, 80, 8, '2027-08-30', 1);

-- Sample Sales
INSERT INTO sales (total_amount, user_id) VALUES 
(140.00, 2),
(300.00, 2);

-- Sample Sale Items
INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES 
(1, 1, 2, 70.00),
(1, 3, 1, 105.00),
(2, 4, 1, 125.00),
(2, 5, 1, 95.00);