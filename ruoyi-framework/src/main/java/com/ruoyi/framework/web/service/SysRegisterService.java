package com.ruoyi.framework.web.service;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.user.CaptchaException;
import com.ruoyi.common.exception.user.CaptchaExpireException;
import com.ruoyi.common.utils.IdCardUtils;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.password.PasswordStrengthUtils;
import com.ruoyi.college.domain.StudentProfile;
import com.ruoyi.college.service.IStudentProfileService;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;

/**
 * 注册校验方法
 *
 * @author ruoyi
 */
@Component
public class SysRegisterService {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private IStudentProfileService studentProfileService;

    /**
     * 注册
     */
    @Transactional(rollbackFor = Exception.class)
    public String register(RegisterBody registerBody) {
        String msg = "";
        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);

        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled) {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        if (StringUtils.isEmpty(username)) {
            msg = "用户名不能为空";
        } else if (StringUtils.isEmpty(password)) {
            msg = "用户密码不能为空";
        } else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            msg = "账户长度必须在2到20个字符之间";
        } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            msg = "密码长度必须在5到20个字符之间";
        } else if (!PasswordStrengthUtils.isAcceptableForRegister(password)) {
            msg = "密码安全性不足，请至少达到「中」：8位及以上，且包含字母、数字、符号中的至少两类";
        } else {
            String userType = StringUtils.trim(registerBody.getUserType());
            if (!"01".equals(userType) && !"02".equals(userType)) {
                msg = "请选择注册角色为考生或家长";
            } else {
                String idCard = StringUtils.trim(registerBody.getIdCard());
                if (StringUtils.isEmpty(idCard)) {
                    msg = "请填写身份证号";
                } else if (!IdCardUtils.isValid(idCard)) {
                    msg = "身份证号格式或校验码不正确";
                } else if (studentProfileService.selectByIdCard(idCard) != null) {
                    msg = "该身份证号已被注册";
                } else if (userService.selectUserByIdCard(idCard) != null) {
                    msg = "该身份证号已被注册";
                } else if (!userService.checkUserNameUnique(sysUser)) {
                    msg = "保存用户'" + username + "'失败，注册账号已存在";
                } else {
                    msg = validateRoleFields(userType, registerBody);
                    if (StringUtils.isEmpty(msg)) {
                        sysUser.setNickName(StringUtils.isNotEmpty(registerBody.getRealName()) ? registerBody.getRealName() : username);
                        sysUser.setPassword(SecurityUtils.encryptPassword(password));
                        sysUser.setUserType(userType);
                        if ("02".equals(userType)) {
                            sysUser.setIdCard(idCard);
                            String related = StringUtils.trim(registerBody.getRelatedExamNumber());
                            if (StringUtils.isNotEmpty(related)) {
                                sysUser.setRemark("关联考生号：" + related);
                            }
                        }

                        boolean regFlag = userService.registerUser(sysUser);
                        if (!regFlag) {
                            msg = "注册失败,请联系系统管理人员";
                        } else {
                            Long userId = sysUser.getUserId();
                            if ("01".equals(userType)) {
                                roleService.insertUserRole(userId, 102L);
                                StudentProfile profile = new StudentProfile();
                                profile.setUserId(userId);
                                profile.setRealName(registerBody.getRealName());
                                profile.setIdCard(idCard);
                                profile.setExamNumber(StringUtils.trim(registerBody.getExamNumber()));
                                studentProfileService.createProfile(profile);
                            } else {
                                roleService.insertUserRole(userId, 103L);
                            }
                            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success")));
                        }
                    }
                }
            }
        }
        return msg;
    }

    private String validateRoleFields(String userType, RegisterBody body) {
        if ("01".equals(userType)) {
            if (StringUtils.isEmpty(StringUtils.trim(body.getExamNumber()))) {
                return "考生注册请填写考生号";
            }
            if (!studentProfileService.checkExamNumberUnique(StringUtils.trim(body.getExamNumber()), null)) {
                return "该考生号已被使用";
            }
        } else {
            if (StringUtils.isEmpty(StringUtils.trim(body.getRelatedExamNumber()))) {
                return "家长注册请填写关联考生号";
            }
        }
        return "";
    }

    /**
     * 校验验证码
     */
    public void validateCaptcha(String username, String code, String uuid) {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null) {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            throw new CaptchaException();
        }
    }
}
