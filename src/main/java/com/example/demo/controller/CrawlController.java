package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.CrawlService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/crawl")
public class CrawlController {

    @Resource
    private CrawlService crawlService;

    @GetMapping("/start")
    public Result<String> start(
            @RequestParam Long novelId,
            @RequestParam String keyword) {
        new Thread(() -> crawlService.crawlNovel(novelId, keyword)).start();
        return Result.success("爬取任务已启动，请查看IDEA控制台进度");
    }
}