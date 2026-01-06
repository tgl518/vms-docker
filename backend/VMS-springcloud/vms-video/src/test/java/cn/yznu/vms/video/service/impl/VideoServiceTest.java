package cn.yznu.vms.video.service.impl;

import cn.yznu.vms.common.enums.VideoStatusEnum;
import cn.yznu.vms.video.entity.Video;
import cn.yznu.vms.video.entity.VideoEpisode;
import cn.yznu.vms.video.feign.FileClient;
import cn.yznu.vms.video.mapper.VideoEpisodeMapper;
import cn.yznu.vms.video.mapper.VideoMapper;
import cn.yznu.vms.video.mapper.VideoTagRelMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private VideoMapper videoMapper;
    @Mock
    private VideoEpisodeMapper videoEpisodeMapper;
    @Mock
    private VideoTagRelMapper videoTagRelMapper;
    @Mock
    private FileClient fileClient;

    @InjectMocks
    private VideoServiceImpl videoService;

    @Test
    void listVideos_Success() {
        // Prepare
        int pageNum = 1;
        int pageSize = 10;
        Page<Video> mockPage = new Page<>(pageNum, pageSize);
        mockPage.setRecords(Collections.singletonList(new Video()));

        when(videoMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        // Act
        IPage<Video> result = videoService.listVideos(pageNum, pageSize, null, VideoStatusEnum.PUBLISHED, "Test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        verify(videoMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void deleteWithRelations_Success() {
        // Prepare
        Long videoId = 1L;
        Video mockVideo = new Video();
        mockVideo.setId(videoId);
        mockVideo.setCoverUrl("cover.jpg");
        mockVideo.setVideoUrl("video.mp4");

        List<VideoEpisode> episodes = Collections.singletonList(new VideoEpisode());

        when(videoMapper.selectById(videoId)).thenReturn(mockVideo);
        when(videoEpisodeMapper.selectList(any())).thenReturn(episodes);
        when(videoMapper.deleteById(videoId)).thenReturn(1);

        // Act
        videoService.deleteWithRelations(videoId);

        // Assert
        verify(videoEpisodeMapper).delete(any(LambdaQueryWrapper.class));
        verify(videoTagRelMapper).delete(any(LambdaQueryWrapper.class));
        verify(videoMapper).deleteById(videoId);
        // Verify asynchronous file deletion logic (might not be executed immediately in unit test environment if it's truly async, but here it's synchronous)
        verify(fileClient, atLeastOnce()).deleteFile(anyString(), anyLong());
    }

    @Test
    void incrementViewCount_Success() {
        // Prepare
        Long videoId = 100L;
        when(videoMapper.update(any(), any())).thenReturn(1);

        // Act
        videoService.incrementViewCount(videoId);

        // Assert
        verify(videoMapper).update(any(), any());
    }
}
