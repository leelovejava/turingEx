package com.yami.trading.admin.facade;

import cn.hutool.core.collection.CollectionUtil;
import com.yami.trading.bean.model.User;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.RedisUtil;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.user.UserRecomService;
import com.yami.trading.service.user.UserService;
import com.yami.trading.sys.model.SysRole;
import com.yami.trading.sys.service.SysRoleService;
import com.yami.trading.sys.service.SysUserService;
import lombok.extern.flogger.Flogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PermissionFacade {
    @Autowired
    private UserRecomService userRecomService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    SysRoleService sysRoleService;
    @Autowired
    UserService userService;

    public boolean checkAgent() {

        List<Long> roleIds = sysRoleService.listRoleIdByUserId(SecurityUtils.getSysUser().getUserId());
        Map<Long, SysRole> sysRoleMap = sysRoleService.list().stream().collect(Collectors.toMap(SysRole::getRoleId, SysRole -> SysRole));
        List<String> roleNames = new ArrayList<>();
        boolean isAgent = false;
        for (Long id : roleIds) {
            if (sysRoleMap.containsKey(id)) {
                if (sysRoleMap.get(id).getRoleName().equals("代理商")) {
                    isAgent = true;
                }
            }
        }
        return isAgent;
    }

    public  List<String> getCahceOwnerUserIds(){
        String userName = SecurityUtils.getSysUser().getUsername();
        if (userName.equals("admin")) {
            return null;
        }
        String key="agent:user:username:"+userName;
        List<String> userIds=	RedisUtil.get(key);
        if (userIds!=null){
           userIds=   getOwnerUserIds();
           RedisUtil.set(key,userIds,60);
        }
        return userIds;
    }

    /**
     * 可以看到的前端用户的数据，如果没有，传入-,null表示所有所的权限
     *
     * @return
     */
    public List<String> getOwnerUserIds() {
        String userName = SecurityUtils.getSysUser().getUsername();
        if (userName.equals("admin")) {
            return null;
        }
        if (checkAgent()) {
            User user = userService.findByUserName(userName);

            if (user == null) {
                throw new BusinessException("账号异常");
            }
            List<String> checked_list = userRecomService.
                    findChildren(user.getUserId());
            if (checked_list.size() > 0) {
                return checked_list;
            } else {
                checked_list.add(userName);
                return checked_list;
            }
        } else {
            return null;
        }
//        if (CollectionUtil.isNotEmpty(roleNames) && roleNames.contains(Constants.SECURITY_ROLE_AGENT)) {
//            List<String> children = this.userRecomService.findChildren(userId.toString());
//            if (CollectionUtil.isEmpty(children)) {
//                children = Lists.newArrayList("-");
//            }
//            return children;
//        } else {
//            return null;
//        }
    }


    /**
     * 可以看到的前端用户的数据，如果没有，传入-,null表示所有所的权限
     *
     * @return
     */
    public List<String> getAdminOwnerUserIds() {
        String userName = SecurityUtils.getSysUser().getUsername();
        if (userName.equals("admin")) {
            return null;
        }
        if (checkAgent()) {
            User user = userService.findByUserName(userName);

            if (user == null) {
                throw new BusinessException("账号异常");
            }
            List<String> checked_list = userRecomService.
                    findChildren(user.getUserId());
            //log.info("getAdminOwnerUserIds => "+checked_list);
            for (int i = 0 ; checked_list!=null && i < checked_list.size() ; i++) {
                String id = checked_list.get(i);
                User child = userService.findByUserId(id);
//                log.info("getAdminOwnerUserIds id=> "+id);
//                log.info("getAdminOwnerUserIds child=> "+child);
                User virtuallyUser = userService.findByUserName(child.getUserId());
                if(virtuallyUser!=null){
                    //log.info("getAdminOwnerUserIds virtuallyUser=> "+virtuallyUser);
                    checked_list.add(virtuallyUser.getUserId());
                }
            }


            if (checked_list.size() > 0) {
                return checked_list;
            } else {
                checked_list.add(userName);
                return checked_list;
            }
        } else {
            return null;
        }
//        if (CollectionUtil.isNotEmpty(roleNames) && roleNames.contains(Constants.SECURITY_ROLE_AGENT)) {
//            List<String> children = this.userRecomService.findChildren(userId.toString());
//            if (CollectionUtil.isEmpty(children)) {
//                children = Lists.newArrayList("-");
//            }
//            return children;
//        } else {
//            return null;
//        }
    }


}
