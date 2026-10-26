package com.alamin.ecommerce.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Settings REST endpoint. Delegates to {@link SettingService}; returns JSON.
 * Note: {@link SettingServiceImpl} is currently stubbed, so mutating endpoints
 * surface a 500 until backed by real storage.
 */
@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @PostMapping("/home/category")
    public ResponseEntity<Void> addCategoryToHome() {
        settingService.addCategoryToHome();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/home/category")
    public ResponseEntity<Void> removeCategoryFromHome() {
        settingService.removeCategoryFromHome();
        return ResponseEntity.ok().build();
    }
}
