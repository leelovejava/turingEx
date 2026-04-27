package com.yami.trading.admin.controller.channelBlockchain;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yami.trading.admin.model.IdModel;
import com.yami.trading.admin.model.channelBlockchain.PartyBlockchainAddModel;
import com.yami.trading.admin.model.channelBlockchain.PartyBlockchainListModel;
import com.yami.trading.admin.model.channelBlockchain.PartyBlockchainUpdateModel;
import com.yami.trading.bean.exchange.PartyBlockchain;
import com.yami.trading.bean.exchange.dto.PartyBlockchainDto;
import com.yami.trading.common.domain.Result;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.service.exchange.PartyBlockchainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("partyBlockchain")
@Api(tags = "区块链个人充值地址维护")
public class PartyBlockchainController {


    @Autowired
    PartyBlockchainService partyBlockchainService;

    @ApiOperation(value = "列表")
    @PostMapping("list")
    public Result<Page<PartyBlockchainDto>> list(@RequestBody @Valid PartyBlockchainListModel model) {
        Page page = new Page(model.getCurrent(), model.getSize());
        partyBlockchainService.pageList(page,model.getAddress(),
                model.getUserName(),
                model.getRoleName());
        return Result.ok(page);
    }

    @ApiOperation(value = "新增")
    @PostMapping("add")
    public Result add(@RequestBody @Valid PartyBlockchainAddModel model) {
        PartyBlockchain partyBlockchain=new PartyBlockchain();
        partyBlockchain.setAddress(model.getAddress());
        partyBlockchain.setChainName(model.getChainName());
        partyBlockchain.setCoinSymbol(model.getCoinSymbol());
        partyBlockchain.setUserName(model.getUserName());
        partyBlockchainService.save(partyBlockchain);
        return Result.succeed();
    }

    @ApiOperation(value = "更新")
    @PostMapping("update")
    public Result add(@RequestBody @Valid PartyBlockchainUpdateModel model) {
        PartyBlockchain partyBlockchain=  partyBlockchainService.getById(model.getId());
        if (partyBlockchain==null){
            throw  new BusinessException("记录不存在!");
        }
        partyBlockchain.setAddress(model.getAddress());
        partyBlockchain.setChainName(model.getChainName());
        partyBlockchain.setCoinSymbol(model.getCoinSymbol());
        partyBlockchain.setUserName(model.getUserName());
        partyBlockchainService.save(partyBlockchain);
        return Result.succeed();
    }

    @ApiOperation(value = "更新")
    @PostMapping("delete")
    public Result add(@RequestBody @Valid IdModel model) {
        partyBlockchainService.removeById(model.getId());
        return Result.succeed();
    }


}
