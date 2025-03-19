-- Tạo cơ sở dữ liệu
CREATE DATABASE final_graduation;
GO

-- Sử dụng cơ sở dữ liệu
USE final_graduation;
GO

-- Bảng roles
CREATE TABLE roles (
    id INT PRIMARY KEY IDENTITY(1,1),
    [name] NVARCHAR(50) NOT NULL
);

-- Bảng employees
CREATE TABLE employees (
    id INT PRIMARY KEY IDENTITY(1,1),
    role_id INT NOT NULL,
    username NVARCHAR(50) NOT NULL UNIQUE,
    [password] NVARCHAR(255) NOT NULL,
    fullname NVARCHAR(100) NOT NULL,
    phone_number NVARCHAR(15),
    email NVARCHAR(100) NOT NULL UNIQUE,
	[address] NVARCHAR(100),
    photo NVARCHAR(255),
	created_date DATETIME NOT NULL DEFAULT GETDATE()
);

-- Bảng customers
CREATE TABLE customers (
    id INT PRIMARY KEY IDENTITY(1,1),
    username NVARCHAR(50) NOT NULL UNIQUE,
    [password] NVARCHAR(255) NOT NULL,
    fullname NVARCHAR(100) NOT NULL,
    phone_number NVARCHAR(15),
    email NVARCHAR(100) NOT NULL UNIQUE,
	[address] NVARCHAR(100),
    photo NVARCHAR(255),
	created_date DATETIME NOT NULL DEFAULT GETDATE()
);

-- Bảng categories
CREATE TABLE categories (
    id INT PRIMARY KEY IDENTITY(1,1),
    [name] NVARCHAR(100) NOT NULL
);

-- Bảng sizes
CREATE TABLE sizes (
    id INT PRIMARY KEY IDENTITY(1,1),
    [name] NVARCHAR(50) NOT NULL
);

-- Bảng colors
CREATE TABLE colors (
    id INT PRIMARY KEY IDENTITY(1,1),
    [name] NVARCHAR(50) NOT NULL
);

-- Bảng brands
CREATE TABLE brands (
    id INT PRIMARY KEY IDENTITY(1,1),
    [name] NVARCHAR(100) NOT NULL
);

-- Bảng products
CREATE TABLE products (
    id INT PRIMARY KEY IDENTITY(1,1),
    [name] NVARCHAR(100) NOT NULL,
	[status] BIT DEFAULT 1 -- 1-đang kinh doanh, 0-ngừng kinh doanh
);

-- Bảng products
CREATE TABLE product_details (
    id INT PRIMARY KEY IDENTITY(1,1),
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
    id INT PRIMARY KEY IDENTITY(1,1),
    [customer_id] INT,
    employee_id INT NOT NULL,
    created_date DATETIME NOT NULL DEFAULT GETDATE(),
    total_price DECIMAL(10, 2) NOT NULL DEFAULT 0,
    [type] NVARCHAR(50) NOT NULL,
    [status] NVARCHAR(50) NOT NULL,
    [address] NVARCHAR(255) NOT NULL
);

-- Bảng order_details
CREATE TABLE order_details (
    id INT PRIMARY KEY IDENTITY(1,1),
    order_id INT NOT NULL,
    product_detail_id INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL
);

CREATE TABLE promotions (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    discount DECIMAL(5,2) CHECK (Discount >= 0 AND Discount <= 100), -- Giảm giá từ 0% đến 100%
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    is_active BIT DEFAULT 1 -- 1: Đang hoạt động, 0: Không hoạt động
);

-- Thêm khóa ngoại sau khi tạo bảng
-- Bảng employees
ALTER TABLE employees
ADD CONSTRAINT FK_employees_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE NO ACTION;

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
ADD CONSTRAINT FK_orders_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL;

ALTER TABLE orders
ADD CONSTRAINT FK_orders_admin FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE NO ACTION;

-- Bảng order_details
ALTER TABLE order_details
ADD CONSTRAINT FK_order_details_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;

ALTER TABLE order_details
ADD CONSTRAINT FK_order_details_product_detail FOREIGN KEY (product_detail_id) REFERENCES product_details(id) ON DELETE CASCADE;

ALTER TABLE orders 
ADD promotion_id INT NULL;

ALTER TABLE orders 
ADD payment_method NVARCHAR(50) NULL;

ALTER TABLE orders 
ADD CONSTRAINT FK_orders_promotions FOREIGN KEY (promotion_id) 
REFERENCES promotions(id);

ALTER TABLE promotions ADD min_order_value NUMERIC(38,2);

ALTER TABLE promotions 
ADD remaining_quantity INT NOT NULL DEFAULT 0;

