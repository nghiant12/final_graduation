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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
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
        return "product_details/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("productDetail", new ProductDetail());
        // Add necessary data for dropdowns
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("sizes", sizeRepository.findAll());
        model.addAttribute("colors", colorRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        return "product_details/add";
    }

    @PostMapping("/add")
    public String addProductDetail(@Valid @ModelAttribute("productDetail") ProductDetail productDetail,
                                   BindingResult bindingResult,
                                   @RequestParam("imageFile") MultipartFile imageFile,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            // Re-add necessary data for dropdowns
            model.addAttribute("products", productRepository.findAll());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("sizes", sizeRepository.findAll());
            model.addAttribute("colors", colorRepository.findAll());
            model.addAttribute("brands", brandRepository.findAll());
            return "product_details/add";
        }

        try {
            if (!imageFile.isEmpty()) {
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
                    throw new IOException("Could not save image file: " + filename, e);
                }
            }

            ProductDetail savedProductDetail = productDetailRepository.save(productDetail);

            redirectAttributes.addFlashAttribute("success", "Product detail added successfully! ID: " + savedProductDetail.getId());
            return "redirect:/admin/product-details/list";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload image: " + e.getMessage());
            // Re-add necessary data for dropdowns
            model.addAttribute("products", productRepository.findAll());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("sizes", sizeRepository.findAll());
            model.addAttribute("colors", colorRepository.findAll());
            model.addAttribute("brands", brandRepository.findAll());
            return "product_details/add";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add product detail: " + e.getMessage());
            // Re-add necessary data for dropdowns
            model.addAttribute("products", productRepository.findAll());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("sizes", sizeRepository.findAll());
            model.addAttribute("colors", colorRepository.findAll());
            model.addAttribute("brands", brandRepository.findAll());
            return "product_details/add";
        }
    }



    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        ProductDetail productDetail = productDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product detail Id:" + id));
        model.addAttribute("productDetail", productDetail);
        return "product_details/edit";
    }

    @PostMapping("/update/{id}")
    public String updateProductDetail(@PathVariable Integer id,
                                      @Valid @ModelAttribute("productDetail") ProductDetail productDetail,
                                      BindingResult bindingResult,
                                      @RequestParam("imageFile") MultipartFile imageFile,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "product_details/edit";
        }

        if (!imageFile.isEmpty()) {
            String filename = StringUtils.cleanPath(imageFile.getOriginalFilename());
            try {
                Path path = Paths.get("uploads/products/" + filename);
                Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                productDetail.setImage("/uploads/products/" + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        productDetailRepository.save(productDetail);
        redirectAttributes.addFlashAttribute("message", "Product detail updated successfully!");
        return "redirect:/admin/product-details/list";
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

    @GetMapping("/json")
    @ResponseBody
    public List<ProductDetail> getProductDetailsJson() {
        return productDetailRepository.findALL();
    }

}
