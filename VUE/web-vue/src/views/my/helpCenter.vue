<template>
  <div class="right-padding">
    <div class="right-title">{{ $t("message.user.bangzhuzhongxin") }}</div>
    <div class="margin-top-bottom20">
      <el-collapse v-for="item in helpData" :key="item.id">
        <el-collapse-item :title="item.title" :name="item.id">
          <div v-html="item.content"></div>
        </el-collapse-item>
      </el-collapse>
    </div>
  </div>
</template>

<script>
import Axios from "@/api/my.js";
export default {
  name: "helpCenter",
  data() {
    return {
      helpData: [],
    };
  },
  mounted() {
    console.log("mounted");
    this.getData();
  },
  created() {
    console.log("created");
  },
  methods: {
    getData() {
      Axios.getCms({
        model: "help_center",
      })
        .then((res) => {
          this.helpData = res.data;
        })
        .catch((e) => {
          console.log("cms error", e);
        });
      //
    },
  },
};
</script>

<style scoped>
.helps-box {
  padding: 15px;
}
.helps-box > div {
  padding-bottom: 15px;
  font-size: 32px;
  font-weight: 600;
}
</style>
