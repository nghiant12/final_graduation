package org.example.final_graduation.controllers;

import java.text.NumberFormat;
import java.util.*;

import org.example.final_graduation.entities.Category;
import org.example.final_graduation.entities.ProductDetail;
import org.example.final_graduation.repositories.products.ProductDetailRepository;
import org.example.final_graduation.repositories.products.attributes.CategoryRepository;
import org.example.final_graduation.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("product")
public class ProductCtrl {
    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepo;

    @Autowired
    ProductDetailRepository productDetailRepo;

    @Autowired
    HttpSession session;

    @ModelAttribute("categoryCounts")
    public Map<Category, Long> categoryCounts() {
        return categoryService.countProductsByCategory();
    }

    @RequestMapping("list")
    public String list(Model model,
                       @RequestParam("cid") Optional<String> cid,
                       @RequestParam("sortType") Optional<String> sortType,
                       @RequestParam(defaultValue = "1") int pageNo,
                       @RequestParam(defaultValue = "10") int pageSize) {
        Sort sort;
        Page<ProductDetail> page;
        if (sortType.isPresent() && sortType.get().equalsIgnoreCase("desc")) {
            sort = Sort.by(Sort.Direction.DESC, "price");
            model.addAttribute("sortType", sortType.get());
        } else if (sortType.isPresent() && sortType.get().equalsIgnoreCase("asc")) {
            sort = Sort.by(Sort.Direction.ASC, "price");
            model.addAttribute("sortType", sortType.get());
        } else {
            sort = Sort.by(Sort.Direction.ASC, "product.name");
        }

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

        if (cid.isPresent()) {
            session.setAttribute("categoryId", cid.get());
            page = productDetailRepo.findByCategoryId(cid.get(), pageable);
            model.addAttribute("cname", categoryRepo.getReferenceById(Integer.parseInt(cid.get())).getName());
            model.addAttribute("cid", categoryRepo.getReferenceById(Integer.parseInt(cid.get())).getId());
        } else {
            page = productDetailRepo.paging(pageable);
            model.addAttribute("cname", "ALL");
        }
        model.addAttribute("page", page);
        return "product/list";
    }

    @GetMapping("filter")
    public String range(Model model,
                        @RequestParam(required = false) Double fromPrice,
                        @RequestParam(required = false) Double toPrice,
                        @RequestParam(required = false) List<Integer> categoryIds,
                        @RequestParam("sortType") Optional<String> sortType,
                        @RequestParam(defaultValue = "1") int pageNo,
                        @RequestParam(defaultValue = "10") int pageSize) {

        // Format giá
        Locale vn = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getInstance(vn);
        String fromFormatted = currencyFormatter.format(fromPrice.intValue());
        String toFormatted = currencyFormatter.format(toPrice.intValue());

        // Sắp xếp
        Sort sort = sortType.map(type -> {
            if ("desc".equalsIgnoreCase(type)) return Sort.by(Sort.Direction.DESC, "price");
            if ("asc".equalsIgnoreCase(type)) return Sort.by(Sort.Direction.ASC, "price");
            return Sort.by(Sort.Direction.ASC, "category.name");
        }).orElse(Sort.by(Sort.Direction.ASC, "category.name"));

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

        // Xử lý danh mục
        List<Category> categoryEntities = new ArrayList<>();
        StringBuilder categoryParamBuilder = new StringBuilder();
        StringBuilder conditionBuilder = new StringBuilder();

        if (categoryIds != null && !categoryIds.isEmpty()) {
            for (Integer id : categoryIds) {
                categoryRepo.findById(id).ifPresent(category -> {
                    categoryEntities.add(category);
                    categoryParamBuilder.append("&categoryIds=").append(id);
                    conditionBuilder.append(category.getName()).append(", ");
                });
            }

            model.addAttribute("categoryIds", categoryParamBuilder.toString());
        }

        Page<ProductDetail> page = (categoryEntities.isEmpty())
                ? productDetailRepo.findByPriceBetween(fromPrice, toPrice, pageable)
                : productDetailRepo.findByPriceBetweenAndCategoryIn(fromPrice, toPrice, categoryEntities, pageable);

        // Hiển thị điều kiện lọc
        String showCondition = conditionBuilder.toString();
        if (showCondition.endsWith(", ")) {
            showCondition = showCondition.substring(0, showCondition.length() - 2);
        }

        if (categoryEntities.isEmpty()) {
            model.addAttribute("cname", fromFormatted + " ₫ đến " + toFormatted + " ₫");
        } else if (fromPrice != 0 || toPrice != 5000000) {
            model.addAttribute("cname", showCondition + ": " + fromFormatted + " ₫ đến " + toFormatted + " ₫");
        } else {
            model.addAttribute("cname", showCondition);
        }

        model.addAttribute("fromPriceFormatted", fromFormatted);
        model.addAttribute("toPriceFormatted", toFormatted);
        model.addAttribute("page", page);

        return "product/list";
    }

    @RequestMapping("search")
    public String search(Model model, @RequestParam("name") String name,
                         @RequestParam(defaultValue = "1") int pageNo,
                         @RequestParam(defaultValue = "10") int pageSize,
                         @RequestParam("sortType") Optional<String> sortType) {
        Sort sort;
        if (sortType.isPresent() && sortType.get().equalsIgnoreCase("desc")) {
            sort = Sort.by(Sort.Direction.DESC, "price");
            model.addAttribute("sortType", sortType.get());
        } else if (sortType.isPresent() && sortType.get().equalsIgnoreCase("asc")) {
            sort = Sort.by(Sort.Direction.ASC, "price");
            model.addAttribute("sortType", sortType.get());
        } else {
            sort = Sort.by(Sort.Direction.ASC, "product.name");
        }

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<ProductDetail> page = productDetailRepo.findByName("%" + name + "%", pageable);

        model.addAttribute("page", page);

        if (name.isEmpty()) {
            model.addAttribute("cname", "ALL");
        } else {
            model.addAttribute("cname", name);
        }

        model.addAttribute("name", name);

        return "product/list";
    }

    @GetMapping("{id}")
    public String getMethodName(@PathVariable Integer id, Model model) {
        ProductDetail productDetail = productDetailRepo.findByID(id);
        model.addAttribute("productDetail", productDetail);

        List<ProductDetail> tuongTu = productDetailRepo.find4TuongTu(productDetail.getProduct().getName(), id, PageRequest.of(0, 4));
        model.addAttribute("tuongTu", tuongTu);
        return "product/detail";
    }

}

