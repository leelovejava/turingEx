<template>
  <div class="pb-fix">
    <div class="container-box">
      <header class="header flex" @click="router.back()">
        <div class="icon back">
          <van-icon name="arrow-left" size="20" />
        </div>
        <p class="title">{{ t('managementPortfolio') }}</p>
      </header>
      <p class="title-box">{{ t('myPortfolio') }}</p>
      <div class="drag-container">
        <Draggable :list="list" :animation="200" item-key="id" class="drag-box" :forceFallback="true" ghost-class="ghost"
          @change="onMoveCallback" :move="getData" handle=".drag-icon">
          <template #item="{ element }">
            <div class="flex item" @click="itemClick(element.label)" v-if="element.isFixed === true">
              <div class="flex-l">
                <div class="flex-l-item">
                  <div class="minus-icon">
                  </div>
                </div>
                <div class="flex-l-item">
                  <p class="name">{{ element.label }}</p>
                </div>
              </div>
              <div class="flex-r">
                <div class="flex-r-item data-box">
                  <div class="data-item eye-open-icon">
                    <img src="@/assets/image/optional/eye-open.png" alt="">
                  </div>
                </div>
                <div class="flex-r-item data-box">
                  <div class="data-item drag-icon">
                    <img src="@/assets/image/optional/drag.png" alt="">
                  </div>
                </div>
              </div>
            </div>
            <div v-else>
              <van-swipe-cell :ref="el => swipeCellRefs[element.id] = el" :name="element.id">
                <div class="flex item">
                  <div class="flex-l">
                    <div class="flex-l-item" @click.stop="handleSwipe(element.id)">
                      <div class="icon minus-icon">
                        <img src="@/assets/image/optional/minus.png" alt="">
                      </div>
                    </div>
                    <div class="flex-l-item">
                      <p class="name">{{ element.label }}</p>
                    </div>
                  </div>
                  <div class="flex-r">
                    <div class="flex-r-item data-box">
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
                <template #right>
                  <van-button square type="danger" @click="itemUserOptionalDelete(element.id)" :text="t('delete')" />
                </template>
              </van-swipe-cell>
            </div>
          </template>
        </Draggable>
      </div>
    </div>
  </div>
</template>
  
<script setup>
import { ref, onMounted, reactive } from "vue";
import { useQuotesStore } from '@/store/quotes.store'
import { useRouter, useRoute } from "vue-router";
import { useI18n } from "vue-i18n";
import Draggable from 'vuedraggable';
import { SwipeCell, showSuccessToast } from 'vant';
import { _itemUserOptionalList, _itemUserOptionalDelete } from '@/service/quotes.api'
const swipeCellRefs = ref([]);
const { t } = useI18n()
const router = useRouter()
const route = useRouter()
const checkedAll = ref(false)


let list = reactive([
  // {
  //   label: t('我的自选'),
  //   id: 0,
  //   isFixed: false,
  // },
  {
    label: t('UsStocks'),
    id: 1,
    isFixed: true,
  },
  {
    label: t('ETF'),
    id: 2,
    isFixed: true,
  },
  {
    label: t('外汇'),
    id: 3,
    isFixed: true,
  },
  {
    label: t('digitalCurrency'),
    id: 4,
    isFixed: true,
  },
]);

onMounted(() => {
  getMyCoinsList()
})

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

const handleSwipe = (index) => {
  // console.log(proxy.$refs, 'proxy.$refs')
  // proxy.$refs['swipeCellDom' + id].open('right')
  console.log(index, 'index')
  console.log(swipeCellRefs.value[index], 'swipeCellRefs.value')
  swipeCellRefs.value[index].open('right');
}

const onRoute = (path) => {
  router.push(path)
}
//获取我的自选列表
const getMyCoinsList = () => {
  let params = {}
  _itemUserOptionalList(params).then(data => {

    data.list.map((item) => {
      item.isFixed = false
      item.id = item.listId
      item.label = item.listName
      list.push(item)
    })
  })
}

//删除自选分组
const itemUserOptionalDelete = (id) => {
  let obj = {
    ids: id
  }
  _itemUserOptionalDelete(obj).then((res) => {
    showSuccessToast(t('successfullyDeleted'))
    for (let i = 0; i < list.length; i++) {
      if (list[i].id == id) {
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
    background:  $selectSymbol_background;
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

        .minus-icon {
          height: 20px;
          width: 20px;
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
}
</style>