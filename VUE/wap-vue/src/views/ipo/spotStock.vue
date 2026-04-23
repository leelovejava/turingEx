<template>
    <div class="lotteryRecord">
        <div class="px-5">
            <div class="flex  py-10 border-b-color  border-t-color  justify-between ">
                <div class="text-center">
                    <div class="text_color6">{{t('市值')}}</div>
                    <div>{{ topData.marketValue }}</div>
                </div>
                <div class="text-center">
                    <div class="text_color6">{{t('库存损益')}}</div>
                    <div :class="topData.inventoryGainsLosses > 0 ? 'red' : 'green'">{{ topData.inventoryGainsLosses }}</div>
                </div>
                <div class="text-center">
                    <div class="text_color6">{{t('可用额度')}}</div>
                    <div>{{ topData.availableLimit }}</div>
                </div>
            </div>
            <van-list v-model:loading="loading" :finished="finished" :loading-text="$t('加载中') + '...'" :finished-text="$t('没有更多了')" @load="onLoad" ref="myRef">
            <div v-for="(item, index) in list" :key="index" class="list-div">
                <div class="flex  justify-between">
                    <div>
                      <span>{{ item.symbolName }}</span>
                    </div>
                    <div>{{ item.createTime }}</div>
                </div>
                <div class="flex justify-between">
                    <div class="text_color6">{{ item.symbolCode }}</div>
                </div>
                <div class="flex  justify-between">
                    <div class="text_color6">{{t('库存损益')}}</div>
                    <div :class="item.inventoryGainsLosses > 0 ? 'red' : 'green'">{{ item.inventoryGainsLosses }}</div>
                </div>
                <div class="flex  justify-between">
                    <div class="text_color6">{{t('损益百分比')}}</div>
                    <div :class="item.inventoryGainsLossesValue > 0 ? 'red' : 'green'">{{ item.inventoryGainsLossesValue }}%</div>
                </div>
                <div class="flex  justify-between">
                    <div class="text_color6">{{t('交易股数')}}</div>
                    <div>{{ item.subNumber }}</div>
                </div>
                <!-- <div class="flex  justify-between">
                    <div class="text_color6">{{t('信用金')}}</div>
                    <div>{{ item.subscribedAmount }}</div>
                </div> -->
                <div class="flex  justify-between">
                    <div class="text_color6">{{t('库存市值')}}</div>
                    <div>{{ item.marketValue }}</div>
                </div>
                <div class="flex  justify-between">
                    <div class="text_color6">{{t('买入')}}</div>
                    <div>{{ item.subPrice }}</div>
                </div>
                <div class="flex  justify-between">
                    <div class="text_color6">{{t('现价')}}</div>
                    <div>{{ item.closePrice }}</div>
                </div>
                <button class="but bg-red text-white" @click="sell(item.orderNo)">{{t('卖出')}}</button>
            </div>
        </van-list>
        </div>
    </div>
</template>

<script setup>
import {inject, onMounted, provide, ref} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from "vue-i18n";
import { sNewSharesOrderList, getNowTopData, spotStockSell } from '@/service/ipo.api'
import { showToast } from 'vant'
const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const list = ref([]);
const topData = ref({})
const loading = ref(false);
const finished = ref(false);
const index = ref(1)
const myRef = ref(null);
onMounted(() => {
  getTopData()
})

const stockType = inject('stockType')
provide('stockType', stockType)
const onLoad = () => {
    let params = {
      current: index.value,
      size: 10,
      type: 3,
      symbolType: stockType || 'US-stocks'
    }
    sNewSharesOrderList(params).then(res => {
      if(res.length) {
        list.value = list.value.concat(res)
      }
      index.value++
      loading.value = false;
      if (res.length < 10) {
        finished.value = true;
      }
    })
}
const sell = (orderNo) => {
  spotStockSell({orderNo: orderNo}).then((res) => {
    showToast(t('操作成功'));
    window.location.reload();
  })
}

const getTopData = () => {
   getNowTopData({type: 3, symbolType: stockType || 'US-stocks'}).then((res)=>{
    topData.value = res
   })
}
</script>
<style lang="scss" scoped>
.lotteryRecord {
    font-size: 14px;

    .search-icon {
        width: 23px;
        height: 23px;
    }
    .list-div {
        padding: 20px 20px;
        background: $input_background;
        border-radius: 10px;
        margin-top: 20px;
        .but{
            width: 100%;
            height: 42px;
            margin-top: 10px;
            color: #fff;
        }
        .bg-red{
            background: $red;
        }
    }
}
</style>