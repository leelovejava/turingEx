<template>
    <div class="pb-fix">
        <van-sticky>
            <fx-header>
                <template #title>
                    <van-field class="flex-1 text-gray-100" v-model="searchVal" @input="onInput"
                        :placeholder="$t('search')" />
                </template>
            </fx-header>
        </van-sticky>
        <list-item v-for="(item, index) in searchList" :item="item" :key="index" @click-item="onClickItem"></list-item>
        <!-- 长按弹框 -->
    </div>
</template>
  
<script setup>
import ListItem from "@/components/list-item/index.vue";
import { ref, onMounted } from "vue";
import { _ItemUserOptionalItemAdd } from '@/service/quotes.api'
import { useQuotesStore } from '@/store/quotes.store'
import { useRouter, useRoute } from "vue-router";
const router = useRouter()
const route = useRoute()

const quotesStore = useQuotesStore()
const allList = ref([])
const searchList = ref([])


onMounted(() => {
    allList.value = quotesStore.coins
    searchList.value = allList.value
})




const onClickItem = (evt) => {
    if (route.query.id) {
        ItemUserOptionalItemAddFn(evt.symbol, route.query.id)
    }

}

const ItemUserOptionalItemAddFn = (symbol, list_id) => {
    _ItemUserOptionalItemAdd({
        symbol: symbol,
        list_id: list_id
    }).then(res => {
        router.push('/quotes/index')
    })
}
const onInput = () => {
    if (searchVal.value == '') {
        searchList.value = allList.value
    } else {
        searchList.value = allList.value.filter((item) => {
            return item.symbol.indexOf(searchVal.value.toUpperCase()) !== -1
        })
    }
}
// 搜索
const searchVal = ref('');

</script>
  
<style lang="scss" scoped>
:deep(.van-nav-bar__title) {
    position: relative;
    top: 2px;
    max-width: 72%;
}

:deep(.van-nav-bar__title) {
    flex: 1;
}

:deep(.van-field__control) {
    font-size: 16px;
    font-weight: 500;
    caret-color: #3157BE;
}

// :deep(.van-field__control::placeholder) {
//     color: var(--dark-grey);
// }
</style>