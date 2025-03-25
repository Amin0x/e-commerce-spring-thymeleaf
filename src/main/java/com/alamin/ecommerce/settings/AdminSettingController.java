package com.alamin.ecommerce.settings;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminSettingController {

    @GetMapping("/admin/settings")
    public String showSettingPage() {
        return "admin/admin_settings";
    }
}
