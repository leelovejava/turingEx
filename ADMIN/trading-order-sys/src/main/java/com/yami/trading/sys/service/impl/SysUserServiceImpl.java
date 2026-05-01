/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.trading.sys.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.GoogleAuthenticator;
import com.yami.trading.common.util.RedisUtil;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.security.common.manager.PasswordManager;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.sys.dao.SysUserMapper;
import com.yami.trading.sys.dao.SysUserRoleMapper;
import com.yami.trading.sys.model.SysRole;
import com.yami.trading.sys.model.SysUser;
import com.yami.trading.sys.service.SysRoleService;
import com.yami.trading.sys.service.SysUserService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 系统用户
 * @author lgh
 */
@Service("sysUserService")
@AllArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

	private SysUserRoleMapper sysUserRoleMapper;

	private SysUserMapper sysUserMapper;
	@Autowired
	private SysparaService sysparaService;

	@Autowired
	PasswordManager passwordManager;

	@Autowired
	PasswordEncoder passwordEncoder;



	@Autowired
	private SysRoleService sysRoleService;
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveUserAndUserRole(SysUser user) {
		user.setCreateTime(new Date());
		sysUserMapper.insert(user);
		if(CollUtil.isEmpty(user.getRoleIdList())){
			return ;
		}
		//保存用户与角色关系
		sysUserRoleMapper.insertUserAndUserRole(user.getUserId(), user.getRoleIdList());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateUserAndUserRole(SysUser user) {
		// 更新用户
		sysUserMapper.updateById(user);

		//先删除用户与角色关系
		sysUserRoleMapper.deleteByUserId(user.getUserId());

		if(CollUtil.isEmpty(user.getRoleIdList())){
			return ;
		}
		//保存用户与角色关系
		sysUserRoleMapper.insertUserAndUserRole(user.getUserId(), user.getRoleIdList());
	}

	@Override
	public void updatePasswordByUserId(Long userId, String newPassword) {
        SysUser user = new SysUser();
        user.setPassword(newPassword);
        user.setUserId(userId);
        sysUserMapper.updateById(user);
    }

	@Override
	public void deleteBatch(Long[] userIds,Long shopId) {
		sysUserMapper.deleteBatch(userIds,shopId);
	}

	@Override
	public SysUser getByUserName(String username) {
		return sysUserMapper.selectByUsername(username);
	}

	@Override
	public SysUser getSysUserById(Long userId) {
		return sysUserMapper.selectById(userId);
	}

	@Override
	public List<String> queryAllPerms(Long userId) {
		return sysUserMapper.queryAllPerms(userId);
	}

	@Override
	public boolean checkGooleAuthCode(long code) {
		long t = System.currentTimeMillis();
		log.error(SecurityUtils.getSysUser().getUserId()+"=====");
	 	 SysUser sysUser= getById(SecurityUtils.getSysUser().getUserId());
		GoogleAuthenticator ga = new GoogleAuthenticator();
		ga.setWindowSize(5);
		boolean flag = ga.check_code(sysUser.getGoogleAuthSecret(),code,t);
		return flag;
	}

	@Override
	public boolean checkSuperGoogleAuthCode(String code) {
		String secret = sysparaService.find("super_google_auth_secret").getSvalue();
		if (StringUtils.isEmpty(code)) {
			throw new YamiShopBindException("验证码不能为空");
		}
		long t = System.currentTimeMillis();
		GoogleAuthenticator ga = new GoogleAuthenticator();
		ga.setWindowSize(5); // should give 5 * 30 seconds of grace...
		boolean checkCode = ga.check_code(secret, Long.valueOf(code), t);
		if (!checkCode) {
			throw new YamiShopBindException("超级管理员谷歌验证码错误");
		}
		return checkCode;
	}

	@Override
	public boolean checkSafeWord(String safeword) {
		SysUser sysUser= getById(SecurityUtils.getSysUser().getUserId());
		safeword=passwordManager.decryptPassword(safeword);
		if (StrUtil.isEmpty(sysUser.getSafePassword())){
			throw new YamiShopBindException("登录人资金密码未设置!");
		}
		if (StrUtil.isEmpty(safeword)){
			throw new YamiShopBindException("登录人资金密码不正确!");
		}
		System.out.printf(safeword+"=="+passwordEncoder+"======"+sysUser.getSafePassword());
		if (!passwordEncoder.matches(safeword, sysUser.getSafePassword())) {
			throw new YamiShopBindException("登录人资金密码不正确!");
		}
		return true;
	}

	@Override
	public Long demoUpdateTime(long userId) {//333
		return sysUserMapper.demoUpdateTime(userId, new Date());
	}

	@Override
	public List<String> getRoleNames(String userName){
		String key="role:username:"+userName;
		List<String> roleNames=	RedisUtil.get(key);
		if (CollectionUtil.isNotEmpty(roleNames)){
			Map<Long, SysRole> sysRoleMap = sysRoleService.list().stream().collect(Collectors.toMap(SysRole::getRoleId, SysRole -> SysRole));
			SysUser sec = this.getByUserName(userName);
			List<Long> roleIds = sysRoleService.listRoleIdByUserId(sec.getUserId());
			roleIds.forEach(rid -> {
				if (sysRoleMap.containsKey(rid)) {
					roleNames.add(sysRoleMap.get(rid).getRoleName());
				}
			});
			RedisUtil.set(key,roleNames,60);
		}

		return roleNames;
	}
}
