package cn.yznu.vms.interaction.service;

import cn.yznu.vms.interaction.entity.UserFavorite;
import cn.yznu.vms.interaction.vo.FavoriteVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserFavoriteService extends IService<UserFavorite> {

    boolean isFavorited(Long userId, Long videoId);

    void toggleFavorite(Long userId, Long videoId);

    IPage<UserFavorite> listByUserId(Long userId, Integer pageNum, Integer pageSize);
    
    /**
     * 分页查询用户收藏列表（包含视频信息）
     */
    IPage<FavoriteVO> listWithVideoInfo(Long userId, Integer pageNum, Integer pageSize);
}
