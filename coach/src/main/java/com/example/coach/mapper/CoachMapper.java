package com.example.coach.mapper;

import com.example.coach.dto.CoachItemDTO;
import com.example.coach.entity.Coach;
import org.apache.ibatis.annotations.*;
import com.example.masterslavedatasource.DataSource;

import java.util.List;

public interface CoachMapper {
//    @Select(
//        "<script>" +
//            "select * from coach " +
//            "where is_deleted = 0 " +
//            "<if test='keyword != null'>" +
//            "   and name like CONCAT('%', #{keyword}, '%')" +
//            "</if>" +
//            "order by id " +
//            "limit #{index}, #{pageSize}" +
//        "</script>"
//    )
//    @Select("select * from coach WHERE is_deleted = 0 order by id limit #{index}, #{pageSize}")
    @DataSource("slave")
    List<Coach> getPageList(@Param("index") int index,
                            @Param("pageSize") int pageSize,
                            @Param("keyword") String keyword, @Param("orCategoryIds") String orCategoryIds);

    @DataSource("slave")
    List<CoachItemDTO> getPageListLinkTable(@Param("index") int index,
                                            @Param("pageSize") int pageSize,
                                            @Param("keyword") String keyword);

    @DataSource("slave")
    List<CoachItemDTO> getPageListLinkTable2(@Param("index") int index,
                                      @Param("pageSize") int pageSize,
                                      @Param("leafCategoryIds") List<Long> leafCategoryIds);


    //    @Select("select count(*) from coach WHERE is_deleted = 0")
    @DataSource("slave")
    int count(@Param("keyword") String keyword, @Param("orCategoryIds") String orCategoryIds);

//    **************************五大基础方法**************************
    @DataSource("slave")
    @Select("select * from coach WHERE id = #{id} and is_deleted = 0")
    Coach getById(@Param("id") Long id);

    @DataSource("slave")
    @Select("select * from coach WHERE id = #{id}")
    Coach extractById(@Param("id") Long id);

//    @Insert(
//            "insert into coach  " +
//            "(`name`,`pics`,`speciality`,`intro`,`create_time`,`update_time`) " +
//            "VALUES(#{coach.name},#{coach.pics},#{coach.speciality},#{coach.intro},#{coach.createTime},#{coach.updateTime})"
//    )
//    todo 尝试@SelectProvider
    int insert(@Param("entity") Coach entity);


    @Update("update coach set is_deleted=1, update_time=#{timestamp} where id=#{id} and is_deleted = 0 limit 1")
    int delete(@Param("id") Long id, @Param("timestamp") Integer timestamp);

//    @Update(
//            "update coach " +
//            "set pics=#{coach.pics}, `name`=#{coach.name}, speciality=#{coach.speciality}, intro=#{coach.intro}, update_time=#{coach.updateTime} " +
//            "where id=#{id} limit 1"
//    )
//    todo 尝试@UpdateProvider
    int update(@Param("entity") Coach entity);

    @DataSource("slave")
    List<Coach> getByProperty(@Param("entity") Coach entity);

    int deleteByProperty(@Param("entity") Coach entity, @Param("timestamp") Integer timestamp);
}
