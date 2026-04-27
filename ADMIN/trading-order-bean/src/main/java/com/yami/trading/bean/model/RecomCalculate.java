package com.yami.trading.bean.model;

import java.util.*;

public class RecomCalculate {

    private Map<String, Integer> recomCount = new HashMap<>();
    private Map<String, List<String>> recomMap = new HashMap<>();
    private List<UserRecom> userRecomList;

    public Map<String, Integer> getRecomCount() {
        return recomCount;
    }

    public List<UserRecom> getUserRecomList() {
        return userRecomList;
    }


    public Map<String, List<String>> getRecomMap() {
        return recomMap;
    }

    public RecomCalculate(List<UserRecom> userRecomList) {
        this.userRecomList = userRecomList;
        // 构建推荐映射
        for (UserRecom ur : userRecomList) {
            recomMap.computeIfAbsent(ur.getUserId(), k -> new ArrayList<>()).add(ur.getRecomUserId());
        }
        // 计算推荐人数
        for (String userId : recomMap.keySet()) {
            countRecom(userId, 0, new HashSet<>());
        }
    }

    public static void main(String[] args) {
        List<UserRecom> userRecomList = new ArrayList<>();
//        userRecomList.add(new UserRecom("user2", "user1"));
//        userRecomList.add(new UserRecom("user3", "user2"));
//        userRecomList.add(new UserRecom("user4", "user2"));
//        userRecomList.add(new UserRecom("user5", "user4"));
//        userRecomList.add(new UserRecom("user1", "user5"));

        RecomCalculate calculate = new RecomCalculate(userRecomList);
        Map<String, Integer> recomCount = calculate.getRecomCount();
        for (Map.Entry<String, Integer> entry : recomCount.entrySet()) {
            System.out.println("UserId: " + entry.getKey() + ", 推荐了 " + entry.getValue() + " 人");
        }


        Map<String, List<String>> recomMap1 = calculate.getRecomMap();
        for (Map.Entry<String, List<String>> entry : recomMap1.entrySet()) {
            System.out.println("UserId: " + entry.getKey() + ", 推荐了 " + entry.getValue());
        }
    }

    public  int countRecom(String userId, int depth, Set<String> visited) {
        if (depth >= 4) {
            return 0;
        }

        if (visited.contains(userId)) {
            return 0;
        }

        visited.add(userId);

        // 如果已经计算过该用户的推荐数，直接返回
        if (recomCount.containsKey(userId)) {
            return recomCount.get(userId);
        }

        int count = 0;
        List<String> recomUserIds = recomMap.getOrDefault(userId, new ArrayList<>());
        for (String recomUserId : recomUserIds) {
            count++;
            count += countRecom(recomUserId, depth + 1, new HashSet<>(visited));
        }

        recomCount.put(userId, count);

        visited.remove(userId);
        return count;
    }
}