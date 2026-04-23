<template>
  <div>
    <div
      class="content-view-box"
      style="
        margin-top: 70px;
        background-color: #ffffff;
        height: auto;
        width: 1200px;
      "
    >
      <div class="text-title-0">{{ $t("message.home.gouzhiyuzhuanhuan") }}</div>
      <div
        style="
          margin-top: 0px;
          background-color: #ffffff;
          height: 50px;
          width: 1200px;
        "
      ></div>
      <div v-for="index of 8" :key="index">
        <div>
          <div class="text_0">
            {{ $t("message.home.gouzhizhuanhuan_" + index) }}
          </div>
        </div>
      </div>
      <div
        style="
          margin-top: 0px;
          background-color: #ffffff;
          height: 50px;
          width: 1200px;
        "
      ></div>
      <img
        src="@/assets/images/exchangeHome/other/purchase.png"
        width="1200"
        height="1499"
      />
    </div>
    <div
      style="
        margin-top: 0px;
        background-color: #ffffff;
        height: 50px;
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
  components: { FooterView },
  data() {
    return {};
  },
};
</script>

<style scoped>
.text-title-0 {
  width: 400px;
  height: 26px;

  font-weight: 500;
  font-size: 32px;
  line-height: 26px;

  color: #000000;

  flex: none;
  order: 0;
  flex-grow: 0;
}

.text-title2 {
  width: 644px;
  height: 26px;
  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 600;
  font-size: 46px;
  line-height: 26px;
  color: #000000;
  flex: none;
  order: 1;
  flex-grow: 0;
}

.text-content {
  width: 799px;
  height: 60px;
  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 400;
  font-size: 18px;
  line-height: 30px;
  color: #495666;
  flex: none;
  order: 2;
  flex-grow: 0;
}
.item-box {
  display: flex;
  padding: 40px 0 20px 0;
}

.text_2_1 {
  width: 799px;
  height: 30px;
  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 500;
  font-size: 18px;
  line-height: 30px;
  color: #000000;
  flex: none;
  order: 0;
  flex-grow: 0;
  margin-top: 20px;
  /* background-color: bisque; */
}

.text_2_2 {
  width: 799px;
  height: 60px;

  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 500;
  font-size: 18px;
  line-height: 30px;
  color: #495666;
  flex: none;
  order: 0;
  flex-grow: 0;
  margin-top: 10px;
}

.text_2_3 {
  position: absolute;
  width: 162px;
  height: 30px;
  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 600;
  font-size: 90px;
  line-height: 30px;
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

  color: #000000;
  text-align: left;
}

.text_3_1 {
  position: absolute;
  width: 799px;
  height: 90px;
  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 500;
  font-size: 18px;
  line-height: 30px;
  color: #000000;

  margin-top: 5px;
}

.text_3_2 {
  position: absolute;
  width: 799px;
  height: 90px;
  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 400px;
  font-size: 18px;
  line-height: 25px;
  color: #495666;
  margin-top: 40px;
}

.text_0 {
  width: 1200px;
  height: auto;
  font-family: "PingFang SC";
  font-style: normal;
  font-weight: 400;
  font-size: 15px;
  color: #000000;
  flex: none;
  order: 1;
  flex-grow: 0;
}
</style>
