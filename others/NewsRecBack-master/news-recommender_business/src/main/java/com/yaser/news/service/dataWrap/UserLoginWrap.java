package com.yaser.news.service.dataWrap;

import com.yaser.news.constant.Roles;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
public class UserLoginWrap {
    private  long uid;
    private  String nickname;
    private  String introduce;
    private  String email;
    private  String token;
    private int gender;
    private List<String> labels;
    private Roles role;
    private int userViewedNum;
}
