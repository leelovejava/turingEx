<template>
  <div id="c2cOrderListPage">
    <div class="flex flex-col w-full h-full c2c-order-list">
      <div>
        <order-nav class="w-full" ref="nav">
          <template #right>
            <img class="w-6 h-7" src="@/assets/image/c2c/Vector27.png" alt="" @click="selectShow = true">
          </template>
          <template #title>
            {{ $t('订单列表') }}
          </template>
        </order-nav>
      </div>
      <div class="flex-1 w-full overflow-auto">
        <div v-if="isEmpty === 'true'" class="flex justify-center mt-44">
          <div>
            <img class="w-36 h-36" src="@/assets/image/c2c/Group323.png" alt="">
            <p class="mt-9 text-28 text-center text-grey">{{ $t('暂无订单') }}</p>
          </div>
        </div>
        <div v-else>
          <van-pull-refresh :pulling-text="$t('下拉即可刷新')" :loosing-text="$t('释放即可刷新')" :loading-text="$t('加载中')"
            v-model="isLoading" @refresh="onRefresh">
            <van-list v-model="loading" :finished="finished" :immediate-check="false" :finished-text="$t('没有更多了')"
              @load="onLoad">
              <template v-if="list.length > 0">
                <items v-for="(item, index) in list" :items="item" :key="index" />
              </template>
              <div v-else class="w-full h-full">
                <van-empty class="custom-image" :description="$t('暂无数据')">
                  <template #image>
                    <img class="no-data-img" src="@/assets/image/otc/nodatas.png" />
                  </template>
                </van-empty>
              </div>
            </van-list>
          </van-pull-refresh>
          <!--        <div class="w-full mt-76 mb-52 text-28 text-grey text-center">已经全部加载完毕</div>-->
        </div>
      </div>
      <!--  显示筛选  -->
      <div class="select">
        <van-popup v-model:show="selectShow" position="bottom" :style="fulHeight">
          <order-nav>
            <template #left><i></i></template>
            <template #title>
              <span class="text-36 font-bold">{{ $t('订单历史筛选') }}</span>
            </template>
            <template #right>
              <van-icon @click.native="selectShow = false" name="cross" color="#9399A4" />
            </template>
          </order-nav>
          <div class="px-9">
            <div class="">
              <p class="mb-10 text-28 text-grey">{{ $t('交易类型') }}</p>
              <div>
                <select-item :list="selectList.title" :value="currentDirection" @select='handleSelect' />
              </div>
            </div>
            <div class="mt-14">
              <p class="mb-10 text-28 text-grey">{{ $t('订单状态') }}</p>
              <div>
                <select-item :list="selectList.items" :value="currentState" @select='handleSelect2' />
              </div>
            </div>
            <div class="absolute right-9 left-9 bottom-10 flex">
              <van-button @click.native="reset" class="flex-1 text-32 rounded-lg" type="info" color="#EAEBEE"
                style="margin-right: 10px;">
                <span style="color: #21262F;">{{ $t('重置') }}</span>
              </van-button>
              <van-button @click.native="selectEnter" class="flex-1 text-32 rounded-lg" type="info" color="#1D91FF">
                {{ $t('确认') }}
              </van-button>
            </div>
          </div>
        </van-popup>
      </div>

      <!--  未读消息  -->
      <!--    <van-popup class="w-full h-full" v-model="showUnread" position="right">-->
      <!--      <unread @back="hiddenUnread" :data="unreadData"/>-->
      <!--    </van-popup>-->
    </div>
  </div>
</template>

<script>
import {
  Popup,
  Icon,
  PullRefresh,
  Empty,
  Button,
  List,
  showLoadingToast,
  closeToast
} from "vant";
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import Items from "../orderList/items.vue";
import SelectItem from "../c2c-order-list/SelectItem.vue";
import Unread from "../c2c-order-list/Unread.vue";
import listLoadMixins from '@/utils/list-load-mixins'
import { ctcOrderList } from "@/service/otc.api";

export default {
  name: "c2cOrderList",
  props: ['isEmpty'],
  mixins: [listLoadMixins],
  data() {
    return {
      currentDirection: '全部',
      currentState: '全部',
      form: {
        direction: '',
        state: '', // 0未付款/1已付款/2申诉中/3已完成/4已取消/5已超时
      },
      navHeight: 0,
      showUnread: false,
      selectShow: false,
      titleActive: '全部',
      itemActive: '全部',
      selectList: {
        title: [{
          title: this.$t('全部'),
          value: '',
        }, {
          title: this.$t('购买'),
          value: 'buy',
        }, {
          title: this.$t('出售'),
          value: 'sell',
        }],
        items: [
          {
            title: this.$t('全部'),
            value: '',
          },
          {
            title: this.$t('未付款'),
            value: 0,
          },
          {
            title: this.$t('已付款'),
            value: 1,
          },
          {
            title: this.$t('申诉中'),
            value: 2,
          },
          {
            title: this.$t('已完成'),
            value: 3,
          },
          {
            title: this.$t('已取消'),
            value: 4,
          },
          {
            title: this.$t('已超时'),
            value: 5,
          },
        ],
      },
    }
  },
  created() {
    this.get()
  },
  mounted() {
    // let obj =  this.selectList.title.find((item) => {
    //   return item.value === 'buy'
    // })
    this.navHeight = this.$refs.nav.$el.getBoundingClientRect().height + 100;
  },
  methods: {
    handleImage(url) {
      return new URL(url, import.meta.url).href
    },
    get() { // 获取数据的方法需要自定义
      showLoadingToast({
        duration: 0,
        forbidClick: true,
        message: this.$t('加载中')
      })
      ctcOrderList({ ...this.form }).then(res => {
        closeToast();
        // res.data.forEach((item) => {
        //   item.time = '01-03 17：20：20'
        // })
        this.handleData(res || [])
      })
    },
    handleSelect(data) {
      this.form.direction = data.value
      this.currentDirection = data.title
      // this.get()
    },
    handleSelect2(data) {
      this.form.state = data.value
      this.currentState = data.title

    },
    hiddenUnread() {
      this.showUnread = false;
    },
    // 筛选确定
    selectEnter() {
      this.selectShow = false
      this.onRefresh()
    },
    // 重置选择
    reset() {
      this.currentDirection = '全部'
      this.currentState = '全部'
      this.form.direction = ''
      this.form.state = ''
    },
  },
  computed: {
    fulHeight() {
      return {
        height: `calc(100vh - ${this.navHeight}px)`
      }
    },
    // unreadData() {
    //   const arr = this.formatData([...this.sell, ...this.buy])
    //   return arr.filter(v => v.msg !== undefined)
    // }
  },
  components: {
    [Popup.name]: Popup,
    [Icon.name]: Icon,
    [Button.name]: Button,
    [List.name]: List,
    [PullRefresh.name]: PullRefresh,
    [Empty.name]: Empty,
    OrderNav,
    Items,
    SelectItem,
    Unread,
  }
}
</script>

<style lang="scss" scoped>
#c2cOrderListPage {
  font-size: 30px;

  .select {
    height: 100%;

    :deep(.van-popup) {
      border-radius: 20px 20px 0 0;
    }
  }

  :deep(.van-empty__image) {
    width: 180px;
    height: 180px;
  }
}
</style>
