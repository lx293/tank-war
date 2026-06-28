package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Comment;
import com.example.demo.entity.User;
import com.example.demo.mapper.CommentMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.CommentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private UserMapper userMapper;

    @Override
    public void addComment(Long userId, Long novelId, String content) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setNovelId(novelId);
        comment.setContent(content);
        baseMapper.insert(comment);
    }

    @Override
    public List<Comment> getCommentsByNovelId(Long novelId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getNovelId, novelId)
                .orderByDesc(Comment::getCreateTime);
        List<Comment> comments = baseMapper.selectList(wrapper);
        for (Comment comment : comments) {
            User user = userMapper.selectById(comment.getUserId());
            if (user != null) {
                comment.setUsername(user.getNickname() != null ? user.getNickname() : user.getUsername());
            } else {
                comment.setUsername("匿名用户");
            }
        }
        return comments;
    }
}