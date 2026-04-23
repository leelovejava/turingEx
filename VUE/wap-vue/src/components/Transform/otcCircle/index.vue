<template>
  <div id="cryptos">
    <div class="flex items-center justify-center otcCircle" style="position: relative">
      <van-count-down @finish="finish" :time="fullTime" class="count text-24" format="mm:ss" @change="handleChange" />
      <div v-for="(item, index) in arr" :key="index" class="circle bg-blue" :class="handleClass(item)"></div>
    </div>
  </div>
</template>

<script>

import { CountDown } from "vant";
export default {
  components: {
    [CountDown.name]: CountDown,
  },
  name: "otcCircle",
  props: ['time', 'expireTime'],
  data() {
    return {
      // time: 2 * 60 * 1000, // 剩余时间 15分钟，
      currentTime: '', // 当前时间
      progress: '', // 进度
      arr: [
        {
          value: 0,
          active: false,
          percent: 8.33
        },
        {
          value: 30,
          active: false,
          percent: 16.66
        },
        {
          value: 60,
          active: false,
          percent: 25
        },
        {
          value: 90,
          active: false,
          percent: 33.33
        },
        {
          value: 120,
          active: false,
          percent: 41.66
        },
        {
          value: 150,
          active: false,
          percent: 50
        },
        {
          value: 180,
          active: false,
          percent: 58.33
        },
        {
          value: 210,
          active: false,
          percent: 66.66
        },
        {
          value: 240,
          active: false,
          percent: 75
        },
        {
          value: 270,
          active: false,
          percent: 83.33
        },
        {
          value: 300,
          active: false,
          percent: 91.66
        },
        {
          value: 330,
          active: false,
          percent: 100
        }
      ],
    }
  },
  computed: {
    fullTime() {
      return this.time * 1 * 1000;
      // return parseInt(this.time) * 60
    }
  },
  methods: {
    handleChange(e) {
      let restTime = e.minutes * 60 * 1000 + e.seconds * 1000 + e.milliseconds // 剩余毫秒
      this.progress = ((restTime / 1000) / this.expireTime * 100).toFixed(0) // 进度
      // console.log('进度', this.progress)
      this.arr.forEach(item => {
        if (this.progress >= item.percent) {
          item.active = true
        } else {
          item.active = false
        }
      })
    },
    handleClass(item) {
      let a = `circle${item.value}`
      let b = item.active ? 'active' : ''
      return a + ' ' + b
    },
    // 计时结束
    finish() {
      this.$emit('finish');
    }
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  .line {
    width: 60px;
    height: 60px;

    border-radius: 50%;
    position: relative;
    //animation: myRotate 10s linear infinite;
  }


  @for $i from 0 through 400 {
    .circle#{$i} {
      transform: rotateZ(#{$i}deg) translateY(-50px);
    }
  }

  //.active {
  //  background: #ccc;
  //}
  .active {
    background: #CCCCCC !important;
  }

  .count {
    font-size: 20px;
  }

  .circle {
    position: absolute;
    width: 7px;
    height: 26px;
    line-height: 60px;
    text-align: center;
    color: #fff;
    border-radius: 2px;
    left: 50%;
    top: 50%;
    margin-top: -13px;
  }

  .otcCircle :deep(.van-count-down) {
    color: #1D91FF;
  }
}
</style>
