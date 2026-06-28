package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Chapter;

import java.util.List;
import java.util.Map;

/**
 * 章节Service接口
 */
public interface ChapterService extends IService<Chapter> {

    /**
     * 查询小说章节列表
     */
    List<Chapter> getChapterList(Long novelId);

    /**
     * 查询章节内容
     */
    Map<String, Object> getChapterContent(Long novelId, Integer chapterNo);
}