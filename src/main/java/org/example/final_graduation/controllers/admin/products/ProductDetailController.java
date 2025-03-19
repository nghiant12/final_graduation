package org.example.final_graduation.controllers.admin.products;

import jakarta.validation.Valid;
import org.example.final_graduation.entities.ProductDetail;
import org.example.final_graduation.repositories.products.ProductDetailRepository;
import org.example.final_graduation.repositories.products.ProductRepository;
import org.example.final_graduation.repositories.products.attributes.BrandRepository;
import org.example.final_graduation.repositories.products.attributes.CategoryRepository;
import org.example.final_graduation.repositories.products.attributes.ColorRepository;
import org.example.final_graduation.repositories.products.attributes.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/admin/product-details")
public class ProductDetailController {
    @Autowired
    private ProductDetailRepository productDetailRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private SizeRepository sizeRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @ModelAttribute("formData")
    public void addAttributes(Model model) {
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("sizes", sizeRepository.findAll());
        model.addAttribute("colors", colorRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
    }

    @GetMapping("/list")
    public String listProductDetails(Model model) {
        model.addAttribute("productDetails", productDetailRepository.findAll());
        model.addAttribute("productDetail", new ProductDetail());
        addAttributes(model);
        return "product_details/list";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addProductDetail(@Valid @ModelAttribute("productDetail") ProductDetail productDetail,
                                              BindingResult bindingResult,
                                              @RequestParam("imageFile") MultipartFile imageFile) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        if (productDetail.getPrice() == null || productDetail.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Price must be a positive number");
        }

        if (productDetail.getQuantity() == null || productDetail.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("Quantity must be a positive number");
        }

        try {
            if (!imageFile.isEmpty()) {
                String uploadDir = "static/images/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String filename = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(imageFile.getOriginalFilename());
                Path filePath = uploadPath.resolve(filename);

                try (InputStream inputStream = imageFile.getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    productDetail.setImage("/images/" + filename);
                }
            }

            ProductDetail savedProductDetail = productDetailRepository.save(productDetail);
            return ResponseEntity.ok().body(savedProductDetail);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add product detail: " + e.getMessage());
        }
    }



    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getProductDetail(@PathVariable Integer id) {
        try {
            ProductDetail productDetail = productDetailRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product detail Id:" + id));
            return ResponseEntity.ok().body(productDetail);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch product detail: " + e.getMessage());
        }
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateProductDetail(@PathVariable Integer id,
                                                 @Valid @ModelAttribute("productDetail") ProductDetail productDetail,
                                                 BindingResult bindingResult,
                                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        if (productDetail.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Price must be a positive number");
        }

        if (productDetail.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("Quantity must be a positive number");
        }

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String uploadDir = "uploads/products/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String filename = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(imageFile.getOriginalFilename());
                Path filePath = uploadPath.resolve(filename);

                try (InputStream inputStream = imageFile.getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    productDetail.setImage("/uploads/products/" + filename);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image: " + e.getMessage());
                }
            }

            ProductDetail savedProductDetail = productDetailRepository.save(productDetail);
            return ResponseEntity.ok().body(savedProductDetail);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update product detail: " + e.getMessage());
        }
    }


    @GetMapping("/delete/{id}")
    public String deleteProductDetail(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        ProductDetail productDetail = productDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product detail Id:" + id));
        productDetailRepository.delete(productDetail);
        redirectAttributes.addFlashAttribute("message", "Product detail deleted successfully!");
        return "redirect:/admin/product-details/list";
    }

    @GetMapping("/detail/{id}")
    public String viewProductDetail(@PathVariable Integer id, Model model) {
        ProductDetail productDetail = productDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product detail Id:" + id));
        model.addAttribute("productDetail", productDetail);
        return "product_details/detail";
    }

}
