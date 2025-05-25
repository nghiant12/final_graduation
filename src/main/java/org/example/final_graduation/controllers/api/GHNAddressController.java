package org.example.final_graduation.controllers.api;

import org.example.final_graduation.service.GHNAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipping")
@CrossOrigin(origins = "*")
public class GHNAddressController {
    private final GHNAddressService ghnAddressService;

    @Autowired
    public GHNAddressController(GHNAddressService ghnAddressService) {
        this.ghnAddressService = ghnAddressService;
    }

    @GetMapping("/provinces")
    public ResponseEntity<?> getProvinces() {
        return ghnAddressService.getProvinces();
    }

    @GetMapping("/districts/{provinceId}")
    public ResponseEntity<?> getDistricts(@PathVariable Integer provinceId) {
        return ghnAddressService.getDistricts(provinceId);
    }

    @GetMapping("/wards/{districtId}")
    public ResponseEntity<?> getWards(@PathVariable Integer districtId) {
        return ghnAddressService.getWards(districtId);
    }

    @PostMapping("/calculate-fee")
    public ResponseEntity<Integer> calculateFee(
            @RequestParam Integer districtId,
            @RequestParam Integer wardCode,
            @RequestParam Integer weight) {
        return ResponseEntity.ok(ghnAddressService.calculateShippingFee(districtId, wardCode, weight));
    }
} 