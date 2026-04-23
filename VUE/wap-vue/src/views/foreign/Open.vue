<template>
  <section>
    <fx-header></fx-header>
    <div class="open-index">
      <!-- 币种基本信息 -->
      <div class="base-wrapper">
        <div class="name">{{ route.query.symbol }}</div>
        <div class="font-bold">{{ quoteData?.close }}</div>
      </div>
      <!-- tab 栏 -->
      <div class="tab-wrapper flex-all-center">
        <div :class="{ 'active': activeTab === 1 }" class="flex-all-center flex-1" @click="changeTab(1)">
          {{ t('marketPrice') }} </div>
        <div :class="{ 'active': activeTab === 2 }" class="flex-all-center flex-1" @click="changeTab(2)">
          {{ t('限价') }} </div>
      </div>
      <!-- 开仓价格 -->
      <div class="volumn-wrapper" v-if="activeTab == 2">
        <div class="title">{{ t('price') }} {{ `(${route.query.closePrice})` }}</div>
        <div class="operator">
          <div class="minus btn flex-all-center" @click="handleChangePrice('0.1', '-')">-</div>
          <div class="number  flex-all-center">
            <input style="text-align: center;" class="btn" v-model="inputPrice" type="number" />
          </div>
          <div class="add btn flex-all-center" @click="handleChangePrice('0.1', '+')">+</div>
        </div>
      </div>
      <!-- 开仓数量 -->
      <div class="volumn-wrapper">
        <div class="title">{{ t('可开张数') }}
          <!-- <span v-if="initOpen.volume > 1">{{ `(1 ~ ${initOpen.volume})` }}</span> -->
          <span>{{ `(${initOpen.volume})` }}</span>
        </div>
        <div class="operator">
          <div class="minus btn flex-all-center" @click="handleChangeVolumn(1, '-')">-</div>
          <div class="number  flex-all-center">
            <input style="text-align: center;" @input="changeVolumn" class="btn" v-model="inputVolumn" type="number" />
          </div>
          <div class="add btn flex-all-center" @click="handleChangeVolumn(1, '+')">+</div>
        </div>
        <div class="mainBox">
          <div class="slider" v-if="initOpen.volume > 1">
            <vue-slider @change="onSliderChange" class="mainBox" :marks="marks" v-model="inputVolumnSlider"
              :hide-label="true" width="98%" tooltip="hover" :tooltip-formatter="'{value}%'"
              :railStyle="{ height: '4px' }" :processStyle="{ background: '#266BFF', height: '4px' }">
              <template v-slot:step="{ active }">
                <div :class="['custom-step', { active }]"></div>
              </template>
            </vue-slider>
          </div>
        </div>

      </div>
      <!-- 下单信息 -->
      <div class="open-wrapper">
        <div class="open-item mb-10" v-for="(item, index) in infoList" :key="index">
          <span class="label">{{ item.label }}</span>
          <!-- 杠杆下拉框 -->
          <van-dropdown-menu class="value" v-if="item.value == 'lever'">
            <van-dropdown-item v-model="curLevel" :options="levelList" /></van-dropdown-menu>
          <span class="value" v-else>{{ getInfoValue(item.value) }}</span>
        </div>
      </div>
      <!-- 下单按钮 -->
      <div :class="[inputVolumn > 0 ? route.query.type : 'disabled']" class="btn-wrapper flex-all-center"
        @click="handleSubmit">{{ t(route.query.type) }} </div>
      <!-- 下单的弹窗 -->
      <van-popup round v-model:show="showPop" position="bottom" :close-on-click-overlay="false" @close="onClose">
        <div class="pop-box mainBackground">
          <img src="@/assets/image/wallet/close3.png" class="top-46 close-btn" @click="onClose" />
          <div class="title">{{ t('Opening') + t('success') }}</div>
          <div class="content flex-all-center">
            <img class="w-12 h-12 mb-4" src="@/assets/image/choice-blue.png" alt="avatar" />
            <div class="text1">{{ t('Finish') }}</div>
            <div class="text2">{{ route.query.symbol }}</div>
            <div class="text3" :class="route.query.type + '-text'">{{ activeTab === 1 ? t('marketPrice') :
              t('限价')
            }}
              <span>{{ t('volumn') }} {{ inputVolumn }}</span>
            </div>
            <div :class="route.query.type" class="btn-wrapper flex-all-center see-btn" @click="gotoPosition">{{
              t('viewposition')
            }} </div>
          </div>
        </div>
      </van-popup>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import { showToast } from 'vant';
import { _getAllAssets } from "@/service/user.api.js";
import { _initOpen, _orderOpen, _initClose } from "@/service/trade.api.js";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from 'vue-router';
import VueSlider from "vue-slider-component";

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const activeTab = ref(1)
const inputVolumn = ref('1') //数量输入
const inputPrice = ref(route.query.closePrice) //价格输入
const showPop = ref(false)
const levelList = ref([])
const curLevel = ref(1) //杠杆初始化为1倍
const initOpen = ref({})
const quoteData = ref({})
const inputVolumnSlider = ref('1')
const marks = (val) => val % 25 === 0
const isClick = ref(true)

const infoList = [{
  label: t('lever'),
  value: 'lever',
}, {
  label: t('margin'),
  value: 'margin',
}, {
  label: t('money'), //合约金额
  value: 'money',
}, {
  label: t('fee'),
  value: 'fee',
}];

// 下单的参数
const formData = ref({
  symbol: '', // 币种
  session_token: '',
  direction: 'buy', // 买or卖
  price_type: 'opponent', // 市价or限价
  lever_rate: 1, // 杠杆
  price: '',
  amount: '', // 数量
})

// TODO 路由参数不刷新
watch(route, (newval, oldval) => {
  console.log('开仓页面watch', route.query.closePrice)
}, { deep: true })

onMounted(async () => {
  await initParams()
  innitLever(initOpen.value)
})

const initParams = async () => {
  const { symbol, type } = route.query
  const res = await _initOpen({ symbol })
  initOpen.value = res;
}

const innitLever = (res) => {
  if (res.lever && res.lever.length) {
    levelList.value = res.lever?.map(it => {
      return {
        text: `${it.lever_rate}x`,
        value: it.lever_rate
      }
    });
    curLevel.value = levelList.value[0].value //默认选中第一个
  } else {
    levelList.value = [{
      text: "1x",
      value: 1
    }]
  }
}


const onSliderChange = (value) => {
  let data;
  if (initOpen.value.volume) {
    if (value == 0) {
      inputVolumn.value = 1;
    } else {
      const rate = value / 100; //如0.25
      data = initOpen.value.volume * rate;
      inputVolumn.value = parseInt(data);
    }
  }
};

const handleSubmit = async () => {
  if (!isClick.value) {
    return
  }
  isClick.value = false
  if (inputVolumn.value === 0) {
    return
  }
  // 下单之前，刷新token
  await initParams()
  const { symbol, type } = route.query;
  formData.value.symbol = symbol;
  formData.value.direction = type;
  formData.value.lever_rate = curLevel.value;
  formData.value.price_type = activeTab.value === 2 ? 'limit' : 'opponent'; //
  const closePrice = route.query.closePrice;
  formData.value.price = activeTab.value === 2 ? inputPrice.value : closePrice;
  formData.value.session_token = initOpen.value.session_token;
  formData.value.amount = inputVolumn.value;  //不用乘以一手的数量，服务端乘了
  _orderOpen(formData.value).then(res => {
    showPop.value = true
    isClick.value = true
  }).catch(error => {
    // TODO:
    // router.go(-1)
    isClick.value = true
    showToast(t(error))
  })
}

const getInfoValue = (val) => {
  switch (val) {
    case 'fee':
      return Number(inputVolumn.value * initOpen.value.fee * curLevel.value).toFixed(0);
      break;
    case 'lever':
      return `${curLevel.value}x`;
      break;
    default:
      // 合约和保证金
      return Number(inputVolumn.value * initOpen.value.amount * curLevel.value).toFixed(0);
  }
}

const changeTab = (type) => {
  inputVolumnSlider.value = 1
  inputVolumn.value = 1
  activeTab.value = type
}

const handleChangePrice = (step, type) => {
  if (type === '-') {
    if (Number(inputPrice.value) > 0) {
      inputPrice.value = (Number(inputPrice.value) - Number(step)).toFixed(4)
    } else {
      inputPrice.value = 0
    }
  } else {
    inputPrice.value = (Number(inputPrice.value) + Number(step)).toFixed(4)
  }
}

const handleChangeVolumn = (step, type) => {
  if (type === '-') {
    if (Number(inputVolumn.value) > 0) {
      inputVolumn.value = Number(inputVolumn.value) - Number(step)
    } else {
      inputVolumn.value = 0
    }
  } else {
    inputVolumn.value = Number(inputVolumn.value) + Number(step)
  }
}
//滑块处理
const changeVolumn = () => {
  inputVolumnSlider.value = ((inputVolumn.value / initOpen.value.volume) * 100).toFixed(2)
}
const onClose = () => {
  showPop.value = false
}

const gotoPosition = () => {
  router.push('/position/index')
}
</script>

<style lang="scss" scoped>
:deep(.van-dropdown-menu__bar) {
  height: 30px !important;
  width: 90px;
  background: $options_background;
  border: 1px solid $border_color;
}

:deep(.van-dropdown-menu__title) {
  color: $text_color;
}

:deep(.van-cell.van-dropdown-item__option) {
  background: $options_background;
  color: $text_color;
}

:deep(.van-dropdown-item__option--active) {
  color: $color_main !important;
}

:deep(.van-cell:after) {
  border-bottom: none;
}

:deep(.vue-slider-rail) {
  background-color: $input_background;
}

// :deep(.van-dropdown-item){
//   left: 70%;
// }
// :deep(.van-dropdown-item--down){
//   width: 90px;
//   margin-top: 10px;
//   background: #fff;
// }
// // z-index高就可以了
// :deep(.van-dropdown-item__content){
//   box-shadow: 0px 0px 4px rgba(0, 0, 0, 0.15);
//   border-radius: 6px;

// }

.open-index {
  padding: 0 20px;

  .base-wrapper {
    display: flex;
    justify-content: space-between;
    margin: 24px 0;
    font-size: 20px;
    font-weight: 600;

  }

  .tab-wrapper {
    width: 100%;
    height: 50px;
    background: $tab-second-bg;
    border-radius: 22px;
    font-weight: 700;
    font-size: 16px;

    .active {
      width: 186px;
      height: 45px;
      background: $blue;
      border-radius: 22px;
      color: $main-btn-color;
    }
  }

  .volumn-wrapper {
    margin-top: 24px;

    .title {
      font-weight: 600;
      font-size: 16px;
      color: #818181;
    }

    .operator {
      display: flex;
      margin-top: 28px;

      .btn {
        height: 48px;
        background: $input_background;
        border-radius: 8px;
        color: $blue;
        font-size: 24px;
      }

      input.btn {
        color: $text_color !important;
      }

      .minus,
      .add {
        width: 48px;
      }

      .number {
        width: 260px;
      }

      .minus,
      .number {
        margin-right: 8px;
      }
    }

    .slider {
      margin-top: 40px;
    }

    .gears {
      margin-top: 10px;
      font-size: 12px;
    }

  }

  .open-wrapper {
    margin-top: 24px;

    .open-item {
      display: flex;
      justify-content: space-between;
      margin-bottom: 12px;

      .label {
        font-weight: 600;
        font-size: 16px;
        color: $text_color;
      }

      .value {
        font-weight: 600;
        font-size: 18px;
        color: $text_color;
      }
    }
  }

  .btn-wrapper {
    width: 100%;
    height: 50px;
    border-radius: 12px;
    margin-top: 24px;
    font-weight: 600;
    font-size: 22px;
    color: $main-btn-color;
  }

  .sell {
    background: linear-gradient(90deg, #2C64D4 0%, #38AEEA 100%);

  }

  .disabled {
    background: #E5E5E5;
  }

  .buy {
    background: linear-gradient(90deg, #2C64D4 0%, #38AEEA 100%);
  }

  .flex-all-center {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .mainBackground {
    padding: 20px;

    .close-btn {
      background-color: #1B2134;
      width: 30px;
      height: 30px;
      border-radius: 4px;
    }

    .title {
      font-weight: 600;
      font-size: 25px;
      line-height: 30px;
      color: $text_color;
      margin: 20px 0;
    }

    .content {
      flex-direction: column;

      .text1 {
        font-size: 15px;
        line-height: 26px;
        color: #fff;
      }

      .text2 {
        font-weight: 800;
        font-size: 22px;
        line-height: 27px;
        color: $text_color;
        margin: 12px 0;
      }

      .text3 {
        font-size: 15px;
        color: #98B9FA !important;
      }

      .sell-text {
        color: $red;
      }

      .buy-text {
        color: $light-blue;
      }
    }
  }
}

.mainBox {
  :deep(.vue-slider-dot-tooltip-inner) {
    background-color: $US_tabActice_background !important;
    color: #118DEC !important;
    padding: 0 10px !important;
  }

  :deep(.vue-slider-dot) {
    background: #417EF6 !important;
    color: #fafafa !important;
    width: 16px !important;
    height: 16px !important;
    border-radius: 50%;
  }


  :deep(.vue-slider-dot-tooltip-inner-top::after) {
    display: none;
  }

  :deep(.vue-slider-marks) {
    background: $input_background;
  }

  :deep(.custom-step) {
    width: 16px;
    height: 16px;
    border-radius: 50%;
    /* 没有选中时候圈圈的颜色 */
    background: $input_background;
    border: 1px solid $input_background;
    margin-top: -6px;
    position: relative;
    left: -6px;
  }
}

.custom-step.active {
  // box-shadow: 0 0 0 3px $color_main;
  background-color: #417EF6;
  border: 1px solid #417EF6;
}
</style>