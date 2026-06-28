package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Comment;
import com.example.demo.entity.User;
import com.example.demo.service.CommentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    private Long getUserId(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null ? user.getId() : null;
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Map<String, Object> body, HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) return Result.error(401, "请先登录");
        Long novelId = Long.valueOf(body.get("novelId").toString());
        String content = body.get("content").toString();
        if (content == null || content.trim().isEmpty()) {
            return Result.error("评论内容不能为空");
        }
        commentService.addComment(userId, novelId, content);
        return Result.success("评论成功");
    }

    @GetMapping("/list/{novelId}")
    public Result<List<Comment>> list(@PathVariable Long novelId) {
        return Result.success(commentService.getCommentsByNovelId(novelId));
    }
}