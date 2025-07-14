package com.example.objcoach.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * <p>
 * 分类信息表
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-04-02
 */
@Data
@Document(indexName = "category")  // 指定索引名称
public class Category {
    @Id                                // Elasticsearch文档ID
    @Field(type = FieldType.Keyword)   // ID通常设为keyword类型
    private Long id;
    @Field(type = FieldType.Text)
    private String name;
    @Field(type = FieldType.Keyword)
    private String pic;
    @Field(type = FieldType.Keyword)
    private Long parentId;
    @Field(type = FieldType.Keyword)
    private Integer createTime;
    @Field(type = FieldType.Keyword)
    private Integer updateTime;
    @Field(type = FieldType.Keyword)
    private Integer isDeleted;
}
