package com.alamin.ecommerce.settings;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminSettingController {

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
    public void addCategoryToHome(){

    }

    @PostMapping("/admin/settings/general/removeCategoryFromHome")
    public void removeCategoryFromHome(){
        
    }
}
