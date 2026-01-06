package cn.yznu.vms.video.controller;

import cn.yznu.vms.common.enums.VideoStatusEnum;
import cn.yznu.vms.video.entity.Video;
import cn.yznu.vms.video.service.VideoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VideoController.class)
class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideoService videoService;

    @Test
    void list_Success() throws Exception {
        // Prepare
        Page<Video> mockPage = new Page<>();
        mockPage.setRecords(Collections.singletonList(new Video()));
        mockPage.setTotal(1);

        when(videoService.listVideos(anyInt(), anyInt(), anyLong(), any(VideoStatusEnum.class), anyString()))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/video/list")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .param("categoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void detail_Success() throws Exception {
        // Prepare
        Long videoId = 1L;
        Video mockVideo = new Video();
        mockVideo.setId(videoId);
        mockVideo.setTitle("Test Video");

        when(videoService.getById(videoId)).thenReturn(mockVideo);

        // Act & Assert
        mockMvc.perform(get("/video/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.title").value("Test Video"));
    }
}
