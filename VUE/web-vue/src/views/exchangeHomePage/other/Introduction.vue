<template>
  <div>
    <div
      class="content-view-box text-content"
      style="
        margin-top: 70px;
        width: 800px;
        height: auto;
        background-color: white;
      "
    >
      <div class="text-title-introduction">
        {{ $t("message.user.aboutus") }}
      </div>
      <div class="text-title2-introduction" style="margin-top: 40px">
        {{ $t("message.home.jinrongshengtai") }}
      </div>
      <div class="text-content-introduction" style="margin-top: 40px">
        {{ $t("message.user.aboutustwotext") }}
      </div>
    </div>

    <div
      class="content-view-box"
      style="margin-top: 70px; background-color: #f2f4f8; height: 600px"
    >
      <div
        style="
          width: 799px;
          height: auto;
          background-color: #f2f4f8;
          margin: 0 auto;
          top: 80px;
          margin-top: 80px;
        "
      >
        <div style="width: 799px; height: 50px"></div>
        <div class="text_2_1-introduction">
          {{ $t("message.home.tuanduishiming") }}
        </div>
        <div class="text_2_2-introduction">
          {{ $t("message.home.tuanduishiming_1") }}
        </div>
        <div
          style="
            background-color: #f2f4f8;
            width: 800px;
            height: 300px;
            margin-top: 20px;
          "
        >
          <div class="item-box">
            <div
              style="
                background-color: #f2f4f8;
                width: 800px;
                height: 100px;
                margin-top: 10px;
                position: relative;
              "
            >
              <div class="text_2_3">6</div>
              <div class="text_2_4">
                {{ $t("message.home.tuanduishiming_2") }}
              </div>
            </div>
            <div
              style="
                background-color: #f2f4f8;
                width: 800px;
                height: 100px;
                margin-top: 10px;
                position: relative;
              "
            >
              <div class="text_2_3">100+</div>
              <div class="text_2_4">
                {{ $t("message.home.tuanduishiming_3") }}
              </div>
            </div>
          </div>
          <div class="item-box">
            <div
              style="
                background-color: #f2f4f8;
                width: 800px;
                height: 100px;
                margin-top: 40px;
                position: relative;
              "
            >
              <div class="text_2_3">190</div>
              <div class="text_2_4">
                {{ $t("message.home.tuanduishiming_4") }}
              </div>
            </div>
            <div
              style="
                background-color: #f2f4f8;
                width: 800px;
                height: 100px;
                margin-top: 40px;
                position: relative;
              "
            >
              <div class="text_2_3">100K+</div>
              <div class="text_2_4">
                {{ $t("message.home.tuanduishiming_5") }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div
      class="content-view-box"
      style="
        margin-top: 70px;
        background-color: #ffffff;
        height: 90px;
        width: 800px;
      "
    >
      <div class="text_3_1">{{ $t("message.home.jiaruwomen") }}</div>
      <div class="text_3_2">{{ $t("message.home.jiaruwomen_1") }}</div>
    </div>

    <div
      style="
        margin-top: 0px;
        background-color: #ffffff;
        height: 100px;
        width: 1200px;
      "
    ></div>
    <!-- footer -->
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
};
</script>

<style scoped>
.text-title-introduction {
  /* text-align:center;
    font-size:18px;
    padding:30px 0;
    font-weight: bold; */

  width: 272px;
  height: 26px;

  font-weight: 500;
  font-size: 18px;
  line-height: 26px;

  color: #000000;

  flex: none;
  order: 0;
  flex-grow: 0;
}

.text-title2-introduction {
  width: 900px;
  height: auto;

  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 600;
  font-size: 46px;
  line-height: 46px;
  /* identical to box height, or 57% */

  color: #000000;

  /* Inside auto layout */

  flex: none;
  order: 1;
  flex-grow: 0;
}

.text-content-introduction {
  width: 799px;
  height: auto;

  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 400;
  font-size: 18px;
  line-height: 30px;
  /* or 167% */

  color: #495666;

  /* Inside auto layout */

  flex: none;
  order: 2;
  flex-grow: 0;
}
.item-box {
  display: flex;
  padding: 40px 0 20px 0;
}

.text_2_1-introduction {
  width: 799px;
  height: auto;

  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 500;
  font-size: 18px;
  line-height: 30px;
  /* or 167% */

  color: #000000;

  /* Inside auto layout */

  flex: none;
  order: 0;
  flex-grow: 0;
  margin-top: 20px;
  /* background-color: bisque; */
}

.text_2_2-introduction {
  width: 799px;
  height: auto;

  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 500;
  font-size: 18px;
  line-height: 30px;
  /* or 167% */

  color: #495666;

  /* Inside auto layout */

  flex: none;
  order: 0;
  flex-grow: 0;
  margin-top: 10px;
  /* background-color: bisque; */
}

.text_2_3 {
  position: absolute;
  width: 162px;
  height: 30px;
  /* left: 0px;
    top: 0px; */

  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 600;
  font-size: 90px;
  line-height: 30px;
  /* identical to box height, or 33% */

  color: #000000;
  text-align: left;
}

.text_2_4 {
  position: absolute;
  width: 300px;
  height: 30px;
  left: 0px;
  top: 60px;

  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 400;
  font-size: 20px;
  line-height: 30px;
  /* identical to box height, or 150% */

  color: #000000;
  text-align: left;
}

.text_3_1 {
  position: absolute;
  width: 799px;
  height: 90px;
  /* left: 560px;
    top: 1036px; */

  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 500;
  font-size: 18px;
  line-height: 30px;
  /* or 167% */

  color: #000000;

  margin-top: 5px;
}

.text_3_2 {
  position: absolute;
  width: 799px;
  height: 90px;
  /* left: 560px;
    top: 1036px; */

  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 400px;
  font-size: 18px;
  line-height: 25px;
  /* or 167% */

  color: #495666;

  margin-top: 40px;
}
</style>
