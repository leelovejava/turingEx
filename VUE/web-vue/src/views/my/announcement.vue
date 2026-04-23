<template>
  <div class="right-padding">
    <div class="right-title">{{ $t("message.user.gonggaozhongxin") }}</div>
    <div class="margin-top-bottom20">
      <el-collapse v-for="item in newsData" :key="item.id">
        <el-collapse-item :title="item.title" :name="item.id">
          <img :src="item.imgUrl" class="imgs" />

          <div v-html="item.content"></div>
        </el-collapse-item>
      </el-collapse>
    </div>
  </div>
</template>

<script>
import Axios from "@/api/my.js";
export default {
  name: "Announcement",
  data() {
    return {
      newsData: [],
      language: "en",
    };
  },
  mounted() {
    this.getData();
  },
  methods: {
    getData() {
      this.language = localStorage.getItem("lang");
      Axios.news().then((res) => {
        this.newsData = res.data;
      });
      //
    },
  },
};
</script>

<style scoped>
.imgs {
  width: 50%;
}
</style>
