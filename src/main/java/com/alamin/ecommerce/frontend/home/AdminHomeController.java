package com.alamin.ecommerce.frontend.home;

import com.alamin.ecommerce.frontend.client.api.AdminOrderClient;
import com.alamin.ecommerce.frontend.client.api.AdminStatisticsClient;
import com.alamin.ecommerce.frontend.client.api.AdminUserClient;
import com.alamin.ecommerce.frontend.dto.OrderDto;
import com.alamin.ecommerce.frontend.dto.PageDto;
import com.alamin.ecommerce.frontend.dto.StatisticsDto;
import com.alamin.ecommerce.frontend.dto.UserDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Admin home dashboard. Statistics are pulled from the backend admin REST
 * endpoints through {@link AdminOrderClient}, {@link AdminUserClient} and
 * {@link AdminStatisticsClient}.
 */
@Controller
public class AdminHomeController {

    private final AdminOrderClient orderClient;
    private final AdminUserClient userClient;
    private final AdminStatisticsClient statisticsClient;

    public AdminHomeController(AdminOrderClient orderClient,
                               AdminUserClient userClient,
                               AdminStatisticsClient statisticsClient) {
        this.orderClient = orderClient;
        this.userClient = userClient;
        this.statisticsClient = statisticsClient;
    }

    @GetMapping("/admin")
    public String index(@RequestParam(name = "dv", defaultValue = "m") String type, Model model){
        UserDto user = new UserDto();

        PageDto<OrderDto> ordersPage = orderClient.list(0, 10, 0, 0);
        List<OrderDto> orders = ordersPage == null ? List.of() : ordersPage.getContent();

        List<StatisticsDto> stats = statisticsClient.list();

        List<String> listLabels = new ArrayList<>();
        List<Double> listData = new ArrayList<>();
        if (stats != null) {
            for (StatisticsDto s : stats) {
                listLabels.add(s.getType());
                listData.add(s.getStValue() == null ? 0.0 : s.getStValue().doubleValue());
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("statsUsers", userClient.count());
        model.addAttribute("usersThisMonth", userClient.count());
        model.addAttribute("statsOrders", ordersPage == null ? 0L : ordersPage.getTotalElements());
        model.addAttribute("ordersThisMonth", ordersPage == null ? 0L : ordersPage.getTotalElements());
        model.addAttribute("statsRevenue", revenueTotal(orders));
        model.addAttribute("revenueThisMonth", revenueTotal(orders));
        model.addAttribute("activities", List.of());
        model.addAttribute("data", listData);
        model.addAttribute("labels", listLabels);
        model.addAttribute("orderChartLabels", null);
        model.addAttribute("orderChartData", null);
        model.addAttribute("pageTitle", "Admin Dashboard - Index");

        model.addAttribute("pageTitle", "Admin Dashboard | Index");
        model.addAttribute("pageAuthor", "Alamin Omer | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin orders reports page");
        model.addAttribute("pageTags", "orders,order, report");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

        return "admin/home/admin_home";
    }

    @GetMapping("/admin/create")
    public String create(Model model){

        model.addAttribute("pageTitle", "Admin Dashboard | Create");
        model.addAttribute("pageAuthor", "Alamin Omer | Amin0x | garogigi@gmail.com");
        model.addAttribute("pageDescription", "Admin orders reports page");
        model.addAttribute("pageTags", "orders,order, report");
        model.addAttribute("pageLink", "");
        model.addAttribute("pageAltLink", "");
        model.addAttribute("page", "");

        return "admin/home/create_links";
    }

    private Double revenueTotal(List<OrderDto> orders) {
        double total = 0.0;
        for (OrderDto o : orders) {
            if (o.getTotalAmount() != null) {
                total += o.getTotalAmount().doubleValue();
            }
        }
        return total;
    }
}
