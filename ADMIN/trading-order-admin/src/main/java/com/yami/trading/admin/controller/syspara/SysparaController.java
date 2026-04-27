package com.yami.trading.admin.controller.syspara;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.yami.trading.admin.controller.finance.AdminFinanceOrderController;
import com.yami.trading.admin.util.BackupUtil;
import com.yami.trading.bean.model.Log;
import com.yami.trading.bean.syspara.dto.SysparasDto;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.query.QueryWrapperGenerator;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.system.LogService;
import com.yami.trading.sys.service.SysLogService;
import com.yami.trading.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.bean.syspara.dto.SysparaDTO;
import com.yami.trading.bean.syspara.mapstruct.SysparaWrapper;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.bean.syspara.query.SysparaQuery;
import org.springframework.web.servlet.ModelAndView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 配置参数Controller
 *
 * @author lucas
 * @version 2023-03-17
 */
@Api(tags = "配置参数")
@RestController
@CrossOrigin
@Slf4j
@RequestMapping(value = "normal/adminSysparaAction!")
public class SysparaController {
    @Autowired
    private SysparaService sysparaService;
    @Autowired
    private SysparaWrapper sysparaWrapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    protected SysLogService sysLogService;

    @Autowired
    LogService logService;

    /**
     * 配置参数列表数据
     */
    @ApiOperation(value = "获取 系统参数（ROOT) 列表")
    @GetMapping("list.action")
    public Result<IPage<Syspara>> list(@RequestParam(required = false) String notes, Page<Syspara> page) throws Exception {
        SysparaQuery sysparaQuery = new SysparaQuery();
        if (StringUtils.isNotEmpty(notes)) {
            sysparaQuery.setNotes(notes);
        }
        sysparaQuery.setNotStype(3L);
        QueryWrapper queryWrapper = QueryWrapperGenerator.buildQueryCondition(sysparaQuery, SysparaQuery.class);
        queryWrapper.orderByDesc("code");
        IPage<Syspara> result = sysparaService.page(page, queryWrapper);
        return Result.ok(result);
    }

    /**
     * 配置参数列表数据
     */
    @ApiOperation(value = "获取 系统参数（ADMIN) 列表")
    @GetMapping("listAdmin.action")
    public Result<IPage<Syspara>> listAdmin(@RequestParam(required = false) String notesPara, Page<Syspara> page) throws Exception {
        SysparaQuery sysparaQuery = new SysparaQuery();
        if (StringUtils.isNotEmpty(notesPara)) {
            sysparaQuery.setNotes(notesPara);
        }
        QueryWrapper queryWrapper = QueryWrapperGenerator.buildQueryCondition(sysparaQuery, SysparaQuery.class);
        queryWrapper.in("stype", 1L, 2L);
        queryWrapper.orderByDesc("code");
        IPage<Syspara> result = sysparaService.page(page, queryWrapper);
        return Result.ok(result);
    }

    /**
     * 配置参数列表数据
     */
    @ApiOperation(value = "修改 系统参数admin 页面")
    @GetMapping("toUpdate.action")
    public Result<SysparasDto> toUpdate() throws Exception {
        List<Syspara> list = sysparaService.list();
        Map<String, String> map = list.stream()
                .filter(s -> s.getSvalue() != null)
                .collect(Collectors.toMap(Syspara::getCode, Syspara::getSvalue, (s1, s2) -> s2));
        SysparasDto sysparasDto = new SysparasDto();
        BeanUtil.fillBeanWithMap(map, sysparasDto, true);
        List<String> needMultipleProperties = Lists.newArrayList("",
                "", "withdraw_limit_turnover_percent", "futures_most_prfit_level"
        );//exchange_apply_order_buy_fee  exchange_apply_order_sell_fee
        for (String field : needMultipleProperties) {
            Object fieldValueObject = BeanUtil.getFieldValue(sysparasDto, field);
            System.out.println("field => " + field);
            System.out.println("fieldValueObject => " + fieldValueObject);
            if (fieldValueObject == null) {
                continue;
            }
            String fieldValue = fieldValueObject.toString();
            if (StrUtil.isBlank(fieldValue)) {
                continue;
            }
            BeanUtil.setFieldValue(sysparasDto, field, Arith.mul(fieldValue, "100"));
        }
        String withdraw_limit_time = sysparasDto.getWithdraw_limit_time();
        if (StringUtils.isNotBlank(withdraw_limit_time)) {
            String[] withdraw_time = withdraw_limit_time.split("-");
            sysparasDto.setWithdraw_limit_time_min(withdraw_time[0]);
            sysparasDto.setWithdraw_limit_time_max(withdraw_time[1]);
        }
        // 设置 usdt 的最低和最高限额
        if (sysparasDto.getWithdraw_limit_usdt() != null) {
            sysparasDto.setWithdraw_limit_dapp(sysparasDto.getWithdraw_limit_usdt());
        } else {
            sysparasDto.setWithdraw_limit_dapp(sysparasDto.getWithdraw_limit());
        }
        if (sysparasDto.getWithdraw_limit_max_usdt() != null) {
            sysparasDto.setWithdraw_limit_max(sysparasDto.getWithdraw_limit_max_usdt());
        } else {
            sysparasDto.setWithdraw_limit_max(sysparasDto.getWithdraw_limit_max());
        }
        return Result.ok(sysparasDto);
    }

    /**
     * 配置参数列表数据
     */
    @ApiOperation(value = "修改 系统参数admin ")
    @PostMapping("updateAdmin.action")
    public Result<String> updateAdmin(@Valid @RequestBody SysparasDto sysparasDto) throws Exception {
        verification(sysparasDto);
        verificationWithdraw(sysparasDto);
        verificationOthers(sysparasDto);
        sysUserService.checkSafeWord(sysparasDto.getLogin_safeword());
        List<String> needDivideProperties = Lists.newArrayList("",
                "", "withdraw_limit_turnover_percent", "futures_most_prfit_level"
        );
        //exchange_apply_order_buy_fee  exchange_apply_order_sell_fee
        for (String field : needDivideProperties) {
            Object fieldValueObject = BeanUtil.getFieldValue(sysparasDto, field);
            if (fieldValueObject == null) {
                continue;
            }
            String fieldValue = fieldValueObject.toString();
            if (StrUtil.isBlank(fieldValue)) {
                continue;
            }
            BeanUtil
                    .setFieldValue(sysparasDto, field, Arith.div(fieldValue, "100"));
        }
        String withdraw_limit_time_min = sysparasDto.getWithdraw_limit_time_min();
        String withdraw_limit_time_max = sysparasDto.getWithdraw_limit_time_max();
        if (!"".equals(withdraw_limit_time_min) && !"".equals(withdraw_limit_time_max)
                && isValidDate(withdraw_limit_time_min) && isValidDate(withdraw_limit_time_max)) {
            sysparasDto.setWithdraw_limit_time(withdraw_limit_time_min + "-" + withdraw_limit_time_max);
        } else {
            sysparasDto.setWithdraw_limit_time_min("");
            sysparasDto.setWithdraw_limit_time_max("");
        }
        sysparaService.updateSysparas(sysparasDto);
        //todo log
        return Result.ok("更新成功");
    }

    private void verification(SysparasDto sysparasDto) {
        if (StringUtils.isEmpty(sysparasDto.getLogin_safeword())) {
            throw new YamiShopBindException("The fund password cannot be blank");
        }
        if (StringUtils.isEmpty(sysparasDto.getSuper_google_auth_code())) {
            throw new YamiShopBindException("谷歌验证码不能为空");
        }
    }

    private void verificationWithdraw(SysparasDto sysparasDto) {
        if (StringUtils.isBlank(sysparasDto.getWithdraw_limit_num())) {
            throw new YamiShopBindException("withdraw_limit_num不能为空");
        }
        if (StringUtils.isBlank(sysparasDto.getWithdraw_limit())) {
            throw new YamiShopBindException("withdraw_limit不能为空");
        }
        if (StringUtils.isBlank(sysparasDto.getWithdraw_limit_turnover_percent())) {
            throw new YamiShopBindException("withdraw_limit_turnover_percent不能为空");
        }
        if (StringUtils.isBlank(sysparasDto.getWithdraw_limit_max())) {
            throw new YamiShopBindException("withdraw_limit_max不能为空");
        }
        if (StringUtils.isBlank(sysparasDto.getWithdraw_limit_dapp())) {
            throw new YamiShopBindException("withdraw_limit_dapp不能为空");
        }
        double withdraw_limit_num = Double.valueOf(sysparasDto.getWithdraw_limit_num());
        double withdraw_limit = Double.valueOf(sysparasDto.getWithdraw_limit());
        double withdraw_limit_turnover_percent = Double.valueOf(sysparasDto.getWithdraw_limit_turnover_percent());
        String withdraw_limit_time_min = sysparasDto.getWithdraw_limit_time_min();
        String withdraw_limit_time_max = sysparasDto.getWithdraw_limit_time_max();
        double withdraw_limit_max = Double.valueOf(sysparasDto.getWithdraw_limit_max());
        double withdraw_limit_dapp = Double.valueOf(sysparasDto.getWithdraw_limit_dapp());
        if (withdraw_limit_num < 0) {
            throw new YamiShopBindException("每日可提现次数不得小于0");
        }
        if (withdraw_limit < 0) {
            throw new YamiShopBindException("提现最低金额不得小于0");
        }
        if (withdraw_limit_turnover_percent < 0) {
            throw new YamiShopBindException("提现限制流水按百分币不得小于0");
        }
        if (StringUtils.isEmpty(withdraw_limit_time_min)) {
            throw new YamiShopBindException("最早提现时间不能为空");
        }
        if (StringUtils.isEmpty(withdraw_limit_time_max)) {
            throw new YamiShopBindException("最晚提现时间不能为空");
        }
        if (withdraw_limit_dapp < 0) {
            throw new YamiShopBindException("最低提现额度不得小于0");
        }
        if (withdraw_limit_max < withdraw_limit_dapp) {
            throw new YamiShopBindException("usdt提现最高限额小于最低限额");
        }
        // 设置usdt提现最低和最高限额
        sysparasDto.setWithdraw_limit_usdt(sysparasDto.getWithdraw_limit_dapp());
        sysparasDto.setWithdraw_limit_max_usdt(sysparasDto.getWithdraw_limit_max());
        sysparasDto.setWithdraw_limit_max(null);
        sysparasDto.setWithdraw_limit_dapp(null);
        sysparasDto.setWithdraw_limit(null);
    }

    private void verificationOthers(SysparasDto sysparasDto) {
        Double recharge_limit_min = Double.valueOf(sysparasDto.getRecharge_limit_min());
        Double recharge_limit_max = Double.valueOf(sysparasDto.getRecharge_limit_max());
        Double exchange_apply_order_buy_fee = Double.valueOf(sysparasDto.getExchange_apply_order_buy_fee());
        Double exchange_apply_order_sell_fee = Double.valueOf(sysparasDto.getExchange_apply_order_sell_fee());
        Double futures_most_prfit_level = Double.valueOf(sysparasDto.getFutures_most_prfit_level());
        if (recharge_limit_min < 0) {
            throw new YamiShopBindException("充值最低金额不得小于0");
        }
        if (recharge_limit_max < 0 || recharge_limit_max < recharge_limit_min) {
            throw new YamiShopBindException("充值最高金额不得小于0或小于充值最低金额");
        }
        if (exchange_apply_order_buy_fee < 0) {
            throw new YamiShopBindException("币币交易买入手续费不得小于0");
        }
        if (exchange_apply_order_sell_fee < 0) {
            throw new YamiShopBindException("币币交易卖出手续费不得小于0");
        }
        if (futures_most_prfit_level < 0) {
            throw new YamiShopBindException("交割合约赢率不得小于0");
        }
    }

    /**
     * 保存配置参数
     */
    @ApiOperation(value = "修改 系统参数（ROOT）/ 系统参数（ADMIN）")
    @PostMapping("update.action")
    public Result<String> update(@Valid @RequestBody SysparaDTO sysparaDTO) {
        Syspara syspara = this.sysparaService.find(sysparaDTO.getCode());
        if (syspara != null) {
            String notes = syspara.getNotes();
            String code = syspara.getCode();
            String oldValue = syspara.getSvalue();
            syspara.setSvalue(sysparaDTO.getValue());
            this.sysparaService.saveOrUpdate(syspara);

            // 操作日志
            Log log = new Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setCreateTime(new Date());
            log.setOperator(SecurityUtils.getSysUser().getUsername());
//            log.setUsername(party.getUserName());
//            log.setOperator(operator_name);
//            log.setUserId(partyId);
            log.setLog("修改系统参数,模块:"+notes+"["+code+"]"+",修改之前:"+oldValue+",修改之后:"+sysparaDTO.getValue());
            logService.save(log);

            return Result.ok("修改成功");
        } else {
            throw new YamiShopBindException("参数不存在");
        }
    }

    /**
     * 根据Id获取配置参数数据
     */
    @ApiOperation(value = "根据Id获取配置参数数据")
    @GetMapping("queryById")
    public Result<SysparaDTO> queryById(String id) {
        return Result.ok(sysparaWrapper.toDTO(sysparaService.getById(id)));
    }

    /**
     * 保存配置参数
     */
    @ApiOperation(value = "保存配置参数")
    @PostMapping("save")
    public Result<String> save(@Valid @RequestBody SysparaDTO sysparaDTO) {
        //新增或编辑表单保存
        sysparaService.saveOrUpdate(sysparaWrapper.toEntity(sysparaDTO));
        return Result.ok("保存配置参数成功");
    }

    public class JobDelayThread implements Runnable {
        public void run() {
           BackupUtil.backup(sysLogService, sysparaService);
            log.info("local 备份");
        }
    }



    /**
     * 备份数据
     */
    @PostMapping("backupDB")
    public Result backup(HttpServletRequest request) {
        if (!"root".equals(SecurityUtils.getSysUser().getUsername())) {
            throw new YamiShopBindException("权限不足");
        }
        JobDelayThread thread = new JobDelayThread();
        Thread t = new Thread(thread);
        t.start();
        return Result.succeed();
    }

    /**
     * 删除配置参数
     */
    @ApiOperation(value = "删除配置参数")
    @DeleteMapping("delete")
    public Result<String> delete(String ids) {
        String idArray[] = ids.split(",");
        sysparaService.removeByIds(Lists.newArrayList(idArray));
        return Result.ok("删除配置参数成功");
    }

    public static boolean isValidDate(String str) {
        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        try {
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }
}
