package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Novel;

/**
 * 小说Service接口
 */
public interface NovelService extends IService<Novel> {

    /**
     * 分页查询小说列表
     */
    IPage<Novel> getNovelList(Page<Novel> page, Long categoryId, String keyword, String orderBy);

    /**
     * 查询小说详情
     */
    Novel getNovelDetail(Long id);

    /**
     * 增加点击量
     */
    void incrementClickCount(Long id);
}