package com.yaser.news.service.dataWrap;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class PageData<T> {

    private boolean isEnd;
    private int pageNum;
    private int total;

    public PageData(Page<T> page, int now_page) {
        this.total = page.getTotalPages();
        this.isEnd = page.getTotalPages() <= now_page + 1;//是否结束
        this.pageNum = now_page;
    }

}
