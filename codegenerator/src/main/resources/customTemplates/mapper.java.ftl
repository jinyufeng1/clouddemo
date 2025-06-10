package ${package.Mapper};

import ${package.Entity}.${entity};
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * ${table.comment!} Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
public interface ${table.mapperName} {
//    **************************五大基础方法**************************
	@DataSource("slave")
	@Select("select * from ${table.name} WHERE id = <#noparse>#</#noparse>{id} and is_deleted = 0")
	${entity} getById(@Param("id") Long id);

	@DataSource("slave")
	@Select("select * from ${table.name} WHERE id = <#noparse>#</#noparse>{id}")
	${entity} extractById(@Param("id") Long id);
	
	@Update("update ${table.name} set is_deleted = 1, update_time = <#noparse>#</#noparse>{timestamp} where id = <#noparse>#</#noparse>{id} limit 1")
	int delete(@Param("id") Long id, @Param("timestamp") Integer timestamp);

	int insert(@Param("entity") ${entity} entity);
	
	int update(@Param("entity") ${entity} entity);
}
