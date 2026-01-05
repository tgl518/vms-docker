package cn.yznu.vms.user.service;

import cn.yznu.vms.user.dto.UserInfoUpdateDTO;
import cn.yznu.vms.user.dto.UserLoginDTO;
import cn.yznu.vms.user.dto.UserRegisterDTO;
import cn.yznu.vms.user.vo.LoginVO;
import cn.yznu.vms.user.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param dto 登录信息
     * @return 登录结果 (含Token)
     */
    LoginVO login(UserLoginDTO dto);

    /**
     * 用户注册
     *
     * @param dto 注册信息
     * @return 用户ID
     */
    Long register(UserRegisterDTO dto);

    /**
     * 获取当前登录用户信息
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    UserVO getUserInfo(Long userId);

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param dto    更新信息
     */
    void updateUserInfo(Long userId, UserInfoUpdateDTO dto);

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户详情
     */
    UserVO getByUsername(String username);

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 分页查询用户列表 (管理员功能)
     *
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    com.baomidou.mybatisplus.core.metadata.IPage<UserVO> listUsers(Integer pageNum, Integer pageSize, cn.yznu.vms.user.dto.UserQueryDTO queryDTO);

    /**
     * 创建用户 (管理员功能)
     *
     * @param dto 创建信息
     * @return 用户ID
     */
    Long createUser(cn.yznu.vms.user.dto.UserCreateDTO dto);

    /**
     * 更新用户 (管理员功能)
     *
     * @param dto 更新信息
     * @param operatorId 操作者用户ID
     */
    void updateUser(cn.yznu.vms.user.dto.UserUpdateDTO dto, Long operatorId);

    /**
     * 删除用户 (管理员功能)
     *
     * @param userId 用户ID
     * @param operatorId 操作者用户ID
     */
    void deleteUser(Long userId, Long operatorId);

    /**
     * 重置用户密码 (管理员功能)
     *
     * @param userId      用户ID
     * @param newPassword 新密码
     * @param operatorId 操作者用户ID
     */
    void resetPassword(Long userId, String newPassword, Long operatorId);

    /**
     * 获取用户总数
     */
    Long count();

    /**
     * 获取今日新增用户数
     */
    Long getTodayNewUsers();
}
