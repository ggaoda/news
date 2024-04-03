package com.yaser.news.controller;

import com.yaser.news.service.LabelService;
import com.yaser.news.service.UserService;
import com.yaser.news.service.dataWrap.UserInfoWrap;
import com.yaser.news.utils.UseToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@CrossOrigin(allowCredentials = "true", origins = "http://localhost:3000")

@RequestMapping(value = "/v1/labels")
public class LabelsController {
    private final UserService userService;
    private final LabelService labelService;

    public LabelsController(UserService userService, LabelService labelService) {
        this.userService = userService;
        this.labelService = labelService;
    }

    @GetMapping("/labels")
    public List<String> getLabels(@RequestParam int num) {
        return labelService.getLabels(num);
    }

    @PostMapping("/setUserLabels")
    @UseToken
    public UserInfoWrap setUserLabels(@RequestBody List<String> labels) {
        return labelService.setUserLabels(labels);
    }
}
