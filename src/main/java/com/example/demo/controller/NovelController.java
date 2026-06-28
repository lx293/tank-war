package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.Novel;
import com.example.demo.service.NovelService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/novel")
public class NovelController {

    @Resource
    private NovelService novelService;

    /**
     * 小说列表（分页）
     */
    @GetMapping("/list")
    public Result<IPage<Novel>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "time") String orderBy) {

        Page<Novel> pageParam = new Page<>(page, size);
        IPage<Novel> result = novelService.getNovelList(pageParam, categoryId, keyword, orderBy);
        return Result.success(result);
    }

    /**
     * 小说详情
     */
    @GetMapping("/detail/{id}")
    public Result<Novel> detail(@PathVariable Long id) {
        Novel novel = novelService.getNovelDetail(id);
        if (novel == null) {
            return Result.error("小说不存在");
        }
        return Result.success(novel);
    }

    /**
     * 热门小说
     */
    @GetMapping("/hot")
    public Result<IPage<Novel>> hot(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Page<Novel> pageParam = new Page<>(page, size);
        IPage<Novel> result = novelService.getNovelList(pageParam, null, null, "click");
        return Result.success(result);
    }

    /**
     * 设置封面
     */
    @GetMapping("/cover/{id}")
    public Result<String> setCover(@PathVariable Long id) {
        Novel novel = novelService.getById(id);
        if (novel != null) {
            novel.setCoverUrl("/static/covers/" + id + ".jpg");
            novelService.updateById(novel);
            return Result.success("封面已更新");
        }
        return Result.error("小说不存在");
    }
}