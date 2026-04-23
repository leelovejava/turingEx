<template>
  <div id="cryptos">
    <div class="text-26 promote">
      <assets-head :title="$t('推荐')" />
      <div class="advise w-full h-56 px-10 py-10 box-border flex flex-col items-start justify-center"
        :class="{ 'h-260 ': $i18n.locale !== 'CN' }">
        <span v-if="$i18n.locale === 'CN'" class="font-semibold text-50" style="color:#273549;">{{ $t('邀请好友，一起赚币')
        }}</span>
        <span v-else class="font-bold text-50" style="color:#273549;">{{ $t('邀请好友，一起赚币') }}</span>
        <span class="mt-5 text-40 font-bold" style="color:#273549">{{ $t('马上充值即刻获取佣金') }}</span>
      </div>
      <div class="performance-title">
        <div class="performance-title-1 flex-col flex items-start"
          :class="{ 'performance-title-1-1': $i18n.locale !== 'CN' }">
          {{ $t('我的总推广') }}
          <div class="performance-text text-left">{{ promote_list.total && promote_list.total.children }}</div>
        </div>
        <div class="performance-title-2  flex-col flex items-center"
          :class="{ 'performance-title-1-1': $i18n.locale !== 'CN' }">
          {{ $t('总充值') }}
          <div class="performance-text2 text-left">{{ promote_list.recharge_sum ? (promote_list.recharge_sum *
            1).toFixed(2)
            :
            '0.00' }}</div>
        </div>
        <div class="flex items-center w-40 justify-end colorMain" style="align-self: start">
          <div class="performance-title-3" @click="$router.push('/promote/rules')">{{ $t('规则') }}</div>

          <img class="performance-title-4" src="../../../assets/image/promote/right.png" />
        </div>
      </div>
      <div class="numberOfPeople">
        <div class="numberOfPeople-text flex flex-col align-center">
          {{ $t('一代人数') }}
          <div class="mt-20 numberOfPeople-text4 text-center">{{ promote_list.total && promote_list.total.level_1
          }}</div>
        </div>
        <div class="numberOfPeople-text2 flex flex-col align-center">
          {{ $t('二代人数') }}
          <div class="mt-20 numberOfPeople-text5 flex-1 text-center ">{{ promote_list.total &&
            promote_list.total.level_2
          }}</div>
        </div>
        <!--      <div style="flex: 1;"></div>-->
        <div class="numberOfPeople-text3 flex flex-col align-center">
          {{ $t('三代人数') }}
          <div class="mt-20 numberOfPeople-text6  flex-1 text-center ">{{ promote_list.total &&
            promote_list.total.level_3
          }}</div>
        </div>
      </div>
      <div class="Recommend">
        <div class="Recommend-text btnMain text-white" @click="ShareQRCode_push">{{ $t('立即推广') }}</div>
      </div>
      <div class="segmentation-max"></div>
      <div class="MyRecommendation-title">{{ $t('我的推广') }}</div>
      <div style="width: 100%;">
        <van-tabs ref="tabs" v-model:active="active" sticky animated @change="onChange">
          <van-tab :title="item.title" v-for="item in selectData" :key="item.title" :name="item.type">
            <div class="Recommendation-tab-title">
              <div class="Recommendation-tab-title1">{{ $t('用户名') }}</div>
              <div class="Recommendation-tab-title1">{{ $t('总人数') }}</div>
              <div class="Recommendation-tab-title1">{{ $t('总充值') }}</div>
            </div>
            <van-list v-model:loading="loading" :finished="finished" :finished-text="$t('没有更多了')" @load="onLoad"
              :loading-text="$t('加载中...')">
              <div v-for="(item, index) in promote_list_data" :key="index" class="Recommendation-tab">
                <div class="Recommendation-tab1">{{ item.username }}</div>
                <div class="Recommendation-tab1">{{ item.reco_sum }}</div>
                <div class="Recommendation-tab1">{{ (item.recharge_sum * 1).toFixed(2) }}</div>
              </div>
            </van-list>
          </van-tab>
        </van-tabs>
      </div>
    </div>
  </div>
</template>
<script>
import { Tab, Tabs } from 'vant';
import { _promote } from '@/service/promote.js'
import { List } from 'vant';
import assetsHead from "@/components/Transform/assets-head/index.vue";
export default {
  data() {
    return {
      active: '1',
      level: 1,
      page_no: 1,
      promote_list: [],
      promote_list_data: [],
      list: [],
      loading: false,
      finished: false,
      pageSize: 10,
      selectData: [
        { title: this.$t('一代'), type: '1' },
        { title: this.$t('二代'), type: '2' },
        { title: this.$t('三代'), type: '3' },
      ]
    }
  },
  components: {
    assetsHead,
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    [List.name]: List,
  },
  created() {
    // this.init()
  },
  methods: {
    handleImage(url) {
      return new URL(url, import.meta.url).href
    },
    init() {
      this.promote_data()
    },
    onLoad() {
      // 异步更新数据
      this.promote_data()
    },
    promote_data() {
      const t = this
      this.loading = true;
      _promote({ level: t.level, page_no: t.page_no }).then((res) => {
        console.log(111, res)
        t.promote_list = res
        this.promote_list_data = [...this.promote_list_data, ...res.list]
        // console.log(logs)
        this.loading = false
        if (res.list.length < this.pageSize) {
          this.finished = true
        } else {
          this.page_no++
        }
        this.$refs.tabs && this.$refs.tabs.resize();
      })
    },
    onClickLeft() {
      this.$router.push('/')
    },
    ShareQRCode_push() {
      this.$router.push('/ShareQRCode')
    },
    onChange(e) {
      this.promote_list_data = []
      this.finished = false
      this.page_no = 1
      this.level = e
      this.loading = true;
      if (this.loading) {
        this.promote_data()
      }
    }
  },
}
</script>
<style lang="scss" scoped>
#cryptos {
  .promote {
    width: 100%;
    box-sizing: border-box;
  }

  .advise {
    background: url("./invite-bg.png") no-repeat center center;
    background-size: 100% 100%;
  }

  .banner {
    width: 100%;
  }

  .performance-title {
    width: 100%;
    display: flex;
    padding-right: 35px;
    padding-left: 35px;
    margin-top: 40px;
    box-sizing: border-box;
    align-items: center;
    justify-content: center;
  }

  .performance-title-1 {
    width: 200px;
    font-style: normal;
    font-weight: 400;
    font-size: 26px;
    color: $text_color5;

    &.performance-title-1-1 {
      width: 250px;
      font-size: 26px;
      text-align: center;
      margin-right: 10px;
    }
  }

  .performance-title-2 {
    flex: 1;
    font-style: normal;
    font-weight: 400;
    font-size: 26px;
    color: $text_color5;

    &.performance-title-1-1 {
      font-size: 26px;
      text-align: left;
    }
  }

  .performance-title-3 {
    font-style: normal;
    font-weight: 400;
    font-size: 26px;
  }

  .performance-title-4 {
    width: 35px;
  }

  .performance {
    width: 100%;
    display: flex;
    padding-right: 35px;
    padding-left: 35px;
    margin-top: 25px;
    box-sizing: border-box;
    align-items: center;
    justify-content: center;
  }

  .performance-text {
    width: 250px;
    font-style: normal;
    font-weight: 600;
    font-size: 66px;
    color: $green;
    margin-top: 22px;
  }

  .performance-text2 {
    flex: 1;
    font-style: normal;
    font-weight: 600;
    font-size: 66px;
    color: $green;
    margin-top: 22px;
  }

  .numberOfPeople {
    width: 100%;
    display: flex;
    padding-right: 35px;
    padding-left: 35px;
    margin-top: 45px;
    box-sizing: border-box;
    align-items: center;
    justify-content: space-between;
  }

  .numberOfPeople-text,
  .numberOfPeople-text2,
  .numberOfPeople-text3 {
    font-style: normal;
    font-weight: 400;
    text-align: center;
    font-size: 26px;
    color: $text_color5;
  }

  .numberOfPeople2 {
    width: 100%;
    display: flex;
    padding-right: 35px;
    padding-left: 35px;
    margin-top: 20px;
    box-sizing: border-box;
    align-items: center;
    justify-content: center;
  }

  .numberOfPeople-text4,
  .numberOfPeople-text5,
  .numberOfPeople-text6 {
    font-style: normal;
    font-weight: 700;
    text-align: center;
    font-size: 40px;

    // @include themify() {
    //   color: themed("text_color");
    // }
    color: "text_color";
  }


  .Recommend {
    padding-right: 35px;
    padding-left: 35px;
    margin-top: 45px;
    height: 100px;
    box-sizing: border-box;
  }

  .Recommend-text {
    //background: #FCD436;
    border-radius: 4px;
    line-height: 100px;
    font-weight: 400;
    font-size: 32px;
    text-align: center;
    border-radius: 8px;
    //color: #333333;
  }

  .segmentation-max {
    margin-top: 45px;
    width: 100%;
    height: 20px;

    // @include themify() {
    //   background: themed("bg_dark");
    // }
    background: "bg_dark";
  }

  .MyRecommendation-title {
    padding: 32px 35px 38px;
    font-style: normal;
    font-weight: 400;
    font-size: 32px;
    text-align: left;

    color: "text_color";
  }

  .Recommendation-tab-title {
    margin-left: 35px;
    margin-right: 35px;
    height: 85px;

    background: "tab_background";
    display: flex;
    margin-top: 35px;
  }

  .Recommendation-tab-title1 {
    flex: 1;
    font-style: normal;
    font-weight: 400;
    font-size: 30px;
    line-height: 85px;
    text-align: center;
    color: $text_color1;
    ;
  }

  .Recommendation-tab {
    margin-left: 35px;
    margin-right: 35px;
    height: 85px;
    display: flex;
    margin-top: 35px;
  }

  .Recommendation-tab1 {
    flex: 1;
    font-style: normal;
    font-weight: 400;
    font-size: 34px;
    line-height: 85px;
    text-align: center;
    color: $text_color2;
  }

}
</style>
