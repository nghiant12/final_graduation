package org.example.final_graduation.controllers.admin.products;

import org.example.final_graduation.entities.*;
import org.example.final_graduation.repositories.products.ProductDetailRepository;
import org.example.final_graduation.repositories.products.ProductRepository;
import org.example.final_graduation.repositories.products.attributes.BrandRepository;
import org.example.final_graduation.repositories.products.attributes.CategoryRepository;
import org.example.final_graduation.repositories.products.attributes.ColorRepository;
import org.example.final_graduation.repositories.products.attributes.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("admin/products")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("")
    public String index(Model model) {
        List<Product> products = productRepository.findAll();
        Map<Integer, Long> productQuantities = new HashMap<>();

        for (Product product : products) {
            long totalQuantity = productDetailRepository.sumQuantityByProductId(product.getId());
            productQuantities.put(product.getId(), totalQuantity);
        }
        Product p = new Product();
        p.setStatus(true);
        model.addAttribute("products", products);
        model.addAttribute("productQuantities", productQuantities);
        model.addAttribute("product", p);

        return "admin/products/index";
    }


    @PostMapping("add")
    public String add(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        if (product.getStatus() == null) {
            product.setStatus(true); // Hoặc false nếu muốn mặc định là không hoạt động
        }
        boolean exists = productRepository.existsByName(product.getName());
        if (exists) {
            redirectAttributes.addFlashAttribute("error", "Tên sản phẩm đã tồn tại!");
            return "redirect:/admin/products";
        }
        productRepository.save(product);
        redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công!");
        return "redirect:/admin/products";
    }

    @PostMapping("/update")
    public String edit(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        // Kiểm tra tên sản phẩm, nhưng bỏ qua sản phẩm hiện tại
        boolean exists = productRepository.existsByName(product.getName()) &&
                !productRepository.findByID(product.getId()).getName().equals(product.getName());
        if (exists) {
            redirectAttributes.addFlashAttribute("error", "Tên sản phẩm đã tồn tại!");
            return "redirect:/admin/products";
        }

        productRepository.save(product);
        redirectAttributes.addFlashAttribute("success", "Chỉnh sửa sản phẩm thành công!");
        return "redirect:/admin/products"; // Quay lại trang danh sách
    }

    @GetMapping("/edit/{idProduct}")
    public String editForm(@PathVariable("idProduct") Integer idProduct, Model model, RedirectAttributes redirectAttributes) {
        Product productEdit = productRepository.findByID(idProduct);

        if (productEdit == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm.");
            return "redirect:/admin/products";
        }

        model.addAttribute("product", productEdit);
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "admin/products/index";
    }


    @GetMapping("/detail")
    public String detail(@RequestParam("idProduct") Integer idProduct, Model model) {
        List<ProductDetail> productDetails = productDetailRepository.findByProductID(idProduct);
        model.addAttribute("productDetails", productDetails);

        model.addAttribute("idProduct", idProduct);

        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        List<Brand> brands = brandRepository.findAll();
        model.addAttribute("brands", brands);

        List<Color> colors = colorRepository.findAll();
        model.addAttribute("colors", colors);

        List<Size> sizes = sizeRepository.findAll();
        model.addAttribute("sizes", sizes);

        return "admin/products/detail";
    }

    @PostMapping("/addDetail")
    public String addDetail(
            @RequestParam("product.id") Integer productId,
            @RequestParam("brand.id") Integer brandId,
            @RequestParam("category.id") Integer categoryId,
            @RequestParam("color.id") Integer colorId,
            @RequestParam("size.id") Integer sizeId,
            @RequestParam("price") BigDecimal price,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("image") String image,
            @RequestParam("available") Boolean available,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra sản phẩm chi tiết có bị trùng không
        long count = productDetailRepository.countExistingProductDetail(productId, brandId, colorId, sizeId);
        if (count > 0) {
            redirectAttributes.addFlashAttribute("error", "Sản phẩm chi tiết đã tồn tại!");
            return "redirect:/admin/products/detail?idProduct=" + productId;
        }

        // Tạo đối tượng ProductDetail từ tham số nhận được
        ProductDetail productDetail = new ProductDetail();

        // Lấy Product từ database
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Product not found!");
            return "redirect:/admin/products";
        }
        productDetail.setProduct(product);

        productDetail.setCategory(categoryRepository.findById(categoryId).orElse(null));
        productDetail.setBrand(brandRepository.findById(brandId).orElse(null));
        productDetail.setColor(colorRepository.findById(colorId).orElse(null));
        productDetail.setSize(sizeRepository.findById(sizeId).orElse(null));

        // Gán các giá trị còn lại
        productDetail.setPrice(price);
        productDetail.setQuantity(quantity);
        productDetail.setImage(image);
        productDetail.setAvailable(available);
        productDetail.setCreatedDate(LocalDateTime.now());

        // Lưu vào database
        productDetailRepository.save(productDetail);

        return "redirect:/admin/products/detail?idProduct=" + productId;
    }

    @PostMapping("/updateDetail")
    public String updateDetail(
            @RequestParam("id") Integer id,
            @RequestParam("brand.id") Integer brandId,
            @RequestParam("color.id") Integer colorId,
            @RequestParam("size.id") Integer sizeId,
            @RequestParam("price") BigDecimal price,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("image") String image,
            @RequestParam("available") Boolean available,
            RedirectAttributes redirectAttributes) {

        // Tìm sản phẩm chi tiết cần cập nhật
        ProductDetail productDetail = productDetailRepository.findById(id).orElse(null);
        if (productDetail == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm chi tiết!");
            return "redirect:/admin/products";
        }

        // Cập nhật thông tin
        productDetail.setBrand(brandRepository.findById(brandId).orElse(null));
        productDetail.setColor(colorRepository.findById(colorId).orElse(null));
        productDetail.setSize(sizeRepository.findById(sizeId).orElse(null));
        productDetail.setPrice(price);
        productDetail.setQuantity(quantity);
        productDetail.setImage(image);
        productDetail.setAvailable(available);

        // Lưu vào database
        productDetailRepository.save(productDetail);

        return "redirect:/admin/products/detail?idProduct=" + productDetail.getProduct().getId();
    }

}
