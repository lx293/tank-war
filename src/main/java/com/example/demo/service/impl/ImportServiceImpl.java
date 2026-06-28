package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.Chapter;
import com.example.demo.entity.Novel;
import com.example.demo.mapper.ChapterMapper;
import com.example.demo.mapper.NovelMapper;
import com.example.demo.service.ImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
public class ImportServiceImpl implements ImportService {

    private static final Logger log = LoggerFactory.getLogger(ImportServiceImpl.class);

    @Resource
    private ChapterMapper chapterMapper;

    @Resource
    private NovelMapper novelMapper;

    @Override
    public void importFromTxt(Long novelId, String filePath) {
        Novel novel = novelMapper.selectById(novelId);
        if (novel == null) {
            log.error("小说不存在: {}", novelId);
            return;
        }

        // 清空旧章节
        LambdaQueryWrapper<Chapter> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(Chapter::getNovelId, novelId);
        chapterMapper.delete(deleteWrapper);

        // 先读取整个文件内容（自动识别编码）
        String text = readFileAutoDetect(filePath);
        if (text == null || text.isEmpty()) {
            log.error("文件为空或读取失败");
            return;
        }

        // 去掉文件开头的版权声明等无用内容，找到第一章
        int firstChapterIndex = findFirstChapter(text);
        if (firstChapterIndex < 0) {
            log.error("未找到任何章节标题");
            return;
        }
        text = text.substring(firstChapterIndex);

        // 按章节标题分割
        String[] lines = text.split("\n");
        StringBuilder currentContent = new StringBuilder();
        String currentTitle = null;
        int chapterNo = 0;
        long totalWords = 0;

        for (String line : lines) {
            String trimmed = line.trim();

            // 判断是否是章节标题
            if (isChapterTitle(trimmed)) {
                // 保存上一章
                if (currentTitle != null && currentContent.length() > 0) {
                    chapterNo++;
                    saveChapter(novelId, chapterNo, currentTitle, currentContent.toString());
                    totalWords += currentContent.length();
                    if (chapterNo % 50 == 0) {
                        log.info("已导入: 第{}章 {}", chapterNo, currentTitle);
                    }
                }
                currentTitle = trimmed;
                currentContent = new StringBuilder();
            } else if (currentTitle != null) {
                currentContent.append(line).append("\n");
            }
        }

        // 保存最后一章
        if (currentTitle != null && currentContent.length() > 0) {
            chapterNo++;
            saveChapter(novelId, chapterNo, currentTitle, currentContent.toString());
            totalWords += currentContent.length();
        }

        // 更新小说
        novel.setWordCount(totalWords);
        novel.setStatus(1);
        novelMapper.updateById(novel);

        log.info("导入完成！共 {} 章，总字数 {}", chapterNo, totalWords);
    }

    /**
     * 判断一行是否是章节标题
     */
    private boolean isChapterTitle(String line) {
        if (line == null || line.isEmpty()) return false;
        // 去掉前导空格和全角空格
        String trimmed = line.trim().replace("\u3000", "").trim();
        // 长度不能太长（章节标题一般不超过30字）
        if (trimmed.length() > 30) return false;
        // 必须以"第"开头，且包含"章"
        return trimmed.startsWith("第") && trimmed.contains("章");
    }

    /**
     * 找到第一个章节标题的位置
     */
    private int findFirstChapter(String text) {
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (isChapterTitle(lines[i].trim())) {
                return text.indexOf(lines[i]);
            }
        }
        return -1;
    }

    /**
     * 保存章节
     */
    private void saveChapter(Long novelId, int chapterNo, String title, String content) {
        Chapter ch = new Chapter();
        ch.setNovelId(novelId);
        ch.setChapterNo(chapterNo);
        ch.setTitle(title);
        ch.setContent(content.trim());
        ch.setWordCount(content.length());
        chapterMapper.insert(ch);
    }

    /**
     * 自动检测编码读取文件
     */
    private String readFileAutoDetect(String filePath) {
        // 先尝试UTF-8
        String text = readFile(filePath, "UTF-8");
        if (text != null && text.contains("第") && text.contains("章")) {
            return text;
        }
        // 再尝试GBK
        text = readFile(filePath, "GBK");
        if (text != null && text.contains("第") && text.contains("章")) {
            return text;
        }
        // 最后尝试系统默认编码
        return readFile(filePath, null);
    }

    private String readFile(String filePath, String encoding) {
        try {
            BufferedReader reader;
            if (encoding != null) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), encoding));
            } else {
                reader = new BufferedReader(new FileReader(filePath));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
}