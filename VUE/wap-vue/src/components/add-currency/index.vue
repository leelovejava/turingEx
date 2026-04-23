<template>
  <van-popup v-model:show="showLeft" closeable position="left" class="popup-bg"
    :style="{ width: '75%', height: '100%', }">
    <div class="main">
      <div class="title">{{ t('selectombination') }}</div>
      <div class="list">
        <div class="item" v-for="(item, index) in optionalList" :key="index">
          <van-checkbox v-model="item.check">{{ item.listName }}</van-checkbox>
        </div>
      </div>
      <div class="submit-div">
        <van-button class="but" type="default" @click="openGroupEditOrAdd">{{ t('createDustom') }}</van-button>
        <van-button class="but" type="primary" @click="finish">{{ t('Finish') }}</van-button>
      </div>
    </div>
  </van-popup>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { _itemUserOptionalList, _itemUserOptionaSaveItem, _isItemHasAdd, _removeItem } from '@/service/quotes.api'
import { useRouter } from "vue-router";
import { showToast } from 'vant';
import { _collect, _deleteCollect } from '@/service/cryptos.api'
import { useI18n } from 'vue-i18n'
const emits = defineEmits(['updateItem'])
const router = useRouter()
const optionalList = ref([])
const { t } = useI18n()
const props = defineProps({
  isCollect: {
    type: Boolean,
    default: false
  },
})
let showLeft = ref(false)
let symbolValue = ref('')
onMounted(async () => {
  getMyCoinsList()
})

const itemClick = (a) => {
  emits('click-item', props.item)
  // console.log(1111)
}
//获取我的自选列表
const getMyCoinsList = () => {
  let params = {}
  _itemUserOptionalList(params).then(data => {
    data.list.map((item) => {
      item.check = false
      optionalList.value.push(item)
    })
  })
}
//打开弹窗
const openCurrency = (symbol) => {
  showLeft.value = true
  symbolValue.value = symbol
  if (props.isCollect) {
    detectionItem()
  }
}
//跳转添加分组
const openGroupEditOrAdd = () => {
  router.push('/optional/groupAdd')
}
//完成
const finish = () => {
  let checkList = optionalList.value.filter((item) => {
    return item.check
  })
  if (!props.isCollect) {
    if (checkList.length === 0) {
      showToast(t('PleaseAdd'))
      return
    }
    checkList.map((item) => {
      let obj = {
        listId: item.listId,
        symbol: symbolValue.value
      }
      if (item.listId == 0) {
        _collect(symbolValue.value).then((res) => {
          showToast(t('添加成功'))
          emits('updateItem')
          detectionItem()
          showLeft.value = false
        })
      } else {
        _itemUserOptionaSaveItem(obj).then((res) => {
          showToast(t('添加成功'))
          emits('updateItem')
          detectionItem()
          showLeft.value = false
        })
      }
    })
  } else {
    if (checkList.length === 0) {
      showToast(t('PleaseDelete'))
      return
    }
    checkList.map((item) => {
      let obj = {
        listId: item.listId,
        symbol: symbolValue.value
      }
      if (item.listId == 0) {
        _deleteCollect(symbolValue.value).then((res) => {
          showToast(t('successfullyDeleted'))
          emits('updateItem')
          detectionItem()
          showLeft.value = false
        })
      }
      _removeItem(obj).then((res) => {
        showToast(t('successfullyDeleted'))
        emits('updateItem')
        detectionItem()
        showLeft.value = false
      })
    })
  }
}
//检查币种是否存在自选
const detectionItem = () => {
  optionalList.value.map((item) => {
    let obj = {
      listId: item.listId,
      symbol: symbolValue.value
    }
    _isItemHasAdd(obj).then((data) => {
      item.check = data
    })
  })
}
defineExpose({
  openCurrency
});

</script>
<style lang="scss" scoped>
.main {
  .title {
    padding: 14px 20px;
    font-size: 16px;
  }

  .submit-div {
    position: fixed;
    bottom: 0;
    width: 100%;
    left: 0;
    padding: 10px 20px;
    display: flex;
    justify-content: space-between;

    .but {
      width: 120px;
    }
  }

  .list {
    padding: 0 20px;
    color: $text_color;

    .item {
      padding: 10px 0;
    }

    :deep(.van-checkbox__label) {
      font-size: 16px !important;
      color: $text_color;
    }
  }
}
</style>