package com.example.objcoach.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "coach")  // 指定索引名称
public class Coach {
    @Id                                // Elasticsearch文档ID
    @Field(type = FieldType.Keyword)   // ID通常设为keyword类型
    private Long id;
    @Field(type = FieldType.Text)
    private String name;
    @Field(type = FieldType.Keyword)
    private String pics;
    @Field(type = FieldType.Keyword)
    private String speciality;
    @Field(type = FieldType.Keyword)
    private String intro;
    @Field(type = FieldType.Keyword)
    private Long categoryId;
    @Field(type = FieldType.Keyword)
    private Integer createTime;
    @Field(type = FieldType.Keyword)
    private Integer updateTime;
    @Field(type = FieldType.Keyword)
    private Integer isDeleted;
}
