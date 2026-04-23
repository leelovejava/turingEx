<template>
  <div class="inputCom">
    <span class="label textColor">{{ label }}</span>
    <form class="iptbox inputBackground">
      <div class="areaCode" v-if="area" @click="selectArea">
        <span class="icon iti-flag" :class="icon"></span>
        <span class="textColor">+{{ dialCode }}</span>
        <img src="../../assets/image/login/more.png" alt="">
      </div>
      <!-- <p>{{ passwordType }} {{ props.value }}</p> -->
      <input autocomplete="off" name="username" class="inputBackground" v-if="typeText == 'password'" :type="passwordType"
        :placeholder="placeholderText" :value="modelValue" @input="onInput" :disabled="disabled" />
      <div class="max-input" v-else>
        <input autocomplete="off" v-if="max > 0" :maxlength="max" class="inputBackground" type="text" :disabled="disabled"
        :placeholder="placeholderText" :value="modelValue" @input="onInput" />
        <input autocomplete="off" v-else  class="inputBackground" type="text" :disabled="disabled"
        :placeholder="placeholderText" :value="modelValue" @input="onInput" />
      </div>
      <div class="rightCon">
        <div class="closeBox" v-if="clearBtn" @click="clear"><img src="../../assets/image/login/clear.png" alt="" />
        </div>
        <div class="eyeBox" v-if="typeText == 'password'" @click="changeType">
          <img v-if="passwordType == 'password'" src="../../assets/image/login/_close.png" alt="">
          <img v-else src="../../assets/image/login/open.png" alt="">
        </div>
      </div>
    </form>
    <div v-if="tips" class="tips">{{ tips }}</div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
const passwordType = ref('password')
const $emit = defineEmits(['selectArea', 'update:modelValue'])

const props = defineProps({
  label: {
    type: String,
    default: ''
  },
  placeholderText: {
    type: String,
    default: ''
  },
  typeText: {
    type: String,
    default: 'text'
  },
  clearBtn: {
    type: Boolean,
    default: true
  },
  area: {
    type: Boolean,
    default: false
  },
  // value: {
  //     type: [Number, String],
  //     default: '1',
  // },
  modelValue: {
    type: [Number, String],
    default: '',
  },
  tips: {
    type: String,
    default: ''
  },
  dialCode: {
    type: Number,
    default: 0
  },
  icon: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false
  },
  max: {
    type: Number,
    default: 0
  }
})

const selectArea = () => {
  console.log(1);
  $emit('selectArea', true)
}

const clear = () => {
  $emit('update:modelValue', '')
}

const onInput = (e) => {
  $emit('update:modelValue', e.target.value)
}

const changeType = () => {
  if (passwordType.value == "password") {
    passwordType.value = 'text'
  } else {
    passwordType.value = 'password'
  }

}
</script>

<style lang="scss" scoped>
@import "@/views/authentication/components/intl.css";

.textColor{
  color: $text_color;
}
.inputCom {
  color: $text_color;
  padding-bottom: 22px;
  font-size: 14px;

  .iptbox {
    height: 44px;
    margin-top: 9px;
    padding: 0 11px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-radius: 3px;
  }

  .areaCode {
    width: 100px;
    display: flex;
    align-items: center;
    padding-left: 10px;
    justify-content: space-between;
    height: 100%;

    img {
      width: 10px;
    }
  }

  input {
    flex: 1;
    height: 100%;
    border: none;
    padding-left: 10px;
    color: $text_color;
  }

  .rightCon {
    display: flex;
  }

  .closeBox,
  .eyeBox {
    width: 17px;
    height: 17px;

    img {
      width: 100%;
      height: 100%;
    }
  }

  .eyeBox {
    margin-left: 14px;
  }
}

.tips {
  font-size: 13px;
  color: $text_color1;
  margin-top: 9px;
}

input:-webkit-autofill {
  -webkit-box-shadow: 0 0 0 200px $light-grey inset;
}

input::-webkit-input-placeholder {
  color: $dark-grey;
}

.icon {
  transform: scale(1.3);
  display: inline-block;
}
.max-input{
  width: 100%;
  input{
    width: 100%;
  }
}
</style>