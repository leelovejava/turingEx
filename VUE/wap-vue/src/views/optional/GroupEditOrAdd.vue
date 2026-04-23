<template>
  <div class="pb-fix">
    <div class="container-box">
      <header class="header flex" @click="router.back()">
        <div class="icon back">
          <van-icon name="arrow-left" size="20" />
        </div>
        <p class="title">{{ isAdd == 0 ? t('createDustom') : t('updateDustom') }}</p>
      </header>
      <div class="content-container">
        <div class="text-filed">
          <van-field class="flex-1 text-gray-100" v-model="optionListName" @input="onInput"
            :placeholder="$t('EnterName')">
            <template #button>
              <span class="len">{{ optionListName.length }} / 24</span>
            </template>
          </van-field>
        </div>
        <div class="select-container">
          <van-cell is-link @click="router.push('/optional/selectSymbol')" :title="t('defaultCurrency')"
            :value="currency" />
        </div>
        <footer class="footer">
          <div class="footer-btn">
            <van-button type="primary" @click="itemUserOptionaAddList">{{ t('Finish') }}</van-button>
          </div>
        </footer>
      </div>
    </div>
  </div>
</template>
  
<script setup>
import { ref, onMounted, reactive } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useI18n } from "vue-i18n";
import { _itemUserOptionaAddList, _itemUserOptionalUpdate } from '@/service/quotes.api'
import { useQuotesStore } from '@/store/quotes.store';
import { showSuccessToast,showToast } from 'vant';
const quotesStore = useQuotesStore()
const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const optionListName = ref('')
const { href } = location
const isAdd = ref(0)
const info = ref({})
const currency = ref('')
onMounted(() => {
  currency.value = quotesStore.$state.currency
  if (route.query.info) {
    info.value = JSON.parse(route.query.info)
    isAdd.value = 1
    optionListName.value = info.value.listName
    if (!info.value.currency) {
      currency.value = quotesStore.$state.currency
    } else {
      currency.value = info.value.currency
    }
    console.log(info.value)
  }
})

const handleShowPopup = () => {
  showPopup.value = true;
};
//新增自选分组
const itemUserOptionaAddList = () => {
  if (!optionListName.value) {
    showToast(t('请输入自选名称'));
    return
  }
  if (!currency.value) {
    showToast(t('请选择币种'));
    return
  }
  let obj = {
    name: optionListName.value,
    currency: currency.value
  }
  if (isAdd.value == 0) {
    _itemUserOptionaAddList(obj).then((res) => {
      showSuccessToast(t('Savesuccessfully'))
      setTimeout(() => {
        router.go(-1)
      }, 1000)
    })
  } else {
    obj.uuid = info.value.listId
    _itemUserOptionalUpdate(obj).then((res) => {
      showSuccessToast(t('SuccessfullyModified'))
      setTimeout(() => {
        router.go(-1)
      }, 1000)
    })
  }
}
</script>
  
<style lang="scss" scoped>
:deep(.van-cell) {
  background-color: $input_background1;
}

:deep(.van-field__control) {
  color: $text_color;
  caret-color: $color_main;
}

:deep(.van-field__control::placeholder) {
  color: #747A8F;
}

:deep(.van-field__button) {
  color: #747A8F;
}

:deep(.van-cell.van-cell--clickable) {
  color: $text_color;
}

:deep(.van-cell__value) {
  color: $text_color;
}

:deep(.van-icon-arrow:before) {
  color: $text_color;
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

  .content-container {
    .text-filed {
      margin-top: 10px;
      border-bottom: 1px solid $input-border;
    }

    .select-container {
      border-bottom: 1px solid $input-border;
    }

    .footer {
      widows: 100%;
      margin-top: 30px;
      text-align: center;

      .footer-btn {
        margin: 0 auto;
        width: 90%;

        .van-button {
          width: 100%;
          border: none;
          background: linear-gradient(90deg, #2C64D4 0%, #38AEEA 100%);
        }
      }
    }

  }

}
</style>