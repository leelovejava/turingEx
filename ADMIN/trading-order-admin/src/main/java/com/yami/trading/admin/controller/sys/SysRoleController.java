/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.trading.admin.controller.sys;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yami.trading.admin.controller.service.SysUserOperService;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.IPHelper;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.sys.model.DeleteRoleModel;
import com.yami.trading.sys.model.SysRole;
import com.yami.trading.sys.model.SysUser;
import com.yami.trading.sys.service.SysUserService;
import io.swagger.annotations.Api;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.yami.trading.common.util.PageParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.trading.sys.service.SysMenuService;
import com.yami.trading.sys.service.SysRoleService;
import com.yami.trading.common.annotation.SysLog;

import cn.hutool.core.util.StrUtil;



/**
 * 角色管理
 * @author lgh
 */
@RestController
@CrossOrigin
@RequestMapping("/sys/role")
@Api(tags = "角色管理")
public class SysRoleController{
	@Autowired
	private SysRoleService sysRoleService;

	@Autowired
	private SysMenuService sysMenuService;


	@Autowired
	SysUserOperService sysUserOperService;

	@Autowired
	private SysUserService sysUserService;

	/**
	 * 角色列表
	 */
	@GetMapping("/page")
//	@PreAuthorize("@pms.hasPermission('sys:role:page')")
	public ResponseEntity<IPage<SysRole>> page(String roleName,PageParam<SysRole> page){
		IPage<SysRole> sysRoles = sysRoleService.page(page,new LambdaQueryWrapper<SysRole>().like(StrUtil.isNotBlank(roleName),SysRole::getRoleName,roleName)
				.orderByDesc(SysRole::getCreateTime));
//		for (SysRole s: sysRoles.getRecords()){
//			List<Long> menuList = sysMenuService.listMenuIdByRoleId(s.getRoleId());
//		}
		return ResponseEntity.ok(sysRoles);
	}

	/**
	 * 角色列表
	 */
	@GetMapping("/list")
//	@PreAuthorize("@pms.hasPermission('sys:role:list')")
	public ResponseEntity<List<SysRole>> list(){
		List<SysRole> list = sysRoleService.list();
		if(CollectionUtils.isNotEmpty(list)) {
			list = list.stream().filter(value -> !"代理商".equals(value.getRoleName())).collect(Collectors.toList());
		}
		return ResponseEntity.ok(list);
	}

	/**
	 * 角色信息
	 */
	@GetMapping("/info/{roleId}")
	// @PreAuthorize("@pms.hasPermission('sys:role:info')")
	public ResponseEntity<SysRole> info(@PathVariable("roleId") Long roleId,String appType){
		SysRole role = sysRoleService.getById(roleId);

		//查询角色对应的菜单
		List<Long> menuList = sysMenuService.listMenuIdByRoleId(roleId,appType);
		role.setMenuIdList(menuList);

		return ResponseEntity.ok(role);
	}

	/**
	 * 保存角色
	 */
	@SysLog("保存角色")
	@PostMapping
//	@PreAuthorize("@pms.hasPermission('sys:role:save')")
	public ResponseEntity<Void> save(@RequestBody SysRole role){
		sysRoleService.saveRoleAndRoleMenu(role);

		Long userId = SecurityUtils.getSysUser().getUserId();
		SysUser sysUser = sysUserService.getById(userId);

		String context = MessageFormat.format("{0},ip:{1},时间[{2}],保存角色:[{3}]",
				new Object[]{
						sysUser.getUsername(),
						IPHelper.getIpAddr(),
						DateUtils.dateToStr(new Date(), DateUtils.DF_yyyyMMddHHmmss),
						JSONObject.toJSONString(role)
		});

		sysUserOperService.saveLog(sysUser,sysUser.getUsername(),context);

		return ResponseEntity.ok().build();
	}

	/**
	 * 修改角色
	 */
	@SysLog("修改角色")
	@PutMapping
//	@PreAuthorize("@pms.hasPermission('sys:role:update')")
	public ResponseEntity<Void> update(@RequestBody SysRole role){
		sysRoleService.updateRoleAndRoleMenu(role);

		Long userId = SecurityUtils.getSysUser().getUserId();
		SysUser sysUser = sysUserService.getById(userId);

		String context = MessageFormat.format("{0},ip:{1},时间[{2}],修改角色:[{3}]",
				new Object[]{
						sysUser.getUsername(),
						IPHelper.getIpAddr(),
						DateUtils.dateToStr(new Date(), DateUtils.DF_yyyyMMddHHmmss),
						JSONObject.toJSONString(role)
				});

		sysUserOperService.saveLog(sysUser,sysUser.getUsername(),context);

		return ResponseEntity.ok().build();
	}

	/**
	 * 删除角色
	 */
	@SysLog("删除角色")
	@DeleteMapping
//	@PreAuthorize("@pms.hasPermission('sys:role:delete')")
	public ResponseEntity<Void> delete(@RequestBody DeleteRoleModel model){
		sysRoleService.deleteBatch(model.getRoleIds(),model.getAppType());

		Long userId = SecurityUtils.getSysUser().getUserId();
		SysUser sysUser = sysUserService.getById(userId);

		String context = MessageFormat.format("{0},ip:{1},时间[{2}],删除角色:[{3}]",
				new Object[]{
						sysUser.getUsername(),
						IPHelper.getIpAddr(),
						DateUtils.dateToStr(new Date(), DateUtils.DF_yyyyMMddHHmmss),
						JSONObject.toJSONString(model)
				});

		sysUserOperService.saveLog(sysUser,sysUser.getUsername(),context);

		return ResponseEntity.ok().build();
	}
}
