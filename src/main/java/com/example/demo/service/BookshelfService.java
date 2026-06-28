package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Bookshelf;
import java.util.List;
import java.util.Map;

public interface BookshelfService extends IService<Bookshelf> {
    void addToShelf(Long userId, Long novelId);
    void removeFromShelf(Long userId, Long novelId);
    List<Map<String, Object>> getUserShelf(Long userId);
    void updateReadProgress(Long userId, Long novelId, Long chapterId);
}