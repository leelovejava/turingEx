<template>
    <div class="listItem px-5">
        <div class="tab-header-1 px-4 text_color6">
            <div class="td-2">{{ t('股票名称') }}</div>
            <div class="td text-center"> {{ t('价格') }}
                <!-- <div class="flex items-center justfly-center">
                    {{ t('发行价') }}
                    <div class="filter-box ml-2">
                        <div class="filter-icon" :class="[sortVal == 0 ? 'icon_top1_active' : 'icon_top1']"
                            @click="listSort(3)">
                        </div>
                        <div class="filter-icon" :class="[sortVal == 1 ? 'icon_top2_active' : 'icon_top2']"
                            @click="listSort(4)">
                        </div>
                    </div>
                </div> -->
            </div>
            <div class="td text-right">{{ t('首日涨幅') }}
                <!-- <div class="flex items-center justfly-end">
                    {{ t('预计上市时间') }}
                    <div class="filter-box ml-2">
                        <div class="filter-icon" :class="[sortVal == 0 ? 'icon_top1_active' : 'icon_top1']"
                            @click="listSort(3)">
                        </div>
                        <div class="filter-icon" :class="[sortVal == 1 ? 'icon_top2_active' : 'icon_top2']"
                            @click="listSort(4)">
                        </div>
                    </div>
                </div> -->
            </div>
        </div>
        <div>
            <div v-for="(item, index) in list" :key="index" class="py-5 tab-header-1 px-4">
                <div class="td-2">
                    <div class="list-title">{{ item.name }}</div>
                    <div class="text_color6">{{ item.symbol }}</div>
                </div>
                <!-- <div class="td text-center">{{ item.marketPrice }}</div> -->
                <div class="td text-center">{{ item.curPrice }}</div>
                <div class="td text-right red">{{ item.firstDayChangeRatio }}</div>
            </div>
            <div v-if="!list.length" class="noMore text_color6">{{ t('没有更多了') }}</div>
          </div>
    </div>
</template>

<script setup>
import {onMounted, ref, computed, inject} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from "vue-i18n";
import { listListingAndlisted,newIssueList } from '@/service/ipo.api.js'
const { t } = useI18n()
const route = useRoute()
const router = useRouter()

const list = ref([]);
const loading = ref(false);
const finished = ref(false);
const currentRate = ref(0);
const sortVal = ref(5)
const index = ref(1)

onMounted(() => {
  onLoad()
})

const stockType = inject('stockType')
const onLoad = () => {
    let params = {
      type: stockType || 'US-stocks'
    }
    newIssueList(params).then(res => {
      list.value = res
    })
}

</script>
<style lang="scss" scoped>

.justfly-end{
  justify-content: flex-end;
}
.listItem {
    width: 100%;
    padding: 10px 10px;
    overflow-x: scroll;
    box-sizing: border-box;
    width: max-content;

    .flex-2 {
        flex: 2;
    }

    .list-title {
        width: 100px;
        text-overflow: ellipsis;
        white-space: nowrap;
        overflow: hidden;
    }



    .tab-header-1 {
        font-size: 13px;
        color: #747A8F;
        display: flex;
        gap: 0 10px;
    }

    .td-2 {
        width: 120px;
        display: inline-block;
        flex-shrink: 0;
    }

    .td {
        display: inline-block;
        width: 100px;
        flex-shrink: 0;

        .filter-box {
            .filter-icon {
                width: 8px;
                height: 6px;
            }

            .icon_top1 {
                background: url('@/assets/image/icon_top1.png') no-repeat center;
                background-size: 100% 100%;
            }

            .icon_top2 {
                background: url('@/assets/image/icon_top2.png') no-repeat center;
                background-size: 100% 100%;
                margin-top: 2px;
            }

            .icon_top1_active {
                background: url('@/assets/image/icon_top3.png') no-repeat center;
                background-size: 100% 100%;
            }

            .icon_top2_active {
                background: url('@/assets/image/icon_top4.png') no-repeat center;
                background-size: 100% 100%;
                margin-top: 2px;
            }
        }
    }

}
.noMore{
  text-align: center;
  margin: 10px;
}
</style>