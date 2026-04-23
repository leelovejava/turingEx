<template>
  <div class="ioe-swiper h-330 ">
    <van-swipe ref="swipe" :autoplay="3000" :show-indicators="false" lazy-render>
      <van-swipe-item v-for="(item, index) in bannerList" :key="index + language" class="w-full h-330">
        <img :src="item.image" alt="" title="" @click="toPath(item.url)" class="h-330 border bannerimg w-full" />
        <!-- <div class="w-full h-420 absolute top-1 flex flex-col text-white"
             :class="item.subTitle ? 'pl-60' : 'items-center'">
          <template v-if="$i18n.locale === 'CN' || $i18n.locale === 'zh-CN'">
            <p :class="item.subTitle ? 'font-60 mt-124 mb-16' : 'text-48  mt-140 mb-54'">{{item.title}}</p>
          </template>
          <template v-else>
            <p :class="item.subTitle ? 'text-44 mt-124 mb-16' : 'text-48  mt-140 mb-54'">{{item.title}}</p>
          </template>
          <p v-show="item.subTitle" class="text-26 mb-50">{{item.subTitle}}</p>
          <p class="text-36 btn w-222 h-65 btnMain flex justify-center items-center">{{item.btnText}}</p>
        </div> -->
      </van-swipe-item>
    </van-swipe>
  </div>
</template>

<script>
import { Swipe, SwipeItem, showToast } from 'vant';
import { mapGetters } from 'vuex';
import { _getBanner } from '@/service/user.api'
export default {
  name: 'IndexSwiper',
  components: {
    [Swipe.name]: Swipe,
    [SwipeItem.name]: SwipeItem
  },
  props: {
    keyNum: {
      type: Number,
      default: 1
    },
    type: {
      type: String,
      default: 'home'
    }
  },
  data() {
    return {
      bannerList: [],
      timer: null
    }
  },
  computed: {
    ...mapGetters('language', ['language']),
    imgList() {
      return this.type === 'home' ? [
        { id: 1, img: 'swiper_item', title: this.$t('參與挖礦贏大獎'), subTitle: this.$t('加入我們，收益超乎你的想像！'), btnText: this.$t('立即加入') },
        { id: 2, img: 'swiper_item', title: this.$t('收益更簡單，免費領空投！'), subTitle: '', btnText: this.$t('立即加入') },
        // { id: 3, img: 'swiper_item', title: this.$t('收益更簡單，免費領空投！'), subTitle: '', btnText: this.$t('立即加入') }
      ] : [
        { id: 3, img: 'swiper_item' },
      ]
    },
    language() {
      this.getBanner();
      return this.$i18n.locale;
    }
  },
  mounted() {
    this.getBanner();
  },
  activated() {
    this.getBanner()
    this.timer = setTimeout(() => {
      this.$refs.swipe.resize()
    }, 300);
  },
  deactivated() {
    clearTimeout(this.timer)
  },
  methods: {
    toPath(url) {
      if (url) {
        this.$router.push(url)
      }
    },
    getBanner() {
      let language
      if (this.$i18n.locale === 'en-US') {
        language = 'en'
      } else if (this.$i18n.locale.indexOf('CN') >= 0) {
        language = 'CN'
      } else {
        language = this.$i18n.locale
      }
      _getBanner({
        model: 'top',
        language
      }).then((res) => {
        this.bannerList = res
      }).catch((error) => {
        if (error.code === 'ECONNABORTED') { showToast(this.$t('网络超时！')); }
        else if (error.msg !== undefined) { showToast(this.$t(error.msg)); }
      });
    }
  },
}
</script>
<style lang="scss" scoped>
#cryptos {
  .ioe-swiper {
    box-sizing: border-box;
  }

  .btn {
    border-radius: 55px;
  }

  .bannerimg {
    border-radius: 30px !important;
    border: none !important;
  }

  .h-330 {
    height: 340px;
  }
}
</style>
