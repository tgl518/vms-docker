package cn.yznu.vms.user.controller;

import cn.yznu.vms.common.exception.BusinessException;
import cn.yznu.vms.common.result.ResultCode;
import cn.yznu.vms.user.dto.UserRegisterDTO;
import cn.yznu.vms.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("UserController 集成测试")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Nested
    @DisplayName("POST /user/register - 用户注册接口测试")
    class RegisterEndpointTests {

        private UserRegisterDTO createValidDTO() {
            UserRegisterDTO dto = new UserRegisterDTO();
            dto.setUsername("valid_user");
            dto.setPassword("ValidPass123!");
            dto.setConfirmPassword("ValidPass123!");
            dto.setNickname("A Valid User");
            return dto;
        }

        @Test
        @DisplayName("注册成功_当请求数据完全合法")
        void register_shouldReturnSuccess_whenRequestIsValid() throws Exception {
            // Arrange
            UserRegisterDTO dto = createValidDTO();
            when(userService.register(any(UserRegisterDTO.class))).thenReturn(100L);

            // Act & Assert
            mockMvc.perform(post("/user/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.message").value("注册成功"))
                    .andExpect(jsonPath("$.data").value(100L));
        }

        @Test
        @DisplayName("注册失败_当用户名为空")
        void register_shouldReturnBadRequest_whenUsernameIsBlank() throws Exception {
            // Arrange
            UserRegisterDTO dto = createValidDTO();
            dto.setUsername("");

            // Act & Assert
            mockMvc.perform(post("/user/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("用户名不能为空")));
        }

        @Test
        @DisplayName("注册失败_当密码不符合复杂度要求")
        void register_shouldReturnBadRequest_whenPasswordIsWeak() throws Exception {
            // Arrange
            UserRegisterDTO dto = createValidDTO();
            dto.setPassword("weak");
            dto.setConfirmPassword("weak");

            // Act & Assert
            mockMvc.perform(post("/user/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("密码长度必须在8-30之间")));
        }

        @Test
        @DisplayName("注册失败_当两次密码不一致")
        void register_shouldReturnBadRequest_whenPasswordsDoNotMatch() throws Exception {
            // Arrange
            UserRegisterDTO dto = createValidDTO();
            dto.setConfirmPassword("DifferentPass123!");

            // Act & Assert
            mockMvc.perform(post("/user/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("两次输入的密码不一致")));
        }

        @Test
        @DisplayName("注册失败_当用户名已存在 (业务异常)")
        void register_shouldReturnOkWithFailCode_whenUsernameAlreadyExists() throws Exception {
            // Arrange
            UserRegisterDTO dto = createValidDTO();
            // 模拟Service层抛出业务异常
            when(userService.register(any(UserRegisterDTO.class)))
                    .thenThrow(new BusinessException(ResultCode.USER_ALREADY_EXISTS));

            // Act & Assert
            // 全局异常处理器将业务异常转换为 code != 200 的JSON响应，但HTTP状态码仍为200
            mockMvc.perform(post("/user/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(ResultCode.USER_ALREADY_EXISTS.getCode()))
                    .andExpect(jsonPath("$.message").value(ResultCode.USER_ALREADY_EXISTS.getMessage()));
        }
    }
}