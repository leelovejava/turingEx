<template>
  <div class="Full-warehouse">
    <div class="Full-warehouse-bottom-flex">
      <div class="Full-warehouse-bottom">{{ $t('全仓') }}</div>
    </div>
    <div class="segmentation"></div>
    <div class="Full-warehouse-list">
      <div class="Delivery-time">
        <div class="Delivery-time-title">{{ $t('交割时间') }}</div>
        <div class="Delivery-time-list">
          <div class="Delivery-time-item" v-for="(item,index) in DeliveryTime" :key="index" @click="DeliveryTime_index=index">
            <div class="Delivery-time-item-time" :style="index==DeliveryTime_index?'background: #7F81FD;color: #ffffff;':''">{{ item.time }}</div>
            <div class="Delivery-time-item-percentage" :style="index==DeliveryTime_index?'background: #6C52E6;color: #ffffff;':''">{{ item.percentage }}</div>
          </div>
        </div>
        <div class="Delivery-time-input">
          <van-cell-group>
            <van-field v-model="value" :placeholder="$t('请输入用户名')" style="background: #C8CAD2;">
              <template #extra >
                <span class="Delivery-time-input-text">USDT</span>
              </template>
            </van-field>
          </van-cell-group>
        </div>
        <div class="Delivery-time-open-more">
          <div class="Delivery-time-open-more-flex"></div>
          <div class="Delivery-time-open-more-text">{{ $t('多开') }}</div>
          <div class="Delivery-time-open-more-text2">{{ $t('看涨') }}</div>
        </div>
        <div class="Delivery-time-open-more Delivery-time-open-more-red">
          <div class="Delivery-time-open-more-flex"></div>
          <div class="Delivery-time-open-more-text">{{ $t('开空') }}</div>
          <div class="Delivery-time-open-more-text2">{{ $t('看跌') }}</div>
        </div>
        <div class="handling-fee">
          <span class="handling-fee-text1">{{ $t('手续费') }}</span>
          <span class="handling-fee-text2">1.11</span>
        </div>

      </div>
      <div class="price">
        <div class="price-title">
          <span class="price-text">{{ $t('价格') }}</span>
          <span class="quantity">{{ $t('数量') }}</span>
        </div>
        <div class="unit">
          <span class="unit-text">(USDT)</span>
          <span class="unit-text2">(USDT)</span>
        </div>
        <div class="price-content">
          <div class="price-item"><span class="price-item-text">22637</span><span class="price-item-text2">0.59</span></div>
          <div class="price-item"><span class="price-item-text">22637</span><span class="price-item-text2">0.59</span></div>
          <div class="price-item"><span class="price-item-text">22637</span><span class="price-item-text2">0.59</span></div>
          <div class="price-item"><span class="price-item-text">22637</span><span class="price-item-text2">0.59</span></div>
          <div class="price-item"><span class="price-item-text">22637</span><span class="price-item-text2">0.59</span></div>
          <div class="price-item"><span class="price-item-text">22637</span><span class="price-item-text2">0.59</span></div>
          <div class="price-item"><span class="price-item-text">22637</span><span class="price-item-text2">0.59</span></div>
          <div class="price-item"><span class="price-item-text">22637</span><span class="price-item-text2">0.59</span></div>
        </div>
        <div class="price-content-title">22637.22</div>
        <div class="price-content">
          <div class="price-item"><span class="price-item-text price-item-text-red">22637</span><span class="price-item-text2 ">0.59</span></div>
          <div class="price-item"><span class="price-item-text price-item-text-red">22637</span><span class="price-item-text2 ">0.59</span></div>
          <div class="price-item"><span class="price-item-text price-item-text-red">22637</span><span class="price-item-text2 ">0.59</span></div>
          <div class="price-item"><span class="price-item-text price-item-text-red">22637</span><span class="price-item-text2 ">0.59</span></div>
          <div class="price-item"><span class="price-item-text price-item-text-red">22637</span><span class="price-item-text2 ">0.59</span></div>
          <div class="price-item"><span class="price-item-text price-item-text-red">22637</span><span class="price-item-text2 ">0.59</span></div>
          <div class="price-item"><span class="price-item-text price-item-text-red">22637</span><span class="price-item-text2 ">0.59</span></div>
        </div>
      </div>
    </div>

  </div>
</template>
<script>
import {Field} from 'vant';
export default {
  data() {
    return {
      title:this.$t('交割'),
      symbol:'BTC',
      price:'22637.22',
      range:'-3.81%',
      DeliveryTime_index: 0,
      DeliveryTime:[
        {time:'30s',percentage:'0.6~0.6%'},
        {time:'120s',percentage:'0.6~0.6%'},
        {time:'30s',percentage:'0.6~0.6%'},
        {time:'120s',percentage:'0.6~0.6%'},
        {time:'30s',percentage:'0.6~0.6%'},
        {time:'120s',percentage:'0.6~0.6%'},
        {time:'30s',percentage:'0.6~0.6%'},
        {time:'120s',percentage:'0.6~0.6%'},
      ],

    }
  },
  components: {
    [Field.name]: Field,
  },
  mounted() {
  },
  methods: {
  }
}
</script>
<style lang="scss" scoped>
.Full-warehouse{
  width: 100%;
  background:  $mainbgWhiteColor;
  border-top-left-radius: 35px;
  border-top-right-radius: 35px;
  .Full-warehouse-bottom-flex{
    display: flex;
  }
  .Full-warehouse-bottom{
    margin-left: 30px;
    margin-top: 39px;
    margin-bottom: 37px;
    padding: 13px 60px;
    background: $light-grey;
    border-radius: 6px;
    font-style: normal;
    font-weight: 400;
    font-size: 30px;
    text-align: center;
    color: $black;
  }
  .segmentation{
    width: 100%;
    height: 1px;
    background: $border-grey;
  }
  .Full-warehouse-list{
    padding: 36px 30px 78px 30px;
    box-sizing: border-box;
    display: flex;
    .Delivery-time{
      width: 440px;
      display: flex;
      flex-direction: column;
      margin-right: 39px;
      .Delivery-time-title{
        font-style: normal;
        font-weight: 400;
        font-size: 24px;
        color: $dark-grey;
      }
      .Delivery-time-list{
        width: 440px;
        display: flex;
        flex-wrap: wrap;
        .Delivery-time-item{
          margin-top: 17px;
          width: 220px;
          height: 92px;
          background: #DEDEDF;
          border-radius: 6px 0 0 6px;
          display: flex;
          .Delivery-time-item-time{
            flex: 1;
            height: 92px;
            font-style: normal;
            font-weight: 400;
            font-size: 22px;
            line-height: 92px;
            text-align: center;
            color: #4C4A54;
          }
          .Delivery-time-item-percentage{
            padding-left: 15px;
            padding-right: 15px;
            box-sizing: border-box;
            height: 92px;
            font-style: normal;
            font-weight: 400;
            font-size: 22px;
            line-height: 92px;
            text-align: center;
            color: #4C4A54;
            background: $light-grey;
          }
        }
      }
      .Delivery-time-input{
        width: 100%;
        margin-top: 30px;
        background: $light-grey;
        border-radius: 6px;
      }
      .Delivery-time-input-text{
        color: $dark-grey;
      }
      .Delivery-time-open-more{
        margin-top: 30px;
        width: 100%;
        height: 90px;
        background: $green;
        border-radius: 6px;
        text-align: center;
        display: flex;
        .Delivery-time-open-more-flex{
          flex: 1;
        }
        .Delivery-time-open-more-text{
          font-style: normal;
          font-weight: 400;
          font-size: 35px;
          line-height: 90px;
          text-align: center;
          color: $text_color;
        }
        .Delivery-time-open-more-text2{
          flex: 1;
          text-align: right;
          margin-right: 23px;
          font-style: normal;
          font-weight: 400;
          font-size: 22px;
          line-height: 90px;
          color: $text_color;
        }
      }
      .Delivery-time-open-more-red{
        background: $red;
      }
      .handling-fee{
        width: 100%;
        margin-top: 45px;
        .handling-fee-text1{
          float: left;
          font-style: normal;
          font-weight: 400;
          font-size: 24px;
          color: $dark-grey;
        }
        .handling-fee-text2{
          float: right;
          font-style: normal;
          font-weight: 400;
          font-size: 24px;
          line-height: 28px;
          text-align: right;

          color: $dark-grey;
        }
      }
    }
    .price{
      flex: 1;
      display: flex;
      flex-direction: column;
      .price-title{
        width: 100%;
        .price-text{
          float: left;
          font-style: normal;
          font-weight: 400;
          font-size: 24px;
          color: $dark-grey;
        }
        .quantity{
          float: right;
          font-style: normal;
          font-weight: 400;
          font-size: 24px;
          color: $dark-grey;
        }
      }
      .unit{
        margin-top: 10px;
        width: 100%;
        .unit-text{
          float: left;
          font-style: normal;
          font-weight: 400;
          font-size: 24px;
          color: $dark-grey;
        }
        .unit-text2{
          float: right;
          font-style: normal;
          font-weight: 400;
          font-size: 24px;
          color: $dark-grey;
        }
      }
      .price-content{
        width: 100%;
        margin-top: 12px;
        .price-item{
          float: left;
          width: 100%;
          margin-bottom: 20px;
          .price-item-text{
            float: left;
            font-style: normal;
            font-weight: 400;
            font-size: 24px;
            color: $green;
          }
          .price-item-text2{
            float: right;
            font-style: normal;
            font-weight: 400;
            font-size: 24px;
            text-align: right;
            color: #4C4A54;
          }
        }
      }
      .price-item-text-red{
        color: $red !important;
      }
      .price-content-title{
        width: 100%;
        text-align: center;
        padding-bottom: 22px;
        font-style: normal;
        font-weight: 400;
        font-size: 28px;
        color: $red;
      }
    }
  }
}
</style>
