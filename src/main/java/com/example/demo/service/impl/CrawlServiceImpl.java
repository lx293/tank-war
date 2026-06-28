package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.Chapter;
import com.example.demo.entity.Novel;
import com.example.demo.mapper.ChapterMapper;
import com.example.demo.mapper.NovelMapper;
import com.example.demo.service.CrawlService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CrawlServiceImpl implements CrawlService {

    private static final Logger log = LoggerFactory.getLogger(CrawlServiceImpl.class);

    @Resource
    private ChapterMapper chapterMapper;

    @Resource
    private NovelMapper novelMapper;

    @Override
    public void crawlNovel(Long novelId, String searchKeyword) {
        Novel novel = novelMapper.selectById(novelId);
        if (novel == null) {
            log.error("小说不存在: {}", novelId);
            return;
        }

        try {
            // 1. 搜索小说
            String searchUrl = "https://www.biquge.com.cn/search?q=" + searchKeyword;
            Document searchDoc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(15000)
                    .get();

            Element firstResult = searchDoc.selectFirst(".result-list .result-item");
            if (firstResult == null) {
                log.error("未搜索到小说: {}", searchKeyword);
                return;
            }

            String novelUrl = firstResult.selectFirst("a").attr("href");
            log.info("找到小说: {} -> {}", searchKeyword, novelUrl);

            // 2. 获取章节列表
            Document novelDoc = Jsoup.connect(novelUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(15000)
                    .get();

            Elements chapterLinks = novelDoc.select("#list dd a");
            int totalChapters = chapterLinks.size();
            log.info("共找到 {} 章，开始爬取...", totalChapters);

            long totalWords = 0;
            int savedCount = 0;

            // 3. 逐章爬取
            for (int i = 0; i < totalChapters; i++) {
                Element link = chapterLinks.get(i);
                String chapterTitle = link.text();
                String chapterUrl = link.attr("abs:href");
                int chapterNo = i + 1;

                // 检查是否已存在
                LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Chapter::getNovelId, novelId).eq(Chapter::getChapterNo, chapterNo);
                if (chapterMapper.selectCount(wrapper) > 0) {
                    log.info("跳过已存在: 第{}章", chapterNo);
                    continue;
                }

                // 爬取内容
                String content = crawlContent(chapterUrl);
                if (content == null || content.isEmpty()) {
                    log.warn("内容为空: 第{}章 {}", chapterNo, chapterTitle);
                    continue;
                }

                Chapter chapter = new Chapter();
                chapter.setNovelId(novelId);
                chapter.setChapterNo(chapterNo);
                chapter.setTitle(chapterTitle);
                chapter.setContent(content);
                chapter.setWordCount(content.length());
                chapterMapper.insert(chapter);

                totalWords += content.length();
                savedCount++;
                log.info("已爬取: 第{}章 {} ({}字) [{}/{}]", chapterNo, chapterTitle, content.length(), savedCount, totalChapters);

                Thread.sleep(1500);
            }

            // 4. 更新小说
            novel.setWordCount(totalWords);
            if (savedCount >= totalChapters * 0.9) {
                novel.setStatus(1);
            }
            novelMapper.updateById(novel);
            log.info("爬取完成！共保存 {} 章，总字数 {}", savedCount, totalWords);

        } catch (Exception e) {
            log.error("爬取失败", e);
        }
    }

    private String crawlContent(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            Element content = doc.selectFirst("#content");
            if (content != null) {
                return content.text().replace("　　", "\n　　").trim();
            }
        } catch (Exception e) {
            log.error("爬取章节失败: {}", url);
        }
        return null;
    }
}