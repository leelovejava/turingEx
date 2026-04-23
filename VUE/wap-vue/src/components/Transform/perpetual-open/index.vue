<template>
  <!-- 永续合约开仓页 -->
  <div id="cryptos">
    <div class="perpetual-open">
      <div class="flex pt-8">
        <div class="w-40 h-16 text-center rounded tabBackground textColor">{{ $t('全仓') }}</div>
        <div class="w-36 h-16 ml-8 rounded tabBackground textColor" v-if="selectIndex / 1 === 1">
          <div @click="showOptions = !showOptions" style="height:100%; box-sizing: border-box"
               class="relative word-30 pl-7 pr-2 w-36 h-16 tabBackground select-wrap flex justify-between items-center">
            {{ form.lever_rate }}x <img src="../../../assets/image/public/grey-select.png" alt="select-icon"
                                        class="w-5 h-3" />
            <div v-if="showOptions" class="options tabBackground w-36 absolute top-24 left-0 z-10">
              <div class="w-full" @click.stop="handleChoose(item)"
                   :class="{ 'option-active': form.lever_rate === item.lever_rate }" :value="item.lever_rate"
                   v-for="item in initData.lever" :key="item.id">
                {{ item.lever_rate }}x
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="pt-8 pb-5">
        <div class="flex">
          <!-- 港股隐藏 -->
          <div class="flex flex-col flex-1" :class="['HK-stocks', 'JP-stocks','UK-stocks','DE-stocks','BZ-stocks'].includes(queryType) ? 'w-full' : 'w-auto'">
            <template v-if="selectIndex == 1">
              <div class="flex items-center h-16 rounded-lg text-grey">
                <p class="text-28 flex-1 flex items-center justify-center h-16 "
                  :class="currentType == 'long' ? 'long' : ''" @click="changeTab('long')">
                  {{ $t("开多") }}
                </p>
                <p class="text-28 flex-1 flex items-center justify-center h-16"
                  :class="currentType == 'short' ? 'short' : ''" @click="changeTab('short')">
                  {{ $t("开空") }}
                </p>
              </div>
              <div class="mt-5 mb-8" style="position:relative;">
                <div class="greyBg flex justify-between items-center w-full h-18 rounded-lg textColor" @click="selectBtn">
                  <img src="../../../assets/image/public/warn.png" alt="warn-icon" class="w-6 h-6 ml-5" />
                  <div class="text-center" style="width:80%;">{{ title }}</div>
                  <img src="../../../assets/image/public/grey-select.png" alt="select-icon" class="w-6 h-3 mr-5" />
                </div>
                <div class="option-box" v-show="isShow">
                  <div class="text-30" v-for="item in selectData" :key="item.type" @click="selectItem(item)">{{
                    item.title
                  }}
                  </div>
                </div>
              </div>
            </template>
            <contract-futrue v-if="selectIndex == 2 && JSON.stringify(initFutrue.para) != '[]'" class="mb-5"
              :initFutrue="initFutrue" @paraId="onParaId" />
            <div class="h-18 greyBg mb-8 flex pr-5 justify-center rounded-lg textColor items-center"
              v-if="selectIndex == 1">
              <input placeholder="" class="greyBg w-full pl-5 h-16 border-none text-left rounded-lg"
                :disabled="type / 1 === 1" @focus="focus = true" v-model="form.price" />
              <span class="ml-5">{{ getCurrentUnit() }}</span>
            </div>

            <div class="flex items-center greyBg h-18 rounded-lg pr-4"
              :class="['HK-stocks', 'JP-stocks','UK-stocks','DE-stocks','BZ-stocks'].includes(queryType) ? 'w-full' : 'w-auto'" v-if="initFutrue">
              <div v-if="selectIndex == 1" class="w-20 flex items-center justify-center" style="height:100%;"
                @click="onReduce">
                <img src="../../../assets/image/public/reduce.png" alt="add" class="w-8 h-2" />
              </div>
              <input v-if="selectIndex == 1" :placeholder="$t('张数')" class="border-none greyBg text-center textColor"
                :class="['HK-stocks', 'JP-stocks','UK-stocks','DE-stocks','BZ-stocks'].includes(queryType) ? 'full-input' : 'half-input'"
                v-model="form.amount" type="number" @input="onInput" />
              <input v-if="selectIndex == 2 && JSON.stringify(initFutrue.para) != '[]'"
                :placeholder="($t('最少') + initFutrue && initFutrue.para ? $t('最小金额') + initFutrue.para[paraIndex].buy_min : '')"
                class="border-none greyBg text-center textColor"
                :class="['HK-stocks', 'JP-stocks','UK-stocks','DE-stocks','BZ-stocks'].includes(queryType) ? 'full-input' : 'half-input'"
                v-model="form.amount" type="number" @input="onInput" />
              <div class="w-20 flex items-center justify-center" style="height:100%;">
                <template v-if="selectIndex == 1">
                  <img @click="onAdd" src="../../../assets/image/public/add.png" alt="reduce" class="w-8 h-8" />
                </template>
                <template v-else>
                  <span class="textColor">{{ getCurrentUnit() }}</span>
                </template>
              </div>
            </div>
            <template v-if="selectIndex == 1">
              <div class="mt-10 mb-8 w-full flex justify-between items-center">
                <span class="text-22 text-grey">{{ $t("可开张数") }}</span>
                <span class="text-22 textColor">
                  {{ initData.volume }}
                  {{ $t("张") }}
                </span>
              </div>
              <!-- <vue-slider v-bind="options" v-model="form.amount"></vue-slider> -->
              <!-- <vue-slider class="mainBox" v-bind="options" :marks="marks" v-model="form.volume" :hide-label="true"  width="90%"
              :railStyle="{ background: '#404040', height: '4px' }"
              :processStyle="{ background: '#266BFF', height: '4px' }">
              <template v-slot:step="{ active }">
                <div :class="['custom-step', { active }]"></div>
              </template>
            </vue-slider>
            <div style="color: #868D9A" class="mt-36 text-24 w-full flex justify-between items-center">
              <span class="flex-1 text-left">0%</span>
              <span class="flex-1 text-left">25%</span>
              <span class="flex-1 text-center">50%</span>
              <span class="flex-1 text-right">75%</span>
              <span class="flex-1 text-right">100%</span>
            </div> -->
              <!-- 张数输入 -->
              <amount-slider ref="sliderRef" :maxAmount="getVolumnByLever()" @getAmount="getAmount"></amount-slider>
            </template>
            <template v-if="selectIndex == 1 && userInfo.token">
              <div class="flex justify-between mt-8">
                <div class="text-grey">{{ $t('合约金额') }}</div>
                <div class="textColor">{{ initData.amount * (form.amount / 1) * form.lever_rate }}
                  <span>{{  getCurrentUnit() }}</span>
                </div>
              </div>
              <div class="flex justify-between mt-8">
                <div class="text-grey">{{ $t("保证金") }}</div>
                <div class="textColor">
                  {{ (initData.amount * (form.amount / 1)) }} {{ getCurrentUnit() }}
                </div>
              </div>
              <div class="flex justify-between mt-8">
                <div class="text-grey">{{ $t("建仓手续费") }}</div>
                <div class="textColor">{{
                  userInfo.perpetual_contracts_status === '1' ? initData.fee *
                (form.amount / 1) : initData.fee * (form.amount / 1) * form.lever_rate
                }} {{ getCurrentUnit() }}
                </div>
              </div>
            </template>

            <div class="flex text-24 text-grey justify-between mt-8" v-if="selectIndex == 2">
              <div>{{ $t("可用") }}</div>
              <div class="textColor">{{ initFutrue.amount }} {{ getCurrentUnit() }}</div>
            </div>
            <div class="flex text-24 text-grey justify-between mt-5"
              v-if="selectIndex == 2 && JSON.stringify(initFutrue.para) != '[]'">
              <div>{{ $t("手续费") }}</div>
              <div class="textColor">
                {{
                  (
                    form.amount *
                    (initFutrue && initFutrue.para
                      ? initData.para[paraIndex].unit_fee
                      : "")
                  ).toFixed(2)
                }}
                {{ getCurrentUnit() }}
              </div>
            </div>

            <template v-if="userInfo.token">
              <template v-if="selectIndex == 1">
                <div class="w-full h-20 bg-green flex text-white rounded-md mt-16 justify-center items-center"
                  v-if="currentType == 'long'" @click="throttleFn('long')">
                  <div class="text-34 relative text-center" style="flex-grow:1;" v-if="selectIndex == 1">
                    {{ $t('开多') }}
                    <span class="right-word text-22 text-center">{{
                      $t("看涨")
                    }}</span>
                  </div>
                  <div class="text-34 relative text-center" style="flex-grow:1;" v-else>
                    {{ $t('做多买入') }}
                  </div>
                </div>
                <div class="w-full h-20 bg-red flex text-white rounded-md mt-16 justify-center items-center"
                  v-if="currentType == 'short'" @click="throttleFn('short')">
                  <div class="relative text-34 text-center" style="flex-grow:1;" v-if="selectIndex == 1">
                    {{ $t('开空') }}
                    <span class="right-word text-22 text-center">{{
                      $t("看跌")
                    }}</span>
                  </div>
                  <div class="relative text-34 text-center" style="flex-grow:1;" v-else>
                    {{ $t('做空买入') }}
                  </div>
                </div>
              </template>
              <template v-if="selectIndex == 2">
                <div class="w-full h-20 bg-green flex text-white rounded-md mt-2 justify-center items-center"
                  @click="throttleFn('open')" v-if="userInfo.token">
                  <div class="text-34 relative text-center" style="flex-grow: 1">
                    {{ $t("开多") }}
                    <span class="right-word text-22 text-center">{{
                      $t("看涨")
                    }}</span>
                  </div>
                </div>
                <div class="w-full h-20 bg-red flex text-white rounded-md mt-5 mb-2 justify-center items-center"
                  style="position: relative;" :class="{ 'mt-22': selectIndex == 2 }" @click="throttleFn('close')"
                  v-if="userInfo.token">
                  <div class="relative text-34 text-center" style="flex-grow: 1">
                    {{ $t("开空") }}
                    <span class="right-word text-22 text-center">{{
                      $t("看跌")
                    }}</span>
                  </div>
                </div>
              </template>
            </template>
            <div v-else class="w-full mt-28">
              <div class="textColor">
                <p>{{ $t('套期保值，风险对冲') }}</p>
                <p class="pt-2">{{ $t('登陆后继续') }}</p>
              </div>
              <div class="h-20 btnBackground flex text-white rounded-md justify-center mt-10 items-center"
                @click="$router.push('/login')">{{ $t('logIn') }}
              </div>
            </div>
          </div>
          <div class="ml-9" v-if="!['HK-stocks', 'JP-stocks' ,'UK-stocks','DE-stocks','BZ-stocks'].includes(queryType)">
            <div class="w-72 flex justify-between text-grey text-22">
              <div>
                <div>{{ $t("价格") }}</div>
                <div class="mt-2">{{ getCurrentUnit() }}</div>
              </div>
              <div class="text-right items-end justify-end">
                <div class="">{{ $t("数量") }}</div>
                <div class="mt-2">{{
                  queryType === 'cryptos' ?
                  `(${symbol.toUpperCase() || "--"})` : '(USD)'
                }}
                </div>
              </div>
            </div>
            <div class="deep-div">
              <div v-if="showType == 0 || showType == 2" class="w-72 flex justify-between pt-1 text-26"
                v-for="(item, index) in redData" :key="item + index" @click="onQuickPrice(item.price)" :style="{
                  background:
                    `linear-gradient(to right,${THEME == 'dark' ? '#131A2E' : '#fff'
                    } 0%` +
                    (1 - item.amount / greenData[greenData.length - 1].amount) *
                    100 +
                    '%,rgba(246,70,93,.1) ' +
                    (1 - item.amount / greenData[greenData.length - 1].amount) *
                    100 +
                    '%,rgba(246,70,93,.1) 100%)',
                }">
                <div class="text-red">{{ item.price }}</div>
                <div class="text-right textColor" v-if="symbol == 'shib'">
                  {{ fixDate(item.amount, $i18n) || "--" }}
                </div>
                <div class="text-right textColor" v-else>
                  {{ item.amount || "--" }}
                </div>
              </div>
            </div>
            <div class="w-72 text-red pt-1 text-34 font-bold text-center">
              {{ price || '--' }}
            </div>
            <div class="pb-1 text-26 text-center">
              ≈ {{
                ((price *
                  currency.rate).toFixed(price.toString().split('.')[1] ?
                    price.toString().split('.')[1].length : 2)) || '--'
              }}
            </div>
            <div class="deep-div">


              <div v-if="showType == 0 || showType == 1" class="w-72 flex justify-between pt-1 text-26"
                v-for="(item, index) in greenData" :key="index" @click="onQuickPrice(item.price)" :style="{
                  background:
                    `linear-gradient(to right,${THEME == 'dark' ? '#131A2E' : '#fff'
                    } 0%` +
                    (1 - item.amount / greenData[greenData.length - 1].amount) *
                    100 +
                    '%,rgba(94,186,137,.1) ' +
                    (1 - item.amount / greenData[greenData.length - 1].amount) *
                    100 +
                    '%,rgba(94,186,137,.1) 100%)',
                }">
                <div class="text-green">{{ item.price }}</div>
                <div class="text-right textColor" v-if="symbol == 'shib'">
                  {{ fixDate(item.amount, $i18n) || "--" }}
                </div>
                <div class="text-right textColor" v-else>
                  {{ item.amount || "--" }}
                </div>
              </div>
            </div>
            <div class="flex k-select-box">
              <div class="mt-2 mb-2 select-box" style="position: relative">
                <div class="flex justify-between items-center w-full h-16" @click="selectArryBtn">
                  <!-- <img src="@/assets/image/public/warn.png" alt="warn-icon" class="w-25 h-25 pl-20"/> -->
                  <div class="pl-4 text-28 textColor" style="width: 80%">
                    {{ dataArrTitle }}
                  </div>
                  <img src="../../../assets/image/public/grey-select.png" alt="select-icon" class="w-5 h-2 mr-2" />
                </div>
                <div class="option-box" v-show="arryIsShow">
                  <div class="text-28" v-for="(item, index) in selectDataArry" :key="index" @click="selectItemArry(item)">
                    {{ item.name }}
                  </div>
                </div>
              </div>
              <div @click="isSelectShow = true">
                <img src="../../../assets/image/selectIcon.png" alt="warn-icon" class="w-9 h-8 ml-2" />
              </div>
            </div>
          </div>
        </div>
      </div>
      <van-popup v-model:show="show" class="rounded-2xl">
        <popup-delivery showBtns :detailData="detailData" :key="detailData.order_no" @close="onClose"
          @continueToBuy="continueTobuy" @timeEnd="handleTimeEnd" :price="price" />
      </van-popup>
      <van-action-sheet v-model:show="isSelectShow" @select="onSelect" :actions="actions" :cancel-text="$t('取消')"
        close-on-click-action @cancel="onCancel">
      </van-action-sheet>
    </div>
  </div>
</template>

<script>
import config from "@/config";
import { Popup, showToast } from 'vant';
import { mapGetters } from 'vuex'
import VueSlider from 'vue-slider-component/dist-css/vue-slider-component.umd.js'
import 'vue-slider-component/theme/default.css'
import { _orderOpen, _futrueOrder, _futrueOrderDetail, _getBalance, _futrueOrderInit } from '@/service/trade.api'
import ContractFutrue from '@/components/Transform/contract-futrue/index.vue'
import PopupDelivery from "@/components/Transform/popup-delivery/index.vue";
import { fixDate, throttle } from "@/utils/index.js";
import AmountSlider from "./amountSlider.vue";
import "vue-slider-component/theme/default.css";
import { _getHomeList } from "@/service/home.api";
import { themeStore } from '@/store/theme';
import { currentUnit } from '@/utils/coinUnit.js'

const thStore = themeStore()
const THEME = thStore.theme
export default {
  name: "perpetualPosition",
  components: {
    VueSlider,
    ContractFutrue,
    PopupDelivery,
    AmountSlider,
    [Popup.name]: Popup,
  },
  props: {
    greenData: { // 买单
      type: Array,
      default() {
        return []
      }
    },
    redData: { // 卖单
      type: Array,
      default() {
        return []
      }
    },
    symbol: {
      type: String,
      default: ''
    },
    price: {
      type: [Number, String],
      default: '0.00'
    },
    initOpen: {
      type: Object,
      default() {
        return {}
      }
    },
    initClose: {
      type: Object,
      default() {
        return {}
      }
    },
    initFutrue: {
      type: Object,
      default() {
        return {}
      }
    },
    selectIndex: {
      type: [String, Number],
      default: 1
    },
    currentType: {
      type: String,
      default: "long",
    },
  },
  watch: {
    price(val) {
      if (this.type === '1') { // !this.focus &&
        this.form.price = val
      }
      if (JSON.stringify(this.initFutrue.para) != '[]') {
        this.form.para_id = this.initFutrue.para && this.initFutrue.para[this.paraIndex].para_id // 不优雅，不可靠
      }

    },
    initOpen: { // 处理滚动条初始值
      deep: true,
      handler(val) {
        this.handleInitSliderOption()
      }
    },
    initClose: { // 处理滚动条初始值
      deep: true,
      handler(newVal, oldVal) {
        if (newVal.amount / 1 > 0) {
          this.handleInitSliderOption(true)
        }
      }
    },
    greenData(val) {
      if (this.dataArrValue >= 1) {
        this.greenData.map((item) => {
          item.price = parseFloat(item.price).toFixed(this.dataArrValue);
        });
        this.redData.map((item) => {
          item.price = parseFloat(item.price).toFixed(this.dataArrValue);
        });
      } else {
        if (this.dataArrValue == -1) {
          this.greenData.map((item) => {
            item.price = item.price.substring(0, item.price.indexOf("."));
          });
          this.redData.map((item) => {
            item.price = item.price.substring(0, item.price.indexOf("."));
          });
        }
        if (this.dataArrValue == -2) {
          this.greenData.map((item) => {
            item.price =
              item.price.substring(0, item.price.indexOf(".") - 1) + "0";
          });
          this.redData.map((item) => {
            item.price =
              item.price.substring(0, item.price.indexOf(".") - 1) + "0";
          });
        }
        if (this.dataArrValue == -3) {
          this.greenData.map((item) => {
            item.price =
              item.price.substring(0, item.price.indexOf(".") - 2) + "00";
          });
          this.redData.map((item) => {
            item.price =
              item.price.substring(0, item.price.indexOf(".") - 2) + "00";
          });
        }
      }
    },
    symbol(val) {
      // this.getHomeList(val);
    },
    '$route'(val) {
      this.getHomeList(val.params.symbol);
    },
    initData(val) {
      if (this.selectIndex / 1 === 1) {
        if (val.lever.length > 0) {
          val.lever = val.lever.sort(this.orderListAsc('lever_rate'))
        }
        this.form.lever_rate = val.lever[0]?.lever_rate || 1
      }

    }
  },
  computed: {
    ...mapGetters('user', ['userInfo']),
    ...mapGetters('home', ['currency']),
    initData() {
      let obj = null
      if (this.selectIndex / 1 === 1) {

        obj = this.initOpen
        if (!obj.lever || !obj.lever.length) { // 倍数
          obj.lever = [{ id: 1, lever_rate: 1 }]
        } else {
          this.form.lever_rate = obj.lever[0].lever_rate
        }
        return obj
      }
      return this.initFutrue
    },
    coudBuyVolume() { // 可买数量
      return Math.floor((this.initOpen.volume / 1) / this.form.lever_rate)
    },
  },
  data() {
    return {
      THEME,
      fixDate,
      currentBuyType: '', // 交割合约当前下单的类型，用于弹窗倒计时结束以后，点击再下一单
      timeout2: null,
      timeout: null,
      detailData: {}, // 交割合约订单详情数据
      show: false,
      popType: 'confirm', // 弹框类型 confirm / counting
      showOptions: false,
      options: config.sliderOptions,
      value: 0,
      isShow: false,
      title: this.$t('市价'),
      paraIndex: 0,
      selectData: [
        { title: this.$t('市价'), type: '1' },
        { title: this.$t('限价'), type: '2' },
      ],
      form: {
        symbol: '', // 币种
        session_token: '',
        direction: 'buy', // 买or卖
        price_type: 'opponent', // 市价or限价
        lever_rate: 1, // 杠杆
        price: '',
        amount: '', // 数量
        para_id: '' // 交割周琦id
      },
      focus: false,
      type: "1",//选中市价或限价类型
      // action: _orderOpen // 开仓or
      isShow: false,
      selectDataArry: [],
      isSelectShow: false,
      actions: [
        { name: this.$t("默认"), value: 0, className: 'actions-active' },
        { name: this.$t("展示买单"), value: 1, className: '' },
        { name: this.$t("展示卖单"), value: 2, className: '' },
      ],
      showType: 0,
      dataArrTitle: 0,
      dataArrValue: 0,
      arryIsShow: false,
      marks: (val) => val % 50 === 0,
      queryType: 'cryptos',
    }
  },
  created() {
    if (this.$route.query.type) {
      this.queryType = this.$route.query.type
    }
  },
  activated() {
    this.selectData = [
      { title: this.$t('市价'), type: '1' },
      { title: this.$t('限价'), type: '2' },
    ]
    this.title = this.$t('市价');
  },
  mounted() {
    this.getHomeList(this.$route.params.symbol);
    this.form.price = this.price
  },
  beforeUnmount() {
    this.clearTimeout()
  },
  methods: {
    //获取张数
    getAmount(val) {
      if (!val) {
        val = ''
      }
      this.form.amount = val;
    },
    getCurrentUnit() {
      return currentUnit(this.queryType)
    },
    onSelect(item) {
      this.actions.map((item) => {
        item.className = ''
      })
      item.className = 'actions-active'
      this.showType = item.value;
      this.$emit("changeValueBack", this.showType);
    },
    // 获取张数,数据转换
    getVolumnByLever() {
      let vol;
      vol = this.initOpen.volume / 1;
      return Math.floor(vol);
    },
    selectItemArry(item) {
      this.dataArrValue = item.value;
      this.arryIsShow = false;
      this.dataArrTitle = item.name;
    },
    onCancel() {
      this.isSelectShow = false;
    },
    //价格类型下拉框切换
    selectBtn() {
      this.isShow = !this.isShow;
    },
    selectArryBtn() {
      this.arryIsShow = !this.arryIsShow;
    },
    getHomeList(symbol) {
      _getHomeList(symbol).then((res) => {
        let numberText = res[0].close.toString();
        let numberLength = numberText.substring(numberText.indexOf(".") + 1, numberText.length).length;
        let arry = [];
        let str = "0.";
        for (let j = 0; j < numberLength - 1; j++) {
          str = str + "0";
          arry.push(str);
        }
        let newArry = [];
        arry.map((item) => {
          let obj = {
            name: item + "1",
            value:
              item.substring(item.indexOf(".") + 1, item.length).length + 1,
          };
          newArry.push(obj);
        });
        if (numberLength <= 2) {
          let obj = [
            {
              name: "0.1",
              value: 1,
            },
            {
              name: "1",
              value: -1,
            },
            {
              name: "10",
              value: -2,
            },
            // {
            //   name: '100',
            //   value: -3,
            // }
          ];
          let arryItem = {
            name: "100",
            value: -3,
          };
          if (numberText.substring(0, numberText.indexOf(".")).length > 2) {
            obj.push(arryItem);
          }
          newArry = newArry.concat(obj);
        }
        if (this.symbol == "shib") {
          newArry = newArry.slice(-4);
        }
        let dataObj = {
          arry: newArry,
          numberLength: numberLength,
        };
        this.selectDataArry = dataObj.arry;
        if (numberLength >= 2) {
          this.dataArrTitle = this.selectDataArry[0].name;
        } else {
          this.dataArrTitle = this.selectDataArry[this.selectDataArry.length - 1].name;
        }
      });
    },
    continueTobuy(detailData) {
      this.show = false
      setTimeout(() => {
        this.$router.push(`/cryptos/trendDetails/${detailData.symbol}?direction=${detailData.direction}&type=${this.queryType}`)
      }, 300);
    },
    onQuickPrice(price) { // 点击金额变化
      if (this.type === '2') {
        this.form.price = price
      }
    },
    clearTimeout() {
      clearTimeout(this.timeout)
      this.timeout = null
    },
    handleTimeEnd(order) {
      if (this.selectIndex / 1 === 2) { // 交割合约需要弹出详情框
        _futrueOrderDetail(order).then(data => {
          this.clearTimeout()
          this.detailData = data
          if (data.state !== 'created') {
            this.timeout = setTimeout(() => {
              this.handleTimeEnd(order)
            }, 1000)
          }
        })
      }
    },
    handleInitSliderOption(amount) {
      if (!amount) {
        // 金额是否需要变动
        this.form.amount = "";
      }
      console.log(this.initOpen.volume, this.form.lever_rate);
      let vol;
      vol = this.initOpen.volume / 1;
      this.options.max = Math.floor(vol);
      console.log("this.options.max", this.options.max);
      if (this.options.max > 0) {
        this.options.disabled = false;
      } else {
        this.options.disabled = true;
      }
    },
    handleChoose(item) {
      this.showOptions = !this.showOptions
      this.form.lever_rate = item.lever_rate
      console.log('handleChoose')
      this.handleInitSliderOption()
    },
    onAdd() { // +
      if (this.options.max === 0) {
        return
      }
      if (this.form.amount === this.options.max) {
        return;
      }
      this.form.amount++
    },
    onReduce() { // -
      if (this.form.amount > 1) {
        this.form.amount--
      }
    },
    onParaId(evt) { // 交割日期
      this.form.para_id = evt.id
      this.paraIndex = evt.index
      this.form.price = ''
      this.form.amount = ''
    },
    onInput() { // 数量变化
      // console.log(1111111, this.form.amount, this.selectIndex)
      if (this.selectIndex == 1 && this.options.max == 0) {
        this.form.amount = this.form.amount / 1
      } else if (this.selectIndex == 1 && this.form.amount / 1 > this.options.max / 1) {
        this.form.amount = this.options.max / 1
      }
    },
    //价格类型下拉框切换
    selectBtn() {
      this.isShow = !this.isShow;
    },
    //选择价格类型
    selectItem(item) {
      if (item.type == 1) {
        this.form.price = this.price
      }
      this.form.price_type = item.type === '1' ? 'opponent' : 'limit'
      this.title = item.title;
      this.type = item.type;
      this.form.price = this.price
      this.isShow = false;
    },
    //选择开仓类型
    changeTab(type) {
      // 开仓和
      console.log("changeTab", type);

      if (this.currentType === type) {
        return;
      }
      this.$emit("changeCurrentType", type);
      // this.currentType = type;

      this.$refs.sliderRef.emptyValue()
      this.handleInitSliderOption();
    },
    onClose() { // 关闭
      this.show = false
      setTimeout(() => {
        this.popType = 'confirm'
      }, 500)
    },
    // onOrderConfirm() { /// 交割合约确认
    //   this.show = false
    //   this.popType = 'counting'
    //   this.order(this.form.direction === 'buy' ? 'open' : 'close')
    // },
    throttleFn: throttle(function (type) {
      this.order(type)
    }, 500),
    order(type) {
      if (!this.userInfo.token) {
        this.$router.push('/login')
        return false;
      }
      if (this.selectIndex == 1) {
        if (!this.form.price) {
          showToast(this.$t('请输入金额'))
          return
        }
      }
      if (!this.form.amount) {
        if (this.selectIndex == 1) {
          showToast(this.$t('请输入合约张数'))
        } else {
          showToast(this.$t('请输入金额'))
        }
        return
      }


      // this.form.direction = type
      this.form.symbol = this.$route.params.symbol
      // this.form.direction = type === 'open' ? 'buy' : 'sell'
      if (type == 'long' || type == 'open') {
        this.form.direction = 'buy'
      } else {
        this.form.direction = 'sell'
      }
      let _order = null // api
      let emitFunc = null // 触发函数
      if (this.selectIndex / 1 === 1) { // 永续合约
        // 永续合约
        this.form.session_token = this.initOpen.session_token
        _order = _orderOpen
        emitFunc = this.currentType
        this.openOrder(_order, emitFunc);
      } else { // 交割合约
        this.form.session_token = this.initFutrue.session_token
        _order = _futrueOrder
        emitFunc = 'futrue'

        if (this.initFutrue.session_token == undefined || this.initFutrue.session_token == '') {
          _futrueOrderInit(this.symbol).then(data => {
            this.form.session_token = data.session_token;
            this.openOrder(_order, emitFunc);
          }).catch((err) => {
            if (err.code == 'ECONNABORTED') {
              showToast(this.$t('网络超时！'));
            } else if (err.msg != undefined) {
              showToast(this.$t(err.msg));
            }
          });
        } else {
          this.form.session_token = this.initFutrue.session_token;
          this.openOrder(_order, emitFunc);
        }
      }
    },
    openOrder(_order, emitFunc) {
      _order(this.form).then((res) => {
        showToast(this.$t('操作成功'))
        if (this.selectIndex / 1 === 1) {
          this.$refs.sliderRef.emptyValue()
        }
        this.$emit('ordered', emitFunc)
        _getBalance().then(data => { // 刷新余额
          this.$store.commit('user/SET_USERINFO', { balance: data.money })
        })

        if (this.selectIndex / 1 === 2) { // 交割合约需要弹出详情框
          _futrueOrderDetail(res.order_no).then(data => {
            this.detailData = data
            this.show = true
          })
        }

      }).catch(() => { // 也需要重新初始化
        this.$emit('ordered', emitFunc)
        if (this.selectIndex / 1 === 1) {
          this.$refs.sliderRef.emptyValue()
        }
      })
    },
    //数字排序
    orderListAsc(filed, type = "asc") {
      return (a, b) => {
        if (type == "asc") return parseFloat(a[filed]) > parseFloat(b[filed]) ? 1 : -1;
        return parseFloat(a[filed]) > parseFloat(b[filed]) ? -1 : 1;
      };
    }
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  .perpetual-open {
    font-size: 26px;

    .full-input {
      width: 100%;
    }

    .half-input {
      width: 100%;
    }
  }

  .select-bet-box {
    top: 4.5rem;
  }

  .options {
    box-shadow: 0px 0px 7px rgba(0, 0, 0, 0.3);
    border-radius: 6px;

    div {
      font-size: 22px;
      text-align: center;
      width: 140px;
      height: 50px;
      line-height: 50px;

      &.option-active {
        background-color: $tab_background;
      }
    }
  }

  .right-word {
    top: 50%;
    transform: translateY(-50%);
    right: 19px;
    position: absolute;
    border-radius: 6px;
  }

  .vue-slider {
    height: 3px !important;
    padding: 0 20px !important;
  }

  .vue-slider-dot {
    width: 32px !important;
    height: 32px !important;

  }

  .vue-slider-mark-label {
    font-size: 16px;
    margin-top: 14px !important;
  }

  .vue-slider-mark-label-active {
    color: $text_color1 !important;
  }

  .vue-slider-dot-handle {
    border: 3px solid $btn_main;
  }

  .vue-slider-mark {
    width: 20px !important;
    height: 20px !important;

    &:nth-child(1) {
      .vue-slider-mark-label {
        transform: translateX(-14px) !important;
      }
    }

    &:nth-child(5) {
      .vue-slider-mark-label {
        transform: translateX(-60px) !important;
      }
    }
  }

  // 开仓样式
  .tabcon {
    // padding-bottom: 16%;
    position: relative;
    height: 80px;
  }

  .tab {
    position: absolute;
    left: 0px;
    right: 0px;
    top: 0px;
    bottom: 0px;
    display: flex;
    align-items: center;
    background-color: $border-grey;
    border-radius: 5px;
    overflow: hidden;
  }

  .tab>* {
    height: 100%;
  }

  .tab>img {
    margin-left: -2px;
    margin-right: -2px;
  }

  .tab>a {
    flex-grow: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
  }

  .open {
    background-color: $green;
    color: white;
  }

  // 下拉
  .option-box {
    position: absolute;
    left: 0;
    right: 0;
    top: 90px;
    width: 100%;
    background-color: $grey_bg;
    text-align: center;
    box-shadow: 0px 0px 3px 3px $grey_bg;
    border-radius: 4px;
    color: $text-color;
    z-index: 10;

  }

  .option-box>div {
    padding: 10px 0;

  }

  .option-box>div:hover {
    background-color: $grey_bg;
  }

  .num-text-color {
    color: #4C4A54;
  }


  .with100 {
    width: 100%;
  }

  .height100 {
    height: 100%;
  }

  .long {
    background-color: $green;
    background: url(@/assets/image/public/open-bg.png) no-repeat right center;
    background-size: 100% 100%;
    color: white;
  }

  .short {
    background-color: $green;
    background: url(@/assets/image/public/close-bg.png) no-repeat left center;
    background-size: 100% 100%;
    color: white;
  }

  .van-action-sheet__content {
    background: transparent;

    .van-action-sheet__item {
      background: transparent !important;
      color: $text_color;
    }
  }

  .van-action-sheet__gap {
    background: transparent;
  }

  .van-action-sheet__cancel {
    background: transparent;
  }
}

.k-select-box {
  justify-content: flex-end;
  // padding-right: 40px;
  align-items: center;

  .select-box {
    width: 220px;

    background: $recommend_bg;

    margin-right: 20px;
  }
}

.deep-div {
  min-height: 370px;
}
</style>
