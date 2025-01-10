-- Tạo cơ sở dữ liệu
CREATE DATABASE final_graduation;
GO

-- Sử dụng cơ sở dữ liệu
USE final_graduation;
GO

-- Bảng roles
CREATE TABLE roles (
    id INT PRIMARY KEY IDENTITY,
    [name] NVARCHAR(50) NOT NULL
);

-- Bảng accounts
CREATE TABLE accounts (
    id INT PRIMARY KEY IDENTITY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    [password] NVARCHAR(255) NOT NULL,
    fullname NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NOT NULL UNIQUE,
	[address] NVARCHAR(100),
    photo NVARCHAR(255),
	created_date DATETIME NOT NULL DEFAULT GETDATE()
);

-- Bảng authorities
CREATE TABLE authorities (
    id INT PRIMARY KEY IDENTITY,
    account_id INT NOT NULL,
    role_id INT NOT NULL
);

-- Bảng categories
CREATE TABLE categories (
    id INT PRIMARY KEY IDENTITY,
    [name] NVARCHAR(100) NOT NULL
);

-- Bảng sizes
CREATE TABLE sizes (
    id INT PRIMARY KEY IDENTITY,
    [name] NVARCHAR(50) NOT NULL
);

-- Bảng colors
CREATE TABLE colors (
    id INT PRIMARY KEY IDENTITY,
    [name] NVARCHAR(50) NOT NULL
);

-- Bảng brands
CREATE TABLE brands (
    id INT PRIMARY KEY IDENTITY,
    [name] NVARCHAR(100) NOT NULL
);

-- Bảng products
CREATE TABLE products (
    id INT PRIMARY KEY IDENTITY,
    [name] NVARCHAR(100) NOT NULL,
	[status] BIT DEFAULT 1 -- 1-đang kinh doanh, 0-ngừng kinh doanh
);

-- Bảng products
CREATE TABLE product_details (
    id INT PRIMARY KEY IDENTITY,
	product_id INT,
    [image] NVARCHAR(255),
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    created_date DATETIME NOT NULL DEFAULT GETDATE(),
    available BIT NOT NULL DEFAULT 1, -- 1-đang kinh doanh, 0-ngừng kinh doanh
    category_id INT NOT NULL,
    size_id INT NULL,
    color_id INT NULL,
    brand_id INT NULL
);

-- Bảng orders
CREATE TABLE orders (
    id INT PRIMARY KEY IDENTITY,
    [user_id] INT,
    admin_id INT NOT NULL,
    created_date DATETIME NOT NULL DEFAULT GETDATE(),
    total_price DECIMAL(10, 2) NOT NULL DEFAULT 0,
    [type] NVARCHAR(50) NOT NULL,
    [status] NVARCHAR(50) NOT NULL,
    [address] NVARCHAR(255) NOT NULL
);

-- Bảng order_details
CREATE TABLE order_details (
    id INT PRIMARY KEY IDENTITY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL
);

-- Thêm khóa ngoại sau khi tạo bảng
-- Bảng authorities
ALTER TABLE authorities
ADD CONSTRAINT FK_authorities_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE NO ACTION;

ALTER TABLE authorities
ADD CONSTRAINT FK_authorities_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE NO ACTION;

-- Bảng product_details
ALTER TABLE product_details
ADD CONSTRAINT FK_product_details_products FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE;

ALTER TABLE product_details
ADD CONSTRAINT FK_product_details_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE NO ACTION;

ALTER TABLE product_details
ADD CONSTRAINT FK_product_details_size FOREIGN KEY (size_id) REFERENCES sizes(id) ON DELETE SET NULL;

ALTER TABLE product_details
ADD CONSTRAINT FK_product_details_color FOREIGN KEY (color_id) REFERENCES colors(id) ON DELETE SET NULL;

ALTER TABLE product_details
ADD CONSTRAINT FK_product_details_brand FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE SET NULL;

-- Bảng orders
ALTER TABLE orders
ADD CONSTRAINT FK_orders_user FOREIGN KEY (user_id) REFERENCES accounts(id) ON DELETE SET NULL;

ALTER TABLE orders
ADD CONSTRAINT FK_orders_admin FOREIGN KEY (admin_id) REFERENCES accounts(id) ON DELETE NO ACTION;

-- Bảng order_details
ALTER TABLE order_details
ADD CONSTRAINT FK_order_details_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;

ALTER TABLE order_details
ADD CONSTRAINT FK_order_details_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE;
