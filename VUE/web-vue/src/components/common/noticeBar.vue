<!-- 公告 -->
<template>
  <div class="roll content-view-box">
    <img
      src="@/assets/images/exchangeHome/trumpet.png"
      alt=""
      class="el-icon-bell"
      style="width: 28px; height: 28px"
    />
    <ul class="list">
      <li
        v-for="(item, index) in ulList"
        :key="item.id"
        :class="[!index && play ? 'toUp' : '']"
        @click="toDetail(item)"
      >
        {{ item.title }}
      </li>
    </ul>
    <!-- <img src="../../assets/newImages/home/noticeMore.png" alt="" class="el-icon-bell" style="width:28px;height:28px;" /> -->
  </div>
</template>
<script>
import Axios from "@/api/my";
export default {
  data() {
    return {
      ulList: [],
      play: false,
      interval: null,
      timer: null, // //接收定时器
    };
  },
  mounted() {
    this.$nextTick(()=>{
      Axios.news({
        page_no: 1,
      }).then((res) => {
        this.ulList = res.data;
      });
      //页面挂载完成时就开始定时器，公告文字滚动
      this.interval = setInterval(this.startPlay, 4000);
    })
  },
  unmounted() {
    // 页面销毁时清除定时器
    if(this.timer){
      clearTimeout(this.timer);
      this.timer = null
    }

    if(this.interval){
      clearInterval(this.interval);
      this.interval = null
    }
  },
  methods: {
    startPlay() {
      let that = this;
      that.play = true; //开始播放
      that.timer = setTimeout(() => {
        //创建并执行定时器
        that.ulList.push(that.ulList[0]); //将第一条数据塞到最后一个
        that.ulList.shift(); //删除第一条数据
        that.play = false; //暂停播放
      }, 500);
    },
    toDetail(item) {
      this.$router.push({
        path: "/my/announcement",
        query: { type: "announcement" },
      });
      // console.log(item);
    },
  },
};
</script>

<style scoped>
.roll {
  height: 50px; /*关键样式*/
  line-height: 50px; /*关键样式*/
  background: #fff;
  margin-bottom: 24px;
  padding: 0 20px;
  color: 707A8A;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.el-icon-bell {
  margin-right: 10px;
  display: inline-block;
}

.toUp {
  margin-top: -50px; /*关键样式*/
  transition: all 1s; /*关键样式*/
}
.list {
  list-style: none;
  width: 100%;
  text-align: center;
  overflow: hidden; /*关键样式*/
  height: 50px; /*关键样式*/
  padding: 0;
  margin: 0;
}
.list > li {
  text-align: left;
  height: 50px; /*关键样式*/
  line-height: 50px; /*关键样式*/
  font-size: 18px;
  color: #707a8a;
}
</style>
