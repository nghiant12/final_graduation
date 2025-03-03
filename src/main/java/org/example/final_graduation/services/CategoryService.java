package org.example.final_graduation.services;

import org.example.final_graduation.entities.Category;
import org.example.final_graduation.repositories.products.attributes.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository cdao;

    public List<Category> findAll() {
        // TODO Auto-generated method stub
        return cdao.findAll();
    }

    public Category create(Category category) {
        return cdao.save(category);
    }

    public List<Object[]> getAllCategoriesWithCount() {
        return cdao.findAllCategoriesWithProductCount();
    }

    public Map<Category, Long> countProductsByCategory() {
        List<Object[]> result = cdao.findAllCategoriesWithProductCount();
        Map<Category, Long> countMap = new HashMap<>();
        for (Object[] objects : result) {
            Category category = (Category) objects[0];
            Long count = (Long) objects[1];
            countMap.put(category, count);
        }
        return countMap;
    }
}
