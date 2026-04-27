package com.yami.trading.admin.controller.c2c;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.controller.c2c.model.PaymentMethodAddModel;
import com.yami.trading.admin.controller.c2c.model.PaymentMethodDeleteModel;
import com.yami.trading.admin.controller.c2c.model.PaymentMethodUpdateModel;
import com.yami.trading.admin.model.IdModel;
import com.yami.trading.admin.model.c2c.C2cPaymentMethodListModel;
import com.yami.trading.bean.c2c.dto.C2cPaymentMethodDto;
import com.yami.trading.bean.model.C2cPaymentMethod;
import com.yami.trading.bean.model.C2cPaymentMethodConfig;
import com.yami.trading.bean.model.Log;
import com.yami.trading.bean.model.User;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.util.IPHelper;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.AwsS3OSSFileService;
import com.yami.trading.service.c2c.C2cAdvertService;
import com.yami.trading.service.c2c.C2cPaymentMethodConfigService;
import com.yami.trading.service.c2c.C2cPaymentMethodService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.system.LogService;
import com.yami.trading.service.user.UserService;
import com.yami.trading.sys.model.SysUser;
import com.yami.trading.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("paymentMethod")
@Api(tags = "C2C支付方式管理")
@Slf4j
public class C2cPaymentMethodController {
    @Autowired
    private C2cPaymentMethodService adminC2cPaymentMethodService;
    @Autowired
    private UserService userService;
    @Autowired
    private LogService logService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private C2cPaymentMethodConfigService c2cPaymentMethodConfigService;
    @Autowired
    private SysparaService sysparaService;
    @Autowired
    private C2cPaymentMethodService c2cPaymentMethodService;
    @Autowired
    private C2cAdvertService c2cAdvertService;
    @Autowired
    SysUserService sysUserService;

    @Autowired
    AwsS3OSSFileService awsS3OSSFileService;

    /**
     * 获取 支付方式 列表
     */
    @PostMapping("list")
    @ApiOperation("列表")
    public Result<Page<C2cPaymentMethodDto>> list(@RequestBody @Valid C2cPaymentMethodListModel model) {

        Map<String, String> pmtMap = this.c2cAdvertService.getC2cSyspara("c2c_payment_method_type");
        // TODO: 2023/4/17  c2c 权限
//            String secUuid = "";
//            String userNameLogin = this.getUsername_login();
//            if (null != userNameLogin) {
//                SecUser sec = this.secUserService.findUserByLoginName(userNameLogin);
//                Set<Role> roles = sec.getRoles();
//                Iterator<Role> it = roles.iterator();
//                while (it.hasNext()) {
//                    Role role = (Role) it.next();
//                    if (role.getRoleName().equals("C2C")) {
//                        secUuid = sec.getId().toString();
//                        break;
//                    }
//                }
//            }
        Page<C2cPaymentMethodDto> page = new Page<>(model.getCurrent(), model.getSize());
        adminC2cPaymentMethodService.listPage(page, "", model.getUserName(), model.getMethodType(), model.getMethodName(),model.getType());
        List<C2cPaymentMethodDto> list = page.getRecords();
        for (C2cPaymentMethodDto dto : list) {
            dto.setMethodTypeName(pmtMap.containsKey(dto.getMethodType() + "") ? pmtMap.get(dto.getMethodType() + "") : dto.getMethodType() + "");
            dto.setQrcodeImg(awsS3OSSFileService.getUrl(dto.getQrcode()));
            dto.setMethodImgUrl(awsS3OSSFileService.getUrl(dto.getMethodImg()));
        }
        return Result.ok(page);
    }

    @GetMapping("getPaymentMethodConfig")
    @ApiOperation("获取支付方式模板和支付类型")
    public Result getPaymentMethodConfig(@RequestParam String type) {
        Map<String, Object> map = new HashMap<>();
        if ("1".equals(type)) {
            Map<String, C2cPaymentMethodConfig> payMap = this.c2cPaymentMethodConfigService.getBankCardPMethodConfigMap();
            Map<String, String> payNameMap = new HashMap<>();
            for (String onePayId : payMap.keySet()) {
                C2cPaymentMethodConfig payEntity = payMap.get(onePayId);
                payNameMap.put(onePayId, payEntity.getMethodName());
            }
            map.put("methodConfigMap", payNameMap);
        }
        else if ("2".equals(type)) {
            Map<String, C2cPaymentMethodConfig> payMap = this.c2cPaymentMethodConfigService.getC2cPMethodConfigMap();
            Map<String, String> payNameMap = new HashMap<>();
            for (String onePayId : payMap.keySet()) {
                C2cPaymentMethodConfig payEntity = payMap.get(onePayId);
                payNameMap.put(onePayId, payEntity.getMethodName());
            }

            map.put("methodConfigMap", payNameMap);
        }

        map.put("methodTypeMap", this.c2cAdvertService.getC2cSyspara("c2c_payment_method_type"));
        return Result.ok(map);
    }

    @PostMapping("add")
    @ApiOperation("新增支付方式")
    public Result add(@RequestBody @Valid PaymentMethodAddModel model) {
        String error = verifAdd(model.getUserCode(), model.getMethodConfigId(),
                model.getRealName(), model.getParamValue1(), model.getLoginSafeword());
        if (!StringUtils.isNullOrEmpty(error)) {
            throw new YamiShopBindException(error);
        }
        sysUserService.checkSafeWord(model.getLoginSafeword());
        User party = userService.findUserByUserCode(model.getUserCode());
        if (null == party) {
            throw new YamiShopBindException("用户不存在");
        }
        C2cPaymentMethodConfig methodConfig = this.c2cPaymentMethodConfigService.getById(model.getMethodConfigId());
        if (null == methodConfig) {
            throw new YamiShopBindException("支付方式模板不存在");
        }
        // C2C用户和承兑商添加支付方式最大数量
        Map<String, C2cPaymentMethod> methodMap = this.c2cPaymentMethodService.getByPartyId(party.getUserId().toString());
        Object obj = this.sysparaService.find("c2c_payment_method_count_max");
        if (null != obj) {
            if (methodMap.size() >= Integer.valueOf(this.sysparaService.find("c2c_payment_method_count_max").getSvalue()).intValue()) {
                throw new YamiShopBindException("支付方式数量已达上限");
            }
        }
        C2cPaymentMethod method = new C2cPaymentMethod();
        method.setType(model.getType());
        method.setPartyId(party.getUserId().toString());
        method.setMethodConfigId(methodConfig.getUuid().toString());
        method.setMethodType(methodConfig.getMethodType());
        method.setMethodName(methodConfig.getMethodName());
        method.setMethodImg(methodConfig.getMethodImg());
        method.setRealName(model.getRealName());
        method.setParamName1(methodConfig.getParamName1());
        method.setParamValue1(model.getParamValue1());
        method.setParamName2(methodConfig.getParamName2());
        method.setParamValue2(model.getParamValue2());
        method.setParamName3(methodConfig.getParamName3());
        method.setParamValue3(model.getParamValue3());
        method.setParamName4(methodConfig.getParamName4());
        method.setParamValue4(model.getParamValue4());
        method.setParamName5(methodConfig.getParamName5());
        method.setParamValue5(model.getParamValue5());
        method.setParamName6(methodConfig.getParamName6());
        method.setParamValue6(model.getParamValue6());
        method.setParamName7(methodConfig.getParamName7());
        method.setParamValue7(model.getParamValue7());
        method.setParamName8(methodConfig.getParamName8());
        method.setParamValue8(model.getParamValue8());
        method.setParamName9(methodConfig.getParamName9());
        method.setParamValue9(model.getParamValue9());
        method.setParamName10(methodConfig.getParamName10());
        method.setParamValue10(model.getParamValue10());
        method.setParamName11(methodConfig.getParamName11());
        method.setParamValue11(model.getParamValue11());
        method.setParamName12(methodConfig.getParamName12());
        method.setParamValue12(model.getParamValue12());
        method.setParamName13(methodConfig.getParamName13());
        method.setParamValue13(model.getParamValue13());
        method.setParamName14(methodConfig.getParamName14());
        method.setParamValue14(model.getParamValue14());
        method.setParamName15(methodConfig.getParamName15());
        method.setParamValue15(model.getParamValue15());
        method.setQrcode(model.getQrcode());
        method.setRemark(model.getRemark());
        adminC2cPaymentMethodService.save(method);
        String log = MessageFormat.format("ip:" + IPHelper.getIpAddr()
                        + ",新增支付方式,id:{0},用户PARTY_ID:{1},支付方式模板id:{2},支付方式类型:{3},支付方式名称:{4},支付方式图片:{5},真实姓名:{6},"
                        + "参数名1:{7},参数值1:{8},参数名2:{9},参数值2:{10},参数名3:{11},参数值3:{12},参数名4:{13},参数值4:{14},参数名5:{15},参数值5:{16},"
                        + "参数名6:{17},参数值6:{18},参数名7:{19},参数值7:{20},参数名8:{21},参数值8:{22},参数名9:{23},参数值9:{24},参数名10:{25},参数值10:{26},"
                        + "参数名11:{27},参数值11:{28},参数名12:{29},参数值12:{30},参数名13:{31},参数值13:{32},参数名14:{33},参数值14:{34},参数名15:{35},参数值15:{36},"
                        + "支付二维码:{37},备注:{38},创建时间:{39},更新时间:{40}",
                method.getUuid(), method.getPartyId(), method.getMethodConfigId(), method.getMethodType(), method.getMethodName(), method.getMethodImg(), method.getRealName(),
                method.getParamName1(), method.getParamValue1(), method.getParamName2(), method.getParamValue2(), method.getParamName3(), method.getParamValue3(), method.getParamName4(), method.getParamValue4(), method.getParamName5(), method.getParamValue5(),
                method.getParamName6(), method.getParamValue6(), method.getParamName7(), method.getParamValue7(), method.getParamName8(), method.getParamValue8(), method.getParamName9(), method.getParamValue9(), method.getParamName10(), method.getParamValue10(),
                method.getParamName11(), method.getParamValue11(), method.getParamName12(), method.getParamValue12(), method.getParamName13(), method.getParamValue13(), method.getParamName14(), method.getParamValue14(), method.getParamName15(), method.getParamValue15(),
                method.getQrcode(), method.getRemark(), method.getCreateTime(), method.getUpdateTime());
        SysUser sysUser = sysUserService.getById(SecurityUtils.getSysUser().getUserId());
        Log dbLog = new Log();
        dbLog.setCategory(Constants.LOG_CATEGORY_C2C);
        dbLog.setOperator(sysUser.getUsername() + "");
        dbLog.setUsername(sysUser.getUsername() + "");
        dbLog.setUserId(sysUser.getUserId() + "");
        dbLog.setLog(log);
        dbLog.setCreateTime(new Date());
        logService.save(dbLog);
        return Result.ok(null);
    }

    //
//    /**
//     * 修改 支付方式 页面
//     */
    @ApiOperation("获取详情")
    @PostMapping("get")
    public Result<C2cPaymentMethodDto> get(@RequestBody @Valid IdModel model) {

        C2cPaymentMethod method = this.adminC2cPaymentMethodService.get(model.getId());
        if (null == method) {
            throw new YamiShopBindException("支付方式不存在");
        }
        User party = userService.getById(method.getPartyId());
        if (null == party) {
            throw new YamiShopBindException("用户不存在");
        }
        C2cPaymentMethodDto dto = new C2cPaymentMethodDto();
        BeanUtils.copyProperties(method, dto);
        dto.setUserCode(party.getUserCode());
        Map<String, String> pmtMap = this.c2cAdvertService.getC2cSyspara("c2c_payment_method_type");
        dto.setMethodTypeName(pmtMap.get(dto.getMethodType()+""));
        dto.setUserName(party.getUserName());
        dto.setQrcodeImg(awsS3OSSFileService.getUrl(dto.getQrcode()));
        dto.setMethodImgUrl(awsS3OSSFileService.getUrl(dto.getMethodImg()));
        return Result.ok(dto);
    }

    @ApiOperation("修改 支付方式")
    @PostMapping("update")
    public Result update(@RequestBody @Valid PaymentMethodUpdateModel model) {

        String error = verifUpdate(model.getRealName(), model.getParamValue1(), model.getLoginSafeword());
        if (!StringUtils.isNullOrEmpty(error)) {
            throw new YamiShopBindException(error);
        }
        sysUserService.checkSafeWord(model.getLoginSafeword());
        C2cPaymentMethod method = adminC2cPaymentMethodService.get(model.getId());
        if (null == method) {
            throw new YamiShopBindException("支付方式不存在");
        }
        String log = MessageFormat.format("ip:" + IPHelper.getIpAddr()
                        + ",修改支付方式,id:{0},原用户PARTY_ID:{1},原支付方式模板:{2},原支付方式类型:{3},原支付方式名称:{4},原支付方式图片:{5},原真实姓名:{6},"
                        + "原参数名1:{7},原参数值1:{8},原参数名2:{9},原参数值2:{10},原参数名3:{11},原参数值3:{12},原参数名4:{13},原参数值4:{14},原参数名5:{15},原参数值5:{16},"
                        + "原参数名6:{17},原参数值6:{18},原参数名7:{19},原参数值7:{20},原参数名8:{21},原参数值8:{22},原参数名9:{23},原参数值9:{24},原参数名10:{25},原参数值10:{26},"
                        + "原参数名11:{27},原参数值11:{28},原参数名12:{29},原参数值12:{30},原参数名13:{31},原参数值13:{32},原参数名14:{33},原参数值14:{34},原参数名15:{35},原参数值15:{36},"
                        + "原支付二维码:{37},原备注:{38},原创建时间:{39},原更新时间:{40}",
                method.getUuid(), method.getPartyId(), method.getMethodConfigId(), method.getMethodType(), method.getMethodName(), method.getMethodImg(), method.getRealName(),
                method.getParamName1(), method.getParamValue1(), method.getParamName2(), method.getParamValue2(), method.getParamName3(), method.getParamValue3(), method.getParamName4(), method.getParamValue4(), method.getParamName5(), method.getParamValue5(),
                method.getParamName6(), method.getParamValue6(), method.getParamName7(), method.getParamValue7(), method.getParamName8(), method.getParamValue8(), method.getParamName9(), method.getParamValue9(), method.getParamName10(), method.getParamValue10(),
                method.getParamName11(), method.getParamValue11(), method.getParamName12(), method.getParamValue12(), method.getParamName13(), method.getParamValue13(), method.getParamName14(), method.getParamValue14(), method.getParamName15(), method.getParamValue15(),
                method.getQrcode(), method.getRemark(), method.getCreateTime(), method.getUpdateTime());
        method.setRealName(model.getRealName());
        method.setParamValue1(model.getParamValue1());
        method.setParamValue2(model.getParamValue2());
        method.setParamValue3(model.getParamValue3());
        method.setParamValue4(model.getParamValue4());
        method.setParamValue5(model.getParamValue5());
        method.setParamValue6(model.getParamValue6());
        method.setParamValue7(model.getParamValue7());
        method.setParamValue8(model.getParamValue8());
        method.setParamValue9(model.getParamValue9());
        method.setParamValue10(model.getParamValue10());
        method.setParamValue10(model.getParamValue11());
        method.setParamValue10(model.getParamValue12());
        method.setParamValue10(model.getParamValue13());
        method.setParamValue10(model.getParamValue14());
        method.setParamValue10(model.getParamValue15());
        method.setQrcode(model.getQrcode());
        method.setRemark(model.getRemark());
        method.setUpdateTime(new Date());
        this.adminC2cPaymentMethodService.updateById(method);
        log += MessageFormat.format(",id:{0},新用户PARTY_ID:{1},新支付方式模板:{2},新支付方式类型:{3},新支付方式名称:{4},新支付方式图片:{5},新真实姓名:{6},"
                        + "新参数名1:{7},新参数值1:{8},新参数名2:{9},新参数值2:{10},新参数名3:{11},新参数值3:{12},新参数名4:{13},新参数值4:{14},新参数名5:{15},新参数值5:{16},"
                        + "新参数名6:{17},新参数值6:{18},新参数名7:{19},新参数值7:{20},新参数名8:{21},新参数值8:{22},新参数名9:{23},新参数值9:{24},新参数名10:{25},新参数值10:{26},"
                        + "新参数名11:{27},新参数值11:{28},新参数名12:{29},新参数值12:{30},新参数名13:{31},新参数值13:{32},新参数名14:{33},新参数值14:{34},新参数名15:{35},新参数值15:{36},"
                        + "新支付二维码:{37},新备注:{38},新创建时间:{39},新更新时间:{40}",
                method.getUuid(), method.getPartyId(), method.getMethodConfigId(), method.getMethodType(), method.getMethodName(), method.getMethodImg(), method.getRealName(),
                method.getParamName1(), method.getParamValue1(), method.getParamName2(), method.getParamValue2(), method.getParamName3(), method.getParamValue3(), method.getParamName4(), method.getParamValue4(), method.getParamName5(), method.getParamValue5(),
                method.getParamName6(), method.getParamValue6(), method.getParamName7(), method.getParamValue7(), method.getParamName8(), method.getParamValue8(), method.getParamName9(), method.getParamValue9(), method.getParamName10(), method.getParamValue10(),
                method.getParamName11(), method.getParamValue11(), method.getParamName12(), method.getParamValue12(), method.getParamName13(), method.getParamValue13(), method.getParamName14(), method.getParamValue14(), method.getParamName15(), method.getParamValue15(),
                method.getQrcode(), method.getRemark(), method.getCreateTime(), method.getUpdateTime());
        SysUser sysUser = sysUserService.getSysUserById(SecurityUtils.getSysUser().getUserId());
        Log dbLog = new Log();
        dbLog.setCategory(Constants.LOG_CATEGORY_C2C);
        dbLog.setOperator(sysUser.getUsername() + "");
        dbLog.setUsername(sysUser.getUsername() + "");
        dbLog.setUserId(sysUser.getUserId() + "");
        dbLog.setLog(log);
        dbLog.setCreateTime(new Date());
        logService.save(dbLog);
        return Result.ok(null);
    }

    /**
     * 删除 支付方式
     */

    @ApiOperation("删除 支付方式")
    @PostMapping("delete")
    public Result delete(@RequestBody @Valid PaymentMethodDeleteModel model) {

        sysUserService.checkSafeWord(model.getLoginSafeword());
        C2cPaymentMethod method = this.adminC2cPaymentMethodService.get(model.getId());
        if (null == method) {
            throw new YamiShopBindException("支付方式不存在");
        }
        String log = MessageFormat.format("ip:" + IPHelper.getIpAddr()
                        + ",删除支付方式,id:{0},用户PARTY_ID:{1},支付方式模板:{2},支付方式类型:{3},支付方式名称:{4},支付方式图片:{5},真实姓名:{6},"
                        + "参数名1:{7},参数值1:{8},参数名2:{9},参数值2:{10},参数名3:{11},参数值3:{12},参数名4:{13},参数值4:{14},参数名5:{15},参数值5:{16},"
                        + "参数名6:{17},参数值6:{18},参数名7:{19},参数值7:{20},参数名8:{21},参数值8:{22},参数名9:{23},参数值9:{24},参数名10:{25},参数值10:{26},"
                        + "参数名6:{27},参数值6:{28},参数名7:{29},参数值7:{30},参数名8:{31},参数值8:{32},参数名9:{33},参数值9:{34},参数名10:{35},参数值10:{36},"
                        + "支付二维码:{37},备注:{38},创建时间:{39},更新时间:{40}",
                method.getUuid(), method.getPartyId(), method.getMethodConfigId(), method.getMethodType(), method.getMethodName(), method.getMethodImg(), method.getRealName(),
                method.getParamName1(), method.getParamValue1(), method.getParamName2(), method.getParamValue2(), method.getParamName3(), method.getParamValue3(), method.getParamName4(), method.getParamValue4(), method.getParamName5(), method.getParamValue5(),
                method.getParamName6(), method.getParamValue6(), method.getParamName7(), method.getParamValue7(), method.getParamName8(), method.getParamValue8(), method.getParamName9(), method.getParamValue9(), method.getParamName10(), method.getParamValue10(),
                method.getParamName11(), method.getParamValue11(), method.getParamName12(), method.getParamValue12(), method.getParamName13(), method.getParamValue13(), method.getParamName14(), method.getParamValue14(), method.getParamName15(), method.getParamValue15(),
                method.getQrcode(), method.getRemark(), method.getCreateTime(), method.getUpdateTime());
        adminC2cPaymentMethodService.removeById(model.getId());
        SysUser sysUser = sysUserService.getSysUserById(SecurityUtils.getSysUser().getUserId());
        Log dbLog = new Log();
        dbLog.setCategory(Constants.LOG_CATEGORY_C2C);
        dbLog.setOperator(sysUser.getUsername() + "");
        dbLog.setUsername(sysUser.getUsername() + "");
        dbLog.setUserId(sysUser.getUserId() + "");
        dbLog.setLog(log);
        dbLog.setCreateTime(new Date());
        logService.save(dbLog);
        return Result.ok(null);
    }




    public String verifAdd(String user_code, String method_config_id, String real_name, String param_value1, String login_safeword) {

        if (StringUtils.isNullOrEmpty(user_code)) {
            return "用户UID必填";
        }
        if (StringUtils.isNullOrEmpty(method_config_id)) {
            return "支付方式模板不正确";
        }
        if (StringUtils.isNullOrEmpty(real_name)) {
            return "真实姓名必填";
        }
        if (StringUtils.isNullOrEmpty(param_value1)) {
            return "参数值1必填";
        }
        if (StringUtils.isNullOrEmpty(login_safeword)) {
            return "资金密码错误";
        }
        return "";
    }

    public String verifUpdate(String real_name, String param_value1, String login_safeword) {

        if (StringUtils.isNullOrEmpty(real_name)) {
            return "真实姓名必填";
        }
        if (StringUtils.isNullOrEmpty(param_value1)) {
            return "参数值1必填";
        }
        if (StringUtils.isNullOrEmpty(login_safeword)) {
            return "资金密码错误";
        }
        return "";
    }
}


