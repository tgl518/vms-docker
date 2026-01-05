package cn.yznu.vms.video.service.impl;

import cn.yznu.vms.common.enums.StatusEnum;
import cn.yznu.vms.video.entity.Category;
import cn.yznu.vms.video.feign.FileClient;
import cn.yznu.vms.video.mapper.CategoryMapper;
import cn.yznu.vms.video.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final FileClient fileClient;
    private static final Long SYSTEM_USER_ID = 0L;

    @Override
    @Cacheable(value = "categoryList", unless = "#result.isEmpty()")
    public List<Category> listEnabled() {
        return list(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, StatusEnum.ENABLED)
                .orderByAsc(Category::getSort));
    }

    @Override
    public void deleteWithFile(Long id) {
        // 1. 先获取分类信息
        Category category = getById(id);
        if (category == null) {
            return;
        }

        // 2. 删除数据库记录
        removeById(id);
        log.info("已删除分类, id={}", id);

        // 3. 删除图标文件
        if (StringUtils.hasText(category.getIcon())) {
            try {
                fileClient.deleteFile(category.getIcon(), SYSTEM_USER_ID);
                log.debug("已删除分类图标: {}", category.getIcon());
            } catch (Exception e) {
                log.warn("删除分类图标失败: {}", e.getMessage());
            }
        }
    }
}

