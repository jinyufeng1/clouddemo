package com.example.message.mapper;

import com.example.masterslavedatasource.DataSource;
import com.example.message.entity.MessageRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 短信记录表 Mapper 接口
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-05-07
 */
public interface MessageRecordMapper {
//    **************************五大基础方法**************************
	@DataSource("slave")
	@Select("select * from message_record WHERE id = #{id} and is_deleted = 0")
	MessageRecord getById(@Param("id") Long id);

	@DataSource("slave")
	@Select("select * from message_record WHERE id = #{id}")
	MessageRecord extractById(@Param("id") Long id);
	
	@Update("update message_record set is_deleted = 1, update_time = #{timestamp} where id = #{id} limit 1")
	int delete(@Param("id") Long id, @Param("timestamp") Integer timestamp);

	int insert(@Param("entity") MessageRecord entity);
	
	int update(@Param("entity") MessageRecord entity);
}
