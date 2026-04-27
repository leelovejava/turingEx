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

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yami.trading.common.annotation.SysLog;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.sys.constant.Constant;
import com.yami.trading.sys.constant.MenuType;
import com.yami.trading.sys.model.SysMenu;
import com.yami.trading.sys.service.SysMenuService;
import com.yami.trading.sys.service.SysUserService;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统菜单
 * @author lgh
 */
@RestController
@CrossOrigin
@RequestMapping("/sys/menu")
@AllArgsConstructor
@Slf4j
public class SysMenuController{

	@Autowired
	 SysMenuService sysMenuService;


	@Autowired
	SysUserService sysUserService;


	@GetMapping("/nav")
	@ApiOperation(value="获取用户所拥有的菜单和权限", notes="通过登陆用户的userId获取用户所拥有的菜单和权限")
	public ResponseEntity<Map<Object, Object>> nav(String appType){
		List<SysMenu> menuList = sysMenuService.listMenuByUserId(SecurityUtils.getSysUser().getUserId(),appType);
		List<SysMenu> newMenuList=new ArrayList<>();
		if (StrUtil.isNotBlank(appType)){
			for (SysMenu sysMenu:menuList){
				if (!StrUtil.isEmpty(sysMenu.getAppType())){
					if (checkAppType(sysMenu.getAppType(),appType)){
						newMenuList.add(sysMenu);
					}
				}
			}
		}
		else {
			newMenuList.addAll(menuList);
		}

		Set<String> permissinos=getUserPermissions(SecurityUtils.getSysUser().getUserId());
		if (SecurityUtils.getSysUser().getUsername().equals("root")){
			permissinos.add("sys:user:root");
		}
		return ResponseEntity.ok(MapUtil.builder().put("menuList", newMenuList).put("authorities",permissinos ).build());
	}

	public  boolean checkAppType(String menuAppType,String appType){
		boolean ok=false;
		String []	 appTypes=menuAppType.split(",");
		for (String str:appTypes){
			if (str.equals(appType)){
				ok=true;
				break;
			}
		}
		return ok;
	}


	private Set<String> getUserPermissions(Long userId) {
		List<String> permsList;
		//系统管理员，拥有最高权限
		if (userId == Constant.SUPER_ADMIN_ID||userId == Constant.SUPER_ROOT_ID) {
			List<SysMenu> menuList = sysMenuService.list(Wrappers.emptyWrapper());
			permsList = menuList.stream().map(SysMenu::getPerms).collect(Collectors.toList());
		} else {
			permsList = sysUserService.queryAllPerms(userId);
		}
		return permsList.stream().flatMap((perms) -> {
					if (StrUtil.isBlank(perms)) {
						return null;
					}
					return Arrays.stream(perms.trim().split(StrUtil.COMMA));
				}
		).collect(Collectors.toSet());
	}

	/**
	 * 获取菜单页面的表
	 * @return
	 */
	@GetMapping("/table")
	public ResponseEntity<List<SysMenu>> table(String appType){
		List<SysMenu> sysMenuList = sysMenuService.listMenuAndBtn(appType);
		return ResponseEntity.ok(sysMenuList);
	}

	/**
	 * 所有菜单列表(用于新建、修改角色时 获取菜单的信息)
	 */
	@GetMapping("/list")
	@ApiOperation(value="获取用户所拥有的菜单(不包括按钮)", notes="通过登陆用户的userId获取用户所拥有的菜单和权限")
	public ResponseEntity<List<SysMenu>> list(String appType){
		List<SysMenu> sysMenuList= sysMenuService.listSimpleMenuNoButton(appType);
		return ResponseEntity.ok(sysMenuList);
	}

	/**
	 * 选择菜单
	 */
	@GetMapping("/listRootMenu")
	public ResponseEntity<List<SysMenu>> listRootMenu(){
		//查询列表数据
		List<SysMenu> menuList = sysMenuService.listRootMenu();

		return ResponseEntity.ok(menuList);
	}

	/**
	 * 选择子菜单
	 */
	@GetMapping("/listChildrenMenu")
	public ResponseEntity<List<SysMenu>> listChildrenMenu(Long parentId){
		//查询列表数据
		List<SysMenu> menuList = sysMenuService.listChildrenMenuByParentId(parentId);

		return ResponseEntity.ok(menuList);
	}

	/**
	 * 菜单信息
	 */
	@GetMapping("/info/{menuId}")
//	@PreAuthorize("@pms.hasPermission('sys:menu:info')")
	public ResponseEntity<SysMenu> info(@PathVariable("menuId") Long menuId){
		SysMenu menu = sysMenuService.getById(menuId);
		return ResponseEntity.ok(menu);
	}

	/**
	 * 保存
	 */
	@SysLog("保存菜单")
	@PostMapping
//	@PreAuthorize("@pms.hasPermission('sys:menu:save')")
	public ResponseEntity<Void> save(@Valid @RequestBody SysMenu menu){
		//数据校验
		verifyForm(menu);
		sysMenuService.save(menu);
		return ResponseEntity.ok().build();
	}

	/**
	 * 修改
	 */
	@SysLog("修改菜单")
	@PutMapping
//	@PreAuthorize("@pms.hasPermission('sys:menu:update')")
	public ResponseEntity<String> update(@Valid @RequestBody SysMenu menu){
		//数据校验
		verifyForm(menu);

		if(menu.getType() == MenuType.MENU.getValue()){
			if(StrUtil.isBlank(menu.getUrl())){
				return ResponseEntity.badRequest().body("菜单URL不能为空");
			}
		}
		sysMenuService.updateById(menu);

		return ResponseEntity.ok().build();
	}

	/**
	 * 删除
	 */
	@SysLog("删除菜单")
	@DeleteMapping("/{menuId}")
//	@PreAuthorize("@pms.hasPermission('sys:menu:delete')")
	public ResponseEntity<String> delete(@PathVariable Long menuId){
		if(menuId <= Constant.SYS_MENU_MAX_ID){
			return ResponseEntity.badRequest().body("系统菜单，不能删除");
		}
		//判断是否有子菜单或按钮
		List<SysMenu> menuList = sysMenuService.listChildrenMenuByParentId(menuId);
		if(menuList.size() > 0){
			return ResponseEntity.badRequest().body("请先删除子菜单或按钮");
		}

		sysMenuService.deleteMenuAndRoleMenu(menuId);

		return ResponseEntity.ok().build();
	}

	/**
	 * 验证参数是否正确
	 */
	private void verifyForm(SysMenu menu){

		if(menu.getType() == MenuType.MENU.getValue()){
			if(StrUtil.isBlank(menu.getUrl())){
				throw new YamiShopBindException("菜单URL不能为空");
			}
		}
		if(Objects.equals(menu.getMenuId(), menu.getParentId())){
			throw new YamiShopBindException("自己不能是自己的上级");
		}


		if(StrUtil.isBlank(menu.getAppType())){
			throw new YamiShopBindException("菜单类型不能为空");
		}
		//上级菜单类型
		int parentType = MenuType.CATALOG.getValue();
		if(menu.getParentId() != 0){
			SysMenu parentMenu = sysMenuService.getById(menu.getParentId());
			parentType = parentMenu.getType();
		}

		//目录、菜单
		if(menu.getType() == MenuType.CATALOG.getValue() ||
				menu.getType() == MenuType.MENU.getValue()){
			if(parentType != MenuType.CATALOG.getValue()){
				throw new YamiShopBindException("上级菜单只能为目录类型");
			}
			return ;
		}

		//按钮
		if(menu.getType() == MenuType.BUTTON.getValue()){
			if(parentType != MenuType.MENU.getValue()){
				throw new YamiShopBindException("上级菜单只能为菜单类型");
			}
		}
	}
}
