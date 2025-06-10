package com.example.provider.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.example.provider.entity.Student;

import java.util.List;

/**
 * <p>
 * 学生表 Mapper 接口
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-06-02
 */
@Mapper
public interface StudentMapper {
//    **************************五大基础方法**************************
	@Select("select * from student WHERE id = #{id} and is_deleted = 0")
	Student getById(@Param("id") Long id);

	@Select("select * from student WHERE id = #{id}")
	Student extractById(@Param("id") Long id);

	@Select("select * from student where is_deleted = 0")
	List<Student> getAll();
	
	@Update("update student set is_deleted = 1, update_time = #{timestamp} where id = #{id} limit 1")
	int delete(@Param("id") Long id, @Param("timestamp") Integer timestamp);

	int insert(@Param("entity") Student entity);
	
	int update(@Param("entity") Student entity);
}
