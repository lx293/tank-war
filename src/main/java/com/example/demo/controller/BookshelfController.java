package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.User;
import com.example.demo.service.BookshelfService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookshelf")
public class BookshelfController {

    @Resource
    private BookshelfService bookshelfService;

    private Long getUserId(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null ? user.getId() : null;
    }

    @PostMapping("/add/{novelId}")
    public Result<String> add(@PathVariable Long novelId, HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) return Result.error(401, "请先登录");
        bookshelfService.addToShelf(userId, novelId);
        return Result.success("已加入书架");
    }

    @DeleteMapping("/remove/{novelId}")
    public Result<String> remove(@PathVariable Long novelId, HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) return Result.error(401, "请先登录");
        bookshelfService.removeFromShelf(userId, novelId);
        return Result.success("已移出书架");
    }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) return Result.error(401, "请先登录");
        return Result.success(bookshelfService.getUserShelf(userId));
    }

    @PostMapping("/progress/{novelId}/{chapterId}")
    public Result<String> updateProgress(@PathVariable Long novelId, @PathVariable Long chapterId, HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) return Result.error(401, "请先登录");
        bookshelfService.updateReadProgress(userId, novelId, chapterId);
        return Result.success("阅读进度已更新");
    }
}