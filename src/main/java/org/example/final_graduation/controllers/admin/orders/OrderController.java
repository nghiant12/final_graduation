package org.example.final_graduation.controllers.admin.orders;

import org.example.final_graduation.entities.*;
import org.example.final_graduation.repositories.CustomerRepository;
import org.example.final_graduation.repositories.EmployeeRepository;
import org.example.final_graduation.repositories.orders.OrderDetailRepository;
import org.example.final_graduation.repositories.orders.OrderRepository;
import org.example.final_graduation.repositories.products.ProductDetailRepository;
import org.example.final_graduation.repositories.products.ProductRepository;
import org.example.final_graduation.repositories.products.attributes.ColorRepository;
import org.example.final_graduation.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductDetailService productDetailService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EmpolyeeService empolyeeService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SizeService sizeService;

    @Autowired
    private ColorService colorService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private BrandService brandService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private OrderDetailService orderDetailService;

    // Hiển thị form tạo đơn hàng mới
    @GetMapping("")
    public String showCreateOrderForm(Model model) {
        Order order = new Order();
        // Giả sử bạn có người dùng đang đăng nhập (user)
        // Bạn có thể lấy từ session hoặc security context
        // Ví dụ đơn giản:
        // Thiết lập admin nếu cần
        Optional<Employee> adminUser = empolyeeService.getEmployeeById(4); // Giả sử admin ID = 2
        adminUser.ifPresent(order::setEmployee);
        model.addAttribute("order", order);

        model.addAttribute("productDetails", productDetailRepository.findALL());

        List<Order> orders = orderRepository.findAllProcessing();
        model.addAttribute("orders", orders);

        return "admin/orders/order_form";
    }

    // Xử lý tạo đơn hàng mới
    @PostMapping("/create")
    public String createOrder(RedirectAttributes redirectAttributes) {
        try {
            Integer soLuongHoaDonCho = orderRepository.countByTypeStatus();

            if (soLuongHoaDonCho >= 5) {
                redirectAttributes.addFlashAttribute("error", "Vượt quá số lượng hóa đơn chờ! Không thể tạo thêm.");
                return "redirect:/admin/orders";
            }

            orderService.createNewOrder(); // Lưu hóa đơn
            redirectAttributes.addFlashAttribute("success", "Hóa đơn được tạo thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể tạo hóa đơn!");
        }
        return "redirect:/admin/orders"; // Điều hướng lại trang danh sách hóa đơn
    }


    @GetMapping("/detail")
    public String showOrderDetail(@RequestParam Integer idOrder, Model model) {
        // Lấy danh sách hóa đơn
        model.addAttribute("idOrder", idOrder);

        List<Order> orders = orderRepository.findAllProcessing();
        model.addAttribute("orders", orders);

        List<ProductDetail> productDetails = productDetailRepository.findALL();
        System.out.println("Số sản phẩm: " + productDetails.size()); // Debug dữ liệu

        // Lấy chi tiết hóa đơn dựa trên idOrder

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderID(idOrder);
        model.addAttribute("orderDetails", orderDetails);

        model.addAttribute("productDetails", productDetails);

        model.addAttribute("totalPrice", orderDetailRepository.tongTien(idOrder));

        // Gửi thêm thông tin nếu cần thiết
        Optional<Order> selectedOrder = orderRepository.findById(idOrder);
        selectedOrder.ifPresent(order -> model.addAttribute("order", order));

        return "admin/orders/order_form";
    }


    // Xử lý hủy đơn hàng
    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable("id") Integer orderId, RedirectAttributes redirectAttributes) {
        try {
            // Lấy danh sách OrderDetail của đơn hàng
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrderID(orderId);

            // Hoàn trả số lượng sản phẩm vào kho
            for (OrderDetail orderDetail : orderDetails) {
                ProductDetail productDetail = orderDetail.getProductDetail();
                int quantityToReturn = orderDetail.getQuantity();

                if (productDetail != null) {
                    productDetail.setQuantity(productDetail.getQuantity() + quantityToReturn);
                    productDetailService.saveProductDetail(productDetail);
                }
            }

            // Hủy đơn hàng
            orderService.cancelOrder(orderId);

            // Xóa đơn hàng khỏi DB
            orderService.deleteOrderById(orderId);

            redirectAttributes.addFlashAttribute("success", "Hóa đơn đã được hủy, sản phẩm đã được hoàn kho.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể hủy hóa đơn: " + e.getMessage());
        }

        return "redirect:/admin/orders";
    }


    // API Lấy danh sách sản phẩm
    @GetMapping("/products")
    @ResponseBody
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // API Lấy danh sách thương hiệu, màu sắc, kích thước theo sản phẩm
    @GetMapping("/productDetails")
    @ResponseBody
    public Map<String, Object> getProductDeatils(@RequestParam("productId") Integer productId) {
        Map<String, Object> response = new HashMap<>();
        response.put("brands", brandService.getAvailableBrandsForProduct(productId));
        response.put("colors", colorService.getAvailableColorsForProduct(productId));
        response.put("sizes", sizeService.getAvailableSizesForProduct(productId));
        return response;
    }

    // API Lấy giá sản phẩm chi tiết theo thuộc tính
    @GetMapping("/productPrice")
    @ResponseBody
    public Map<String, Object> getProductPrice(@RequestParam("productId") Integer productId, @RequestParam("brandId") Integer brandId, @RequestParam("colorId") Integer colorId, @RequestParam("sizeId") Integer sizeId) {
        Optional<ProductDetail> productDetail = productDetailService.getProductDetail(productId, brandId, colorId, sizeId);
        if (productDetail.isPresent()) {
            return Map.of("price", productDetail.get().getPrice(), "productDetailId", productDetail.get().getId());
        } else {
            return Map.of("error", "Không tìm thấy sản phẩm phù hợp");
        }
    }

    // API Thêm sản phẩm vào đơn hàng
    @PostMapping("/addProductToOrder")
    public String addProductToOrder(
            @RequestParam(required = false) Integer orderId,
            @RequestParam Integer productDetailId,
            @RequestParam Integer quantity,
            RedirectAttributes redirectAttributes
    ) {
        // Kiểm tra nếu orderID không tồn tại
        if (orderId == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn đơn hàng!");
            return "redirect:/admin/orders";
        }
        try {
            ProductDetail productDetail = productDetailService.findById(productDetailId);
            if (productDetail.getQuantity() < quantity) {
                redirectAttributes.addFlashAttribute("error", "Số lượng sản phẩm không đủ!");
                return "redirect:/admin/orders/detail?idOrder=" + orderId;
            }
            orderDetailService.addProductToOrder(orderId, productDetailId, quantity);
            productDetail.setQuantity(productDetail.getQuantity() - quantity);
            productDetailService.saveProductDetail(productDetail);

            redirectAttributes.addFlashAttribute("success", "Đã thêm sản phẩm vào hóa đơn!");
        } catch (Exception e) {
            // Xử lý lỗi nếu có vấn đề xảy ra khi thêm sản phẩm
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
        }

        redirectAttributes.addFlashAttribute("totalPrice", orderDetailRepository.tongTien(orderId));
        // Quay về trang chi tiết đơn hàng
        return "redirect:/admin/orders/detail?idOrder=" + orderId;
    }

    // Xử lý tăng/giảm số lượng sản phẩm trong đơn hàng
    @PostMapping("/updateQuantity")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateQuantity(@RequestParam("orderDetailId") Integer orderDetailId,
                                                              @RequestParam("action") String action) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(orderDetailId);

            if (optionalOrderDetail.isPresent()) {
                OrderDetail orderDetail = optionalOrderDetail.get();
                Order order = orderDetail.getOrder();
                Integer orderId = order.getId();
                ProductDetail productDetail = orderDetail.getProductDetail();

                if (orderId == null || productDetail == null) {
                    response.put("success", false);
                    response.put("message", "Không tìm thấy đơn hàng hoặc sản phẩm.");
                    return ResponseEntity.badRequest().body(response);
                }

                int currentQuantity = orderDetail.getQuantity();
                int quantityInStock = productDetail.getQuantity();
                BigDecimal unitPrice = productDetail.getPrice(); // Đơn giá của sản phẩm

                // Xử lý tăng hoặc giảm số lượng
                if ("increase".equals(action)) {
                    if (quantityInStock > 0) {
                        orderDetail.setQuantity(currentQuantity + 1);
                        productDetail.setQuantity(quantityInStock - 1);
                    } else {
                        response.put("success", false);
                        response.put("message", "Sản phẩm đã hết hàng!");
                        return ResponseEntity.badRequest().body(response);
                    }
                } else if ("decrease".equals(action) && currentQuantity > 1) {
                    orderDetail.setQuantity(currentQuantity - 1);
                    productDetail.setQuantity(quantityInStock + 1);
                } else {
                    response.put("success", false);
                    response.put("message", "Yêu cầu không hợp lệ!");
                    return ResponseEntity.badRequest().body(response);
                }

                // Cập nhật lại giá của OrderDetail theo số lượng mới
                BigDecimal newPrice = unitPrice.multiply(BigDecimal.valueOf(orderDetail.getQuantity()));
                orderDetail.setPrice(newPrice);

                // Cập nhật tổng giá trị đơn hàng
                BigDecimal totalPrice = orderDetailRepository.tongTien(orderId);
                order.setTotalPrice(totalPrice);

                // Lưu thay đổi vào DB
                productDetailService.saveProductDetail(productDetail);
                orderDetailRepository.save(orderDetail);
                orderService.saveOrder(order);

                // ✅ Thêm ID sản phẩm vào JSON để cập nhật bảng
                response.put("success", true);
                response.put("newQuantity", orderDetail.getQuantity());
                response.put("totalPrice", totalPrice);
                response.put("stockRemaining", productDetail.getQuantity());
                response.put("productDetailId", productDetail.getId()); // Thêm ID sản phẩm
                response.put("message", "Số lượng sản phẩm đã được cập nhật!");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy chi tiết đơn hàng.");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi cập nhật số lượng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }







    // Xử lý xóa sản phẩm khỏi đơn hàng
    @PostMapping("/removeProduct")
    public String removeProduct(@RequestParam("orderDetailId") Integer orderDetailId, RedirectAttributes redirectAttributes) {
        try {
            Optional<OrderDetail> optionalOrderDetail = orderDetailRepository.findById(orderDetailId);
            if (optionalOrderDetail.isPresent()) {
                OrderDetail orderDetail = optionalOrderDetail.get();
                Order order = orderDetail.getOrder();
                ProductDetail productDetail = orderDetail.getProductDetail(); // Lấy sản phẩm từ order detail

                if (order != null && productDetail != null) {
                    // Cộng lại số lượng vào tồn kho
                    int quantityToReturn = orderDetail.getQuantity();
                    productDetail.setQuantity(productDetail.getQuantity() + quantityToReturn);

                    // Cập nhật tổng giá trị đơn hàng
                    BigDecimal priceTotal = orderDetail.getPrice().multiply(new BigDecimal(quantityToReturn));
                    order.setTotalPrice(order.getTotalPrice().subtract(priceTotal));

                    // Xóa chi tiết đơn hàng khỏi đơn hàng
                    order.removeOrderDetail(orderDetail);
                    orderService.saveOrder(order);
                    productDetailService.saveProductDetail(productDetail); // Lưu thay đổi vào database
                    orderDetailRepository.delete(orderDetail); // Xóa OrderDetail khỏi DB

                    // Thêm thông báo thành công
                    redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được xóa khỏi đơn hàng!");
                    redirectAttributes.addFlashAttribute("totalPrice", orderDetailRepository.tongTien(order.getId()));

                    return "redirect:/admin/orders/detail?idOrder=" + order.getId();
                } else {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng hoặc sản phẩm.");
                    return "redirect:/admin/orders";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy chi tiết đơn hàng.");
                return "redirect:/admin/orders";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa sản phẩm: " + e.getMessage());
            return "redirect:/admin/orders";
        }
    }



    // Xử lý xác nhận đặt hàng
    @PostMapping("/confirm/{id}")
    public String confirmOrder(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Order> optionalOrder = orderService.getOrderById(id);

            if (optionalOrder.isPresent()) {
                Order order = optionalOrder.get();

                // Kiểm tra nếu đơn hàng chưa có sản phẩm
                List<OrderDetail> orderDetails = orderDetailRepository.findByOrderID(order.getId());
                if (orderDetails.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Đơn hàng chưa có sản phẩm, vui lòng thêm sản phẩm trước khi xác nhận.");
                    return "redirect:/admin/orders/detail?idOrder=" + order.getId();
                }

                // Kiểm tra nếu đơn hàng chưa có khách hàng -> Set mặc định account có id = 1
                if (order.getCustomer() == null) {
                    Optional<Customer> defaultAccount = customerRepository.findById(1); // Tìm account có id = 1
                    if (defaultAccount.isPresent()) {
                        order.setCustomer(defaultAccount.get()); // Gán khách hàng mặc định
                    } else {
                        redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản mặc định.");
                        return "redirect:/admin/orders/detail?idOrder=" + order.getId();
                    }
                }

                // Tính tổng tiền đơn hàng
                BigDecimal totalPrice = orderDetailRepository.tongTien(order.getId());
                order.setTotalPrice(totalPrice != null ? totalPrice : BigDecimal.ZERO);

                // Cập nhật trạng thái đơn hàng
                order.setStatus("Confirmed");
                orderService.saveOrder(order);
                redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được xác nhận!");

            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xác nhận đơn hàng: " + e.getMessage());
        }

        return "redirect:/admin/orders";
    }


//    // Lấy danh sách khách hàng và hiển thị modal chọn khách hàng
//    @GetMapping("/{orderId}")
//    public String viewOrder(@PathVariable("orderId") Integer orderId, Model model) {
//        Optional<Order> order = orderService.getOrderById(orderId);
//        List<Authority> customers = accountService.getAllCustomers(); // Lấy danh sách khách hàng
//        model.addAttribute("order", order);
//        model.addAttribute("customers", customers); // Truyền danh sách khách hàng vào view
//        return "admin/orders/order_form"; // Tên trang hiện tại mà bạn muốn hiển thị
//    }

    // Xử lý chọn khách hàng từ modal
    @PostMapping("/selectCustomer/{orderId}")
    public String selectCustomer(@PathVariable("orderId") Integer orderId,
                                 @RequestParam("customerId") Integer customerId,
                                 @RequestParam("idOrder") Integer idOrder,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra dữ liệu đầu vào
            if (orderId == null || customerId == null) {
                redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ!");
                return "redirect:/admin/orders/detail?idOrder=" + orderId;
            }

            // Thêm khách hàng vào đơn hàng
            orderService.addCustomerToOrder(orderId, customerId);
            redirectAttributes.addFlashAttribute("success", "Đã thêm khách hàng vào hóa đơn!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm khách hàng vào hóa đơn!");
            e.printStackTrace(); // Log lỗi để debug
        }

        return "redirect:/admin/orders/detail?idOrder=" + orderId;
    }


    @ResponseBody
    @GetMapping("/searchCustomer")
    public List<Customer> searchCustomer(@RequestParam String query) {
        System.out.println("Searching for customers with query: " + query);
        List<Customer> customers = customerRepository.findCustomers(query);
        System.out.println("Found customers: " + customers.size());
        return customers;
    }

}
