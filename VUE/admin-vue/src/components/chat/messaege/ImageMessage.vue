<template>
  <div class="image-message no-select">
    <el-image
      fit="cover"
      :src="src1"
      :lazy="true"
      :style="getImgStyle(src1)"
      @click="open"
       v-if="status!=1"
    >
      <div slot="error" class="image-slot">图片加载失败...</div>
      <div slot="placeholder" class="image-slot">图片加载中...</div>
    </el-image>
    <div class="loading" v-if="status==1"></div>
  </div>
</template>
<script>
import { imgZoom } from '@/utils/functions'
import { openImage} from "@/common/image";
export default {
  data () {
    return {
      status : 0,
      src1 : "./load.png"
    }
  },
  name: 'ImageMessage',
  props: {
    src: {
      type: String,
      default: '',
    },
  },
  methods: {
    getImgStyle(url) {
      return imgZoom(url, 200)
    },
    open(){
      switch(this.status)
      {
        case 0: //点击加载
          
          this.status = 1;
          let timer = setTimeout(() => {
              clearTimeout(timer);
            this.src1 = this.src;
            this.status = 2;
          },1500);
          break;
        case 2: //打开界面
          openImage(this.src)
          break;
        case 0:
          break;
      } 
    }
  },
}
</script>
<style lang="less" scoped>
.image-message {
  /deep/.el-image {
    border-radius: 5px;
    cursor: pointer;
    background: #f1efef;

    .image-slot {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 100%;
      height: 100%;
      font-size: 13px;
      color: #908686;
      background: #efeaea;
    }
  }
}
</style>

<style scoped>
.loading {
  position: relative;
  width: 200px;
  height: 200px;
  border: 2px solid #000;
  border-top-color: rgba(0, 0, 0, 0.2);
  border-right-color: rgba(0, 0, 0, 0.2);
  border-bottom-color: rgba(0, 0, 0, 0.2);
  border-radius: 100%;

  animation: circle infinite 0.75s linear;
}

@keyframes circle {
  0% {
    transform: rotate(0);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>