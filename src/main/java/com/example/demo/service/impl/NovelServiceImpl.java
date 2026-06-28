package com.example.demo.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Novel;
import com.example.demo.mapper.NovelMapper;
import com.example.demo.service.NovelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NovelServiceImpl extends ServiceImpl<NovelMapper, Novel> implements NovelService {

    private static final Logger log = LoggerFactory.getLogger(NovelServiceImpl.class);

    @Override
    public IPage<Novel> getNovelList(Page<Novel> page, Long categoryId, String keyword, String orderBy) {
        LambdaQueryWrapper<Novel> wrapper = new LambdaQueryWrapper<>();

        if (categoryId != null && categoryId > 0) {
            wrapper.eq(Novel::getCategoryId, categoryId);
        }

        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Novel::getTitle, keyword)
                    .or()
                    .like(Novel::getAuthor, keyword));
        }

        if ("click".equals(orderBy)) {
            wrapper.orderByDesc(Novel::getClickCount);
        } else {
            wrapper.orderByDesc(Novel::getUpdateTime);
        }

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public Novel getNovelDetail(Long id) {
        Novel novel = baseMapper.selectById(id);
        if (novel != null) {
            incrementClickCount(id);
        }
        return novel;
    }

    @Override
    @Async
    public void incrementClickCount(Long id) {
        baseMapper.updateClickCount(id);
    }
}