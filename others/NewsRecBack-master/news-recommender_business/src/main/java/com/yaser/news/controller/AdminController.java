package com.yaser.news.controller;

import com.yaser.news.service.AdminService;
import com.yaser.news.service.dataWrap.UserLoginWrap;
import com.yaser.news.utils.Admin;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:3000")
@RequestMapping(value = "/v1/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    @Admin
    @GetMapping("/test")
    public boolean test() {
        return true;
    }

}
