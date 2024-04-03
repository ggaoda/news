package com.yaser.news.constant;

import lombok.Getter;

public enum Roles {

    USER("USER"),
    ADMIN("ADMIN");

    @Getter
    private final String roleName;

    Roles(String roleName) {
        this.roleName = roleName;

    }
}
