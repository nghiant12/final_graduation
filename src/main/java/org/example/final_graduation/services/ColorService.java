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

    public List<Color> getAvailableColorsForProduct(Integer id) {
        return colorRepository.findAll();
    }

    public Optional<Color> getColorById(Integer colorId) {
        return colorRepository.findById(colorId);
    }
}
