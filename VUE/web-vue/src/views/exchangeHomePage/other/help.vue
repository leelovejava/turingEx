<template>
  <div style="background-color: black">
    <div style="margin-top: 0px; height: auto; width: 100%">
      <div
        class="content-view-box"
        style="
          padding-top: 49px;
          background-color: black;
          height: 26px;
          width: 1200px;
        "
      >
        <div class="text-title">{{ $t("message.home.xinshouzhidao") }}</div>
      </div>
      <!-- button -->
      <div
        class="content-view-box item-box"
        style="
          margin-top: 36px;
          background-color: black;
          height: 66px;
          width: 1200px;
        "
      >
        <div id="box1">
          <input
            type="button"
            :value="$t('message.home.xinshouzhidao_tag_1')"
            class="checked"
            style="height: 66px; width: 205px; margin-left: 0px"
            on
          />
          <input
            type="button"
            :value="$t('message.home.xinshouzhidao_tag_2')"
            class="no-checked"
            style="height: 66px; width: 205px; margin-left: 30px"
          />
          <input
            type="button"
            :value="$t('message.home.xinshouzhidao_tag_3')"
            class="no-checked"
            style="height: 66px; width: 205px; margin-left: 30px"
          />
        </div>
      </div>
      <!-- 内容 -->
      <div
        class="content-view-box"
        style="
          margin-top: 52px;
          background-color: white;
          height: auto;
          width: 1200px;
        "
      >
        <div class="content-view-box-1">
          <div id="help_1" style="display: block" class="help1">
            <div v-for="index of 19" :key="index">
              <div>
                <div class="text_1">
                  {{ $t("message.home.help_1_" + index) }}
                </div>
              </div>
            </div>
          </div>
          <div id="help_2" style="display: none" class="help2">
            <div class="help2-1">
              <div v-for="index of 6" :key="index">
                <div>
                  <div class="text_1">
                    {{ $t("message.home.help_2_1_" + index) }}
                  </div>
                </div>
              </div>
            </div>
            <div class="help2-1 content-view-box" style="width: 600px">
              <img src="@/assets/images/exchangeHome/other/help/help_2_1.png" />
            </div>
            <div class="help2-2">
              <div v-for="index of 3" :key="index">
                <div>
                  <div class="text_1">
                    {{ $t("message.home.help_2_2_" + index) }}
                  </div>
                </div>
              </div>
            </div>
            <div class="help2-2 content-view-box" style="width: 600px">
              <img src="@/assets/images/exchangeHome/other/help/help_2_2.png" />
            </div>
            <div class="help2-3">
              <div v-for="index of 14" :key="index">
                <div>
                  <div class="text_1">
                    {{ $t("message.home.help_2_3_" + index) }}
                  </div>
                </div>
              </div>
            </div>
            <div class="help2-3 content-view-box" style="width: 600px">
              <img src="@/assets/images/exchangeHome/other/help/help_2_3.png" />
            </div>

            <div class="help2-4">
              <div v-for="index of 3" :key="index">
                <div>
                  <div class="text_1">
                    {{ $t("message.home.help_2_4_" + index) }}
                  </div>
                </div>
              </div>
            </div>
            <div class="help2-4 content-view-box" style="width: 600px">
              <img src="@/assets/images/exchangeHome/other/help/help_2_4.png" />
            </div>

            <div class="help2-5">
              <div v-for="index of 28" :key="index">
                <div>
                  <div class="text_1">
                    {{ $t("message.home.help_2_5_" + index) }}
                  </div>
                </div>
              </div>
            </div>
            <div class="help2-5 content-view-box" style="width: 600px">
              <img src="@/assets/images/exchangeHome/other/help/help_2_5.png" />
            </div>
          </div>
          <div id="help_3" style="display: none" class="help3">
            <div class="help3-1">
              <div v-for="index of 5" :key="index">
                <div>
                  <div class="text_1">
                    {{ $t("message.home.help_3_1_" + index) }}
                  </div>
                </div>
              </div>
            </div>
            <div class="content-view-box" style="width: 600px">
              <img src="@/assets/images/exchangeHome/other/help/help_3_1.png" />
            </div>
            <div class="help3-2">
              <div v-for="index of 35" :key="index">
                <div>
                  <div class="text_1">
                    {{ $t("message.home.help_3_2_" + index) }}
                  </div>
                </div>
              </div>
            </div>
            <div class="content-view-box" style="width: 600px">
              <img src="@/assets/images/exchangeHome/other/help/help_3_2.png" />
            </div>

            <div class="help3-3">
              <div v-for="index of 17" :key="index">
                <div>
                  <div class="text_1">
                    {{ $t("message.home.help_3_3_" + index) }}
                  </div>
                </div>
              </div>
            </div>
            <div class="content-view-box" style="width: 600px">
              <img src="@/assets/images/exchangeHome/other/help/help_3_3.png" />
            </div>
            <div class="help3-4">
              <div v-for="index of 8" :key="index">
                <div>
                  <div class="text_1">
                    {{ $t("message.home.help_3_4_" + index) }}
                  </div>
                </div>
              </div>
            </div>
            <div class="content-view-box" style="width: 600px">
              <img src="@/assets/images/exchangeHome/other/help/help_3_4.png" />
            </div>
            <div class="help3-5">
              <div v-for="index of 1" :key="index">
                <div>
                  <div class="text_1">
                    {{ $t("message.home.help_3_5_" + index) }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <footer-view></footer-view>
  </div>
</template>
<script>
import FooterView from "@/components/layout/footerView.vue";
export default {
  data() {
    return {};
  },
  components: { FooterView },
  mounted() {
    this.initUI();
  },
  methods: {
    initUI() {
      let box1 = document.getElementById("box1");
      // let box = document.getElementById('box');
      let btn = box1.getElementsByTagName("input");
      // let div = box.getElementsByTagName('div');

      let div = [];
      for (let i = 1; i <= 3; i++) {
        console.log("div1 = " + ("help" + i));
        let div1 = document.getElementById("help_" + i);
        div[i - 1] = div1;
      }

      for (let i = 0; i < btn.length; i++) {
        btn[i].index = i;
        btn[i].onclick = () => {
          for (let j = 0; j < btn.length; j++) {
            if (i != j) {
              btn[j].className = "no-checked";
              div[j].style.display = "none";
            }
          }
          btn[i].className = "checked";
          div[btn[i].index].style.display = "block";
        };
      }
    },
  },
};
</script>

<style scoped>
.text-title {
  width: 104px;
  height: 26px;
  font-family: "PingFang SC";
  font-style: normal;

  font-size: 26px;
  line-height: 26px;

  color: white;
}

#box div {
  display: none;
}

.checked {
  background-color: white;
  color: #000000;
}

.no-checked {
  background-color: #999999;
  color: #666666;
}

.item-box {
  display: flex;
  padding: 40px 0 20px 0;
}

.help1 {
  background-color: white;
  height: auto;
  width: 1100px;
}

.help2 {
  height: auto;
  width: 1100px;
}

.help3 {
  height: auto;
  width: 1100px;
}

.help2-1 {
  background-color: white;
  height: auto;
  width: 1100px;
}

.help2-2 {
  background-color: white;
  height: auto;
  width: 1100px;
}

.help2-3 {
  background-color: white;
  height: auto;
  width: 1100px;
}

.help2-4 {
  background-color: white;
  height: auto;
  width: 1100px;
}

.help2-5 {
  background-color: white;
  height: auto;
  width: 1100px;
}

.help3-1 {
  background-color: white;
  height: auto;
  width: 1100px;
}

.help3-2 {
  background-color: white;
  height: auto;
  width: 1100px;
}

.help3-3 {
  background-color: white;
  height: auto;
  width: 1100px;
}

.help3-4 {
  background-color: white;
  height: auto;
  width: 1100px;
}

.help3-5 {
  background-color: white;
  height: auto;
  width: 1100px;
}

.image1 {
  position: absolute;
  top: 50%;
}

.text_1 {
  width: 1100px;
  font-family: "PingFang SC";
  font-style: normal;
  font-size: 14px;
  line-height: 30px;
  color: #495666;
}

.content-view-box-1 {
  width: 1100px;
  margin: 0 auto;
  left: 0;
  right: 0;
}
</style>
