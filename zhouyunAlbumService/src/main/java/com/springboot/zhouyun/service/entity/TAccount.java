package com.springboot.zhouyun.service.entity;

import com.zhouyun.core.base.entity.BaseEntity;

import java.util.Date;

public class TAccount extends BaseEntity {


    private String userCode;

    private String userName;

    private String userAccount;

    private String userPassword;

    private String userHeadPath;

    private Long addUserId;

    private String addUserName;



    private Integer enabled;

    private String remark;



    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserHeadPath() {
        return userHeadPath;
    }

    public void setUserHeadPath(String userHeadPath) {
        this.userHeadPath = userHeadPath;
    }

    public Long getAddUserId() {
        return addUserId;
    }

    public void setAddUserId(Long addUserId) {
        this.addUserId = addUserId;
    }

    public String getAddUserName() {
        return addUserName;
    }

    public void setAddUserName(String addUserName) {
        this.addUserName = addUserName;
    }



    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}