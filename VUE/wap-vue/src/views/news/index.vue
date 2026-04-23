<template>
  <section class="inner-tab-container">
    <p class="title">{{ t('news') }}</p>
    <div class="news-container">
      <van-steps direction="vertical" :active="0">
        <van-step v-for="(item, index) in list" :key="item.uuid || index">
          <p class="time">{{ item.createdAt }}</p>
          <p class="context" v-html="item.description"></p>
        </van-step>
        <!-- <van-step>
          <p class="time">2023-06-15 11:30</p>
          <p class="context">南非汇市：兰特兑美元走高，美国通胀报告发布后美元跌至约两周地点</p>
        </van-step>
        <van-step>
          <p class="time">2023-06-14 14:20</p>
          <p class="context">降息预期遭重挫败！CPI环比增速抬头 美联储抗通胀之路注定崎岖</p>
        </van-step> -->
      </van-steps>
      <div class="flex mt-2" v-if="list.length > 0">
        <van-button type="default" plain class="more-btn" @click="onLoadMore">{{ t('更多数据') }}</van-button>
      </div>
    </div>
  </section>
</template>
    
<script setup>
import { ref, onMounted } from 'vue';
import { _getInformationList } from '@/service/etf.api'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const list = ref([])
const maxTime = ref('')

onMounted(async () => {
  getInformationList()
})

const onLoadMore = () => {
  maxTime.value = list.value[list.value.length - 1].createdAt
  getInformationList()
}

const getInformationList = () => {
  _getInformationList(maxTime.value).then(data => {
    list.value = [...list.value, ...data]
  })
}


</script>
<style lang="scss" scoped>
:deep(.van-steps) {
  background: $mainBgColor;

  .van-step__title {
    color: $text_color !important;

    .time {
      color: #747A8F;
      margin: 5px 0;
    }

  }

  .van-step__line {
    background-color: $step-border;
  }

  .van-icon-checked::before {
    content: '';
    background-color: #3478F6;
    width: 6px;
    height: 6px;
    box-shadow: 0 0 0px 4px $step-bg;
    border-radius: 50%;
  }

  .van-step__circle {
    background-color: #3478F6;
    width: 6px;
    height: 6px;
    box-shadow: 0 0 0px 4px $step-bg;
  }

  .van-step--vertical:not(:last-child):after {
    border-bottom-width: 1px;
    border-color: $border_color;
  }
}

.inner-tab-container {
  margin-top: 8px;
  padding: 0 12px 60px;

  .title {
    font-size: 16px;
    font-weight: 700;
    margin-bottom: 8px;
  }

  .news-container {}

  .more-btn {
    margin: 0 auto;
    background-color: transparent;
    color: #3478F6;
  }
}
</style>