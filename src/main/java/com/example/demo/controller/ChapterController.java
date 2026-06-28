package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Chapter;
import com.example.demo.service.ChapterService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chapter")
public class ChapterController {

    @Resource
    private ChapterService chapterService;

    @GetMapping("/list/{novelId}")
    public Result<List<Chapter>> list(@PathVariable Long novelId) {
        List<Chapter> chapters = chapterService.getChapterList(novelId);
        return Result.success(chapters);
    }

    @GetMapping("/read/{novelId}/{chapterNo}")
    public Result<Map<String, Object>> read(
            @PathVariable Long novelId,
            @PathVariable Integer chapterNo) {
        Map<String, Object> result = chapterService.getChapterContent(novelId, chapterNo);
        return Result.success(result);
    }
}