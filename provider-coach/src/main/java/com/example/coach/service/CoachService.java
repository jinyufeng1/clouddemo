package com.example.coach.service;

import com.example.coach.mapper.CoachMapper;
import com.example.common.Constant;
import com.example.common.util.CustomUtil;
import com.example.objcoach.dto.CoachItemDTO;
import com.example.objcoach.entity.Coach;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoachService {

    @Resource // 由于mybatis和spring的整合机制，可以和@Autowired注入互换
    private CoachMapper mapper;

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

    public List<Coach> getPageList(int page, String keyword, String orCategoryIds) {
        return mapper.getPageList((page - 1) * Constant.PAGE_SIZE, Constant.PAGE_SIZE, keyword, orCategoryIds);
    }

    public List<CoachItemDTO> getPageListLinkTable2(int page, List<Long> leafCategoryIds) {
        return mapper.getPageListLinkTable2((page - 1) * Constant.PAGE_SIZE, Constant.PAGE_SIZE, leafCategoryIds);
    }

    public List<CoachItemDTO> getPageListLinkTable(int page, String keyword) {
        return mapper.getPageListLinkTable((page - 1) * Constant.PAGE_SIZE, Constant.PAGE_SIZE, keyword);
    }

    public int count(String keyword, String orCategoryIds) {
        return mapper.count(keyword, orCategoryIds);
    }

    public List<Coach> getAll() {
        return mapper.getAll();
    }
}
