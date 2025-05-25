package org.example.final_graduation.controllers.api;

import org.example.final_graduation.services.GHNService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fee")
public class ShippingController {

    @Autowired
    private GHNService ghnService;

    @GetMapping("/provinces")
    public ResponseEntity<List<Map<String, Object>>> getProvinces() {
        return ResponseEntity.ok(ghnService.getProvinces());
    }

    @GetMapping("/districts/{provinceId}")
    public ResponseEntity<List<Map<String, Object>>> getDistricts(@PathVariable Integer provinceId) {
        return ResponseEntity.ok(ghnService.getDistricts(provinceId));
    }

    @GetMapping("/wards/{districtId}")
    public ResponseEntity<List<Map<String, Object>>> getWards(@PathVariable Integer districtId) {
        return ResponseEntity.ok(ghnService.getWards(districtId));
    }

    @PostMapping("/calculate-fee")
    public ResponseEntity<Integer> calculateFee(
            @RequestParam Integer districtId,
            @RequestParam Integer wardCode,
            @RequestParam Integer weight) {
        return ResponseEntity.ok(ghnService.calculateShippingFee(districtId, wardCode, weight));
    }
} 