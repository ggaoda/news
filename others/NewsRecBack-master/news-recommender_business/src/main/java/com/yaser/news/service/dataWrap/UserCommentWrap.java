package com.yaser.news.service.dataWrap;

import com.yaser.news.dataEntity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCommentWrap {

    private UserInfoWrap user;

    private Comment comment;

}
