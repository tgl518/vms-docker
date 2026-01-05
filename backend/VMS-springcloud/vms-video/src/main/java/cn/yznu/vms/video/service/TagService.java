package cn.yznu.vms.video.service;

import cn.yznu.vms.video.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface TagService extends IService<Tag> {

    /**
     * 获取热门标签
     */
    List<Tag> listHot(Integer limit);

    /**
     * 根据视频ID获取标签
     */
    List<Tag> listByVideoId(Long videoId);

    /**
     * 级联删除标签及关联数据
     */
    void deleteWithRelations(Long tagId);
}
