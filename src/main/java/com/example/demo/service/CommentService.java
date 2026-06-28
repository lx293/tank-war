package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Comment;
import java.util.List;

public interface CommentService extends IService<Comment> {
    void addComment(Long userId, Long novelId, String content);
    List<Comment> getCommentsByNovelId(Long novelId);
}