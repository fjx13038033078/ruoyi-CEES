package com.ruoyi.common.core.domain.model;

/**
 * 用户注册对象
 *
 * @author ruoyi
 */
public class RegisterBody extends LoginBody
{
    /** 用户类型（00系统用户 01考生 02家长 03招生管理员） */
    private String userType;

    /** 真实姓名 */
    private String realName;

    /** 身份证号 */
    private String idCard;

    /** 考生号 */
    private String examNumber;

    /** 关联考生号（家长注册时填写） */
    private String relatedExamNumber;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getExamNumber() {
        return examNumber;
    }

    public void setExamNumber(String examNumber) {
        this.examNumber = examNumber;
    }

    public String getRelatedExamNumber() {
        return relatedExamNumber;
    }

    public void setRelatedExamNumber(String relatedExamNumber) {
        this.relatedExamNumber = relatedExamNumber;
    }
}
