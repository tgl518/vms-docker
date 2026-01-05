package cn.yznu.vms.video.service;

import cn.yznu.vms.video.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CategoryService extends IService<Category> {

    /**
     * 获取所有启用的分类
     */
    List<Category> listEnabled();

    /**
     * 删除分类并同步删除图标文件
     */
    void deleteWithFile(Long id);
}

