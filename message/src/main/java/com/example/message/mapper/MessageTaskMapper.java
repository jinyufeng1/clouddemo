package com.example.message.mapper;

import com.example.masterslavedatasource.DataSource;
import com.example.message.entity.MessageTask;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 短信任务表表 Mapper 接口
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-05-07
 */
public interface MessageTaskMapper {
	@DataSource("slave")
	@Select("select * from message_task WHERE status = #{status} and is_deleted = 0")
	List<MessageTask> getByStatus(@Param("status") Integer status);

//    **************************五大基础方法**************************
	@DataSource("slave")
	@Select("select * from message_task WHERE id = #{id} and is_deleted = 0")
	MessageTask getById(@Param("id") Long id);

	@DataSource("slave")
	@Select("select * from message_task WHERE id = #{id}")
	MessageTask extractById(@Param("id") Long id);
	
	@Update("update message_task set is_deleted = 1, update_time = #{timestamp} where id = #{id} limit 1")
	int delete(@Param("id") Long id, @Param("timestamp") Integer timestamp);

	int insert(@Param("entity") MessageTask entity);
	
	int update(@Param("entity") MessageTask entity);
}
