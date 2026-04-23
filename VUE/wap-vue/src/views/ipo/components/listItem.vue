<template>
    <div class="listItem px-5">
        <van-list v-model:loading="loading" :finished="finished" :loading-text="$t('加载中') + '...'" :finished-text="$t('没有更多了')" @load="onLoad">
            <div v-for="(item, index) in list" :key="index" class="list-div flex" @click="goToDetail(item.productCode,item.productName)">
                <div class="tab1">
                    <div class="list-title">{{ item.productName }}</div>
                    <div class="text_color6">{{ item.productCode }}</div>
                </div>
                <div class="tab2">{{ item.raisedFunds }}</div>
                <div class="tab3 text-right">{{ getTime(item.createTime) }}</div>
            </div>
        </van-list>
    </div>
</template>

<script setup>
import {onMounted, ref, computed, inject} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getProspectus } from '@/service/ipo.api.js'
import { useI18n } from "vue-i18n";
const { t } = useI18n()
const route = useRoute()
const router = useRouter()

const list = ref([]);
const loading = ref(false);
const finished = ref(false);
const index = ref(1)

onMounted(() => {

})

const stockType = inject('stockType')
const onLoad = () => {
    let params = {
      current: index.value,
      size: 10,
      type: stockType || 'US-stocks'
    }
    getProspectus(params).then(res => {
      if(res.length) {
        list.value = list.value.concat(res)
      }
      index.value++
      loading.value = false;
      if (res.length < 10) {
        finished.value = true;
      }
    })
}

const getTime = (time) => {
  return time?.split(' ')[0];
}

const goToDetail = (id,name) => {
    router.push({
        path: "/ipo/progress",
        query: {
            id,
            name
        }
    })
}
</script>
<style lang="scss" scoped>
.listItem {
    .flex-2 {
        flex: 2;
    }

    .tab1{
      width: 40%;
    }
    .tab2{
      width: 30%;
    }
    .tab3{
      width: 30%;
    }

    .list-div {
        padding: 20px 0;

        .list-title {
            width: 100px;
            text-overflow: ellipsis;
            // white-space: nowrap;
            overflow: hidden;
        }
    }
}</style>