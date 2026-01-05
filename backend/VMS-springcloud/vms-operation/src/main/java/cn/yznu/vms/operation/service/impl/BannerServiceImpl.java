package cn.yznu.vms.operation.service.impl;

import cn.yznu.vms.operation.entity.Banner;
import cn.yznu.vms.operation.feign.FileClient;
import cn.yznu.vms.operation.mapper.BannerMapper;
import cn.yznu.vms.operation.service.BannerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    private final FileClient fileClient;

    // 系统用户ID，用于文件删除时的身份验证
    private static final Long SYSTEM_USER_ID = 0L;

    @Override
    public List<Banner> listActive() {
        LocalDateTime now = LocalDateTime.now();
        return list(new LambdaQueryWrapper<Banner>()
                .eq(Banner::getIsShow, 1)
                .and(w -> w
                        .isNull(Banner::getStartTime)
                        .or()
                        .le(Banner::getStartTime, now))
                .and(w -> w
                        .isNull(Banner::getEndTime)
                        .or()
                        .ge(Banner::getEndTime, now))
                .orderByAsc(Banner::getSort));
    }

    @Override
    public void deleteWithFile(Long id) {
        // 1. 先获取轮播图信息
        Banner banner = getById(id);
        if (banner == null) {
            return;
        }

        // 2. 删除数据库记录
        removeById(id);
        log.info("已删除轮播图, id={}", id);

        // 3. 删除图片文件
        deleteFileByUrl(banner.getImgUrl());
    }

    /**
     * 通过 URL 删除物理文件
     */
    private void deleteFileByUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return;
        }
        try {
            fileClient.deleteFile(url, SYSTEM_USER_ID);
            log.debug("已删除物理文件: {}", url);
        } catch (Exception e) {
            log.warn("删除物理文件失败: {}, 原因: {}", url, e.getMessage());
        }
    }
}

