package cn.yznu.vms.user.service.impl;

import cn.yznu.vms.common.exception.BusinessException;
import cn.yznu.vms.common.result.ResultCode;
import cn.yznu.vms.user.config.JwtProperties;
import cn.yznu.vms.user.dto.UserLoginDTO;
import cn.yznu.vms.user.dto.UserRegisterDTO;
import cn.yznu.vms.user.entity.User;
import cn.yznu.vms.user.entity.UserInfo;
import cn.yznu.vms.user.entity.UserRole;
import cn.yznu.vms.user.mapper.PermissionMapper;
import cn.yznu.vms.user.mapper.RoleMapper;
import cn.yznu.vms.user.mapper.UserInfoMapper;
import cn.yznu.vms.user.mapper.UserMapper;
import cn.yznu.vms.user.mapper.UserRoleMapper;
import cn.yznu.vms.user.vo.LoginVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl 认证功能白盒测试")
class UserServiceImplTest {

    // Mock all dependencies of UserServiceImpl
    @Mock private UserMapper userMapper;
    @Mock private UserInfoMapper userInfoMapper;
    @Mock private RoleMapper roleMapper;
    @Mock private UserRoleMapper userRoleMapper;
    @Mock private PermissionMapper permissionMapper;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private JwtProperties jwtProperties;

    @InjectMocks
    private UserServiceImpl userService;

    @Nested
    @DisplayName("注册 (Register) 功能测试")
    class RegisterTests {

        private UserRegisterDTO registerDTO;

        @BeforeEach
        void setUp() {
            registerDTO = new UserRegisterDTO();
            registerDTO.setUsername("testuser");
            registerDTO.setPassword("Password123!");
            registerDTO.setConfirmPassword("Password123!");
            registerDTO.setNickname("Test User");
        }

        @Test
        @DisplayName("注册成功_当业务校验通过")
        void register_shouldSucceed_whenBusinessLogicIsValid() {
            // Arrange
            String hashedPassword = "hashedPassword123";
            when(userMapper.selectCount(any())).thenReturn(0L);
            when(passwordEncoder.encode(registerDTO.getPassword())).thenReturn(hashedPassword);

            // Act
            Long userId = userService.register(registerDTO);

            // Assert
            assertThat(userId).isNotNull();

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userMapper, times(1)).insert(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getUsername()).isEqualTo(registerDTO.getUsername());
            assertThat(capturedUser.getPassword()).isEqualTo(hashedPassword);

            ArgumentCaptor<UserInfo> userInfoCaptor = ArgumentCaptor.forClass(UserInfo.class);
            verify(userInfoMapper, times(1)).insert(userInfoCaptor.capture());
            assertThat(userInfoCaptor.getValue().getUserId()).isEqualTo(capturedUser.getId());

            ArgumentCaptor<UserRole> userRoleCaptor = ArgumentCaptor.forClass(UserRole.class);
            verify(userRoleMapper, times(1)).insert(userRoleCaptor.capture());
            assertThat(userRoleCaptor.getValue().getUserId()).isEqualTo(capturedUser.getId());
            assertThat(userRoleCaptor.getValue().getRoleId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("注册失败_当用户名已存在")
        void register_shouldThrowException_whenUsernameAlreadyExists() {
            // Arrange
            when(userMapper.selectCount(any())).thenReturn(1L);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, () -> userService.register(registerDTO));
            assertThat(exception.getCode()).isEqualTo(ResultCode.USER_ALREADY_EXISTS.getCode());
            verify(userMapper, never()).insert(Collections.singleton(any()));
        }
    }

    @Nested
    @DisplayName("登录 (Login) 功能测试")
    class LoginTests {

        private UserLoginDTO loginDTO;
        private User testUser;

        @BeforeEach
        void setUp() {
            loginDTO = new UserLoginDTO();
            loginDTO.setUsername("testuser");
            loginDTO.setPassword("Password123!");

            testUser = new User();
            testUser.setId(1L);
            testUser.setUsername("testuser");
            testUser.setPassword("hashedPassword123");
            testUser.setStatus(cn.yznu.vms.common.enums.StatusEnum.ENABLED);
        }

        @Test
        @DisplayName("登录成功_当凭证有效且账户启用")
        void login_shouldSucceed_whenCredentialsAreValid() {
            // Arrange
            when(userMapper.selectOne(any())).thenReturn(testUser);
            when(passwordEncoder.matches(loginDTO.getPassword(), testUser.getPassword())).thenReturn(true);
            when(userInfoMapper.selectById(testUser.getId())).thenReturn(new UserInfo());
            when(roleMapper.selectCodesByUserId(testUser.getId())).thenReturn(Collections.singletonList("user"));
            when(permissionMapper.selectCodesByUserId(testUser.getId())).thenReturn(Collections.singletonList("video:view"));
            // Mock properties for JWT generation
            when(jwtProperties.getSecret()).thenReturn("a-very-long-and-secure-secret-key-for-testing");
            when(jwtProperties.getExpiration()).thenReturn(3600000L);

            // Act
            LoginVO loginVO = userService.login(loginDTO);

            // Assert
            assertThat(loginVO).isNotNull();
            assertThat(loginVO.getUserId()).isEqualTo(testUser.getId());
            assertThat(loginVO.getAccessToken()).isNotBlank();
        }

        @Test
        @DisplayName("登录失败_当用户不存在")
        void login_shouldThrowException_whenUserNotFound() {
            // Arrange
            when(userMapper.selectOne(any())).thenReturn(null);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, () -> userService.login(loginDTO));
            assertThat(exception.getCode()).isEqualTo(ResultCode.USER_NOT_FOUND.getCode());
        }

        @Test
        @DisplayName("登录失败_当密码错误")
        void login_shouldThrowException_whenPasswordIsIncorrect() {
            // Arrange
            when(userMapper.selectOne(any())).thenReturn(testUser);
            when(passwordEncoder.matches(loginDTO.getPassword(), testUser.getPassword())).thenReturn(false);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, () -> userService.login(loginDTO));
            assertThat(exception.getCode()).isEqualTo(ResultCode.PASSWORD_ERROR.getCode());
        }

        @Test
        @DisplayName("登录失败_当账户被禁用")
        void login_shouldThrowException_whenUserIsDisabled() {
            // Arrange
            testUser.setStatus(cn.yznu.vms.common.enums.StatusEnum.DISABLED);
            when(userMapper.selectOne(any())).thenReturn(testUser);
            when(passwordEncoder.matches(loginDTO.getPassword(), testUser.getPassword())).thenReturn(true);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, () -> userService.login(loginDTO));
            assertThat(exception.getCode()).isEqualTo(ResultCode.USER_DISABLED.getCode());
        }
    }
}
