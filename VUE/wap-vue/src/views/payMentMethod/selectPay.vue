<template>
  <div class="selectPay pb-10">
    <fx-header>
      <template #title>{{ $t('allPay') }}</template>
    </fx-header>
    <!-- <van-search v-model="value1" placeholder="请输入搜索关键词" /> -->
    <van-index-bar v-for="(item, index) in keyList" :key="index" :index-list="newLetter">
      <van-index-anchor class="index-anchor" :index="item.name" />
      <div @click="openAdd(items)" class="item-cell ml-4 py-4" v-for="(items, index) in item.list" :key="index">
        {{ $t(`${items}`) }}
      </div>
    </van-index-bar>
  </div>
</template>

<script setup>
import { onBeforeMount, ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { _getBankPaymentMethodConfig } from "@/service/user.api.js";
import Pinyin from '@/utils/ChinesePY'
import { useI18n } from "vue-i18n";
const { t } = useI18n()
const route = useRoute()
const router = useRouter()
let value1 = ref('')
let keysList = ref(null)
let keyList = ref([])
let newLetter = ref([])

const openAdd = (val) => {
  let id = ''
  for (const key in keysList.value) {
    if (keysList.value.hasOwnProperty.call(keysList.value, key)) {
      if (keysList.value[key] === val) {
        id = key
      }
    }
  }
  sessionStorage.setItem("editAdd", JSON.stringify({ id: id, name: val, type: 'add' }));
  router.push('add')
}
onMounted(async () => {
  console.log(route.query.name);
  getC2cPaymentMethodConfig()
})
watch(value1, (val, oldVal) => {
  if (!val) {
    return
  }
  keyList.value.map((item) => {
    item.list.map((item2) => {
      if (item2.indexOf(val) !== -1) {
        console.log(item2)
      }
    })
  })
  this.filterFruitList = this.fruitList.filter((item) => {
    return item.name.indexOf(val) !== -1;
  })
  if (!val) {
    return
  }
  // for (let i = 0; i < keyList.value.length; i++) {
  //     for (let j = 0; j < keyList.value[i].list.length; j++) {
  //         // console.log(keyList.value[i].list[j])
  //         console.log(keyList.value[i].list[j].indexOf(val))
  //         if (keyList.value[i].list[j].indexOf(val) === -1) {
  //             // console.log(keyList.value[i].list[j])
  //         } else {
  //             console.log(keyList.value[i].list[j])
  //         }
  //     }

  // }
})
const getC2cPaymentMethodConfig = () => {
  _getBankPaymentMethodConfig().then((res) => {
    keysList.value = res
    let arry = Object.values(res)
    let nweArry = []
    let letter = []
    arry.map((item) => {
      let obj = {
        name: item,
        code: Pinyin.getWordsCode(item).substring(0, 1)
      }
      letter.push(Pinyin.getWordsCode(item).substring(0, 1))
      nweArry.push(obj)
    })
    newLetter.value = Array.from(new Set(letter))
    newLetter.value.map((item) => {
      let obj = {
        name: item,
        list: []
      }
      nweArry.map((items) => {
        if (item == items.code) {
          obj.list.push(items.name)
        }
      })
      keyList.value.push(obj)
    })
  })
}
</script>
<style lang="scss" scoped>
.selectPay {
  .index-anchor {
    background: $recommend_bg;
  }


}

:deep(.van-index-anchor) {
  font-size: 16px;
  color: $text-color;
}

:deep(.van-index-bar__index) {
  color: $active_line;
  font-size: 14px;
  margin-top: 6px;
  display: none;
}

.item-cell {
  border-bottom: 1px solid $grey_bg;
  font-size: 15px;
}
</style>