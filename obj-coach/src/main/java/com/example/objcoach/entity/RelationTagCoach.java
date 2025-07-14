package com.example.objcoach.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * <p>
 * 教练标签关联表
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-04-28
 */
@Data
@Document(indexName = "relation_tag_coach")  // 指定索引名称
public class RelationTagCoach {
    @Id                                // Elasticsearch文档ID
    @Field(type = FieldType.Keyword)   // ID通常设为keyword类型
    private Long id;
    @Field(type = FieldType.Keyword)   // ID通常设为keyword类型
    private Long coachId;
    @Field(type = FieldType.Keyword)   // ID通常设为keyword类型
    private Long tagId;
    @Field(type = FieldType.Keyword)   // ID通常设为keyword类型
    private Integer createTime;
    @Field(type = FieldType.Keyword)   // ID通常设为keyword类型
    private Integer updateTime;
    @Field(type = FieldType.Keyword)   // ID通常设为keyword类型
    private Integer isDeleted;
}
