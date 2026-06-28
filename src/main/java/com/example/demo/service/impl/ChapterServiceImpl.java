package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.BusinessException;
import com.example.demo.entity.Chapter;
import com.example.demo.mapper.ChapterMapper;
import com.example.demo.service.ChapterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    private static final Logger log = LoggerFactory.getLogger(ChapterServiceImpl.class);

    @Override
    public List<Chapter> getChapterList(Long novelId) {
        LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Chapter::getNovelId, novelId)
                .orderByAsc(Chapter::getChapterNo)
                .select(Chapter.class, info -> !"content".equals(info.getColumn()));

        return baseMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> getChapterContent(Long novelId, Integer chapterNo) {
        LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Chapter::getNovelId, novelId)
                .eq(Chapter::getChapterNo, chapterNo);

        Chapter chapter = baseMapper.selectOne(wrapper);
        if (chapter == null) {
            throw new BusinessException("章节不存在");
        }

        String content = baseMapper.getContentById(chapter.getId());
        chapter.setContent(content);

        Integer maxChapterNo = baseMapper.getMaxChapterNo(novelId);

        Map<String, Object> result = new HashMap<>();
        result.put("chapter", chapter);
        result.put("prevChapterNo", chapterNo > 1 ? chapterNo - 1 : null);
        result.put("nextChapterNo", chapterNo < maxChapterNo ? chapterNo + 1 : null);

        return result;
    }
}