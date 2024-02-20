package com.yaser.news.service.dataWrap;

import com.yaser.news.constant.Roles;
import lombok.Data;

import java.util.List;

@Data

public class UserInfoWrap {
    private long uid;
    private String nickname;
    private String introduce;
    private String email;
    private int gender;
    private List<String> labels;
    private Roles role;
    private int userViewedNum;

}
