-- Bảng roles (2 dòng)
INSERT INTO roles ([name]) VALUES
('user'),
('staff');
('admin');

-- Bảng accounts (5 dòng)
INSERT INTO accounts (username, [password], fullname, email, [address]) VALUES
('user1', 'password1', 'Nguyen Van A', 'user1@example.com', '123 ABC Street'),
('user2', 'password2', 'Le Thi B', 'user2@example.com', '456 DEF Street'),
('user3', 'password3', 'Tran Van C', 'user3@example.com', '789 GHI Street'),
('admin1', 'adminpass1', 'Pham Minh D', 'admin1@example.com', '111 JKL Street'),
('admin2', 'adminpass2', 'Hoang Thi E', 'admin2@example.com', '222 MNO Street');

-- Bảng authorities (5 dòng)
INSERT INTO authorities (account_id, role_id) VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 2),
(5, 2);

-- Bảng categories (5 dòng)
INSERT INTO categories ([name]) VALUES
('Running Shoes'),
('Basketball Shoes'),
('Casual Shoes'),
('Training Shoes'),
('Sandals');

-- Bảng sizes (5 dòng)
INSERT INTO sizes ([name]) VALUES
('Size 38'),
('Size 39'),
('Size 40'),
('Size 41'),
('Size 42');

-- Bảng colors (5 dòng)
INSERT INTO colors ([name]) VALUES
('Black'),
('White'),
('Blue'),
('Red'),
('Green');

-- Bảng brands (5 dòng)
INSERT INTO brands ([name]) VALUES
('Nike'),
('Adidas'),
('Puma'),
('Reebok'),
('Converse');

-- Bảng products (10 dòng)
INSERT INTO products ([name], [status]) VALUES
('Nike Air Zoom Pegasus', 1),
('Adidas Ultraboost', 1),
('Puma RS-X', 1),
('Reebok Nano X', 1),
('Converse Chuck Taylor', 1),
('Nike Metcon', 1),
('Adidas NMD', 1),
('Puma Future Rider', 1),
('Reebok Floatride', 1),
('Converse Run Star', 1);

-- Bảng product_details (10 dòng)
INSERT INTO product_details (product_id, price, quantity, category_id, size_id, color_id, brand_id) VALUES
(1, 120.00, 50, 1, 1, 2, 1),
(2, 180.00, 30, 1, 2, 3, 2),
(3, 100.00, 40, 3, 3, 4, 3),
(4, 130.00, 20, 4, 4, 1, 4),
(5, 70.00, 60, 2, 5, 2, 5),
(6, 140.00, 35, 4, 1, 3, 1),
(7, 160.00, 25, 1, 2, 5, 2),
(8, 90.00, 50, 3, 3, 4, 3),
(9, 110.00, 15, 4, 4, 1, 4),
(10, 80.00, 45, 2, 5, 2, 5);

-- Bảng orders (5 dòng)
--INSERT INTO orders (user_id, admin_id, total_price, [type], [status], [address]) VALUES
--(1, 4, 300.00, 'Online', 'Delivered', '123 ABC Street'),
--(2, 4, 150.00, 'In-Store', 'Pending', '456 DEF Street'),
--(3, 4, 200.00, 'Online', 'Processing', '789 GHI Street'),
--(1, 5, 400.00, 'Online', 'Delivered', '123 ABC Street'),
--(2, 5, 250.00, 'In-Store', 'Cancelled', '456 DEF Street');

-- Bảng order_details (10 dòng)
--INSERT INTO order_details (order_id, product_id, price, quantity) VALUES
--(1, 1, 120.00, 2),
--(1, 2, 180.00, 1),
--(2, 3, 100.00, 1),
--(2, 5, 70.00, 2),
--(3, 4, 130.00, 1),
--(3, 7, 160.00, 1),
--(4, 1, 120.00, 3),
--(4, 6, 140.00, 1),
--(5, 3, 100.00, 1),
--(5, 10, 80.00, 2);
