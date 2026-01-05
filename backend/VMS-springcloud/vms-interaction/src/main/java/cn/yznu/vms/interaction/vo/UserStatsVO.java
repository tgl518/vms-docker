package cn.yznu.vms.interaction.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 用户统计数据VO
 */
@Data
@Builder
public class UserStatsVO {
    /**
     * 收藏数
     */
    private Long favorites;
    
    /**
     * 历史记录数
     */
    private Long history;
    
    /**
     * 点赞数
     */
    private Long likes;
    
    /**
     * 评论数
     */
    private Long comments;
}
