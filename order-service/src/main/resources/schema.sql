-- SQL script to initialize the database
-- Run this script in PostgreSQL to create the database and tables

-- Create database (optional - can be created manually)
-- CREATE DATABASE order_db;

-- Create tables
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    total_price DECIMAL(10, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2),
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_order_number ON orders(order_number);
CREATE INDEX idx_customer_id ON orders(customer_id);
CREATE INDEX idx_status ON orders(status);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);

