package cn.yznu.vms.video.service;

import cn.yznu.vms.common.enums.VideoStatusEnum;
import cn.yznu.vms.video.entity.Video;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface VideoService extends IService<Video> {

    /**
     * 分页查询视频列表
     */
    IPage<Video> listVideos(Integer pageNum, Integer pageSize, Long categoryId, VideoStatusEnum status, String title);

    /**
     * 获取热门视频
     */
    IPage<Video> getHotVideos(Integer pageNum, Integer pageSize);

    /**
     * 增加播放量
     */
    void incrementViewCount(Long videoId);

    /**
     * 级联删除视频及关联数据 (分集、标签关联)
     */
    void deleteWithRelations(Long videoId);

    /**
     * 按分类ID删除所有视频及其关联数据
     */
    void deleteByCategoryId(Long categoryId);

    /**
     * 保存视频标签关联
     *
     * @param videoId 视频ID
     * @param tagIds  标签ID列表
     */
    void saveVideoTags(Long videoId, java.util.List<Long> tagIds);

    /**
     * 获取用户投稿的视频列表
     */
    IPage<Video> listVideosByUserId(Integer pageNum, Integer pageSize, Long userId);

    /**
     * 更新视频统计计数（点赞、收藏、评论）
     *
     * @param videoId 视频ID
     * @param field   字段名（like_count, favorite_count, comment_count）
     * @param delta   增量（正数增加，负数减少）
     */
    void updateCount(Long videoId, String field, int delta);
}
