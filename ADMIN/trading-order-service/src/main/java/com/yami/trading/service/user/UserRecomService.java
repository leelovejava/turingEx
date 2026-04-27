package com.yami.trading.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.trading.bean.model.UserRecom;
import com.yami.trading.bean.user.dto.UserAllRecomDto;
import com.yami.trading.bean.user.dto.UserRecomDto;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface UserRecomService extends IService<UserRecom> {

    Page list(Page page,
               String useName,
              String recomUserName,List<String> userIds);

    public List<String> findChildren(String  userId);

    public List<UserRecom> getParents(String partyId);


    public UserRecom findByPartyId(String  userId);
    /**
     * 查找直推 partyId
     *
     */
    public List<String> findRecomsToPartyId(String partyId);

    /**
     * 查找直推
     *
     * @param partyId
     * @return
     */
    public List<UserRecom> findRecoms(String partyId);

    public Map<String, Integer> findAllRecommCount();

    public List<Integer> getRecoNumNetList(String partyId);

    Page listUserAll(Page<UserAllRecomDto> page, String userName, String recomUserName, List<String> ownerUserIds);

    void updateAllRecom(String userCode, String userId);

}
