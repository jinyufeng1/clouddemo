package com.example.coach.mapper;

import com.example.coach.entity.RelationTagCoach;
import com.example.coach.entity.Tag;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.example.masterslavedatasource.DataSource;

import java.util.List;

/**
 * <p>
 * 教练标签关联表 Mapper 接口
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-04-28
 */
public interface RelationTagCoachMapper {
//    **************************五大基础方法**************************
	@DataSource("slave")
	@Select("select * from relation_tag_coach WHERE id = #{id} and is_deleted = 0")
	RelationTagCoach getById(@Param("id") Long id);

	@DataSource("slave")
	@Select("select * from relation_tag_coach WHERE id = #{id}")
	RelationTagCoach extractById(@Param("id") Long id);
	
	@Update("update relation_tag_coach set is_deleted = 1, update_time = #{timestamp} where id = #{id} limit 1")
	int delete(@Param("id") Long id, @Param("timestamp") Integer timestamp);

	int insert(@Param("entity") RelationTagCoach entity);
	
	int update(@Param("entity") RelationTagCoach entity);

	@DataSource("slave")
	List<Tag> getTagByCoachId(Long coachId);

	int deleteByCoachId(@Param("coachId") Long coachId, @Param("tagIds") List<Long> tagIds, @Param("timestamp") Integer timestamp);
}
