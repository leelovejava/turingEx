package com.yami.trading.admin.controller.contract;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.yami.trading.admin.controller.service.SysUserOperService;
import com.yami.trading.admin.facade.PermissionFacade;
import com.yami.trading.bean.contract.domain.ContractApplyOrder;
import com.yami.trading.bean.contract.domain.ContractOrderProfit;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.common.query.QueryWrapperGenerator;

import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.data.DataService;
import com.yami.trading.sys.model.SysUser;
import com.yami.trading.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.bean.contract.domain.ContractOrder;

import com.yami.trading.bean.contract.dto.ContractOrderDTO;
import com.yami.trading.bean.contract.mapstruct.ContractOrderWrapper;
import com.yami.trading.service.contract.ContractOrderService;
import com.yami.trading.bean.contract.query.ContractOrderQuery;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;


/**
 * 非按金额订单Controller
 *
 * @author lucas
 * @version 2023-03-29
 */

@Api(tags = "永续持仓单(state传 submitted),历史持仓单 ")
@RestController
@CrossOrigin
@RequestMapping(value = "normal/adminContractOrderAction!")
@Slf4j
public class ContractOrderController {

    @Autowired
    private ContractOrderService contractOrderService;

    @Autowired
    private ContractOrderWrapper contractOrderWrapper;
    @Autowired(required = false)
    @Qualifier("dataService")
    private DataService dataService;
    @Autowired(required = false)
    private PermissionFacade permissionFacade;
    @Autowired
    SysUserService sysUserService;

    @Autowired
    private SysUserOperService sysUserOperService;

    /**
     * 非按金额订单列表数据
     */
    @ApiOperation(value = "查询非按金额订单列表数据")
    @GetMapping("list.action")
    public Result<IPage<ContractOrderDTO>> list(ContractOrderQuery contractOrderQuery, Page<ContractOrder> page) throws Exception {
        contractOrderQuery.setChildren(permissionFacade.getOwnerUserIds());
        IPage<ContractOrderDTO> result ;
        if("submitted".equals(contractOrderQuery.getState())){
             result = contractOrderService.listRecordCur(page, contractOrderQuery);
        }else{
            result = contractOrderService.listRecordHistory(page, contractOrderQuery);
        }



        result.getRecords().forEach(d -> {
            ContractOrderProfit cacheProfit = contractOrderService.getCacheProfit(d.getUuid());
            String symbol = d.getSymbol();
                    List<Realtime> realtimes = dataService.realtime(symbol);
                    if (CollectionUtil.isEmpty(realtimes)) {
                        d.setMark_price(BigDecimal.ZERO);
                        d.setClose(BigDecimal.ZERO);
                    } else {
                        d.setMark_price(BigDecimal.valueOf(realtimes.get(0).getClose()));
                        d.setClose(BigDecimal.valueOf(realtimes.get(0).getClose()));
                    }
                    
                    //持仓单不显示平仓价格
                    if("submitted".equals(contractOrderQuery.getState())){
                        d.setCloseAvgPrice(null);
                        if(cacheProfit != null){
                            d.setProfit(cacheProfit.getProfit());
                        }else{
                            d.setProfit(BigDecimal.ZERO);
                        }
                    }

                    double deposit = 1;
                    if(d.getDepositOpen().doubleValue() != 0) {
                        deposit = d.getDepositOpen().doubleValue();
                    }
                    //涨幅比
                    double changeRatio = Arith.div(d.getProfit().doubleValue(),deposit);
                    DecimalFormat df = new DecimalFormat("#.##");
                    d.setChangeRatio(Double.valueOf(df.format(Arith.mul(changeRatio, 100))));
                }

        );
        return Result.succeed(result);
    }


    /**
     * 撤单
     * <p>
     * order_no 订单号
     */
    @GetMapping("close.action")
    @ApiOperation(value = "平仓或撤单")
    public Result<String> cancel(@RequestParam @NotBlank String orderNo) throws IOException {
        try {
            ContractOrder order = this.contractOrderService.findByOrderNo(orderNo);
            if (order != null) {
                CloseDelayThread lockDelayThread = new CloseDelayThread(order.getPartyId().toString(), orderNo, this.contractOrderService);
                Thread t = new Thread(lockDelayThread);
                t.start();
            }
        } catch (Exception e) {
            log.error("执行撤单异常", e);
            throw new YamiShopBindException("执行撤单异常");
        }
        return Result.succeed("success");
    }

    /**
     * 新线程处理，直接拿到订单锁处理完成后退出
     */
    public class CloseDelayThread implements Runnable {
        private String partyId;
        private String order_no;
        private ContractOrderService contractOrderService;

        public void run() {

            try {

                while (true) {

                    if (this.contractOrderService.lock(order_no)) {
                        this.contractOrderService.saveClose(partyId, order_no);
                        SysUser secUser = sysUserService.getByUserName(SecurityUtils.getSysUser().getUsername());
                        sysUserOperService.saveLog(secUser, secUser.getUsername(), StrUtil.format("将永续持仓单 {} 进行平仓", order_no));
                        // 处理完退出
                        break;
                    }
                    ThreadUtils.sleep(500);
                }

            } catch (Throwable t) {
                log.error("error:", t);
            } finally {
                this.contractOrderService.unlock(order_no);
            }
        }

        public CloseDelayThread(String partyId, String order_no, ContractOrderService contractOrderService) {
            this.partyId = partyId;
            this.order_no = order_no;
            this.contractOrderService = contractOrderService;
        }
    }

}
