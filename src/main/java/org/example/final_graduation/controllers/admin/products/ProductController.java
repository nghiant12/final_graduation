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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

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
            @RequestParam(value = "image", required = false) String image,
            @RequestParam("available") Boolean available,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        try {
            // Kiểm tra sản phẩm chi tiết có bị trùng không
            long count = productDetailRepository.countExistingProductDetail(productId, brandId, colorId, sizeId);
            if (count > 0) {
                redirectAttributes.addFlashAttribute("error", "Sản phẩm chi tiết đã tồn tại!");
                return "redirect:/admin/products/detail?idProduct=" + productId;
            }

            // Kiểm tra xem sản phẩm có tồn tại không
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm!");
                return "redirect:/admin/products";
            }

            // Kiểm tra các thuộc tính liên quan
            Category category = categoryRepository.findById(categoryId).orElse(null);
            if (category == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy danh mục!");
                return "redirect:/admin/products";
            }

            Brand brand = brandRepository.findById(brandId).orElse(null);
            if (brand == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thương hiệu!");
                return "redirect:/admin/products";
            }

            Color color = colorRepository.findById(colorId).orElse(null);
            if (color == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy màu sắc!");
                return "redirect:/admin/products";
            }

            Size size = sizeRepository.findById(sizeId).orElse(null);
            if (size == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy kích thước!");
                return "redirect:/admin/products";
            }

            // Tạo đối tượng ProductDetail mới
            ProductDetail productDetail = new ProductDetail();
            productDetail.setProduct(product);
            productDetail.setCategory(category);
            productDetail.setBrand(brand);
            productDetail.setColor(color);
            productDetail.setSize(size);
            productDetail.setPrice(price);
            productDetail.setQuantity(quantity);
            productDetail.setAvailable(available);
            productDetail.setCreatedDate(LocalDateTime.now());

            // Xử lý ảnh
            if (imageFile != null && !imageFile.isEmpty()) {
                String uploadDir = "uploads/products/";
                String fileName = UUID.randomUUID().toString() + "_" + Paths.get(imageFile.getOriginalFilename()).getFileName().toString();
                Path uploadPath = Paths.get(uploadDir);

                // Tạo thư mục nếu chưa tồn tại
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Kiểm tra định dạng file
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
                if (!allowedExtensions.contains(fileExtension)) {
                    redirectAttributes.addFlashAttribute("error", "Chỉ cho phép tải lên file JPG, PNG hoặc GIF.");
                    return "redirect:/admin/products/detail?idProduct=" + productId;
                }

                // Lưu file
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                productDetail.setImage("/" + uploadDir + fileName);
            } else if (image != null && !image.isEmpty()) {
                productDetail.setImage(image);
            } else {
                productDetail.setImage("/uploads/products/default.png"); // Ảnh mặc định
            }

            // Lưu vào database
            productDetailRepository.save(productDetail);

            redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm chi tiết thành công!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi tải lên file!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi!");
        }

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
            @RequestParam(value = "image", required = false) String image,
            @RequestParam("available") Boolean available,
            RedirectAttributes redirectAttributes,
            @RequestParam("imageFile") MultipartFile imageFile) {

        ProductDetail productDetail = productDetailRepository.findById(id).orElse(null);
        if (productDetail == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm chi tiết!");
            return "redirect:/admin/products";
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String uploadDir = "uploads/products/";
                String fileName = UUID.randomUUID().toString() + "_" + Paths.get(imageFile.getOriginalFilename()).getFileName().toString();
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
                if (!allowedExtensions.contains(fileExtension)) {
                    redirectAttributes.addFlashAttribute("error", "Invalid file type! Only JPG, PNG, and GIF are allowed.");
                    return "redirect:/admin/products";
                }

                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                productDetail.setImage("/" + uploadDir + fileName);
            } else if (image != null && !image.isEmpty()) {
                productDetail.setImage(image);
            }

            Brand brand = brandRepository.findById(brandId).orElse(null);
            if (brand == null) {
                redirectAttributes.addFlashAttribute("error", "Brand not found!");
                return "redirect:/admin/products";
            }

            Color color = colorRepository.findById(colorId).orElse(null);
            if (color == null) {
                redirectAttributes.addFlashAttribute("error", "Color not found!");
                return "redirect:/admin/products";
            }

            Size size = sizeRepository.findById(sizeId).orElse(null);
            if (size == null) {
                redirectAttributes.addFlashAttribute("error", "Size not found!");
                return "redirect:/admin/products";
            }

            productDetail.setBrand(brand);
            productDetail.setColor(color);
            productDetail.setSize(size);
            productDetail.setPrice(price);
            productDetail.setQuantity(quantity);
            productDetail.setAvailable(available);

            productDetailRepository.save(productDetail);

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "File upload failed!");
            return "redirect:/admin/products";
        }

        return "redirect:/admin/products/detail?idProduct=" + productDetail.getProduct().getId();
    }

}
