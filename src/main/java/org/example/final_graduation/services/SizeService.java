package org.example.final_graduation.services;

import org.example.final_graduation.entities.Size;
import org.example.final_graduation.repositories.products.attributes.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SizeService {
    @Autowired
    private SizeRepository sizeRepository;

    public List<Size> getAllSizes() {
        return sizeRepository.findAll();
    }

    public List<Size> getAvailableSizesForProduct(Integer productId) {
        return sizeRepository.findSizesByProduct(productId);
    }

    public Optional<Size> getSizeById(Integer sizeId) {
        return sizeRepository.findById(sizeId);
    }
}
