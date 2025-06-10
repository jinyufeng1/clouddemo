package com.example.coach.mapper;

import com.example.coach.entity.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.example.masterslavedatasource.DataSource;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 分类信息表 Mapper 接口
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-03-30
 */
public interface CategoryMapper {
//    **************************五大基础方法**************************
	@DataSource("slave")
	@Select("select * from category WHERE id = #{id} and is_deleted = 0")
	Category getById(@Param("id") Long id);

	@DataSource("slave")
	@Select("select * from category WHERE id = #{id}")
	Category extractById(@Param("id") Long id);
	
	@Update("update category set is_deleted = 1, update_time = #{timestamp} where id = #{id} limit 1")
	int delete(@Param("id") Long id, @Param("timestamp") Integer timestamp);

	int insert(@Param("entity") Category entity);
	
	int update(@Param("entity") Category entity);
	@DataSource("slave")
	List<Category> getList(@Param("keyword") String keyword, @Param("ids") Set<Long> ids, @Param("limit") Boolean limit);

	@DataSource("slave")
	List<Category> getListByParent(@Param("keyword") String keyword, @Param("parentIds") List<Long> parentIds);

	@DataSource("slave")
	List<Category> getFirstList(@Param("keyword") String keyword);

	@Update("update category set is_deleted = 1, update_time = #{timestamp} where id = #{id} or parent_id = #{id}")
	int deleteHierarchy(@Param("id") Long id, @Param("timestamp") Integer timestamp);
}
