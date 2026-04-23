<template>
  <div class="w-full">
    <!-- 1.头部 -->
    <div class="banner">
      <div class="banner-top">
        <img src="@/assets/images/c2c/want-buy/banner-1.png" />
        <div class="banner-title">
          <p class="title">{{ TITLE }} C2C: {{ $t("message.c2c.tip1") }}</p>
          <p class="text">
            {{ TITLE }} {{ $t("message.c2c.tip2") }}{{ TITLE
            }}{{ $t("message.c2c.tip3") }}
          </p>
        </div>
      </div>
      <!-- 菜单切换 -->
      <slot name="menu"></slot>
    </div>
    <!-- 2.核心区域 -->
    <slot name="content"></slot>
    <!-- 3  C2C 快捷买币 -->
    <div class="center-w1200 relative mt-110 pb-68">
      <div>
        <h2 class="font-40 font-700">
          C2C {{ $t("message.c2c.kuaijiemaibi") }}
        </h2>
        <div class="flex items-center mt-32 font-13 font-500">
          <p
            class="py-8 px-16 mr-27 rounded-xl cursor-pointer"
            :class="buyBi ? 'bg-blue text-white' : ''"
            @click="handleChangeFastTab(true)"
          >
            C2C{{ $t("message.c2c.kuaijiemaibi") }}
          </p>
          <p
            class="py-8 px-16 rounded-xl cursor-pointer"
            :class="[!buyBi ? 'bg-blue text-white' : '']"
            @click="handleChangeFastTab(false)"
          >
            {{ $t("message.c2c.shushoushuzihuobi") }}
          </p>
        </div>
      </div>
      <div v-if="buyBi" class="flex justify-between items-center w-full mt-31">
        <div class="max-w-288">
          <div>
            <!-- <img
              class="w-56 h-56"
              src="@/assets/images/c2c/want-buy/Group1516.png"
              alt=""
            /> -->
            <el-image  class="w-56 h-56" :src="getImages('c2c/want-buy/Group1516.png')" lazy loading="lazy" />
          </div>
          <div class="mt-25 font-16 font-900">
            {{ $t("message.c2c.xiadingdan") }}
          </div>
          <div class="mt-14 font-12 font-500 color-727">
            。
            {{ $t("message.c2c.tip10") }}
          </div>
        </div>
        <el-image  class="w-28 h-28 want-img" :src="getImages('c2c/want-buy/Group1519.png')" lazy loading="lazy" />
        <div class="max-w-288">
          <div>
            <el-image class="w-56 h-56" :src="getImages('c2c/want-buy/Group1517.png')" lazy loading="lazy" />
          </div>
          <div class="mt-25 font-16 font-900">
            {{ $t("message.c2c.xiangmaijiafukuan") }}
          </div>
          <div class="mt-14 font-12 font-500 color-727">
            {{ $t("message.c2c.tip11") }}
          </div>
        </div>
        <el-image  class="w-28 h-28 want-img" :src="getImages('c2c/want-buy/Group1519.png')" lazy loading="lazy" />
        <div class="max-w-288">
          <div>
            <el-image class="w-56 h-56" :src="getImages('c2c/want-buy/Group1518.png')" lazy loading="lazy" />
          </div>
          <div class="mt-25 font-16 font-900">
            {{ $t("message.c2c.tip12") }}
          </div>
          <div class="mt-14 font-12 font-500 color-727">
            {{ $t("message.c2c.tip13") }}
          </div>
        </div>
      </div>
      <div v-else class="flex justify-between items-center w-full mt-31">
        <div class="max-w-288">
          <div>
            <el-image class="w-56 h-56" :src="getImages('c2c/want-buy/Group1516.png')" lazy loading="lazy" />
          </div>
          <div class="mt-25 font-16 font-900">
            {{ $t("message.c2c.xiadingdan") }}
          </div>
          <div class="mt-14 font-12 font-500 color-727">
            {{ $t("message.c2c.tip10") }}
          </div>
        </div>
        <el-image class="w-28 h-28 want-img" :src="getImages('c2c/want-buy/Group1519.png')" lazy loading="lazy" />
        <div class="max-w-288">
          <div>
             <el-image class="w-56 h-56" :src="getImages('c2c/fast-buy/Group1525.png')" lazy loading="lazy" />
          </div>
          <div class="mt-25 font-16 font-900">
            {{ $t("message.c2c.xiangmaijiafukuan") }}
          </div>
          <div class="mt-14 font-12 font-500 color-727">
            {{ $t("message.c2c.tip11") }}
          </div>
        </div>
        <el-image class="w-28 h-28 want-img" :src="getImages('c2c/want-buy/Group1519.png')" lazy loading="lazy" />
        <div class="max-w-288">
          <div>
            <el-image class="w-56 h-56" :src="getImages('c2c/fast-buy/Group1526.png')" lazy loading="lazy" />
          </div>
          <div class="mt-25 font-16 font-900">
            {{ $t("message.c2c.tip12") }}
          </div>
          <div class="mt-14 font-12 font-500 color-727">
            {{ $t("message.c2c.tip13") }}
          </div>
        </div>
      </div>
    </div>
    <!-- 4.C2C的优势 -->
    <div class="w-full" style="background: #f8f8f8">
      <div class="center-w1200 py-102">
        <div class="">
          <h2 class="font-40 font-700">
            {{ TITLE }} {{ $t("message.c2c.tip14") }}
          </h2>
          <div class="w-full">
            <div class="flex justify-between w-full mt-41">
              <div class="w-260">
                <div
                  class="flex justify-center items-center w-full h-160 bg-white introduce"
                >
                  <el-image class="w-128" :src="getImages('c2c/want-buy/Group2304.png')" lazy loading="lazy" />
                </div>
                <div class="mt-25">
                  <h2 class="font-20 font-900">
                    {{ $t("message.c2c.tip15") }}
                  </h2>
                  <div class="mt-10 font-12 font-500 color-494">
                    <p class="mb-30">
                      {{ $t("message.c2c.zai") }}{{ TITLE }}
                      {{ $t("message.c2c.tip16") }}
                    </p>
                    <p>
                      {{ $t("message.c2c.zai") }}{{ TITLE }}
                      {{ $t("message.c2c.tip17") }}
                    </p>
                  </div>
                </div>
              </div>
              <div class="w-260">
                <div
                  class="flex justify-center items-center w-260 h-160 bg-white introduce"
                >
                  <el-image class="w-128" :src="getImages('c2c/want-buy/Group2312.png')" lazy loading="lazy" />
                </div>
                <div class="mt-25">
                  <h2 class="font-20 font-900">
                    {{ $t("message.c2c.tip18") }}
                  </h2>
                  <div class="mt-10 font-12 font-500 color-494">
                    <p class="mb-30">
                      {{ TITLE }} {{ $t("message.c2c.tip19") }}
                    </p>
                    <p>
                      {{ $t("message.c2c.zai") }}{{ TITLE }}
                      {{ $t("message.c2c.tip20") }}
                    </p>
                  </div>
                </div>
              </div>
              <div class="w-260">
                <div
                  class="flex justify-center items-center w-260 h-160 bg-white introduce"
                >
                  <el-image class="w-128" :src="getImages('c2c/want-buy/Group2313.png')" lazy loading="lazy" />
                </div>
                <div class="mt-25">
                  <h2 class="font-20 font-900">
                    {{ $t("message.c2c.tip21") }}
                  </h2>
                  <div class="mt-10 font-12 font-500 color-494">
                    <p class="mb-30">
                      {{ TITLE }} {{ $t("message.c2c.tip22") }}
                    </p>
                  </div>
                </div>
              </div>
              <div class="w-260">
                <div
                  class="flex justify-center items-center w-260 h-160 bg-white introduce"
                >
                  <el-image class="w-128" :src="getImages('c2c/want-buy/Group2308.png')" lazy loading="lazy" />
                </div>
                <div class="mt-25">
                  <h2 class="font-20 font-900">
                    {{ $t("message.c2c.tip23") }}
                  </h2>
                  <div class="mt-10 font-12 font-500 color-494">
                    <p class="mb-30">
                      {{ TITLE }} {{ $t("message.c2c.tip24") }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 5.关于数字货币 -->
    <div class="center-w1200 py-83">
      <div class="flex justify-between items-center">
        <div class="w-567">
          <h2 class="font-40 font-700">{{ $t("message.c2c.tip25") }}</h2>
          <div class="mt-35 font-16 font-500 color-727">
            <p class="mb-20">
              {{ $t("message.c2c.tip26") }}
            </p>
            <p>{{ TITLE }} {{ $t("message.c2c.tip27") }}</p>
          </div>
        </div>
        <el-image class="w-330" :src="getImages('c2c/want-buy/Group1539.png')" lazy loading="lazy" />
      </div>
    </div>
    <!-- 6.为什么选择在购买泰达币? -->
    <div style="background: #171a1e">
      <div class="center-w1200 py-76">
        <h2 class="font-40 font-700 text-center text-white">
          {{ $t("message.c2c.tip28") }}{{ TITLE }}{{ $t("message.c2c.tip29") }}?
        </h2>
        <div class="flex justify-between mt-99">
          <div class="max-w-216 text-center text-white">
            <div class="flex justify-center">
              <el-image class="w-72 h-72" :src="getImages('c2c/want-buy/Group1544.png')" lazy loading="lazy" />
            </div>
            <div class="mt-37">
              <h3 class="mb-14 font-20 font-700 text-white">
                {{ $t("message.c2c.tip30") }}
              </h3>
              <p class="font-12 lh-18 introduce-title">
                {{ TITLE }}{{ $t("message.c2c.tip31") }}
              </p>
            </div>
          </div>
          <div class="max-w-216 text-center text-white">
            <div class="flex justify-center">
              <el-image class="w-72 h-72" :src="getImages('c2c/want-buy/Group1543.png')" lazy loading="lazy" />
            </div>
            <div class="mt-37">
              <h3 class="mb-14 font-20 font-700 text-white">
                {{ $t("message.c2c.tip32") }}
              </h3>
              <p class="font-12 lh-18 introduce-title">
                {{ TITLE }}{{ $t("message.c2c.tip33") }}
              </p>
            </div>
          </div>
          <div class="max-w-216 text-center text-white">
            <div class="flex justify-center">
              <el-image class="w-72 h-72" :src="getImages('c2c/want-buy/Group1542.png')" lazy loading="lazy" />
            </div>
            <div class="mt-37">
              <h3 class="mb-14 font-20 font-700 text-white">
                {{ $t("message.c2c.tip34") }}
              </h3>
              <p class="font-12 lh-18 introduce-title">
                {{ TITLE }}{{ $t("message.c2c.tip35") }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 7.常见问题 -->
    <div class="px-460 py-102 text-center">
      <h2 class="mb-49 font-40 font-700">{{ $t("message.c2c.tip36") }}</h2>
      <el-collapse v-model="activeNames">
        <el-collapse-item :title="$t('message.c2c.tip37')" name="1">
          <div class="col-text">
            {{ $t("message.c2c.tip38") }} {{ TITLE }}
            {{ $t("message.c2c.tip39") }}
          </div>
        </el-collapse-item>
        <el-collapse-item :title="$t('message.c2c.tip40')" name="2">
          <div class="col-text">
            {{ $t("message.c2c.tip41") }} {{ TITLE }}
            {{ $t("message.c2c.tip42") }} {{ TITLE }}
            {{ $t("message.c2c.tip43") }}
          </div>
        </el-collapse-item>
        <el-collapse-item :title="$t('message.c2c.tip44')" name="3">
          <div class="col-text">
            {{ $t("message.c2c.tip45") }}
          </div>
        </el-collapse-item>
        <el-collapse-item :title="$t('message.c2c.tip46')" name="4">
          <div class="col-text">
            {{ $t("message.c2c.tip47") }}
          </div>
        </el-collapse-item>
        <el-collapse-item :title="$t('message.c2c.tip48')" name="5">
          <div class="col-text">
            {{ $t("message.c2c.tip49") }}
            {{ TITLE }} {{ $t("message.c2c.tip50") }}{{ TITLE }}
            {{ $t("message.c2c.tip51") }}
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
    <footer-view class="w-full" style="background: #171a1e" />
  </div>
</template>

<script>
import FooterView from "@/components/layout/footerView.vue";
import OtcInput from "@/views/c2c/components/OtcInput.vue";
import { getImages } from "@/utils/index";

export default {
  name: "layout",
  data() {
    return {
      activeNames: [],
      buyBi: true,
      TITLE:''
    };
  },

  components: {
    FooterView,
    OtcInput,
  },

  methods: {
    handleChangeFastTab(status) {
      this.buyBi = status;
    },
    getImages
  },
};
</script>

<style lang="scss" scoped>
@import "@/assets/css/c2c/init.scss";

.introduce {
  border-radius: 8px;
  box-shadow: 0px 0px 4px rgba(0, 0, 0, 0.2);
}
// 头部
.banner {
  background: #14151a;
  .banner-top {
    width: 1200px;
    margin: 0 auto;
    position: relative;
    padding: 25px 0;

    img {
      width: 100%;
    }

    .banner-title {
      position: absolute;
      width: 100%;
      top: 30px;
      color: #fff;

      .title {
        text-align: center;
        font-size: 24px;
        font-weight: 800;
      }

      .text {
        color: #b8bdc5;
        text-align: center;
        width: 450px;
        font-size: 14px;
        margin: 0 auto;
        margin-top: 5px;
      }
    }
  }
}

.center-w1200 {
  width: 1200px;
  margin: 0 auto;
}

.introduce-title {
  width: 300px;
  word-break: break-all;
  word-wrap: break-word;
}

.want-img {
  display: block;
  margin: 0 20px;
}

.col-text {
  color: #727a89;
  padding: 10px 0;
  text-align: left;
}
</style>
