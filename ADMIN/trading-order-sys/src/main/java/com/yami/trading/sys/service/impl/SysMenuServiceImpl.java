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


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.trading.sys.constant.Constant;
import com.yami.trading.sys.dao.SysMenuMapper;
import com.yami.trading.sys.dao.SysRoleMenuMapper;
import com.yami.trading.sys.model.SysMenu;
import com.yami.trading.sys.service.SysMenuService;
import lombok.AllArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lgh
 */
@Service("sysMenuService")
@AllArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

	private final SysRoleMenuMapper sysRoleMenuMapper;

	private final SysMenuMapper sysMenuMapper;

	@Override
	public List<SysMenu> listMenuByUserId(Long userId,String appType) {
		// 用户的所有菜单信息
		List<SysMenu> sysMenus ;
		//系统管理员，拥有最高权限
		if(userId == Constant.SUPER_ADMIN_ID||userId == Constant.SUPER_ROOT_ID){
			sysMenus = sysMenuMapper.listMenu(appType);
		}else {
			sysMenus = sysMenuMapper.listMenuByUserId(userId,appType);

			//记录全勾选
			Map<Long,SysMenu> mapSysMenu = new HashMap<>();
            for (SysMenu sysMenu : sysMenus) {
                mapSysMenu.put(sysMenu.getMenuId(), sysMenu);
            }

			List<Long> bigIntArrays = new Vector<>();

			//权限
			List<SysMenu> sysMenus3 = sysMenuMapper.queryAllPerms2(userId);
			for (SysMenu sysMenu : sysMenus3) {
				if (!mapSysMenu.containsKey(sysMenu.getParentId())
						&& !bigIntArrays.contains(sysMenu.getParentId())
						&& sysMenu.getParentId() != 0) {
					bigIntArrays.add(sysMenu.getParentId());
				}
			}

			//补充
			if(!bigIntArrays.isEmpty()){
				List<SysMenu> sysMenus2 = sysMenuMapper.listMenuByUserId2(bigIntArrays,appType);
				for (SysMenu sysMenu : sysMenus2) {
					mapSysMenu.put(sysMenu.getMenuId(), sysMenu);
					//权限按钮的父节点
					if (!mapSysMenu.containsKey(sysMenu.getParentId())
							&& !bigIntArrays.contains(sysMenu.getParentId())
							&& sysMenu.getParentId() != 0) {
						bigIntArrays.add(sysMenu.getParentId());
					}
				}
				sysMenus.addAll(sysMenus2);
			}
			bigIntArrays.clear();

			//子菜单
            for (SysMenu sysMenu : sysMenus) {
                if (!mapSysMenu.containsKey(sysMenu.getParentId())
                        && !bigIntArrays.contains(sysMenu.getParentId())
                        && sysMenu.getParentId() != 0) {
                    bigIntArrays.add(sysMenu.getParentId());
                }
            }

			//补充
			if(!bigIntArrays.isEmpty()){
				List<SysMenu> sysMenus2 = sysMenuMapper.listMenuByUserId2(bigIntArrays,appType);
//				for (SysMenu sysMenu : sysMenus2) {
//					mapSysMenu.put(sysMenu.getMenuId(), sysMenu);
//					//权限按钮的父节点
//					if (!mapSysMenu.containsKey(sysMenu.getParentId())
//							&& !bigIntArrays.contains(sysMenu.getParentId())
//							&& sysMenu.getParentId() != 0) {
//						bigIntArrays.add(sysMenu.getParentId());
//					}
//				}
				sysMenus.addAll(sysMenus2);
			}





		}

		Map<Long, List<SysMenu>> sysMenuLevelMap = sysMenus.stream()
				.sorted(Comparator.comparing(SysMenu::getOrderNum))
				.collect(Collectors.groupingBy(SysMenu::getParentId));

		//System.out.println("sys = " +sysMenuLevelMap.values());

		// 一级菜单
		List<SysMenu> rootMenu = sysMenuLevelMap.get(0L);
		if (CollectionUtil.isEmpty(rootMenu)) {
			return Collections.emptyList();
		}

		// 二级菜单
		for (SysMenu sysMenu : rootMenu) {
			sysMenu.setList(sysMenuLevelMap.get(sysMenu.getMenuId()));
		}

		// 三级菜单
		for (SysMenu sysMenu : rootMenu) {
			List list = sysMenu.getList();
			if(list != null){
				for (Object object : list) {
					SysMenu sysMenu2 = (SysMenu)object;
					sysMenu2.setList(sysMenuLevelMap.get(sysMenu2.getMenuId()));
				}
			}
		}

		return rootMenu;
	}

	@Override
	public void deleteMenuAndRoleMenu(Long menuId){
		//删除菜单
		this.removeById(menuId);
		//删除菜单与角色关联
		sysRoleMenuMapper.deleteByMenuId(menuId);
	}


	@Override
	public List<Long> listMenuIdByRoleId(Long roleId,String appType) {
		return sysMenuMapper.listMenuIdByRoleId(roleId,appType);
	}


	@Override
	public List<SysMenu> listSimpleMenuNoButton(String appType) {
		return sysMenuMapper.listSimpleMenuNoButton(appType);
	}

	@Override
	public List<SysMenu> listRootMenu() {
		return sysMenuMapper.listRootMenu();
	}

	@Override
	public List<SysMenu> listChildrenMenuByParentId(Long parentId) {
		return sysMenuMapper.listChildrenMenuByParentId(parentId);
	}

	@Override
	public List<SysMenu> listMenuAndBtn( String appType) {
		return sysMenuMapper.listMenuAndBtn(appType);
	}

}
