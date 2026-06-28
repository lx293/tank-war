package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Chapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 章节Mapper接口
 */
@Mapper
public interface ChapterMapper extends BaseMapper<Chapter> {

    /**
     * 查询小说最大章节号
     */
    @Select("SELECT MAX(chapter_no) FROM chapter WHERE novel_id = #{novelId} AND deleted = 0")
    Integer getMaxChapterNo(@Param("novelId") Long novelId);

    /**
     * 根据ID查询章节内容
     */
    @Select("SELECT content FROM chapter WHERE id = #{id}")
    String getContentById(@Param("id") Long id);
}