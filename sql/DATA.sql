USE [master]
GO
ALTER DATABASE final_graduation
SET SINGLE_USER
WITH ROLLBACK IMMEDIATE;

DROP DATABASE final_graduation;

CREATE DATABASE [final_graduation]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'final_graduation', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQL\DATA\final_graduation.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'final_graduation_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQL\DATA\final_graduation_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO
ALTER DATABASE [final_graduation] SET COMPATIBILITY_LEVEL = 160
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [final_graduation].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [final_graduation] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [final_graduation] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [final_graduation] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [final_graduation] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [final_graduation] SET ARITHABORT OFF 
GO
ALTER DATABASE [final_graduation] SET AUTO_CLOSE ON 
GO
ALTER DATABASE [final_graduation] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [final_graduation] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [final_graduation] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [final_graduation] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [final_graduation] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [final_graduation] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [final_graduation] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [final_graduation] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [final_graduation] SET  ENABLE_BROKER 
GO
ALTER DATABASE [final_graduation] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [final_graduation] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [final_graduation] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [final_graduation] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [final_graduation] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [final_graduation] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [final_graduation] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [final_graduation] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [final_graduation] SET  MULTI_USER 
GO
ALTER DATABASE [final_graduation] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [final_graduation] SET DB_CHAINING OFF 
GO
ALTER DATABASE [final_graduation] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [final_graduation] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [final_graduation] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [final_graduation] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
ALTER DATABASE [final_graduation] SET QUERY_STORE = ON
GO
ALTER DATABASE [final_graduation] SET QUERY_STORE (OPERATION_MODE = READ_WRITE, CLEANUP_POLICY = (STALE_QUERY_THRESHOLD_DAYS = 30), DATA_FLUSH_INTERVAL_SECONDS = 900, INTERVAL_LENGTH_MINUTES = 60, MAX_STORAGE_SIZE_MB = 1000, QUERY_CAPTURE_MODE = AUTO, SIZE_BASED_CLEANUP_MODE = AUTO, MAX_PLANS_PER_QUERY = 200, WAIT_STATS_CAPTURE_MODE = ON)
GO
USE [final_graduation]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[brands](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](100) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[categories](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](100) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[colors](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](50) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[customers](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[username] [nvarchar](50) NOT NULL,
	[password] [nvarchar](255) NOT NULL,
	[fullname] [nvarchar](100) NOT NULL,
	[phone_number] [nvarchar](100) NULL,
	[email] [nvarchar](100) NOT NULL,
	[address] [nvarchar](100) NULL,
	[photo] [nvarchar](255) NULL,
	[created_date] [datetime] NOT NULL,
	[status] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[employees](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[role_id] [int] NOT NULL,
	[username] [nvarchar](50) NOT NULL,
	[password] [nvarchar](255) NOT NULL,
	[fullname] [nvarchar](100) NOT NULL,
	[phone_number] [nvarchar](100) NULL,
	[email] [nvarchar](100) NOT NULL,
	[address] [nvarchar](100) NULL,
	[photo] [nvarchar](255) NULL,
	[created_date] [datetime] NOT NULL,
	[status] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[order_details](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[order_id] [int] NOT NULL,
	[product_detail_id] [int] NOT NULL,
	[price] [decimal](10, 2) NOT NULL,
	[quantity] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[orders](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[customer_id] [int] NULL,
	[employee_id] [int] NULL,
	[created_date] [datetime] NOT NULL,
	[sub_total] [decimal](10, 2) NULL,
	[shipping_fee] [decimal](10, 2) NULL,
	[discount_amount] [decimal](10, 2) NULL,
	[total_price] [decimal](10, 2) NULL,
	[type] [nvarchar](50) NOT NULL,
	[status] [nvarchar](50) NOT NULL,
	[address] [nvarchar](255) NOT NULL,
	[promotion_id] [int] NULL,
	[payment_method] [varchar](255) NULL,
	[order_code] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[product_details](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[product_id] [int] NULL,
	[image] [nvarchar](255) NULL,
	[price] [decimal](10, 2) NOT NULL,
	[quantity] [int] NOT NULL,
	[created_date] [datetime] NOT NULL,
	[available] [bit] NOT NULL,
	[category_id] [int] NOT NULL,
	[size_id] [int] NULL,
	[color_id] [int] NULL,
	[brand_id] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[products]    Script Date: 5/24/2025 1:51:19 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[products](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](100) NOT NULL,
	[status] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[promotions](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](255) NOT NULL,
	[description] [nvarchar](max) NULL,
	[discount] [decimal](5, 2) NULL,
	[start_date] [datetime] NOT NULL,
	[end_date] [datetime] NOT NULL,
	[is_active] [bit] NULL,
	[min_order_value] [numeric](38, 2) NULL,
	[remaining_quantity] [int] NOT NULL,
	[code] [varchar](50) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[roles](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](50) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[sizes](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](50) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[brands] ON 
GO
INSERT [dbo].[brands] ([id], [name]) VALUES (1, N'Nike')
GO
INSERT [dbo].[brands] ([id], [name]) VALUES (2, N'Adidas')
GO
INSERT [dbo].[brands] ([id], [name]) VALUES (3, N'Puma')
GO
INSERT [dbo].[brands] ([id], [name]) VALUES (4, N'Reebok')
GO
INSERT [dbo].[brands] ([id], [name]) VALUES (5, N'Converse')
GO
SET IDENTITY_INSERT [dbo].[brands] OFF
GO
SET IDENTITY_INSERT [dbo].[categories] ON 
GO
INSERT [dbo].[categories] ([id], [name]) VALUES (1, N'Running Shoes')
GO
INSERT [dbo].[categories] ([id], [name]) VALUES (2, N'Basketball Shoes')
GO
INSERT [dbo].[categories] ([id], [name]) VALUES (3, N'Casual Shoes')
GO
INSERT [dbo].[categories] ([id], [name]) VALUES (4, N'Training Shoes')
GO
INSERT [dbo].[categories] ([id], [name]) VALUES (5, N'Sandals')
GO
SET IDENTITY_INSERT [dbo].[categories] OFF
GO
SET IDENTITY_INSERT [dbo].[colors] ON 
GO
INSERT [dbo].[colors] ([id], [name]) VALUES (1, N'Black')
GO
INSERT [dbo].[colors] ([id], [name]) VALUES (2, N'White')
GO
INSERT [dbo].[colors] ([id], [name]) VALUES (3, N'Blue')
GO
INSERT [dbo].[colors] ([id], [name]) VALUES (4, N'Red')
GO
INSERT [dbo].[colors] ([id], [name]) VALUES (5, N'Green')
GO
SET IDENTITY_INSERT [dbo].[colors] OFF
GO
SET IDENTITY_INSERT [dbo].[customers] ON 
GO
INSERT [dbo].[customers] ([id], [username], [password], [fullname], [phone_number], [email], [address], [photo], [created_date], [status]) VALUES (1, N'user1', N'$2a$10$env5TQfwIl2YOk.Gvphh4egU/9uPSluvoN42VNQNSUywh7.SbJcFC', N'Hoang Van A', N'0123456789', N'user1@example.com', N'123 ABC Street', NULL, CAST(N'2025-05-23T21:57:51.150' AS DateTime), 1)
GO
INSERT [dbo].[customers] ([id], [username], [password], [fullname], [phone_number], [email], [address], [photo], [created_date], [status]) VALUES (2, N'user2', N'$2a$10$TQCrbNEoUDJNVRKsH6q7weZsc01O4JB3kTURdWzHhdG7U1BYvJqNi', N'Le Thi B', N'0987654321', N'user2@example.com', N'456 DEF Street', NULL, CAST(N'2025-05-23T21:57:51.150' AS DateTime), 1)
GO
INSERT [dbo].[customers] ([id], [username], [password], [fullname], [phone_number], [email], [address], [photo], [created_date], [status]) VALUES (3, N'user3', N'$2a$10$n4DP4u8TCEqghQ2AjW2Gy.bnqMY.rLsdwQkReZRaWsmuc0FCtC0c6', N'Tran Van C', N'0912345678', N'user3@example.com', N'789 GHI Street', NULL, CAST(N'2025-05-23T21:57:51.150' AS DateTime), 1)
GO
INSERT [dbo].[customers] ([id], [username], [password], [fullname], [phone_number], [email], [address], [photo], [created_date], [status]) VALUES (4, N'user4', N'$2a$10$OlaZMkeHjINHC38anQ3W2Oid5oRrNegCGWyt4DdYFam4zw0p1NV1W', N'Pham Thi E', N'0965432187', N'user4@example.com', N'115 LKJ Street', NULL, CAST(N'2025-05-23T21:57:51.150' AS DateTime), 1)
GO
INSERT [dbo].[customers] ([id], [username], [password], [fullname], [phone_number], [email], [address], [photo], [created_date], [status]) VALUES (5, N'user5', N'$2a$10$F9eJhqoja2Cm/JkcJJKwzO4fKsFxw6rG1/CJGIdjqTUNTsjRVyDau', N'Nguyen Thi M', N'0932165487', N'user5@example.com', N'678 ABS Street', NULL, CAST(N'2025-05-23T21:57:51.150' AS DateTime), 1)
GO
SET IDENTITY_INSERT [dbo].[customers] OFF
GO
SET IDENTITY_INSERT [dbo].[employees] ON 
GO
INSERT [dbo].[employees] ([id], [role_id], [username], [password], [fullname], [phone_number], [email], [address], [photo], [created_date], [status]) VALUES (1, 1, N'staff1', N'$2a$10$rARRDgqaPlD.e4cinXpNvu12OqPy5x5q5XIGtKxao6SLMXGJJxKcG', N'Pham Minh D', N'0123456789', N'staff1@example.com', N'111 JKL Street', NULL, CAST(N'2025-05-23T21:57:51.150' AS DateTime), 1)
GO
INSERT [dbo].[employees] ([id], [role_id], [username], [password], [fullname], [phone_number], [email], [address], [photo], [created_date], [status]) VALUES (2, 1, N'staff2', N'$2a$10$wKogbcqO2WRWUf0W9vhvluDCDrFzUjcoTqu3JYykxVoF2fpfkr9zy', N'Hoang Thi E', N'0987654321', N'staff2@example.com', N'222 MNO Street', NULL, CAST(N'2025-05-23T21:57:51.150' AS DateTime), 1)
GO
INSERT [dbo].[employees] ([id], [role_id], [username], [password], [fullname], [phone_number], [email], [address], [photo], [created_date], [status]) VALUES (3, 1, N'staff3', N'$2a$10$LlBP2U9LhFhggTI0BEDvqexdz/WH5nEFui7WDDTxO.0h.5TX57n7C', N'Le Van H', N'0912345678', N'staff3@example.com', N'333 ABC Street', NULL, CAST(N'2025-05-23T21:57:51.150' AS DateTime), 1)
GO
INSERT [dbo].[employees] ([id], [role_id], [username], [password], [fullname], [phone_number], [email], [address], [photo], [created_date], [status]) VALUES (4, 2, N'admin', N'$2a$10$env5TQfwIl2YOk.Gvphh4egU/9uPSluvoN42VNQNSUywh7.SbJcFC', N'Nguyen Van L', N'0965432187', N'admin4@example.com', N'444 ABC Street', NULL, CAST(N'2025-05-23T21:57:51.150' AS DateTime), 1)
GO
SET IDENTITY_INSERT [dbo].[employees] OFF
GO
SET IDENTITY_INSERT [dbo].[order_details] ON 
GO
INSERT [dbo].[order_details] ([id], [order_id], [product_detail_id], [price], [quantity]) VALUES (1, 1, 7, CAST(1600000.00 AS Decimal(10, 2)), 1)
GO
SET IDENTITY_INSERT [dbo].[order_details] OFF
GO
SET IDENTITY_INSERT [dbo].[orders] ON 
GO
INSERT [dbo].[orders] ([id], [customer_id], [employee_id], [created_date], [sub_total], [shipping_fee], [discount_amount], [total_price], [type], [status], [address], [promotion_id], [payment_method], [order_code]) VALUES (1, 1, 1, CAST(N'2025-05-24T13:30:58.677' AS DateTime), CAST(1600000.00 AS Decimal(10, 2)), CAST(53999.00 AS Decimal(10, 2)), CAST(80000.00 AS Decimal(10, 2)), CAST(1573999.00 AS Decimal(10, 2)), N'Online', N'PENDING_CONFIRMATION', N'HCM, Xã Suối Quyền, Huyện Văn Chấn, Yên Bái', 5, N'VNPay', NULL)
GO
SET IDENTITY_INSERT [dbo].[orders] OFF
GO
SET IDENTITY_INSERT [dbo].[product_details] ON 
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (1, 1, N'https://myshoes.vn/image/catalog/2023/nike/nk10/giay-nike-air-zoom-structure-25-nam-den-trang-01.jpg', CAST(120000.00 AS Decimal(10, 2)), 41, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 1, 1, 2, 1)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (2, 2, N'https://assets.adidas.com/images/h_840,f_auto,q_auto,fl_lossy,c_fill,g_auto/b9bd6dc6bbb84a8faa3dae8400320b3e_9366/Giay_Ultra_4DFWD_DJen_GX6632_01_standard.jpg', CAST(1800000.00 AS Decimal(10, 2)), 30, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 1, 2, 3, 2)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (3, 3, N'https://supersports.com.vn/cdn/shop/files/39631102-1_1200x1200.jpg?v=1714125577', CAST(1000000.00 AS Decimal(10, 2)), 40, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 3, 3, 4, 3)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (4, 4, N'https://i5.walmartimages.com/seo/Reebok-Men-s-Nano-X-Shoes_4b7cf6c5-26f4-4144-b42c-0925e66fbbae.e2be579c28b9907b8d5b03fcc2967424.jpeg', CAST(1300000.00 AS Decimal(10, 2)), 19, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 4, 4, 1, 4)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (5, 5, N'https://www.converse.vn/media/catalog/product/0/8/0882-CONA00916C005003-1.jpg', CAST(700000.00 AS Decimal(10, 2)), 60, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 2, 5, 2, 5)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (6, 6, N'https://static.nike.com/a/images/t_PDP_936_v1/f_auto,q_auto:eco/b9480aca-bf10-4e5f-a511-41f9fff4a34e/W+NIKE+METCON+9+AMP.png', CAST(1400000.00 AS Decimal(10, 2)), 35, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 4, 1, 3, 1)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (7, 7, N'https://assets.adidas.com/images/h_840,f_auto,q_auto,fl_lossy,c_fill,g_auto/0fd9bc2bfc234ce0b69aaef900fddbdf_9366/Giay_NMD_R1_DJen_HQ4452_01_standard.jpg', CAST(1600000.00 AS Decimal(10, 2)), 16, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 1, 2, 5, 2)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (8, 8, N'https://product.hstatic.net/200000601263/product/107355-01_2_5c41b2719b4a4b9fb4299a54c1ac5941.jpg', CAST(900000.00 AS Decimal(10, 2)), 50, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 3, 3, 4, 3)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (9, 9, N'https://image.hsv-tech.io/1387x0/reebok/common/5ff7cb81-ee12-4bc4-b674-000db543c631.webp', CAST(1100000.00 AS Decimal(10, 2)), 14, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 4, 4, 1, 4)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (10, 10, N'https://bizweb.dktcdn.net/thumb/grande/100/347/923/products/166800v-6.jpg?v=1637871478210', CAST(800000.00 AS Decimal(10, 2)), 45, CAST(N'2025-03-06T02:04:52.287' AS DateTime), 1, 2, 5, 2, 5)
GO
INSERT [dbo].[product_details] ([id], [product_id], [image], [price], [quantity], [created_date], [available], [category_id], [size_id], [color_id], [brand_id]) VALUES (15, 1, N'https://runningstore.vn/wp-content/uploads/2022/07/87825e12da3a1864412b.jpg', CAST(5000000.00 AS Decimal(10, 2)), 18, CAST(N'2025-03-10T00:00:00.000' AS DateTime), 1, 1, 2, 3, 1)
GO
SET IDENTITY_INSERT [dbo].[product_details] OFF
GO
SET IDENTITY_INSERT [dbo].[products] ON 
GO
INSERT [dbo].[products] ([id], [name], [status]) VALUES (1, N'Nike Air Zoom Pegasus', 1)
GO
INSERT [dbo].[products] ([id], [name], [status]) VALUES (2, N'Adidas Ultraboost', 1)
GO
INSERT [dbo].[products] ([id], [name], [status]) VALUES (3, N'Puma RS-X', 1)
GO
INSERT [dbo].[products] ([id], [name], [status]) VALUES (4, N'Reebok Nano X', 1)
GO
INSERT [dbo].[products] ([id], [name], [status]) VALUES (5, N'Converse Chuck Taylor', 1)
GO
INSERT [dbo].[products] ([id], [name], [status]) VALUES (6, N'Nike Metcon', 1)
GO
INSERT [dbo].[products] ([id], [name], [status]) VALUES (7, N'Adidas NMD', 1)
GO
INSERT [dbo].[products] ([id], [name], [status]) VALUES (8, N'Puma Future Rider', 1)
GO
INSERT [dbo].[products] ([id], [name], [status]) VALUES (9, N'Reebok Floatride', 1)
GO
INSERT [dbo].[products] ([id], [name], [status]) VALUES (10, N'Converse Run Star', 1)
GO
SET IDENTITY_INSERT [dbo].[products] OFF
GO
SET IDENTITY_INSERT [dbo].[promotions] ON 
GO
INSERT [dbo].[promotions] ([id], [name], [description], [discount], [start_date], [end_date], [is_active], [min_order_value], [remaining_quantity], [code]) VALUES (1, N'Discount 10%', NULL, CAST(10.00 AS Decimal(5, 2)), CAST(N'2025-05-23T21:57:51.213' AS DateTime), CAST(N'2025-05-25T21:57:51.213' AS DateTime), 1, CAST(500000.00 AS Numeric(38, 2)), 100, N'PROMO1')
GO
INSERT [dbo].[promotions] ([id], [name], [description], [discount], [start_date], [end_date], [is_active], [min_order_value], [remaining_quantity], [code]) VALUES (2, N'Giam 20% cho don hang lon', NULL, CAST(20.00 AS Decimal(5, 2)), CAST(N'2025-05-23T21:57:51.213' AS DateTime), CAST(N'2025-05-26T21:57:51.213' AS DateTime), 1, CAST(1000000.00 AS Numeric(38, 2)), 50, N'PROMO2')
GO
INSERT [dbo].[promotions] ([id], [name], [description], [discount], [start_date], [end_date], [is_active], [min_order_value], [remaining_quantity], [code]) VALUES (5, N'Giảm 5% toàn đơn hàng', NULL, CAST(5.00 AS Decimal(5, 2)), CAST(N'2025-05-24T12:28:00.000' AS DateTime), CAST(N'2025-05-31T12:30:00.000' AS DateTime), 1, CAST(1000000.00 AS Numeric(38, 2)), 49, N'PROMO3')
GO
SET IDENTITY_INSERT [dbo].[promotions] OFF
GO
SET IDENTITY_INSERT [dbo].[roles] ON 
GO
INSERT [dbo].[roles] ([id], [name]) VALUES (1, N'staff')
GO
INSERT [dbo].[roles] ([id], [name]) VALUES (2, N'admin')
GO
INSERT [dbo].[roles] ([id], [name]) VALUES (3, N'ROLE_USER')
GO
SET IDENTITY_INSERT [dbo].[roles] OFF
GO
SET IDENTITY_INSERT [dbo].[sizes] ON 
GO
INSERT [dbo].[sizes] ([id], [name]) VALUES (1, N'Size 38')
GO
INSERT [dbo].[sizes] ([id], [name]) VALUES (2, N'Size 39')
GO
INSERT [dbo].[sizes] ([id], [name]) VALUES (3, N'Size 40')
GO
INSERT [dbo].[sizes] ([id], [name]) VALUES (4, N'Size 41')
GO
INSERT [dbo].[sizes] ([id], [name]) VALUES (5, N'Size 42')
GO
SET IDENTITY_INSERT [dbo].[sizes] OFF
GO
SET ANSI_PADDING ON
GO

ALTER TABLE [dbo].[customers] ADD UNIQUE NONCLUSTERED 
(
	[email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO

ALTER TABLE [dbo].[customers] ADD UNIQUE NONCLUSTERED 
(
	[username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO

ALTER TABLE [dbo].[employees] ADD UNIQUE NONCLUSTERED 
(
	[email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO

ALTER TABLE [dbo].[employees] ADD UNIQUE NONCLUSTERED 
(
	[username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO

CREATE UNIQUE NONCLUSTERED INDEX [UKdhk2umg8ijjkg4njg6891trit] ON [dbo].[orders]
(
	[order_code] ASC
)
WHERE ([order_code] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO

ALTER TABLE [dbo].[promotions] ADD  CONSTRAINT [UQ_promotions_code] UNIQUE NONCLUSTERED 
(
	[code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[customers] ADD  DEFAULT (getdate()) FOR [created_date]
GO
ALTER TABLE [dbo].[customers] ADD  DEFAULT ((1)) FOR [status]
GO
ALTER TABLE [dbo].[employees] ADD  DEFAULT (getdate()) FOR [created_date]
GO
ALTER TABLE [dbo].[employees] ADD  DEFAULT ((1)) FOR [status]
GO
ALTER TABLE [dbo].[orders] ADD  DEFAULT (getdate()) FOR [created_date]
GO
ALTER TABLE [dbo].[orders] ADD  DEFAULT ((0)) FOR [sub_total]
GO
ALTER TABLE [dbo].[orders] ADD  DEFAULT ((0)) FOR [shipping_fee]
GO
ALTER TABLE [dbo].[orders] ADD  DEFAULT ((0)) FOR [discount_amount]
GO
ALTER TABLE [dbo].[orders] ADD  DEFAULT ((0)) FOR [total_price]
GO
ALTER TABLE [dbo].[product_details] ADD  DEFAULT ((0)) FOR [quantity]
GO
ALTER TABLE [dbo].[product_details] ADD  DEFAULT (getdate()) FOR [created_date]
GO
ALTER TABLE [dbo].[product_details] ADD  DEFAULT ((1)) FOR [available]
GO
ALTER TABLE [dbo].[products] ADD  DEFAULT ((1)) FOR [status]
GO
ALTER TABLE [dbo].[promotions] ADD  DEFAULT ((1)) FOR [is_active]
GO
ALTER TABLE [dbo].[promotions] ADD  DEFAULT ((0)) FOR [remaining_quantity]
GO
ALTER TABLE [dbo].[employees]  WITH CHECK ADD  CONSTRAINT [FK_employees_role] FOREIGN KEY([role_id])
REFERENCES [dbo].[roles] ([id])
GO
ALTER TABLE [dbo].[employees] CHECK CONSTRAINT [FK_employees_role]
GO
ALTER TABLE [dbo].[order_details]  WITH CHECK ADD  CONSTRAINT [FK_order_details_order] FOREIGN KEY([order_id])
REFERENCES [dbo].[orders] ([id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[order_details] CHECK CONSTRAINT [FK_order_details_order]
GO
ALTER TABLE [dbo].[order_details]  WITH CHECK ADD  CONSTRAINT [FK_order_details_product_detail] FOREIGN KEY([product_detail_id])
REFERENCES [dbo].[product_details] ([id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[order_details] CHECK CONSTRAINT [FK_order_details_product_detail]
GO
ALTER TABLE [dbo].[orders]  WITH CHECK ADD  CONSTRAINT [FK_orders_admin] FOREIGN KEY([employee_id])
REFERENCES [dbo].[employees] ([id])
GO
ALTER TABLE [dbo].[orders] CHECK CONSTRAINT [FK_orders_admin]
GO
ALTER TABLE [dbo].[orders]  WITH CHECK ADD  CONSTRAINT [FK_orders_customer] FOREIGN KEY([customer_id])
REFERENCES [dbo].[customers] ([id])
ON DELETE SET NULL
GO
ALTER TABLE [dbo].[orders] CHECK CONSTRAINT [FK_orders_customer]
GO
ALTER TABLE [dbo].[orders]  WITH CHECK ADD  CONSTRAINT [FK_orders_promotions] FOREIGN KEY([promotion_id])
REFERENCES [dbo].[promotions] ([id])
GO
ALTER TABLE [dbo].[orders] CHECK CONSTRAINT [FK_orders_promotions]
GO
ALTER TABLE [dbo].[product_details]  WITH CHECK ADD  CONSTRAINT [FK_product_details_brand] FOREIGN KEY([brand_id])
REFERENCES [dbo].[brands] ([id])
ON DELETE SET NULL
GO
ALTER TABLE [dbo].[product_details] CHECK CONSTRAINT [FK_product_details_brand]
GO
ALTER TABLE [dbo].[product_details]  WITH CHECK ADD  CONSTRAINT [FK_product_details_category] FOREIGN KEY([category_id])
REFERENCES [dbo].[categories] ([id])
GO
ALTER TABLE [dbo].[product_details] CHECK CONSTRAINT [FK_product_details_category]
GO
ALTER TABLE [dbo].[product_details]  WITH CHECK ADD  CONSTRAINT [FK_product_details_color] FOREIGN KEY([color_id])
REFERENCES [dbo].[colors] ([id])
ON DELETE SET NULL
GO
ALTER TABLE [dbo].[product_details] CHECK CONSTRAINT [FK_product_details_color]
GO
ALTER TABLE [dbo].[product_details]  WITH CHECK ADD  CONSTRAINT [FK_product_details_products] FOREIGN KEY([product_id])
REFERENCES [dbo].[products] ([id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[product_details] CHECK CONSTRAINT [FK_product_details_products]
GO
ALTER TABLE [dbo].[product_details]  WITH CHECK ADD  CONSTRAINT [FK_product_details_size] FOREIGN KEY([size_id])
REFERENCES [dbo].[sizes] ([id])
ON DELETE SET NULL
GO
ALTER TABLE [dbo].[product_details] CHECK CONSTRAINT [FK_product_details_size]
GO
ALTER TABLE [dbo].[promotions]  WITH CHECK ADD CHECK  (([Discount]>=(0) AND [Discount]<=(100)))
GO
USE [master]
GO
ALTER DATABASE [final_graduation] SET  READ_WRITE 
GO
