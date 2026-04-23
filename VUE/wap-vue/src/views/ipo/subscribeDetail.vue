<template>
  <div class="pb-fix">
    <van-loading color="#1194F7" class="loading-box" v-if="isLoading" />
    <div class="container-box">
      <header class="header">
        <div class="icon-group">
          <div class="icon back" @click="onRoute(-1)">
            <van-icon name="arrow-left" size="20" />
          </div>
          <div class="middle-text title">
            <span>{{t('认缴')}}</span>
          </div>
          <div class="right-text text-record" @click="onRoute(recordPath)">
            <span>{{t('认缴记录')}}</span>
          </div>
        </div>
      </header>
      <section class="card">
        <p class="name">{{ listData.name }}</p>
        <p class="code">{{ listData.productCode }}</p>
        <p class="num">{{ listData.underwritingPrice }}</p>
      </section>
      <section class="details">
        <div class="detail-info">
          <ul class="detail-ul">
            <li class="flex">
              <div class="item-l">{{t('认缴代码')}}</div>
              <div class="item-r">{{ listData.productCode }}</div>
            </li>
            <li class="flex">
              <div class="item-l">{{t('发行总股数')}}</div>
              <div class="item-r">{{ listData.subscribeTotalNumber }}</div>
            </li>
            <!-- <li class="flex">
              <div class="item-l">{{t('认缴开始日')}}</div>
              <div class="item-r">{{ getTime(listData.startSubscribeDate) }}</div>
            </li>
            <li class="flex">
              <div class="item-l">{{t('认缴截止日')}}</div>
              <div class="item-r">{{ getTime(listData.endSubscribeDate) }}</div>
            </li> -->
            <li class="flex">
              <div class="item-l">{{t('认缴日')}}</div>
              <div class="item-r">{{ getTime(listData.subscribeTime) }}</div>
            </li>
            <li class="flex">
              <div class="item-l">{{t('发券日')}}</div>
              <div class="item-r">{{ getTime(listData.issuanceDate) }}</div>
            </li>
          </ul>
          <div class="inputEara">
            <div class="flex justify-between cell-div mt-10 mb-8">
              <div class="text_color6">{{ t('中签额度') }}</div>
              <div class="theCode">{{ t('股') }}</div>
            </div>
            <div class="drawLots-input">
                <input class="inputBackground" :placeholder="t('请输入张数')" v-model="listData.winningNumber" type="number" readonly />
            </div>
            <div class="flex justify-between cell-div mt-10 mb-8">
              <div class="text_color6">{{ t('认缴金额') }}({{ t('股') }})</div>
              <!-- <div class="theCode">{{ t('股') }}</div> -->
            </div>
            <div class="drawLots-input">
                <input class="inputBackground" :placeholder="t('请输入认缴金额') + '(' +  t('股') + ')'" v-model="amount" type="number" />
            </div>
            <div class="flex justify-between cell-div mt-10 mb-8">
              <div class="text_color6">{{ t('剩余认缴股数') }}</div>
              <div class="theCode">{{ t('股') }}</div>
            </div>
            <div class="drawLots-input">
                <input class="inputBackground" :value="listData.residuePromiseNumber || 0" type="number" readonly />
            </div>
            <div class="flex justify-between cell-div mt-10 mb-8">
              <div class="text_color6">{{ t('认缴次数') }}</div>
              <!-- <div class="theCode">{{ t('股') }}</div> -->
            </div>
            <div class="drawLots-input">
                <input class="inputBackground" :value="listData.userPromiseCount || 0" type="number" readonly />
            </div>
          </div>
          <div class="btn text-white" @click="drawLots">{{t('一键认缴')}}</div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, onBeforeUnmount, inject} from "vue";
import { showToast } from 'vant'
import { getNewSharesDesc, applyPromise } from '@/service/ipo.api'
// import { _getExchangeRate } from "@/service/home.api";
// import { _getETFSum } from "@/service/etf.api";
import { useRouter, useRoute } from "vue-router";
import { useI18n } from "vue-i18n";
import store from "@/store/store";
const currency = ref({});
const forexAssets = ref({});
const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const listData = ref([])
const interval = ref(null)
const isLoading = ref(false)
const amount = ref('')

const stockType = route.query.stockType
const stockActive = route.query.stockActive
console.log('stockType', stockType)
const recordPath = `/ipo/stock?type=subscribe&value=stock&stock=${stockType}&stockActive=${stockActive}`

onMounted(() => {
  fetchDesc()
})

onBeforeUnmount(() => {
  if (interval.value) {
    clearInterval(interval.value)
  }
})

const fetchDesc = () => {
  const params = {
    id: route.query?.id
  }
  isLoading.value = true
  getNewSharesDesc(params).then(data => {
    isLoading.value = false
    listData.value = data
  }).catch((e) => {
    isLoading.value = false
  })
}
const drawLots = () => {
  if(!amount.value){
    showToast(t('请输入认缴金额'));
    return
  }
  let params = {
    deductNumber: amount.value,
    orderNo: listData.value.orderNo,
  }
  applyPromise(params).then((res) => {
    showToast(t('认缴成功'));
    router.push(recordPath)
  }).catch((err) => Promise.reject(err));
}


const getTime = (time) => {
  return time?.split(' ')[0];
}

const onRoute = (path) => {
  if (path === -1) {
    router.go(-1)
    return
  }
  router.push(path)
}
</script>

<style lang="scss" scoped>
:deep(.van-loading) {
  position: absolute;
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 999;
}

:deep(.van-nav-bar__title) {
  position: relative;
  top: 2px;
  max-width: 72%;
}

:deep(.van-nav-bar__title) {
  flex: 1;
}

:deep(.van-field__control) {
  font-size: 16px;
  font-weight: 500;
  caret-color: #3157BE;
}

.text-record{
  font-size: 12px;
}

.container-box {
  .header {
    padding: 0 14px;
    margin: 8px 0;

    .title {
      font-weight: 600;
      font-size: 16px;
      color: $text_color;
    }

    .icon-group {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .icon {
        display: inline-block;
        width: 32px;
        height: 32px;
        padding: 8px;
      }
    }
  }

  .card {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: $ipo_card_bg;
    height: 120px;

    .name {
      font-size: 16px;
      font-weight: 700;
    }

    .code {
      margin: 0 0 10px;
      font-size: 12px;
      color: #747A8F;
    }

    .num {
      font-size: 14px;
      color: #F33368;
    }
  }

  .details {
    padding: 0 14px;

    .detail-info {
      ul {
        padding-bottom: 20px;
        border-bottom: 1px solid $border_color;
      }

      li {
        margin-top: 20px;
        font-size: 14px;
        display: flex;
        justify-content: space-between;

        .item-l {
          color: $ipo_normal_text;
        }

        .item-r {
          color: $text_color;
        }
      }
    }
  }
}

.inputEara{
  font-size: 12px;
}

.drawLots-input {
  input {
    width: 100%;
    height: 40px;
    border-radius: 5px;
    text-align: center;
    font-weight: bold;
  }
}

.btn{
  width: 100%;
  margin: 20px auto 0;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 12px;
  background: linear-gradient(180deg, #2C64D4 100%, #38AEEA 100%);
  border-radius: 2px;
  padding: 10px 0;
}
</style>