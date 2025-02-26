//package org.example.final_graduation.controllers.admin.orders;
//
//import org.example.final_graduation.entities.Order;
//import org.example.final_graduation.entities.OrderDetail;
//import org.example.final_graduation.entities.ProductDetail;
//import org.example.final_graduation.repositories.orders.OrderDetailRepository;
//import org.example.final_graduation.services.OrderService;
//import org.example.final_graduation.services.ProductDetailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/admin/ordersa")
//public class OrderRestController {
//
//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    private ProductDetailService productDetailService;
//
//    @Autowired
//    private AccountService accountService;
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Autowired
//    private OrderDetailRepository orderDetailRepository;
//
//    // Hiển thị form tạo đơn hàng mới
//
//    @GetMapping("/searchCustomera")
//    public List<Account> searchCustomer(@RequestParam String query) {
//        System.out.println("aaaaaa" + accountRepository.findCustomers(query).size());
//        return accountRepository.findCustomers(query);
//    }
//
//
//    // Xử lý tạo đơn hàng mới
//    @PostMapping("/addProducta")
//    public ResponseEntity<OrderDetail> addProductToOrder() {
//
//        // Kiểm tra sự tồn tại của đơn hàng và sản phẩm
//        Optional<Order> optionalOrder = orderService.getOrderById(1);
//        Optional<ProductDetail> optionalProduct = productDetailService.getProductDetailById(3);
//
//        // Nếu cả đơn hàng và sản phẩm đều tồn tại
//        if (optionalOrder.isPresent() && optionalProduct.isPresent()) {
//            Order order = optionalOrder.get();
//            ProductDetail product = optionalProduct.get();
//
//            // In thông tin kiểm tra
//            System.out.println("aaaaa" + order.getId());
//            System.out.println(product.getProduct().getName());
//
//            // Tạo một OrderDetail mới và thiết lập các giá trị
//            OrderDetail orderDetail = new OrderDetail();
//            orderDetail.setOrder(order);
//            orderDetail.setProductDetail(product);
//            orderDetail.setPrice(BigDecimal.valueOf(4.0));
//            orderDetail.setQuantity(5);
//
//            OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);
//
//            // Trả về thông tin chi tiết đơn hàng mới đã lưu
//            return ResponseEntity.ok(savedOrderDetail);
//
//        } else {
//            // Nếu không tìm thấy đơn hàng hoặc sản phẩm, trả về mã lỗi 404
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//}
