package com.yaser.news.service.dataWrap;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserNewsScoreWrap {
    private double userScore;
    private double newsScore;
}
