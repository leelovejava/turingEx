<template>
  <div class="pb-fix">
    <div class="container-box">
      <header class="header flex" @click="router.back()">
        <div class="icon back">
          <van-icon name="arrow-left" size="20" />

        </div>
        <p class="title">{{ t('selected') }}{{ count }}{{ t('itemText') }}</p>
      </header>
      <!-- <p class="title-box"></p> -->
      <div class="drag-container">

        <!-- <van-cell-group inset> -->
        <Draggable :list="list" :animation="200" item-key="id" class="drag-box" :forceFallback="true" ghost-class="ghost"
          @change="onMoveCallback" :move="getData" handle=".drag-icon">
          <template #item="{ element }">
            <div class="flex item">
              <div class="flex-l">
                <div class="flex-l-item" v-if="element.checkShow">
                  <van-checkbox v-model="element.check" class="radio" />
                </div>
                <div class="flex-l-item">
                  <p class="name">{{ element.label }}</p>
                </div>
              </div>
              <div class="flex-r">
                <div class="flex-r-item data-box" v-if="element.checkShow">
                  <div class="data-item edit-icon"
                    @click="router.push(`/optional/groupEdit?info=${JSON.stringify(element)}`)">
                    <img src="@/assets/image/optional/edit.png" alt="">
                  </div>
                </div>
                <div class="flex-r-item data-box">
                  <div class="data-item drag-icon">
                    <img src="@/assets/image/optional/drag.png" alt="">
                  </div>
                </div>
              </div>
            </div>
          </template>
        </Draggable>
        <!-- </van-cell-group> -->
      </div>
      <footer class="footer">
        <div class="btn-group">
          <van-checkbox v-model="checkedAll" @click="handleSelectAll">{{ t('selectAll') }}</van-checkbox>
        </div>
        <div class="icon">
          <img src="@/assets/image/optional/del.png" @click="itemUserOptionalDelete" alt="">
        </div>

      </footer>
    </div>
  </div>
</template>
  
<script setup>
import { ref, onMounted, reactive, onBeforeUpdate, computed } from "vue";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import Draggable from 'vuedraggable';
import { SwipeCell, showSuccessToast } from 'vant';
import { getCurrentInstance } from 'vue';
import { _itemUserOptionalList, _itemUserOptionalDelete } from '@/service/quotes.api'

const { proxy, ctx } = getCurrentInstance();
const { t } = useI18n()
const router = useRouter()
// const count = ref(0)
const total = ref(2)
const defaultType = ref('ETF')

const checkboxGroupRef = ref(null);
const checked = ref([]);
const checkboxRefs = ref([]);
const checkedAll = ref(false);

let list = reactive([
  {
    label: t('ETF'),
    id: 1,
  },
  {
    label: t('UsStocks'),
    id: 2,
  },
  {
    label: t('digitalCurrency'),
    id: 3,
  },
  {
    label: t('外汇'),
    id: 4,
  },
  {
    label: t('港股'),
    id: 5,
  },
  {
    label: t('台股'),
    id: 6,
  },
]);

onMounted(() => {
  getMyCoinsList()
})
const count = computed(() => {
  let listLength = list.filter((item) => {
    return item.check
  })
  return listLength.length
})
onBeforeUpdate(() => {
  checkboxRefs.value = [];
});

const itemClick = (item) => {
  console.log(item, 'item')
}

const onMoveCallback = (val) => {
  console.log('拖动前的索引 :' + val.moved.newIndex);
  console.log('拖动后的索引 :' + val.moved.oldIndex);
  console.log(list, 'list');
};

const getData = (val) => {
  console.log(val.draggedContext.element.id);
};

const onRoute = (path) => {
  router.push(path)
}

// // 单个radio的选择
// const toggle = (index) => {
//   checkboxRefs.value[index].toggle();
//   checkedAll.value = checked.value.length === list.length
// };

// 全选 反选
const handleSelectAll = () => {
  if (checkedAll.value) {
    list.map((item) => {
      if (item.checkShow) {
        item.check = true
      }
    })
  } else {
    list.map((item) => {
      if (item.checkShow) {
        item.check = false
      }
    })
  }
}
//获取我的自选列表
const getMyCoinsList = () => {
  let params = {}
  _itemUserOptionalList(params).then(data => {
    data.list.map((item) => {
      item.check = false
      item.checkShow = true
      item.id = item.listId
      item.label = item.listName
      list.push(item)
    })
  })
}

//删除自选分组
const itemUserOptionalDelete = () => {
  let ids = []
  list.map((item) => {
    if (item.check) {
      ids.push(item.id)
    }
  })
  let obj = {
    ids: ids.join(',')
  }
  _itemUserOptionalDelete(obj).then((res) => {
    showSuccessToast(t('successfullyDeleted'))
    for (let i = 0; i < list.length; i++) {
      for (let j = 0; j < ids.length; j++)
        if (list[i].id == ids[j]) {
          list.splice(i, 1);
        }
    }
  })
}


</script>
  
<style lang="scss" scoped>
:deep(.van-swipe-cell__wrapper .van-button--danger) {
  height: 55px;
}

:deep(.footer .van-checkbox__label) {
  color: $text_color;
  font-size: 14px;
}

.container-box {
  .header {
    padding: 0 12px;
    align-items: center;

    .title {
      font-size: 16px;
      color: $text_color;
      text-align: center;
      flex: 1;
      transform: translate(-20px, 0px);
    }

    .icon {
      display: inline-block;
      width: 20px;
      height: 20px;
    }
  }

  .title-box {
    margin-top: 10px;
    padding: 0 12px;
    height: 32px;
    background: $selectSymbol_background;
    line-height: 32px;
    font-weight: 400;
    font-size: 12px;
    color: #747A8F;
  }

  .drag-container {
    overflow: hidden;
    position: absolute;
    left: 0;
    right: 0;

    .drag-box {
      line-height: 22px;

      .item {
        padding: 16px;
        border-bottom: 1px solid $border_color;
      }

      .flex-l {
        display: flex;
        flex: 1;
        align-items: center;

        .name {
          font-size: 14px;
          color: $text_color;
        }

        .radio {
          margin-right: 10px;
        }
      }

      .flex-r {
        display: flex;
        align-items: center;
        font-size: 12px;
        text-align: center;

        .flex-r-item {
          flex: 1;
          align-self: center;
        }

        .data-box {
          padding: 0 6px;

          .data-item {
            display: flex;
            justify-content: flex-end;
            align-items: center;
            text-align: right;

            img {
              margin-left: 10px;
              height: 20px;
              width: 20px;
            }
          }
        }
      }
    }
  }

  .footer {
    position: absolute;
    width: 100%;
    padding: 16px;
    bottom: 0;
    display: flex;
    justify-content: space-between;
    align-items: center;

    img {
      height: 20px;
      width: 20px;
    }
  }
}
</style>