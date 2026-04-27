package com.yami.trading.admin.controller.channelBlockchain;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.model.MoneylogListModel;
import com.yami.trading.admin.model.channelBlockchain.ChannelBlockchainAddModel;
import com.yami.trading.admin.model.channelBlockchain.ChannelBlockchainDeleteModel;
import com.yami.trading.admin.model.channelBlockchain.ChannelBlockchainListModel;
import com.yami.trading.admin.model.channelBlockchain.ChannelBlockchainUpdateModel;
import com.yami.trading.bean.model.ChannelBlockchain;
import com.yami.trading.bean.model.Log;
import com.yami.trading.bean.user.dto.MoneyLogDto;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.common.util.IPHelper;
import com.yami.trading.common.util.IpUtil;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.ChannelBlockchainService;
import com.yami.trading.service.system.LogService;
import com.yami.trading.sys.model.SysUser;
import com.yami.trading.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("channelBlockchain")
@Api(tags = "区块链充值地址维护")
public class ChannelBlockchainController {
    @Autowired
    ChannelBlockchainService channelBlockchainService;
    @Autowired
    SysUserService sysUserService;
    @Autowired
    LogService logService;

    @ApiOperation(value = "列表")
    @PostMapping("list")
    public Result<Page<ChannelBlockchain>> list(@RequestBody @Valid ChannelBlockchainListModel model) {
        Page<ChannelBlockchain> page = new Page(model.getCurrent(), model.getSize());
        LambdaQueryWrapper<ChannelBlockchain> lambdaQueryWrapper = Wrappers.<ChannelBlockchain>query().lambda();
        if (!StringUtils.isNullOrEmpty(model.getBlockchainName())) {
            lambdaQueryWrapper.like(ChannelBlockchain::getBlockchainName, model.getBlockchainName());
        }
        if (!StringUtils.isNullOrEmpty(model.getCoin())) {
            lambdaQueryWrapper.like(ChannelBlockchain::getCoin, model.getCoin());
        }
        channelBlockchainService.page(page, lambdaQueryWrapper);
        return Result.ok(page);
    }

    /**
     * 新增 区块链充值地址
     * <p>
     * blockchain_name 链名称
     * coin 币种 BTC USDT ETH
     * address 充值地址
     * img 充值地址二维码
     */
    @PostMapping("add")
    @ApiOperation("新增 区块链充值地址")
    public Result add(@RequestBody @Valid ChannelBlockchainAddModel model) {
        String blockchain_name = model.getBlockchainName();
        String coin = model.getCoin();
        String address = model.getAddress();
        String img = "1";
        String error = this.verif(model.getBlockchainName(), model.getCoin(), address, img);
        if (!StringUtils.isNullOrEmpty(error)) {
            throw new BusinessException(error);
        }
        ChannelBlockchain channelBlockchain = new ChannelBlockchain();
        channelBlockchain.setBlockchain_name(blockchain_name);
        channelBlockchain.setCoin(coin);
        channelBlockchain.setAddress(address);
        channelBlockchain.setImg("");
        sysUserService.checkSuperGoogleAuthCode(model.getSuperGoogleAuthCode());
        sysUserService.checkSafeWord(model.getSafeword());
        SysUser sysUser = sysUserService.getById(SecurityUtils.getSysUser().getUserId());
        this.channelBlockchainService.save(channelBlockchain);
        String log = "新增地址，名称[" + channelBlockchain.getBlockchain_name() + "]，地址[" + channelBlockchain.getAddress() + "]" + "币种["
                + channelBlockchain.getCoin() + "]，图片[" + channelBlockchain.getImg() + "]，" + "ip[" + IPHelper.getIpAddr() + "]";
        saveLog(sysUser, sysUser.getUsername(), log);
//        sendCodeToSysUsers(sec, userName, "新增区块链地址地址", "用户[" + userName + "],新增区块链地址地址");
        return Result.succeed();
    }

    public void saveLog(SysUser secUser, String operator, String context) {
        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setOperator(operator);
        log.setUsername(secUser.getUsername());
        log.setUserId(secUser.getUserId().toString());
        log.setLog(context);
        log.setCreateTime(new Date());
        logService.save(log);
    }

    /**
     * 修改 区块链充值地址
     * <p>
     * blockchain_name 链名称
     * coin 币种 BTC USDT ETH
     * address 充值地址
     * img 充值地址二维码
     */
    @ApiOperation("修改 区块链充值地址")
    @PostMapping("update")
    public Result update(@RequestBody @Valid ChannelBlockchainUpdateModel model) {
        String id = model.getId();
        String blockchain_name = model.getBlockchainName();
        String coin = model.getCoin();
        String address = model.getAddress();
        ChannelBlockchain channelBlockchain = this.channelBlockchainService.getById(id);
        ChannelBlockchain old = new ChannelBlockchain();
        BeanUtils.copyProperties(channelBlockchain, old);
        String error = this.verif(blockchain_name, coin, address, null);
        if (!StringUtils.isNullOrEmpty(error)) {
            throw new BusinessException(error);
        }
        channelBlockchain.setBlockchain_name(blockchain_name);
        channelBlockchain.setCoin(coin);
//        if (!address.equals(channelBlockchain.getAddress())) {
//            channelBlockchain.setAddress(address);
//            img = this.qRProducerService.generate(address);
//            channelBlockchain.setImg(img);
//        }
        sysUserService.checkSuperGoogleAuthCode(model.getSuperGoogleAuthCode());
        sysUserService.checkSafeWord(model.getSafeword());
        channelBlockchainService.updateById(channelBlockchain);
        String log = "区块链充值地址修改。原名称[" + old.getBlockchain_name() + "]，原地址[" + old.getAddress() + "]" + "原币种["
                + old.getCoin() + "]，原图片[" + old.getImg() + "]。" + "修改后，新名称[" + channelBlockchain.getBlockchain_name() + "]，新地址["
                + channelBlockchain.getAddress() + "]" + "新币种[" + channelBlockchain.getCoin() + "]，" + "ip["
                + IPHelper.getIpAddr() + "]]";
        SysUser sysUser = sysUserService.getById(SecurityUtils.getSysUser().getUserId());
        saveLog(sysUser, sysUser.getUsername(), log);
//        sendCodeToSysUsers(sec, userName, "区块链充值地址修改", "用户[" + userName + "],区块链充值地址修改");
        return Result.succeed();
    }

    /**
     * 删除 区块链充值地址
     */
    @PostMapping("delete")
    @ApiOperation(" 删除 区块链充值地址")
    public Result toDelete(@RequestBody @Valid ChannelBlockchainDeleteModel request) {
      ChannelBlockchain entity=    channelBlockchainService.getById(request.getId());

        channelBlockchainService.removeById(entity.getUuid());

        String log = "删除地址，名称[" + entity.getBlockchain_name() + "]，地址[" + entity.getAddress() + "]" + "币种["
                + entity.getCoin() + "]，" + "ip[" + IPHelper.getIpAddr() + "]";
        SysUser sysUser = sysUserService.getById(SecurityUtils.getSysUser().getUserId());
        saveLog(sysUser, sysUser.getUsername(), log);
//        sendCodeToSysUsers(sec, userName, "区块链充值地址删除", "用户[" + userName + "],区块链充值地址删除");
        return Result.succeed();
    }

    private String verif(String blockchain_name, String coin, String address, String img) {
        if (StringUtils.isEmptyString(blockchain_name))
            return "请输入链名称";
        if (StringUtils.isEmptyString(coin))
            return "请输入币种";
        if (StringUtils.isEmptyString(address))
            return "请输入地址";
        return null;
    }
}
