package com.example.user.service;

import com.example.common.domain.CustomException;
import com.example.user.dto.EditUserDTO;
import com.example.user.entity.User;
import com.example.user.mapper.UserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-04-19
 */
@Slf4j
@Service
public class UserService {

    @Resource
    private UserMapper mapper;

//    **************************五大基础方法**************************
	public User getById(Long id) {
        if (null == id) {
            throw new RuntimeException("查询失败，id为空！");
        }

        return mapper.getById(id);
    }

    public User extractById(Long id) {
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
        return 1 == mapper.delete(id, (int)timestamp);
    }

	public Boolean insert(User entity) {
        // 缺参数的问题是方法使用的问题，让技术人员知道就好，在程序内部打印错误日志
        if (null == entity) {
            throw new RuntimeException("插入失败，entity为空！");
        }

        if (null == entity.getPassword()) {
            throw new RuntimeException("插入失败，password必填！");
        }

        String phone = entity.getPhone();
        if (null == phone) {
            throw new RuntimeException("插入失败，phone必填！");
        }

        long timestamp = System.currentTimeMillis() / 1000;
        entity.setCreateTime((int)timestamp);
        entity.setUpdateTime((int)timestamp);
        return 1 == mapper.insert(entity);
    }

    public Boolean update(User entity) {
        if (null == entity) {
            throw new RuntimeException("更新失败，entity为空！");
        }

        Long id = entity.getId();
        if (null == id) {
            throw new RuntimeException("更新失败，id为空！");
        }

        // 失去修改的意义
        if (null == entity.getName()
                && null == entity.getPassword()
                && null == entity.getPhone()
                && null == entity.getAvatar()) {
            throw new RuntimeException("更新失败，业务字段全为空");
        }

        long timestamp = System.currentTimeMillis() / 1000;
        entity.setUpdateTime((int)timestamp);
        return 1 == mapper.update(entity);
    }

    public User getByPhone(String phone) {
        if (!StringUtils.hasLength(phone)) {
            throw new RuntimeException("phone为空");
        }
        return mapper.getByPhone(phone);
    }


    /**
     * 合并 insert & update
     * @param dto
     * @return
     */
    public Long edit(EditUserDTO dto) {
        if (ObjectUtils.isEmpty(dto)) {
            throw new RuntimeException("dto对象为空");
        }

        String phone = dto.getPhone();
        if (null != phone) {
            // 手机号重复的问题是业务问题，应该让用户知道，所以抛出去
            User user = getByPhone(phone);
            if (null != user) {
                throw new CustomException(2002);
            }
        }

        String password = dto.getPassword();
        //BCrypt算法加密 盐在密文中
        if (null != password) {
            dto.setPassword(BCrypt.hashpw(password, BCrypt.gensalt())); // 生成一个随机的盐值并返回加密后的密码字符串。工作因子默认为10。
        }

        // copy
        User user = new User();
        BeanUtils.copyProperties(dto, user);

        Boolean result;
        // id校验
        if (ObjectUtils.isEmpty(user.getId())) {
            result = insert(user);
        }
        else {
            result = update(user);
        }
        return result ? user.getId() : null;
    }
}