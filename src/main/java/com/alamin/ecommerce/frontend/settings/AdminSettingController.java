package com.alamin.ecommerce.frontend.settings;

import com.alamin.ecommerce.frontend.client.api.SettingClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Admin settings. Mirrored view routes plus mutating calls to the backend
 * {@code /api/settings} endpoints through {@link SettingClient}.
 */
@Controller
public class AdminSettingController {

    private final SettingClient settingClient;

    public AdminSettingController(SettingClient settingClient) {
        this.settingClient = settingClient;
    }

    @GetMapping("/admin/settings")
    public String showSettingPage() {
        return "admin/settings/admin_settings";
    }

    @GetMapping("/admin/settings/general")
    public String showGeneralSettingPage() {
        return "admin/settings/general_settings";
    }

    @GetMapping("/admin/settings/payment")
    public String showPaymentSettingPage() {
        return "admin/settings/payment_settings";
    }

    @GetMapping("/admin/settings/shipping")
    public String showShippingSettingPage() {
        return "admin/settings/shipping_settings";
    }

    @GetMapping("/admin/settings/seo")
    public String showSeoSettingPage() {
        return "admin/settings/seo_settings";
    }

    @GetMapping("/admin/settings/social")
    public String showSocialSettingPage() {
        return "admin/settings/social_settings";
    }

    @PostMapping("/admin/settings/general/addCategoryToHome")
    @ResponseBody
    public ResponseEntity<Void> addCategoryToHome() {
        settingClient.addCategoryToHome();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/settings/general/removeCategoryFromHome")
    @ResponseBody
    public ResponseEntity<Void> removeCategoryFromHome() {
        settingClient.removeCategoryFromHome();
        return ResponseEntity.ok().build();
    }
}
