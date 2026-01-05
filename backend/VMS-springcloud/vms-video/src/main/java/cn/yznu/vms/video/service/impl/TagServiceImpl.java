package cn.yznu.vms.video.service.impl;

import cn.yznu.vms.video.entity.Tag;
import cn.yznu.vms.video.entity.VideoTagRel;
import cn.yznu.vms.video.mapper.TagMapper;
import cn.yznu.vms.video.mapper.VideoTagRelMapper;
import cn.yznu.vms.video.service.TagService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    private final VideoTagRelMapper videoTagRelMapper;

    @Override
    public List<Tag> listHot(Integer limit) {
        return list(new LambdaQueryWrapper<Tag>()
                .orderByDesc(Tag::getUseCount)
                .last("LIMIT " + limit));
    }

    @Override
    public List<Tag> listByVideoId(Long videoId) {
        List<VideoTagRel> rels = videoTagRelMapper.selectList(
                new LambdaQueryWrapper<VideoTagRel>().eq(VideoTagRel::getVideoId, videoId));
        if (rels.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> tagIds = rels.stream().map(VideoTagRel::getTagId).collect(Collectors.toList());
        return listByIds(tagIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWithRelations(Long tagId) {
        // 1. 删除标签与视频的关联
        videoTagRelMapper.delete(new LambdaQueryWrapper<VideoTagRel>()
                .eq(VideoTagRel::getTagId, tagId));
        log.debug("已删除标签关联, tagId={}", tagId);

        // 2. 删除标签本身
        removeById(tagId);
        log.info("已级联删除标签及关联数据, tagId={}", tagId);
    }
}
