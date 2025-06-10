package com.example.coach.service;

import com.alibaba.fastjson.JSON;
import com.example.coach.dto.BlockDTO;
import com.example.coach.dto.CoachItemDTO;
import com.example.coach.dto.EditCoachDTO;
import com.example.coach.entity.Coach;
import com.example.coach.entity.RelationTagCoach;
import com.example.coach.entity.Tag;
import com.example.coach.enums.BlockType;
import com.example.coach.mapper.CoachMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import com.example.common.Constant;
import com.example.common.util.CustomUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CoachService {
    @Autowired
    private CategoryService categoryService; // 谁依赖谁，谁就引入谁，一对多，多引一

    @Autowired
    private TagService tagService;

    @Autowired
    private RelationTagCoachService relationTagCoachService;

    @Resource // 由于mybatis和spring的整合机制，可以和@Autowired注入互换
    private CoachMapper mapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    /**
     * 作为额外的条件
     *
     * @param keyword
     * @return
     */
    private String getOrCategoryIdList(String keyword) {
        StringBuilder builder = new StringBuilder();
        categoryService.getList(keyword, null, true)
                .forEach(e -> builder.append(e.getId()).append(","));

        String orCategoryIds = builder.toString();
        // 去掉最后一个逗号
        orCategoryIds = "".equals(orCategoryIds) ? orCategoryIds : orCategoryIds.substring(0, orCategoryIds.length() - 1);

        return orCategoryIds;
    }

    public List<Coach> getPageList(int page, String keyword) {
        return mapper.getPageList((page - 1) * Constant.PAGE_SIZE, Constant.PAGE_SIZE, keyword, getOrCategoryIdList(keyword));
    }

    public List<CoachItemDTO> getPageListLinkTable2(int page, List<Long> leafCategoryIds) {
        return mapper.getPageListLinkTable2((page - 1) * Constant.PAGE_SIZE, Constant.PAGE_SIZE, leafCategoryIds);
    }

    public List<CoachItemDTO> getPageListLinkTable(int page, String keyword) {
        return mapper.getPageListLinkTable((page - 1) * Constant.PAGE_SIZE, Constant.PAGE_SIZE, keyword);
    }

    public int count(String keyword) {
        return mapper.count(keyword, getOrCategoryIdList(keyword));
    }

    public Coach getById(Long id) {
        if (null == id) {
            throw new RuntimeException("查询失败，id为空！");
        }

        return mapper.getById(id);
    }

    public Coach extractById(Long id) {
        if (null == id) {
            throw new RuntimeException("查询失败，id为空！");
        }

        return mapper.extractById(id);
    }

    public Boolean delete(Long id) {
        if (null == id) {
            throw new RuntimeException("软删除失败，id为空！");
        }

        long timestamp = System.currentTimeMillis() / 1000;
        return 1 == mapper.delete(id, (int) timestamp);
    }

    public Boolean insert(Coach coach) {
        // 缺参数的问题是方法使用的问题，让用户知道程序异常就好，在程序内部打印异常信息让程序员排错
        if (null == coach) {
            throw new RuntimeException("插入失败，coach为空！");
        }

        if (null == coach.getName()) {
            throw new RuntimeException("插入失败，name必填！");
        }

        if (null == coach.getCategoryId()) {
            throw new RuntimeException("插入失败，categoryId必填！");
        }

        long timestamp = System.currentTimeMillis() / 1000;
        coach.setCreateTime((int) timestamp);
        coach.setUpdateTime((int) timestamp);
        return 1 == mapper.insert(coach);
    }

    public static void main(String[] args) {
        Boolean s = null;
        System.out.println(!s);
    }

    public Boolean update(Coach coach, Boolean subTab) {
        if (null == coach) {
            throw new RuntimeException("更新失败，coach为空！");
        }

        if (null == coach.getId()) {
            throw new RuntimeException("更新失败，id为空！");
        }

        // 是否要修改其他子表的信息
        if (null == subTab || !subTab) {
            // 失去修改的意义
            if (null == coach.getName()
                    && null == coach.getPics()
                    && null == coach.getSpeciality()
                    && null == coach.getIntro()
                    && null == coach.getCategoryId()) {
                throw new RuntimeException("更新失败，业务字段全为空！");
            }
        }

        long timestamp = System.currentTimeMillis() / 1000;
        coach.setUpdateTime((int) timestamp);
        return 1 == mapper.update(coach);
    }

    /**
     * 合并 insert & update
     * @param dto
     * @return
     */
    @Transactional
    public Long edit(EditCoachDTO dto) {
        if (ObjectUtils.isEmpty(dto)) {
            throw new RuntimeException("dto对象为空");
        }

        // 分类校验
        Long categoryId = dto.getCategoryId();
        if (null != categoryId && ObjectUtils.isEmpty(categoryService.getById(categoryId))) {
            throw new RuntimeException("categoryId：" + categoryId + "不存在");
        }

        // 富文本类型检测
        String intro = dto.getIntro();
        if (null != intro) {
            // 如果intro不是json数组字符串，这里会抛JSONException异常，运行时异常
            List<BlockDTO> contents = JSON.parseArray(intro, BlockDTO.class);
            for (BlockDTO content : contents) {
                if(!BlockType.isArticleContentType(content.getType())){
                    throw new RuntimeException("block type is error");
                }
            }
        }

        // copy
        Coach coach = new Coach();
        BeanUtils.copyProperties(dto, coach);

        Boolean result;
        // id校验
        if (ObjectUtils.isEmpty(coach.getId())) {
            result = insert(coach);
        } else {
            result = update(coach, true);
        }

        if (!result) {
            return null;
        }

        Long coachId = coach.getId();
        // 标签业务
        String tags = dto.getTags();
        if (null != tags) {
            // 标签信息
            // Arrays.asList 返回的列表是固定大小的，不支持 add、remove 等修改操作。解决方案： 将Arrays.asList返回的结果 转换为 一个新的 ArrayList
            List<String> names = new ArrayList<>( Arrays.asList(tags.split(",")) );
            // 查询已有的标签
            List<Tag> tagList = tagService.getByNames(names);

            // 筛选出新的标签
            for (Tag tag : tagList) {
                names.remove(tag.getName());
            }

            // 插入新的标签
            if (!names.isEmpty()) {
                for (String name : names) {
                    Tag tag = new Tag();
                    tag.setName(name);
                    // 插入成功会被加入tagList
                    if (tagService.insert(tag)) {
                        tagList.add(tag);
                    }
                }
            }

            // 从这里开始只关心tagId
            List<Long> tagIds = tagList.stream().map(Tag::getId).collect(Collectors.toList());

            // 绑定关系信息
            // 查询教练当前标签
            List<Long> relationTagIds = relationTagCoachService.getTagByCoachId(coachId).stream().map(Tag::getId).collect(Collectors.toList());
            // 遍历写入标签id列表 建立新关系
            for (Long tagId : tagIds) {
                if (!relationTagIds.contains(tagId)) {
                    RelationTagCoach relationTagCoach = new RelationTagCoach();
                    relationTagCoach.setTagId(tagId);
                    relationTagCoach.setCoachId(coachId);
                    relationTagCoachService.insert(relationTagCoach);
                }
            }

            // 遍历老关系 筛选出不需要的关系后批量删除
            List<Long> delTagIds = new ArrayList<>();
            for (Long tagId : relationTagIds) {
                if (!tagIds.contains(tagId)) {
                    delTagIds.add(tagId);
                }
            }

            if (!delTagIds.isEmpty()) {
                relationTagCoachService.deleteByCoachId(coachId, delTagIds);
            }
        }
//        throw new RuntimeException("test"); // 测试方法抛出异常后sql有否回滚 结论：回滚
        return coachId;
    }

    public Long edit2(EditCoachDTO dto) {
        return transactionTemplate.execute(status -> {
            try {
                // 执行业务逻辑 如果没有异常，事务会自动提交
                if (ObjectUtils.isEmpty(dto)) {
                    throw new RuntimeException("dto对象为空");
                }

                // 分类校验
                Long categoryId = dto.getCategoryId();
                if (null != categoryId && ObjectUtils.isEmpty(categoryService.getById(categoryId))) {
                    throw new RuntimeException("categoryId：" + categoryId + "不存在");
                }

                // 富文本类型检测
                String intro = dto.getIntro();
                if (null != intro) {
                    // 如果intro不是json数组字符串，这里会抛JSONException异常，运行时异常
                    List<BlockDTO> contents = JSON.parseArray(intro, BlockDTO.class);
                    for (BlockDTO content : contents) {
                        if(!BlockType.isArticleContentType(content.getType())){
                            throw new RuntimeException("block type is error");
                        }
                    }
                }

                // copy
                Coach coach = new Coach();
                BeanUtils.copyProperties(dto, coach);

                Boolean result;
                // id校验
                if (ObjectUtils.isEmpty(coach.getId())) {
                    result = insert(coach);
                } else {
                    result = update(coach, true);
                }

                if (!result) {
                    return null;
                }

                Long coachId = coach.getId();
                // 标签业务
                String tags = dto.getTags();
                if (null != tags) {
                    // 标签信息
                    // Arrays.asList 返回的列表是固定大小的，不支持 add、remove 等修改操作。解决方案： 将Arrays.asList返回的结果 转换为 一个新的 ArrayList
                    List<String> names = new ArrayList<>( Arrays.asList(tags.split(",")) );
                    // 查询已有的标签
                    List<Tag> tagList = tagService.getByNames(names);

                    // 筛选出新的标签
                    for (Tag tag : tagList) {
                        names.remove(tag.getName());
                    }

                    // 插入新的标签
                    if (!names.isEmpty()) {
                        for (String name : names) {
                            Tag tag = new Tag();
                            tag.setName(name);
                            // 插入成功会被加入tagList
                            if (tagService.insert(tag)) {
                                tagList.add(tag);
                            }
                        }
                    }

                    // 从这里开始只关心tagId
                    List<Long> tagIds = tagList.stream().map(Tag::getId).collect(Collectors.toList());

                    // 绑定关系信息
                    // 查询教练当前标签
                    List<Long> relationTagIds = relationTagCoachService.getTagByCoachId(coachId).stream().map(Tag::getId).collect(Collectors.toList());
                    // 遍历写入标签id列表 建立新关系
                    for (Long tagId : tagIds) {
                        if (!relationTagIds.contains(tagId)) {
                            RelationTagCoach relationTagCoach = new RelationTagCoach();
                            relationTagCoach.setTagId(tagId);
                            relationTagCoach.setCoachId(coachId);
                            relationTagCoachService.insert(relationTagCoach);
                        }
                    }

                    // 遍历老关系 筛选出不需要的关系后批量删除
                    List<Long> delTagIds = new ArrayList<>();
                    for (Long tagId : relationTagIds) {
                        if (!tagIds.contains(tagId)) {
                            delTagIds.add(tagId);
                        }
                    }

                    if (!delTagIds.isEmpty()) {
                        relationTagCoachService.deleteByCoachId(coachId, delTagIds);
                    }
                }
//                int i = 10 / 0;
                return coachId;
            }
            catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }

    public Long edit3(EditCoachDTO dto) {
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            // 执行业务逻辑 如果没有异常，事务会自动提交
            if (ObjectUtils.isEmpty(dto)) {
                throw new RuntimeException("dto对象为空");
            }

            // 分类校验
            Long categoryId = dto.getCategoryId();
            if (null != categoryId && ObjectUtils.isEmpty(categoryService.getById(categoryId))) {
                throw new RuntimeException("categoryId：" + categoryId + "不存在");
            }

            // 富文本类型检测
            String intro = dto.getIntro();
            if (null != intro) {
                // 如果intro不是json数组字符串，这里会抛JSONException异常，运行时异常
                List<BlockDTO> contents = JSON.parseArray(intro, BlockDTO.class);
                for (BlockDTO content : contents) {
                    if(!BlockType.isArticleContentType(content.getType())){
                        throw new RuntimeException("block type is error");
                    }
                }
            }

            // copy
            Coach coach = new Coach();
            BeanUtils.copyProperties(dto, coach);

            Boolean result;
            // id校验
            if (ObjectUtils.isEmpty(coach.getId())) {
                result = insert(coach);
            } else {
                result = update(coach, true);
            }

            if (!result) {
                return null;
            }

            Long coachId = coach.getId();
            // 标签业务
            String tags = dto.getTags();
            if (null != tags) {
                // 标签信息
                // Arrays.asList 返回的列表是固定大小的，不支持 add、remove 等修改操作。解决方案： 将Arrays.asList返回的结果 转换为 一个新的 ArrayList
                List<String> names = new ArrayList<>( Arrays.asList(tags.split(",")) );
                // 查询已有的标签
                List<Tag> tagList = tagService.getByNames(names);

                // 筛选出新的标签
                for (Tag tag : tagList) {
                    names.remove(tag.getName());
                }

                // 插入新的标签
                if (!names.isEmpty()) {
                    for (String name : names) {
                        Tag tag = new Tag();
                        tag.setName(name);
                        // 插入成功会被加入tagList
                        if (tagService.insert(tag)) {
                            tagList.add(tag);
                        }
                    }
                }

                // 从这里开始只关心tagId
                List<Long> tagIds = tagList.stream().map(Tag::getId).collect(Collectors.toList());

                // 绑定关系信息
                // 查询教练当前标签
                List<Long> relationTagIds = relationTagCoachService.getTagByCoachId(coachId).stream().map(Tag::getId).collect(Collectors.toList());
                // 遍历写入标签id列表 建立新关系
                for (Long tagId : tagIds) {
                    if (!relationTagIds.contains(tagId)) {
                        RelationTagCoach relationTagCoach = new RelationTagCoach();
                        relationTagCoach.setTagId(tagId);
                        relationTagCoach.setCoachId(coachId);
                        relationTagCoachService.insert(relationTagCoach);
                    }
                }

                // 遍历老关系 筛选出不需要的关系后批量删除
                List<Long> delTagIds = new ArrayList<>();
                for (Long tagId : relationTagIds) {
                    if (!tagIds.contains(tagId)) {
                        delTagIds.add(tagId);
                    }
                }

                if (!delTagIds.isEmpty()) {
                    relationTagCoachService.deleteByCoachId(coachId, delTagIds);
                }
            }
//                int i = 10 / 0;
            // 提交事务
            transactionManager.commit(status);
            return coachId;
        }
        catch (Exception e) {
            // 回滚事务
            transactionManager.rollback(status);
            throw e;
        }
    }

    public List<Coach> getByProperty(Coach entity) {
        // 判断entity，避免无字段条件查到整张表的数据
        if (CustomUtil.isAllFieldsNull(entity)) {
            throw new RuntimeException("entity参数为null或属性全为null");
        }
        return mapper.getByProperty(entity);
    }

    /**
     * 这是一个我想的万金油的做法
     *
     * @param entity
     * @return
     */
    public Boolean deleteByProperty(Coach entity) {
        // 判断entity，避免无字段条件查到整张表的数据
        if (CustomUtil.isAllFieldsNull(entity)) {
            throw new RuntimeException("entity参数为null或属性全为null");
        }

        long timestamp = System.currentTimeMillis() / 1000;
        return 0 < mapper.deleteByProperty(entity, (int) timestamp);
    }

}
