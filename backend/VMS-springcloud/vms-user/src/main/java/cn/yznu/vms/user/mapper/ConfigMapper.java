package cn.yznu.vms.user.mapper;

import cn.yznu.vms.user.entity.Config;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置 Mapper
 */
@Mapper
public interface ConfigMapper extends BaseMapper<Config> {
}
