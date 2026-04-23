<template>
  <!-- 提现历史 -->
  <div class="pl-30 pr-30 wrap">
    <div class="text-center recharge-text text-30">{{ $t('数字货币') }}</div>
    <!--        <div class="flex justify-between mb82" v-for="(item,index) in list" :key="index" @click="onDetail(item)">-->
    <!--            <div>-->
    <!--                <div class="text-36">{{ item.coin }}</div>-->
    <!--                <div class="text-grey text-30 mt20">{{ item.createTime }}</div>-->
    <!--            </div>-->
    <!--            <div>-->
    <!--                <div class="text-36 text-right">{{ item.amount }}</div>-->
    <!--                <div class="mt20">-->
    <!--                    <div class="flex justify-end" v-if="item.status==0">-->
    <!--                        <div class="common-round yellow-round"></div>-->
    <!--                        <div class="text-grey text-30">{{ $t('审核中') }}</div>-->
    <!--                    </div>-->
    <!--                    <div class="flex" v-if="item.status==1">-->
    <!--                        <div class="common-round green-round"></div>-->
    <!--                        <div class="text-grey text-30">{{ $t('成功') }}</div>-->
    <!--                    </div>-->
    <!--                    <div class="flex" v-if="item.status==2">-->
    <!--                        <div class="common-round red-round"></div>-->
    <!--                        <div class="text-grey text-30">{{ $t('失败') }}</div>-->
    <!--                    </div>-->
    <!--                </div>-->
    <!--            </div>-->
    <!--        </div>-->
    <van-pull-refresh v-model="isLoading" @refresh="onRefresh" :pulling-text="$t('下拉即可刷新')" :loosing-text="$t('释放即可刷新')"
      :loading-text="$t('加载中')">
      <div>
        <div v-if='noData' class="textColor">
          {{ $t('暂无数据') }}
        </div>
        <template v-else>
          <van-list v-model:loading="loading" :finished="finished" :finished-text="$t('已经全部加载完毕')" :offset="130"
            @load="onLoad" :loading-text="$t('加载中...')">
            <div class="flex justify-between mb-10" v-for="(item, index) in list" :key="index" @click="onDetail(item)">
              <div>
                <div class="text-36 textColor">{{ item.coin }}</div>
                <div class="text-grey text-30 mt20">{{ item.createTime }}</div>
              </div>
              <div>
                <div class="text-36 text-right textColor">{{ item.amount }}</div>
                <div class="mt20">
                  <div class="flex justify-end" v-if="item.status == 0">
                    <div class="common-round yellow-round"></div>
                    <div class="text-grey text-30">{{ $t('审核中') }}</div>
                  </div>
                  <div class="flex justify-end" v-if="item.status == 1">
                    <div class="common-round green-round"></div>
                    <div class="text-grey text-30">{{ $t('成功') }}</div>
                  </div>
                  <div class="flex" v-if="item.status == 2">
                    <div class="common-round red-round"></div>
                    <div class="text-grey text-30">{{ $t('失败') }}</div>
                  </div>
                </div>
              </div>
            </div>
          </van-list>
        </template>
      </div>
    </van-pull-refresh>
  </div>
</template>

<script>
import { _withdrawList } from "@/service/withdraw.api.js"
import { List, PullRefresh } from 'vant';
export default {
  name: "withdrawHistory",
  components: {
    [List.name]: List,
    [PullRefresh.name]: PullRefresh
  },
  data() {
    return {
      list: [],
      page: 1,
      loading: false, // 当loading为true时，转圈圈
      finished: false,  // 数据是否请求结束，结束会先显示'已经全部加载完毕'
      noData: false,// 如果没有数据，显示暂无数据
      isLoading: false,   // 下拉的加载图案
    }
  },
  mounted() {
    this.page = 1
    this.list = []
    // this.getList();
  },
  methods: {
    onDetail(item) {
      this.$router.push('/cryptos/withdraw/withdrawDetail?order_no=' + item.order_no)
    },
    getList(isInit) {
      _withdrawList({
        page_no: this.page
      }).then((res) => {
        console.log(res.code)
        this.isLoading = false

        this.loading = false;
        this.list = this.list.concat(res);
        console.log(this.list, 2222)
        // 如果没有数据，显示暂无数据
        if (this.list.length === 0 && this.page === 1) {
          this.noData = true
        }
        // 如果加载完毕，显示没有更多了
        if (res.length < 8) {
          console.log('sdjajajd')
          this.finished = true
        }
        this.page++;

      });
    },
    onLoad() {
      setTimeout(() => {
        this.getList();
      }, 500)
    },
    onRefresh() {
      this.list = []
      this.page = 1
      this.loading = true
      this.finished = false
      this.noData = false
      this.onLoad()
    }
  }
}
</script>

<style lang="scss" scoped>
.wrap {
  font-size: 30px;
}

@import "./history.scss";
</style>
