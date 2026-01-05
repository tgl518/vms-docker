package cn.yznu.vms.user.service;

import cn.yznu.vms.user.dto.ConfigCreateDTO;
import cn.yznu.vms.user.dto.ConfigUpdateDTO;
import cn.yznu.vms.user.vo.ConfigVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 系统配置服务接口
 */
public interface ConfigService {

    /**
     * 分页查询配置
     */
    IPage<ConfigVO> listConfigs(Integer pageNum, Integer pageSize, String configKey, String configType);

    /**
     * 根据ID获取配置
     */
    ConfigVO getConfigById(Long id);

    /**
     * 根据Key获取配置值
     */
    String getConfigValueByKey(String key);

    /**
     * 创建配置
     */
    Long createConfig(ConfigCreateDTO dto);

    /**
     * 更新配置
     */
    void updateConfig(Long id, ConfigUpdateDTO dto);

    /**
     * 删除配置
     */
    void deleteConfig(Long id);
}
