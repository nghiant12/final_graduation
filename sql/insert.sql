-- Bảng roles (2 dòng)
INSERT INTO roles ([name]) VALUES
('staff'),
('admin');

-- Bảng employees (5 dòng)
INSERT INTO employees (role_id, username, [password], fullname, email, [address]) VALUES
(1, 'staff1', 'staffpass1', 'Pham Minh D', 'staff1@example.com', '111 JKL Street'),
(1, 'staff2', 'staffpass2', 'Hoang Thi E', 'staff2@example.com', '222 MNO Street'),
(1, 'staff3', 'staffpass3', 'Le Van H', 'staff3@example.com', '333 ABC Street'),
(2, 'admin', '123', 'Nguyen Van L', 'admin4@example.com', '444 ABC Street')

-- Bảng customers (5 dòng)
INSERT INTO customers (username, [password], fullname, email, [address], phone_number) VALUES
('user1', 'password1', 'Nguyen Van A', 'user1@example.com', '123 ABC Street', '0123456789'),
('user2', 'password2', 'Le Thi B', 'user2@example.com', '456 DEF Street', '0987654321'),
('user3', 'password3', 'Tran Van C', 'user3@example.com', '789 GHI Street', '0912345678'),
('user4', 'password4', 'Pham Thi E', 'user4@example.com', '115 LKJ Street', '0965432187'),
('user5', 'password5', 'Nguyen Thi M', 'user5@example.com', '678 ABS Street', '0932165487');


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
USE [final_graduation]
GO
SET IDENTITY_INSERT [dbo].[product_details] ON 
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (1, 1, N'https://myshoes.vn/image/catalog/2023/nike/nk10/giay-nike-air-zoom-structure-25-nam-den-trang-01.jpg', CAST(120.00 AS Decimal(10, 2)), 41, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 1, 1, 2, 1)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (2, 2, N'https://assets.adidas.com/images/h_840,f_auto,q_auto,fl_lossy,c_fill,g_auto/b9bd6dc6bbb84a8faa3dae8400320b3e_9366/Giay_Ultra_4DFWD_DJen_GX6632_01_standard.jpg', CAST(180.00 AS Decimal(10, 2)), 30, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 1, 2, 3, 2)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (3, 3, N'https://supersports.com.vn/cdn/shop/files/39631102-1_1200x1200.jpg?v=1714125577', CAST(100.00 AS Decimal(10, 2)), 40, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 3, 3, 4, 3)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (4, 4, N'https://i5.walmartimages.com/seo/Reebok-Men-s-Nano-X-Shoes_4b7cf6c5-26f4-4144-b42c-0925e66fbbae.e2be579c28b9907b8d5b03fcc2967424.jpeg', CAST(130.00 AS Decimal(10, 2)), 19, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 4, 4, 1, 4)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (5, 5, N'https://www.converse.vn/media/catalog/product/0/8/0882-CONA00916C005003-1.jpg', CAST(70.00 AS Decimal(10, 2)), 60, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 2, 5, 2, 5)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (6, 6, N'https://static.nike.com/a/images/t_PDP_936_v1/f_auto,q_auto:eco/b9480aca-bf10-4e5f-a511-41f9fff4a34e/W+NIKE+METCON+9+AMP.png', CAST(140.00 AS Decimal(10, 2)), 35, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 4, 1, 3, 1)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (7, 7, N'https://assets.adidas.com/images/h_840,f_auto,q_auto,fl_lossy,c_fill,g_auto/0fd9bc2bfc234ce0b69aaef900fddbdf_9366/Giay_NMD_R1_DJen_HQ4452_01_standard.jpg', CAST(160.00 AS Decimal(10, 2)), 21, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 1, 2, 5, 2)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (8, 8, N'https://product.hstatic.net/200000601263/product/107355-01_2_5c41b2719b4a4b9fb4299a54c1ac5941.jpg', CAST(90.00 AS Decimal(10, 2)), 50, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 3, 3, 4, 3)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (9, 9, N'https://image.hsv-tech.io/1387x0/reebok/common/5ff7cb81-ee12-4bc4-b674-000db543c631.webp', CAST(110.00 AS Decimal(10, 2)), 14, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 4, 4, 1, 4)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (10, 10, N'https://bizweb.dktcdn.net/thumb/grande/100/347/923/products/166800v-6.jpg?v=1637871478210', CAST(80.00 AS Decimal(10, 2)), 45, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 2, 5, 2, 5)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (15, 1, N'https://runningstore.vn/wp-content/uploads/2022/07/87825e12da3a1864412b.jpg', CAST(500.00 AS Decimal(10, 2)), 20, CAST(N'2025-03-10T00:00:00.000' AS DateTime), 1, 1, 2, 3, 1)
GO
SET IDENTITY_INSERT [dbo].[product_details] OFF
GO

INSERT INTO promotions (name, is_active, discount, start_date, end_date, min_order_value, remaining_quantity) 
VALUES 
('Discount 10%', 1, 10, GETDATE(), DATEADD(DAY, 2, GETDATE()), 500, 100),
('Giam 20% cho don hang lon', 1, 20, GETDATE(), DATEADD(DAY, 3, GETDATE()), 1000, 50);


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
