package cn.yznu.vms.user.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * PasswordMatches 注解的校验器
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRegisterDTO> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        // 初始化，这里不需要特殊操作
    }

    @Override
    public boolean isValid(UserRegisterDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true; // 如果对象本身为null，不由这个校验器处理
        }
        String password = dto.getPassword();
        String confirmPassword = dto.getConfirmPassword();

        // 两个密码都为null，或者内容相等，则视为有效
        return password == null && confirmPassword == null || (password != null && password.equals(confirmPassword));
    }
}
