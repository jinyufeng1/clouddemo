package com.example.coach.console.service;

import com.alibaba.fastjson.JSON;
import com.example.coach.enums.BlockType;
import com.example.coach.service.CategoryService;
import com.example.coach.service.CoachService;
import com.example.coach.service.RelationTagCoachService;
import com.example.coach.service.TagService;
import com.example.common.Constant;
import com.example.common.domain.Response;
import com.example.common.util.CustomUtil;
import com.example.objcoach.dto.BlockDTO;
import com.example.objcoach.dto.EditCoachDTO;
import com.example.objcoach.entity.Category;
import com.example.objcoach.entity.Coach;
import com.example.objcoach.entity.RelationTagCoach;
import com.example.objcoach.entity.Tag;
import com.example.objcoach.vo.console.CoachDetailsVo;
import com.example.objcoach.vo.console.CoachItemListVo;
import com.example.objcoach.vo.console.CoachItemVo;
import com.example.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ConsoleCoachService {

    @Autowired
    private CoachService coachService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RelationTagCoachService relationTagCoachService;

    @Autowired
    private TagService tagService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

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

    /**
     * 合并 insert & update
     * @param dto
     * @return
     */
    // 在 Spring Boot 中，只要引入了相关依赖并使用了 @Transactional 注解，事务功能就会生效，无需手动添加 @EnableTransactionManagement
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
            result = coachService.insert(coach);
        } else {
            result = coachService.update(coach, true);
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
                    result = coachService.insert(coach);
                } else {
                    result = coachService.update(coach, true);
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
                result = coachService.insert(coach);
            } else {
                result = coachService.update(coach, true);
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

    public Response<Long> addCoach(String pics, String name, Long categoryId, String speciality, String intro, String tags) {
        EditCoachDTO editCoachDTO = new EditCoachDTO(null, pics, name.trim(), speciality, intro, categoryId, tags);
        Long coachId = edit(editCoachDTO);
        // 为保证一致性而删除
        if (null != coachId) {
            redisService.deleteKeysWithPrefix("list_");
        }

        // todo es
        return new Response<>(1001, coachId);
    }

    public Response<Boolean> delCoach(Long id) {
        Boolean result = coachService.delete(id);
        // 为保证一致性而删除
        if (result) {
            redisService.deleteKeysWithPrefix("list_");
        }

        // todo es
        return new Response<>(1001, result);
    }

    public Response<Long> updateCoach(Long id, Long categoryId, String pics, String name, String speciality, String intro, String tags) {
        Long coachId = edit(new EditCoachDTO(id, pics, null == name ? null : name.trim(), speciality, intro, categoryId, tags));
        // todo es
        return new Response<>(1001, coachId);
    }

    public Response<CoachItemListVo> getCoachList(Integer page, String keyword) {
        CoachItemListVo coachItemListVo = new CoachItemListVo();
        coachItemListVo.setPageSize(Constant.PAGE_SIZE);

        int coachTotal = coachService.count(keyword, getOrCategoryIdList(keyword));
        coachItemListVo.setTotal(coachTotal);
        if (0 == coachTotal) {
            log.info("总数都为0就不用查了，节约数据库访问");
            return new Response<>(1001, coachItemListVo);
        }

        //如果没有数据，getCoachList会拿到一个空的ArrayList对象
        List<Coach> pageList = coachService.getPageList(page, keyword, getOrCategoryIdList(keyword));

        // 没有取全表，而是根据id进行in条件查询
        Set<Long> categoryIds = pageList.stream().map(Coach::getCategoryId).collect(Collectors.toSet());

        // 获取分类映射列表
        Map<Long, String> categoryMap = categoryService.getList(null, categoryIds, null).stream().collect(Collectors.toMap(Category::getId, Category::getName));

        // vo就是再controller层做转换
        List<CoachItemVo> list = new ArrayList<>();
        for (Coach coach : pageList) {
            String category = categoryMap.get(coach.getCategoryId());
            if (null == category) {
                continue;
            }

            CoachItemVo coachItemVo = new CoachItemVo();
            coachItemVo.setId(coach.getId());
            coachItemVo.setName(coach.getName());
            String pics = coach.getPics();
            //不需要判断是否包含split参数，没有就不切
            String pic = StringUtils.hasLength(pics) ? pics.split(Constant.PIC_SPLIT)[0] : null;
            coachItemVo.setPic(pic);
            coachItemVo.setSpeciality(coach.getSpeciality());
            coachItemVo.setCategory(category);
            list.add(coachItemVo);
        }
        coachItemListVo.setList(list);

        return new Response<>(1001, coachItemListVo);
    }

    public Response<CoachItemListVo> getCoachList2(Integer page, String keyword) {
        CoachItemListVo coachItemListVo = new CoachItemListVo();
        coachItemListVo.setPageSize(Constant.PAGE_SIZE);

        int coachTotal = coachService.count(keyword, getOrCategoryIdList(keyword));
        coachItemListVo.setTotal(coachTotal);
        if (0 == coachTotal) {
            log.info("总数都为0就不用查了，节约数据库访问");
            return new Response<>(1001, coachItemListVo);
        }

        List<CoachItemVo> list = coachService.getPageListLinkTable(page, keyword)
                .stream()
                .map(e -> {
                    CoachItemVo coachItemVo = new CoachItemVo();
                    BeanUtils.copyProperties(e, coachItemVo);
                    return coachItemVo;
                }).collect(Collectors.toList());
        coachItemListVo.setList(list);
        return new Response<>(1001, coachItemListVo);
    }

    public Response<CoachDetailsVo> getCoachDetail(Long id) {
        CoachDetailsVo coachDetailsVo = new CoachDetailsVo();
        Coach coachInfo = coachService.getById(id);
        //自己写方法判断
        if (ObjectUtils.isEmpty(coachInfo)) {
            log.info("教练id：{}不存在", id);
            return new Response<>(1001, coachDetailsVo);
        }

        Long categoryId = coachInfo.getCategoryId();
        // 获取分类信息
        Category category = categoryService.getById(categoryId);
        if (ObjectUtils.isEmpty(category)) {
            log.info("分类id：{}不存在", categoryId);
            return new Response<>(1001, coachDetailsVo);
        }

        List<BlockDTO> contents = JSON.parseArray(coachInfo.getIntro(), BlockDTO.class);
        coachDetailsVo.setIntro(contents);
        String pics = coachInfo.getPics();
        if (StringUtils.hasLength(pics)) {
            //不需要判断是否包含split参数，没有就不切
            String[] split = pics.split(Constant.PIC_SPLIT);
            coachDetailsVo.setPics(Arrays.asList(split));
        }

        coachDetailsVo.setCategory(category.getName());
        coachDetailsVo.setIcon(category.getPic());
        List<String> tags = relationTagCoachService.getTagByCoachId(coachInfo.getId())
                .stream().map(Tag::getName).collect(Collectors.toList());
        coachDetailsVo.setTags(tags);

        coachDetailsVo.setCreateTime(CustomUtil.transformTimestamp(coachInfo.getCreateTime() * 1000L, Constant.DATE_PATTERN_1));
        coachDetailsVo.setUpdateTime(CustomUtil.transformTimestamp(coachInfo.getUpdateTime() * 1000L, Constant.DATE_PATTERN_1));
        return new Response<>(1001, coachDetailsVo);
    }

    public Response<Boolean> initEsDb() {
        // 先清空旧数据
        IndexOperations indexOperations = elasticsearchTemplate.indexOps(Coach.class);
        if (indexOperations.exists()) {
            indexOperations.delete();
        }

        indexOperations = elasticsearchTemplate.indexOps(Category.class);
        if (indexOperations.exists()) {
            indexOperations.delete();
        }

        indexOperations = elasticsearchTemplate.indexOps(Tag.class);
        if (indexOperations.exists()) {
            indexOperations.delete();
        }

        indexOperations = elasticsearchTemplate.indexOps(RelationTagCoach.class);
        if (indexOperations.exists()) {
            indexOperations.delete();
        }

        // 从数据库拿数据
        List<Category> categoryAll = categoryService.getAll();
        List<Coach> coachAll = coachService.getAll();
        List<Tag> tagAll = tagService.getAll();
        List<RelationTagCoach> relationTagCoachAll = relationTagCoachService.getAll();

        // 写到es
        for (Category category : categoryAll) {
            Category save = elasticsearchTemplate.save(category);
            System.out.println(save);
        }

        for (Coach coach : coachAll) {
            Coach save = elasticsearchTemplate.save(coach);
            System.out.println(save);
        }

        for (Tag tag : tagAll) {
            Tag save = elasticsearchTemplate.save(tag);
            System.out.println(save);
        }

        for (RelationTagCoach relationTagCoach : relationTagCoachAll) {
            RelationTagCoach save = elasticsearchTemplate.save(relationTagCoach);
            System.out.println(save);
        }

        return new Response<>(1001,true);
    }
}
