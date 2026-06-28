package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Novel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 小说Mapper接口
 */
@Mapper
public interface NovelMapper extends BaseMapper<Novel> {

    /**
     * 更新点击量（+1）
     */
    @Update("UPDATE novel SET click_count = click_count + 1 WHERE id = #{id}")
    int updateClickCount(@Param("id") Long id);
}