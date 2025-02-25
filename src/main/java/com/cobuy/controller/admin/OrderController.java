package com.cobuy.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderController {
    /*업체 판매현황*/
    @GetMapping(value = "/admin/order/status")
    public String adminStatus(Model model) {
        model.addAttribute("currentPage", "주문내역관리");
        return "admin/order/adminStatus";
    }

    /*주문내역확인*/
    @GetMapping(value = "/admin/order/list")
    public String orderList(Model model) {
        model.addAttribute("currentPage", "주문내역관리");
        return "admin/order/orderList";
    }



    /*인플루언서 판매현황*/
    @GetMapping(value = "/seller/order/status")
    public String sellerStatus(Model model) {
        model.addAttribute("currentPage", "주문내역관리");
        return "admin/order/sellerStatus";
    }

    /*인플루언서 주문내역확인*/
    @GetMapping(value = "/seller/order/list")
    public String sellerOrderList(Model model) {
        model.addAttribute("currentPage", "주문내역관리");
        return "admin/order/orderList";
    }

}
