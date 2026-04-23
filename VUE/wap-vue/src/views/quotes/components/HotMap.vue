<template>
  <section class="hot-map-container">
    <div class="row1">
      <div class="item">
        <p>{{ t("软件和IT服务") }}</p>
        <p>{{ details[0]?.value }}</p>
      </div>
      <div class="item">
        <p>{{ t("科技设备") }}</p>
        <p>{{ details[1]?.value }}</p>
      </div>
    </div>
    <div class="row2">
      <div class="item">
        <p>{{ t("银行服务") }}</p>
        <p>{{ details[2]?.value }}</p>
      </div>
      <div class="item">
        <p>{{ t("医疗服务") }}</p>
        <p>{{ details[3]?.value }}</p>
      </div>
      <div class="item">
        <p>{{ t("工业品") }}</p>
        <p>{{ details[4]?.value }}</p>
      </div>
      <div class="item">
        <p>{{ t("百货零售") }}</p>
        <p>{{ details[5]?.value }}</p>
      </div>
    </div>
    <div class="row3">
      <div class="item">
        <p>{{ t("投资服务") }}</p>
        <p>{{ details[6]?.value }}</p>
      </div>
      <div class="item">
        <p>{{ t("食物&饮料") }}</p>
        <p>{{ details[7]?.value }}</p>
      </div>
      <div class="item">
        <p>{{ t("周期性消费品") }}</p>
        <p>{{ details[8]?.value }}</p>
      </div>
      <div class="item">
        <p>{{ t("保险") }}</p>
        <p>{{ details[9]?.value }}</p>
      </div>
      <div class="item">
        <p>{{ t("化石燃料") }}</p>
        <p>{{ details[10]?.value }}</p>
      </div>
      <div class="item">
        <p>{{ t("消费品") }}</p>
        <p>{{ details[11]?.value }}</p>
      </div>
      <div class="item">
        <p>{{ t("汽车及零部件") }}</p>
        <p>{{ details[12]?.value }}</p>
      </div>
      <div class="item">
        <p>{{ t("工业和商业服务") }}</p>
        <p>{{ details[13]?.value }}</p>
      </div>
      <div class="item">
        <p>{{ t("电信服务") }}</p>
        <p>{{ details[14]?.value }}</p>
      </div>
      <div class="item">
        <p>{{ t("房地产") }}</p>
        <p>{{ details[15]?.value }}</p>
      </div>
      <div class="item">
        <p>{{ t("周期性消费服务") }}</p>
        <p>{{ details[16]?.value }}</p>
      </div>
      <div class="item">
        <p>{{ t("公用事业") }}</p>
        <p>{{ details[17]?.value }}</p>
      </div>
      <div class="item">
        <p>{{ t("矿产资源") }}</p>
        <p>{{ details[18]?.value }}</p>
      </div>
      <div class="item">
        <p>{{ t("化学品") }}</p>
        <p>{{ details[19]?.value }}</p>
      </div>
    </div>
  </section>
</template>
    
<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useUserStore } from '@/store/user';
import { useRoute, useRouter } from 'vue-router';
import {
  _getHotMapList
} from "@/service/etf.api";
import { useI18n } from 'vue-i18n'

const props = defineProps({
  isSell: {
    type: Boolean,
    default: false
  }
})
const userStore = useUserStore()
const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const details = ref([])
const interval = ref(null)
const isLoading = ref(false)


onMounted(() => {
  getHotMapList()
})

onBeforeUnmount(() => {

})

const getHotMapList = () => {
  _getHotMapList().then(data => {
    details.value = data || []
  })
}

</script>
<style lang="scss" scoped>
.hot-map-container {
  margin: 12px;
  height: 487px;
  overflow: hidden;

  .item {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin: 1px;
    text-align: center;
    font-size: 10px;

    p {
      text-overflow: ellipsis;
      overflow: hidden;
      white-space: nowrap;
      color: $white;
    }
  }

  .row1 {
    display: flex;

    div:first-child {
      width: calc(60% - 1px);
      height: 137px;
      background-color: #AF2356;
    }

    div:nth-child(2) {
      width: calc(40% - 1px);
      height: 137px;
      background-color: #6A1744;
    }
  }

  .row2 {
    position: relative;
    display: flex;

    .item {
      display: inline-block;
      padding-top: 20px;
    }

    div:first-child {
      padding-top: 50px;
      width: 126px;
      height: 148px;
      background-color: #6A1744;
    }

    div:nth-child(2) {
      flex: 1;
      height: 105px;
      background-color: #1A615E;
    }

    div:nth-child(3) {
      flex: 1;
      height: 105px;
      background-color: #6A707E;
    }

    div:nth-child(4) {
      flex: 1;
      height: 105px;
      background-color: #AF2356;
    }
  }

  .row3 {
    position: relative;

    div:first-child {
      padding-top: 20px;
      display: inline-block;
      width: 126px;
      height: 112px;
      background-color: #1A615E;
    }

    div:nth-child(2) {
      position: absolute;
      top: -43px;
      display: inline-block;
      width: 20%;
      height: 98px;
      background-color: #1A615E;
    }

    div:nth-child(3) {
      position: absolute;
      top: -43px;
      left: calc(20% + 130px);
      display: inline-block;
      width: calc((80% - 130px) / 2);
      height: 62px;
      background-color: #6A1744;
    }

    div:nth-child(4) {
      position: absolute;
      top: -43px;
      left: calc(20% + 130px + (80% - 130px) / 2 + 2px);
      display: inline-block;
      width: calc((80% - 138px) / 2);
      height: 62px;
      background-color: #AF2356;
    }

    div:nth-child(5) {
      padding-top: 20px;
      position: absolute;
      top: 114px;
      left: 0;
      display: inline-block;
      width: 126px;
      height: 84px;
      background-color: #6A1744;
    }

    div:nth-child(6) {
      position: absolute;
      top: 57px;
      left: 128px;
      display: inline-block;
      width: 20%;
      height: 71px;
      background-color: #1A615E;
    }

    div:nth-child(7) {
      position: absolute;
      top: 21px;
      left: calc(20% + 130px);
      display: inline-block;
      width: calc((80% - 150px) / 2);
      height: 62px;
      background-color: #AF2356;
    }

    div:nth-child(8) {
      position: absolute;
      top: 21px;
      left: calc(20% + 130px + (80% - 150px) / 2 + 2px);
      display: inline-block;
      width: calc((80% - 118px) / 2);
      height: 50px;
      background-color: #1A615E;
    }

    div:nth-child(9) {
      position: absolute;
      top: 85px;
      left: calc(20% + 130px);
      display: inline-block;
      width: calc((80% - 150px) / 2);
      height: 56px;
      background-color: #6A1744;
    }

    div:nth-child(10) {
      position: absolute;
      top: 74px;
      left: calc(20% + 130px + (80% - 150px) / 2 + 2px);
      display: inline-block;
      width: calc((80% - 118px) / 2);
      height: 50px;
      background-color: #1A615E;
    }

    div:nth-child(11) {
      position: absolute;
      top: 130px;
      left: 128px;
      display: inline-block;
      width: 20%;
      height: 68px;
      background-color: #6A1744;
    }

    div:nth-child(12) {
      position: absolute;
      top: 142px;
      left: calc(20% + 130px);
      display: inline-block;
      width: calc((80% - 150px) / 2);
      height: 56px;
      background-color: #1A615E;
    }

    div:nth-child(13) {
      position: absolute;
      top: 126px;
      left: calc(20% + 130px + (80% - 150px) / 2 + 2px);
      display: inline-block;
      width: calc((80% - 118px) / 4);
      height: 72px;
      background-color: #6A1744;
    }

    div:nth-child(14) {
      position: absolute;
      top: 126px;
      left: calc(20% + 130px + (80% - 150px) / 2 + 4px + (80% - 118px) / 4);
      display: inline-block;
      width: calc((80% - 126px) / 4);
      height: 72px;
      background-color: #6A1744;
    }
  }
}
</style>