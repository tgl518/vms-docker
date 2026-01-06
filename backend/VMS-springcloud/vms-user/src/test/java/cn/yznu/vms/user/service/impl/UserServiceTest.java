package cn.yznu.vms.user.service.impl;

import cn.yznu.vms.common.enums.StatusEnum;
import cn.yznu.vms.common.exception.BusinessException;
import cn.yznu.vms.common.result.ResultCode;
import cn.yznu.vms.user.config.JwtProperties;
import cn.yznu.vms.user.dto.UserLoginDTO;
import cn.yznu.vms.user.entity.User;
import cn.yznu.vms.user.entity.UserInfo;
import cn.yznu.vms.user.mapper.*;
import cn.yznu.vms.user.vo.LoginVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private UserInfoMapper userInfoMapper;
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private PermissionMapper permissionMapper;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private JwtProperties jwtProperties;
    @Mock
    private UserRoleMapper userRoleMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void login_Success() {
        // Prepare
        String username = "admin";
        String password = "password";
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setPassword(password);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(username);
        mockUser.setPassword("encodedPassword");
        mockUser.setStatus(StatusEnum.ENABLED);

        UserInfo mockUserInfo = new UserInfo();
        mockUserInfo.setNickname("Admin Nickname");

        when(userMapper.selectOne(any())).thenReturn(mockUser);
        when(passwordEncoder.matches(password, mockUser.getPassword())).thenReturn(true);
        when(userInfoMapper.selectById(1L)).thenReturn(mockUserInfo);
        when(roleMapper.selectCodesByUserId(1L)).thenReturn(List.of("admin"));
        when(jwtProperties.getSecret()).thenReturn("testSecretKey12345678901234567890");
        when(jwtProperties.getExpiration()).thenReturn(3600000L);
        when(permissionMapper.selectCodesByUserId(1L)).thenReturn(List.of("user:add"));

        // Act
        LoginVO result = userService.login(loginDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(username, result.getUsername());
        assertNotNull(result.getAccessToken());
        assertEquals(1, result.getRoles().size());
        assertEquals("admin", result.getRoles().get(0));
        assertEquals("Admin Nickname", result.getNickname());
        
        // Verify interactions
        verify(userMapper).selectOne(any());
        verify(passwordEncoder).matches(password, mockUser.getPassword());
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        // Prepare
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setUsername("unknown");
        loginDTO.setPassword("123");

        when(userMapper.selectOne(any())).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.login(loginDTO);
        });
        assertEquals(ResultCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void login_WrongPassword_ThrowsException() {
        // Prepare
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("wrong");

        User mockUser = new User();
        mockUser.setUsername("admin");
        mockUser.setPassword("encoded");

        when(userMapper.selectOne(any())).thenReturn(mockUser);
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.login(loginDTO);
        });
        assertEquals(ResultCode.PASSWORD_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    void login_UserDisabled_ThrowsException() {
        // Prepare
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setUsername("disabledUser");
        loginDTO.setPassword("pass");

        User mockUser = new User();
        mockUser.setUsername("disabledUser");
        mockUser.setPassword("encoded");
        mockUser.setStatus(StatusEnum.DISABLED);

        when(userMapper.selectOne(any())).thenReturn(mockUser);
        when(passwordEncoder.matches("pass", "encoded")).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.login(loginDTO);
        });
        assertEquals(ResultCode.USER_DISABLED.getMessage(), exception.getMessage());
    }
}
