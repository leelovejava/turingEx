/**
  * Copyright 2023 bejson.com 
  */
package com.yami.trading.huobi.task.domain;

import java.util.List;

/**
 * Auto-generated: 2023-10-02 22:7:45
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class TradeViewData {

    private int totalCount;
    private List<TradeViewDataItem> data;
    public void setTotalCount(int totalCount) {
         this.totalCount = totalCount;
     }
     public int getTotalCount() {
         return totalCount;
     }

    public void setData(List<TradeViewDataItem> data) {
         this.data = data;
     }
     public List<TradeViewDataItem> getData() {
         return data;
     }

}