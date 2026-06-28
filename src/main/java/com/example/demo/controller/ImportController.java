package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.ImportService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    @Resource
    private ImportService importService;

    @GetMapping("/txt")
    public Result<String> importTxt(@RequestParam Long novelId, @RequestParam String fileName) {
        String filePath = System.getProperty("user.dir") + "/novels/" + fileName;
        new Thread(() -> importService.importFromTxt(novelId, filePath)).start();
        return Result.success("导入任务已启动，请查看IDEA控制台进度");
    }
}