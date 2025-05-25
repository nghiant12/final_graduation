package org.example.final_graduation.services;

import org.example.final_graduation.entities.Color;
import org.example.final_graduation.repositories.products.attributes.ColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ColorService {
    @Autowired
    private ColorRepository colorRepository;

    public List<Color> getAllColors() {
        return colorRepository.findAll();
    }

    public List<Color> getAvailableColorsForProduct(Integer productId) {
        return colorRepository.findColorsByProduct(productId);
    }

    public Optional<Color> getColorById(Integer colorId) {
        return colorRepository.findById(colorId);
    }
}
