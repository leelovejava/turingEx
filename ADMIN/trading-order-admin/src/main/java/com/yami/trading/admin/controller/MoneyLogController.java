package com.yami.trading.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.admin.model.MoneylogListModel;
import com.yami.trading.admin.model.MoneylogUpdateModel;
import com.yami.trading.bean.model.MoneyLog;
import com.yami.trading.bean.user.dto.MoneyLogDto;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.service.MoneyLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("moneylog")
@Api(tags = "账号资金变动记录")
public class MoneyLogController {


    @Autowired
    MoneyLogService moneyLogService;

    @Autowired
    PermissionFacade permissionFacade;

    @ApiOperation(value = "列表")
    @PostMapping("list")
    public Result<Page<MoneyLogDto>> list(@RequestBody @Valid MoneylogListModel model){
        Page<MoneyLogDto> page=new Page(model.getCurrent(),model.getSize());


        if (model.getStartTime()==null&&model.getEndTime()!=null){
            model.setEndTime(DateUtil.maxDate(model.getEndTime()));
            model.setStartTime(DateUtil.minDate(model.getEndTime()));
        }

        if (model.getStartTime()!=null&&model.getEndTime()==null){
            model.setEndTime(DateUtil.maxDate(model.getStartTime()));
            model.setStartTime(DateUtil.minDate(model.getStartTime()));
        }
        moneyLogService.pageMoneyLog(null,page,model.getRoleName(),model.getStartTime(),model.getEndTime(),model.getUserName(),
                model.getLog(),model.getCategory(),
                permissionFacade.getOwnerUserIds());
        page.getRecords().forEach(moneyLogDto -> {
            moneyLogDto.setCategoryText(Constants.MONEYLOG_CATEGORY.get(moneyLogDto.getCategory()));
            moneyLogDto.setRoleNameText(Constants.ROLE_MAP.get(moneyLogDto.getRoleName()));
            if (!StrUtil.isEmpty(moneyLogDto.getSymbol())){
                moneyLogDto.setSymbol(moneyLogDto.getSymbol().toUpperCase());
            }
            if (!StrUtil.isEmpty(moneyLogDto.getWalletType())){
                moneyLogDto.setWalletType(moneyLogDto.getWalletType().toUpperCase());
            }
        });
        return  Result.ok(page);
    }

    @ApiOperation(value = "設置前端是否雄安是")
    @PostMapping("updateShow")
    public Result<String> list(@RequestBody @Valid MoneylogUpdateModel model){
        UpdateWrapper<MoneyLog> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uuid", model.getUuid())
                        .set("`show`", model.getShow());
        moneyLogService.update(updateWrapper);
        return  Result.ok("修改成功");
    }

}
