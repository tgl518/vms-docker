package cn.yznu.vms.interaction.service;

import cn.yznu.vms.interaction.entity.UserLike;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserLikeService extends IService<UserLike> {

    boolean isLiked(Long userId, String targetType, Long targetId);

    void toggleLike(Long userId, String targetType, Long targetId);
}
