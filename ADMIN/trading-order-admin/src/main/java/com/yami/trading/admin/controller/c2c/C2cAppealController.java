package com.yami.trading.admin.controller.c2c;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.c2c.model.C2cAppealHandlerModel;
import com.yami.trading.admin.controller.c2c.model.C2cAppealListModel;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.bean.c2c.C2cAppeal;
import com.yami.trading.bean.c2c.C2cOrder;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.AwsS3OSSFileService;
import com.yami.trading.service.c2c.C2cAppealService;
import com.yami.trading.service.c2c.C2cOrderService;
import com.yami.trading.service.data.RealtimeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("c2cAppeal")
@Api(tags = "C2C申诉")
public class C2cAppealController {
    @Autowired
    C2cAppealService c2cAppealService;
    @Autowired
    C2cOrderService c2cOrderService;

    @Autowired
    AwsS3OSSFileService awsS3OSSFileService;

    @Autowired
    RealtimeService realtimeService;

    @Autowired
    private PermissionFacade permissionFacade;

    /**
     * 获取 C2C申诉 列表
     */
    @PostMapping("list")
    public Result list(@RequestBody C2cAppealListModel request) {
//            String userNameLogin = this.getUsername_login();
//            if (null != userNameLogin) {
//                SecUser sec = this.secUserService.findUserByLoginName(userNameLogin);
//                Set<Role> roles = sec.getRoles();
//                Iterator<Role> it = roles.iterator();
//      3ef          while (it.hasNext()) {
//                    Role role = (Role) it.next();
//                    if (role.getRoleName().equals("C2C")) {
//                        secUuid = sec.getId().toString();
//                        break;
//                    }
//                }
//            }

        List<String> children = permissionFacade.getOwnerUserIds();

        Page<Map> page = new Page(request.getCurrent(), request.getSize());
        c2cAppealService.pagedQuery(page,
                request.getStatus(),
                request.getOrderNo(), request.getUserCode(),
                request.getRole_name(), request.getC2cUserCode(), request.getC2cUserType(),
                request.getC2cUserPartyCode(),children);
        for (Map<String, Object> map : page.getRecords()) {
            if (null == map.get("rolename")) {
                map.put("roleNameDesc", "");
            } else {
                String roleName = map.get("rolename").toString();
                map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
            }
            if (null != map.get("img")) {
                map.put("img", awsS3OSSFileService.getUrl(map.get("img").toString()));
            }
          if (  map.get("symbol")!=null){
              map.put("symbol",map.get("symbol").toString().toUpperCase());

          }

        }
        return Result.succeed(page);
    }

    /**
     * 已处理
     */
    @ApiOperation("已处理")
    @PostMapping("handled")
    public Result handled(@RequestBody  C2cAppealHandlerModel model) {
        C2cOrder order = c2cOrderService.get(model.getOrderNo());
        if (null == order) {
            throw new YamiShopBindException("订单不存在");
        }
        C2cAppeal appeal = c2cAppealService.findByOrderNo(model.getOrderNo());
        if (null == appeal) {
            throw new YamiShopBindException("申诉不存在");
        }
        c2cAppealService.handled(appeal, SecurityUtils.getSysUser().getUsername(), order.getPartyId());
        return Result.succeed();
    }
}
