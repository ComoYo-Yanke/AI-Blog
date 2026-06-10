package com.blog.common;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageResult<T> {
    private List<T> records;
    private long total;
    private int page;
    private int size;

    public static <T> PageResult<T> of(Page<T> pageResult) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(pageResult.getContent());
        result.setTotal(pageResult.getTotalElements());
        result.setPage(pageResult.getNumber());
        result.setSize(pageResult.getSize());
        return result;
    }

    public static <T> PageResult<T> of(List<T> records, long total, int page, int size) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);
        return result;
    }
}
