package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Bookshelf;
import com.example.demo.entity.Chapter;
import com.example.demo.entity.Novel;
import com.example.demo.mapper.BookshelfMapper;
import com.example.demo.mapper.ChapterMapper;
import com.example.demo.mapper.NovelMapper;
import com.example.demo.service.BookshelfService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class BookshelfServiceImpl extends ServiceImpl<BookshelfMapper, Bookshelf> implements BookshelfService {

    @Resource
    private NovelMapper novelMapper;
    @Resource
    private ChapterMapper chapterMapper;

    @Override
    public void addToShelf(Long userId, Long novelId) {
        LambdaQueryWrapper<Bookshelf> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bookshelf::getUserId, userId).eq(Bookshelf::getNovelId, novelId);
        if (baseMapper.selectCount(wrapper) == 0) {
            Bookshelf shelf = new Bookshelf();
            shelf.setUserId(userId);
            shelf.setNovelId(novelId);
            baseMapper.insert(shelf);
        }
    }

    @Override
    public void removeFromShelf(Long userId, Long novelId) {
        LambdaQueryWrapper<Bookshelf> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bookshelf::getUserId, userId).eq(Bookshelf::getNovelId, novelId);
        baseMapper.delete(wrapper);
    }

    @Override
    public List<Map<String, Object>> getUserShelf(Long userId) {
        LambdaQueryWrapper<Bookshelf> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bookshelf::getUserId, userId).orderByDesc(Bookshelf::getCreateTime);
        List<Bookshelf> shelfList = baseMapper.selectList(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Bookshelf shelf : shelfList) {
            Novel novel = novelMapper.selectById(shelf.getNovelId());
            if (novel != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("novelId", novel.getId());
                map.put("title", novel.getTitle());
                map.put("author", novel.getAuthor());
                map.put("coverUrl", novel.getCoverUrl());
                map.put("lastReadChapterId", shelf.getLastReadChapterId());
                if (shelf.getLastReadChapterId() != null) {
                    Chapter chapter = chapterMapper.selectById(shelf.getLastReadChapterId());
                    map.put("lastReadChapterTitle", chapter != null ? chapter.getTitle() : "未阅读");
                    map.put("lastReadChapterNo", chapter != null ? chapter.getChapterNo() : 0);
                } else {
                    map.put("lastReadChapterTitle", "未阅读");
                    map.put("lastReadChapterNo", 0);
                }
                result.add(map);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public void updateReadProgress(Long userId, Long novelId, Long chapterId) {
        LambdaQueryWrapper<Bookshelf> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bookshelf::getUserId, userId).eq(Bookshelf::getNovelId, novelId);
        Bookshelf shelf = baseMapper.selectOne(wrapper);
        if (shelf != null) {
            shelf.setLastReadChapterId(chapterId);
            baseMapper.updateById(shelf);
        } else {
            addToShelf(userId, novelId);
            Bookshelf newShelf = baseMapper.selectOne(wrapper);
            if (newShelf != null) {
                newShelf.setLastReadChapterId(chapterId);
                baseMapper.updateById(newShelf);
            }
        }
    }
}