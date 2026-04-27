package com.yami.trading.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.yami.trading.bean.model.RecomCalculate;
import com.yami.trading.bean.model.User;
import com.yami.trading.bean.model.UserRecom;
import com.yami.trading.bean.user.dto.UserAllRecomDto;
import com.yami.trading.bean.user.dto.UserRecomDto;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.constants.PartyRedisKeys;
import com.yami.trading.common.constants.RedisKeys;
import com.yami.trading.common.exception.YamiShopBindException;
import com.yami.trading.dao.user.UserRecomMapper;
import com.yami.trading.service.user.UserRecomService;
import com.yami.trading.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserRecomServiceImpl extends ServiceImpl<UserRecomMapper, UserRecom> implements UserRecomService {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    @Lazy
    private UserService userService;
    @Override
    public Page list(Page page, String useName, String recomUserName,List<String> userIds) {
        return baseMapper.list(page,useName,recomUserName,userIds);
    }

    @Override
    public List<String> findChildren(String userId) {
        List list = new ArrayList();
        list = findChildren(userId, list);
        return list;
    }

    @Override
    public List<UserRecom> getParents(String  partyId) {
        List list = new LinkedList();
        if (partyId == null) {
            return list;
        }
        list = findParents(partyId, list);

        return list;
    }

    private List<UserRecom> findParents(String partyId, List<UserRecom> list) {
        UserRecom userRecom = findByPartyId(partyId);
        if (userRecom != null) {
            list.add(userRecom);
            findParents(userRecom.getUserId(), list);
        }
        return list;
    }

    @Override
    public List<String> findRecomsToPartyId(String partyId) {
        List<UserRecom> recom_list = findRecoms(partyId);
        return recom_list.stream().map(userRecom->userRecom.getUserId().toString()).collect(Collectors.toList());
    }

    @Override
    public UserRecom findByPartyId(String partyId) {
        if (partyId == null) {
            return null;
        }

        List<UserRecom>  userRecomList=    list(Wrappers.<UserRecom>query().lambda().eq(UserRecom::getRecomUserId,partyId));
        if (CollectionUtil.isEmpty(userRecomList)){
            return null;
        }
        return userRecomList.get(0);
    }


    public  List<UserRecom> findRecoms(String userId){
        return  list(Wrappers.<UserRecom>query().lambda().eq(UserRecom::getUserId,
                userId));
    }

    @Override
    public Map<String, Integer> findAllRecommCount() {
        List<UserRecom> list = list();
        RecomCalculate calculate = new RecomCalculate(list);
        return calculate.getRecomCount();
    }

    @Override
    public Page listUserAll(Page<UserAllRecomDto> page, String userName, String recomUserName, List<String> ownerUserIds) {
       return baseMapper.listUserAll(page,userName,recomUserName,ownerUserIds);
    }

    @Override
    @Transactional
    public void updateAllRecom(String userCode, String userId) {
        User recomUser=userService.findUserByUserCode(userCode);
        if (recomUser==null){
            throw new YamiShopBindException("推荐人UID错误");
        }
        UserRecom userRecom= findByPartyId(userId);
        if (userRecom==null){
            userRecom=new UserRecom();
            userRecom.setRecomUserId(userId);
            userRecom.setUserId(recomUser.getUserId());
            save(userRecom);
        }
        else {
            userRecom.setUserId(recomUser.getUserId());
            updateById(userRecom);
        }
        User user=userService.getById(userId);
        user.setUserRecom(recomUser.getUserId());
        userService.updateById(user);
    }

    /**
     * 获取用户推荐数网络 列表（第i层用户数）
     * @return
     */
    @Override
    public List<Integer> getRecoNumNetList(String partyId) {
        Map<Integer, Integer> recoNumNet = getRecoNumNet(partyId);
        List<Integer> keys = new ArrayList<Integer>(recoNumNet.keySet());
        Collections.sort(keys);
        List<Integer> list = new LinkedList<Integer>();
        for (int i = 0; i < keys.size(); i++) {
            list.add(recoNumNet.get(keys.get(i)));
        }
        return list;
    }
    /**
     * 获取用户推荐数网络
     * @return key:网络层级，value:用户数
     */
    public Map<Integer,Integer> getRecoNumNet(String partyId){
        List<String> all = findChildren(partyId);
        int allSize = all.size();
        int sum = 0;
        int level = 1;
        Map<Integer,Integer> result = new HashMap<Integer,Integer>();
        findRecomsNet(partyId,level,result,sum,allSize);

        return result;
    }
    public void findRecomsNet(String partyId,int level,Map<Integer,Integer> result,int sum,int allSize) {
        if(sum>=allSize) return;
        List<UserRecom> users = findRecoms(partyId);
        if(CollectionUtils.isEmpty(users)) {
            return;
        }
        int num = 0;
        for(UserRecom user:users) {
            findRecomsNet(user.getRecomUserId(),level+1,result,sum,allSize);
            User party = userService.getById(user.getRecomUserId());
            if (!Constants.SECURITY_ROLE_MEMBER.equals(party.getRoleName())) {//非正式用户不统计
                continue;
            }
            num++;
        }
        sum+=users.size();//总数
        if(num>0) {
            result.put(level, result.get(level)==null?num:result.get(level)+num);
        }
    }
    private List<String> findChildren(String userId, List<String> list) {
        List recomList = findRecoms(userId);
        for (int i = 0; i < recomList.size(); i++) {
            list.add(((UserRecom) recomList.get(i)).getRecomUserId());
            findChildren(((UserRecom) recomList.get(i)).getRecomUserId(),list);
        }
        return list;
    }
}
