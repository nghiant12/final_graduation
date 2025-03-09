package org.example.final_graduation.controllers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.final_graduation.entities.Category;
import org.example.final_graduation.entities.ProductDetail;
import org.example.final_graduation.entities.Size;
import org.example.final_graduation.repositories.products.ProductDetailRepository;
import org.example.final_graduation.repositories.products.ProductRepository;
import org.example.final_graduation.repositories.products.attributes.CategoryRepository;
import org.example.final_graduation.repositories.products.attributes.SizeRepository;
import org.example.final_graduation.services.CategoryService;
import org.example.final_graduation.services.ProductService;
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
    ProductService productService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepo;

    @Autowired
    ProductDetailRepository productDetailRepo;

    @Autowired
    HttpSession session;
    @Autowired
    private SizeRepository sizeRepository;

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
        } else if (sortType.isPresent() && sortType.get().equalsIgnoreCase("asc")){
            sort = Sort.by(Sort.Direction.ASC, "price");
            model.addAttribute("sortType", sortType.get());
        } else{
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
        Page<ProductDetail> page;
        String showCondition = "";
        String categoryParam = "";
        Sort sort;
        if (sortType.isPresent() && sortType.get().equalsIgnoreCase("desc")) {
            sort = Sort.by(Sort.Direction.DESC, "price");
            model.addAttribute("sortType", sortType.get());
        } else if (sortType.isPresent() && sortType.get().equalsIgnoreCase("asc")){
            sort = Sort.by(Sort.Direction.ASC, "price");
            model.addAttribute("sortType", sortType.get());
        } else{
            sort = Sort.by(Sort.Direction.ASC, "category.name");
        }

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

        List<Category> categoryEntities = new ArrayList<>();

        if (categoryIds != null && !categoryIds.isEmpty()) {
            for (Integer categoryId : categoryIds) {
                Optional<Category> category = categoryRepo.findById(categoryId);

                categoryParam += "&categoryIds=" + String.valueOf(categoryId);

                if (category != null) {
                    categoryEntities.add(category.get());
                }
            }

            model.addAttribute("categoryIds", categoryParam);

            page = productDetailRepo.findByPriceBetweenAndCategoryIn(fromPrice, toPrice, categoryEntities, pageable);
        } else {

            page = productDetailRepo.findByPriceBetween(fromPrice,toPrice, pageable);
        }

        for (Category category : categoryEntities) {
            if(category == categoryEntities.get(categoryEntities.size() - 1)) {
                showCondition += category.getName();
            } else {
                showCondition += category.getName() + ", ";
            }
        }
        // page.nextPageable();
        model.addAttribute("fromPrice", String.valueOf(fromPrice.intValue()));
        model.addAttribute("toPrice", String.valueOf(toPrice.intValue()));

        model.addAttribute("page", page);

        if (categoryIds == null) {
            model.addAttribute("cname", "$" + fromPrice + " to " + "$" + toPrice);
        } else if(categoryIds != null && (fromPrice != 0 || toPrice != 5000000)) {
            model.addAttribute("cname", showCondition + " $" + fromPrice + " to " + "$" + toPrice);
        } else {
            model.addAttribute("cname", showCondition);
        }

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
        } else if (sortType.isPresent() && sortType.get().equalsIgnoreCase("asc")){
            sort = Sort.by(Sort.Direction.ASC, "price");
            model.addAttribute("sortType", sortType.get());
        } else{
            sort = Sort.by(Sort.Direction.ASC, "product.name");
        }

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<ProductDetail> page = productDetailRepo.findByName("%" + name + "%", pageable);

        model.addAttribute("page", page);

        if(name.isEmpty()) {
            model.addAttribute("cname", "ALL");
        }else {
            model.addAttribute("cname", name);
        }

        model.addAttribute("name", name);

        return "product/list";
    }

    @GetMapping("{id}")
    public String getMethodName(@PathVariable Integer id, Model model) {

        ProductDetail product = productDetailRepo.findByID(id);
//        Pageable pageable = PageRequest.of(0, 5);
//        Page<ProductDetail> page = productDetailRepo.findByCategoryId(product.getCategory().getId().toString(), pageable);

        List<Size> size = sizeRepository.findAll();

        model.addAttribute("size", size);
 //       model.addAttribute("page", page);
        model.addAttribute("product", product);

        List<ProductDetail> tuongTu = productDetailRepo.find4TuongTu(product.getCategory().getId(), id, PageRequest.of(0, 4));
        model.addAttribute("tuongTu", tuongTu);
        return "product/detail";
    }

}

