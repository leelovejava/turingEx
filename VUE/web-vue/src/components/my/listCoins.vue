<template>
  <div class="css-vp41bv" v-if="isShowPop">
    <div class="css-snh7a0">
      <div class="css-hp9x1w">
        <div class="css-1dcbbbv">
          <div class="modaltitle css-4cffwv">
            <div class="css-e6jk6i">
              <div class="css-irv4ls">{{ strTitle }}</div>
            </div>
          </div>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            class="css-10uq0b5"
            @click="OnclickClose"
          >
            <path
              d="M6.697 4.575L4.575 6.697 9.88 12l-5.304 5.303 2.122 2.122L12 14.12l5.303 5.304 2.122-2.122L14.12 12l5.304-5.303-2.122-2.122L12 9.88 6.697 4.575z"
              fill="currentColor"
            ></path>
          </svg>
        </div>
        <div class="css-1gh297f">
          <div :class="note != '' ? 'note-style' : ''">{{ note }}</div>

          <div class="css-1ei9e9v">
            <div class="css-i96p0j">
              <div
                class="css-1qwhk23"
                v-for="(item, index) in listOption"
                :key="index"
                @click="callbackChoose(index)"
              >
                <div class="css-hwqc42">
                  <div class="css-gg0wwx">
                    <img
                      v-if="item.icon"
                      :src="handleSymbolImg(item.icon)"
                      width="32"
                      height="32"
                    />
                    <div class="css-n2vpdm">
                      <div class="css-4cffwv">
                        <span class="css-1jq4ozh">{{
                          item.name?.toUpperCase()
                        }}</span>
                      </div>
                      <div title="Bitcoin" class="css-yo3oki">
                        {{ item.name.toUpperCase() }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { handleSymbolImg } from "@/utils";
export default {
  props: {
    inIsShowPop: {
      type: Boolean,
      default: false,
    },
    inTitle: {
      type: String,
      default: "",
    },
    noteTitle: {
      type: String,
      default: "",
    },
    callbackState: Function,
    callbackChoose: Function,
    inLstOption: {
      type: Array,
      default: () => [],
    },
  },

  data() {
    return {
      isShowPop: this.inIsShowPop,
      strTitle: this.inTitle,
      listOption: this.inLstOption,
      note: this.noteTitle,
    };
  },
  mounted() {},
  watch: {
    inIsShowPop(newVal) {
      this.isShowPop = newVal; //对父组件传过来的值进行监听，如果改变也对子组件内部的值进行改变
    },
    inTitle(newVal) {
      this.strTitle = newVal;
    },
    noteTitle(newVal) {
      this.note = newVal;
    },
    inLstOption(newVal) {
      this.listOption = newVal;
    },
  },
  methods: {
    handleSymbolImg,
    OnclickClose() {
      this.isShowPop = false;
      this.callbackState(this.isShowPop);
    },
  },
};
</script>
<style lang="css" scoped>
/* @import url("../assets/css/listCoin.css"); */
/*! CSS Used from: Embedded */
input {
  font-family: inherit;
}
input::-webkit-input-placeholder {
  font-style: normal !important;
}
input::placeholder {
  font-style: normal !important;
}
/*! CSS Used from: Embedded */
img {
  border-style: none;
}
input {
  font-family: inherit;
  font-size: 100%;
  line-height: 1.15;
  margin: 0px;
}
input {
  overflow: visible;
}
/*! CSS Used from: Embedded */
.css-vurnku {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
}
.css-vp41bv {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  display: flex;
  position: fixed;
  -webkit-box-align: center;
  align-items: center;
  -webkit-box-pack: center;
  justify-content: center;
  z-index: 1200;
  inset: 0px;
  background-color: rgba(0, 0, 0, 0.5);
}
.css-snh7a0 {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  position: relative;
  box-shadow: rgba(20, 21, 26, 0.16) 0px 8px 16px,
    rgba(71, 77, 87, 0.16) 0px 16px 32px, rgba(20, 21, 26, 0.1) 0px 0px 1px;
  border-radius: 6px;
  background-color: rgb(255, 255, 255);
  animation: 0.3s ease-in-out 0s 1 normal none running animation-1wqz9z0;
}
.css-hp9x1w {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  display: flex;
  background-color: rgb(255, 255, 255);
  position: relative;
  flex-direction: column;
  border-radius: 6px;
  padding-left: 0px;
  padding-right: 0px;
}
.css-hp9x1w > div:first-of-type {
  padding-left: 24px;
  padding-right: 24px;
}
.css-1dcbbbv {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  display: flex;
  position: relative;
  height: 64px;
  -webkit-box-align: center;
  align-items: center;
  width: 100%;
  -webkit-box-pack: justify;
  justify-content: space-between;
}
.css-4cffwv {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  display: flex;
}
.css-e6jk6i {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  font-weight: 500;
  font-size: 20px;
  line-height: 24px;
}
.css-irv4ls {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  font-weight: 500;
  font-size: 20px;
  line-height: 28px;
  color: rgb(30, 35, 41);
}
.css-10uq0b5 {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  color: rgb(112, 122, 138);
  font-size: 24px;
  fill: rgb(112, 122, 138);
  width: 1em;
  height: 1em;
}
.css-10uq0b5:hover {
  color: rgb(240, 185, 11);
  cursor: pointer;
}
.css-1gh297f {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
}
@media screen and (min-width: 767px) {
  .css-1gh297f {
    width: 100%;
  }
}
@media screen and (min-width: 1023px) {
  .css-1gh297f {
    width: 384px;
  }
}
.css-8a2xbu {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  padding-left: 16px;
  padding-right: 16px;
}
@media screen and (min-width: 767px) {
  .css-8a2xbu {
    padding-left: 24px;
    padding-right: 24px;
  }
}
.css-1fc5db9 {
  box-sizing: border-box;
  margin: 4px 0px 0px;
  min-width: 0px;
  display: inline-flex;
  position: relative;
  height: 40px;
  -webkit-box-align: center;
  align-items: center;
  line-height: 1.6;
  border: 1px solid rgb(234, 236, 239);
  border-radius: 4px;
  width: 100%;
}
.css-1fc5db9:hover:not(.bn-input-status-disabled):not(.bn-input-status-error) {
  border-color: rgb(240, 185, 11);
}
.css-1fc5db9 input {
  color: rgb(30, 35, 41);
  font-size: 14px;
  padding-left: 8px;
  padding-right: 8px;
}
.css-1fc5db9 .bn-input-prefix {
  flex-shrink: 0;
  font-size: 14px;
}
.css-1fc5db9 input {
  padding-left: 12px;
  padding-right: 12px;
}
.css-k6nsss {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  height: 16px;
}
.css-phgw1n {
  box-sizing: border-box;
  margin: 0px 0px 0px 8px;
  min-width: 0px;
  color: rgb(183, 189, 198);
  font-size: 16px;
  fill: rgb(183, 189, 198);
  width: 1em;
  height: 1em;
}
.css-16fg16t {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  width: 100%;
  height: 100%;
  padding: 0px;
  outline: none;
  border: none;
  background-color: inherit;
  opacity: 1;
}
.css-16fg16t::-webkit-input-placeholder {
  color: rgb(183, 189, 198);
  font-size: 14px;
}
.css-16fg16t::placeholder {
  color: rgb(183, 189, 198);
  font-size: 14px;
}
.css-1ei9e9v {
  box-sizing: border-box;
  margin: 8px 0px 0px;
  min-width: 0px;
  flex: 1 1 0%;
  overflow-y: auto;
}
.css-i96p0j {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  height: 100%;
}
@media screen and (min-width: 767px) {
  .css-i96p0j {
    height: 352px;
  }
}
@media screen and (min-width: 1023px) {
  .css-i96p0j {
    height: 352px;
  }
}
.css-1qwhk23 {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  padding-left: 16px;
  padding-right: 16px;
}
@media screen and (min-width: 767px) {
  .css-1qwhk23 {
    padding-left: 24px;
    padding-right: 24px;
  }
}
.css-1qwhk23:hover {
  background-color: rgb(250, 250, 250);
}
.css-hwqc42 {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  display: flex;
  flex: 1 1 0%;
  -webkit-box-pack: justify;
  justify-content: space-between;
  -webkit-box-align: center;
  align-items: center;
  padding-top: 14px;
  padding-bottom: 14px;
  cursor: pointer;
}
.css-gg0wwx {
  box-sizing: border-box;
  margin: 0px 4px 0px 0px;
  min-width: 0px;
  display: flex;
  -webkit-box-align: center;
  align-items: center;
  width: auto;
  flex-shrink: 0;
}
.css-n2vpdm {
  box-sizing: border-box;
  margin: 0px 0px 0px 16px;
  min-width: 0px;
  display: flex;
  flex: 1 1 0%;
  flex-direction: column;
  align-items: flex-start;
}
.css-1jq4ozh {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  font-weight: 500;
  font-size: 14px;
  line-height: 20px;
  color: rgb(30, 35, 41);
}
.css-yo3oki {
  box-sizing: border-box;
  margin: 0px;
  min-width: 0px;
  color: rgb(71, 77, 87);
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  font-size: 14px;
  line-height: 20px;
}

.note-style {
  border: 1px solid #e5e5e5;
  padding: 10px;
  border-radius: 4px;
  margin: 10px;
}
</style>
