package cn.yznu.vms.operation.service;

import cn.yznu.vms.operation.entity.Banner;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BannerService extends IService<Banner> {

    /**
     * 获取当前有效的轮播图
     */
    List<Banner> listActive();

    /**
     * 删除轮播图并同步删除图片文件
     */
    void deleteWithFile(Long id);
}

