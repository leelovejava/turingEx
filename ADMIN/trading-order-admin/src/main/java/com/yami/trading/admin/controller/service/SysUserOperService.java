package com.yami.trading.admin.controller.service;

import com.yami.trading.sys.model.SysUser;

import java.util.List;

public interface SysUserOperService {
    public List<String> getListUser(String sysUserId);
    void  addAgent(String userName, String password, String safeWord, String roleName, String remarks, String parentsUserCode, boolean loginAuthorityboolean, boolean  operaAuthority);

    void bindGoogleAuthCode(String googleAuthSecret,String id);

    void  restPassword(String password,String id);

    void unbindGoogleAuthCode(String id);


    public void saveLog(SysUser secUser, String operator, String context);

        boolean checkAgent();
}
