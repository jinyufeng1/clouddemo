<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${package.Mapper}.${table.mapperName}">

    <insert id="insert" parameterType="${package.Entity}.${entity}" useGeneratedKeys="true" keyProperty="id">
        insert into ${table.name} (
		<trim suffixOverrides=",">
		<#list table.fields as field>
		<#if !field.keyFlag>
            <#if field.type?contains("char") || field.type?contains("text")>
			<if test="entity.${field.propertyName} != null and entity.${field.propertyName} != ''">`${field.name}`,</if>
			<#else>
			<if test="entity.${field.propertyName} != null">`${field.name}`,</if>
            </#if>
		</#if>
		</#list>
		</trim>
        )
        values (
		<trim suffixOverrides=",">
		<#list table.fields as field>
		<#if !field.keyFlag>
            <#if field.type?contains("char") || field.type?contains("text")>
			<if test="entity.${field.propertyName} != null and entity.${field.propertyName} != ''"><#noparse>#</#noparse>{entity.${field.propertyName}},</if>
            <#else>
            <if test="entity.${field.propertyName} != null"><#noparse>#</#noparse>{entity.${field.propertyName}},</if>
            </#if>
		</#if>
		</#list>
		</trim>
        )
    </insert>
	
	<update id="update" parameterType="${package.Entity}.${entity}">
        update ${table.name}
        set
		<trim suffixOverrides=",">
		<#list table.fields as field>
		<#if !field.keyFlag>
            <#if field.type?contains("char") || field.type?contains("text")>
			<if test="entity.${field.propertyName} != null and entity.${field.propertyName} != ''">`${field.name}` = <#noparse>#</#noparse>{entity.${field.propertyName}},</if>
			<#else>
			<if test="entity.${field.propertyName} != null">`${field.name}` = <#noparse>#</#noparse>{entity.${field.propertyName}},</if>
			</#if>
		</#if>
		</#list>
		</trim>
        where id = <#noparse>#</#noparse>{entity.id} limit 1
    </update>

</mapper>
