package cn.yznu.vms.user.service.impl;

import cn.yznu.vms.common.exception.BusinessException;
import cn.yznu.vms.common.result.ResultCode;
import cn.yznu.vms.user.dto.ConfigCreateDTO;
import cn.yznu.vms.user.dto.ConfigUpdateDTO;
import cn.yznu.vms.user.entity.Config;
import cn.yznu.vms.user.mapper.ConfigMapper;
import cn.yznu.vms.user.service.ConfigService;
import cn.yznu.vms.user.vo.ConfigVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 系统配置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigMapper configMapper;

    @Override
    public IPage<ConfigVO> listConfigs(Integer pageNum, Integer pageSize, String configKey, String configType) {
        Page<Config> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(configKey)) {
            wrapper.like(Config::getConfigKey, configKey);
        }
        if (StringUtils.hasText(configType)) {
            wrapper.eq(Config::getConfigType, configType);
        }
        wrapper.orderByDesc(Config::getUpdateTime);
        
        IPage<Config> configPage = configMapper.selectPage(page, wrapper);
        
        return configPage.convert(this::convertToVO);
    }

    @Override
    public ConfigVO getConfigById(Long id) {
        Config config = configMapper.selectById(id);
        if (config == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(config);
    }

    @Override
    @Cacheable(value = "sysConfig", key = "#key", unless = "#result == null")
    public String getConfigValueByKey(String key) {
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigKey, key);
        Config config = configMapper.selectOne(wrapper);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public Long createConfig(ConfigCreateDTO dto) {
        // 检查key是否重复
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigKey, dto.getConfigKey());
        if (configMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("配置键已存在");
        }
        
        Config config = new Config();
        config.setConfigKey(dto.getConfigKey());
        config.setConfigValue(dto.getConfigValue());
        config.setConfigType(dto.getConfigType() != null ? dto.getConfigType() : "STRING");
        config.setDescription(dto.getDescription());
        config.setIsSystem(0); // 用户创建的非系统内置
        
        configMapper.insert(config);
        return config.getId();
    }

    @Override
    @CacheEvict(value = "sysConfig", allEntries = true)
    public void updateConfig(Long id, ConfigUpdateDTO dto) {
        Config config = configMapper.selectById(id);
        if (config == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        if (dto.getConfigValue() != null) {
            config.setConfigValue(dto.getConfigValue());
        }
        if (dto.getConfigType() != null) {
            config.setConfigType(dto.getConfigType());
        }
        if (dto.getDescription() != null) {
            config.setDescription(dto.getDescription());
        }
        
        configMapper.updateById(config);
    }

    @Override
    @CacheEvict(value = "sysConfig", allEntries = true)
    public void deleteConfig(Long id) {
        Config config = configMapper.selectById(id);
        if (config == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        // 系统内置配置不可删除
        if (config.getIsSystem() != null && config.getIsSystem() == 1) {
            throw new BusinessException("系统内置配置不能删除");
        }
        
        configMapper.deleteById(id);
    }

    /**
     * Config 转 ConfigVO
     */
    private ConfigVO convertToVO(Config config) {
        ConfigVO vo = new ConfigVO();
        vo.setId(config.getId());
        vo.setConfigKey(config.getConfigKey());
        vo.setConfigValue(config.getConfigValue());
        vo.setConfigType(config.getConfigType());
        vo.setDescription(config.getDescription());
        vo.setIsSystem(config.getIsSystem());
        vo.setCreateTime(config.getCreateTime());
        vo.setUpdateTime(config.getUpdateTime());
        return vo;
    }
}
