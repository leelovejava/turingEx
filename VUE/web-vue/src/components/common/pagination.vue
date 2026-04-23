<template>
  <div class="paginatio">
    <el-icon
      ><ArrowLeft
        class="page-btn"
        :disabled="noPre || pageNum == 1"
        v-click="()=>setChangePageNum('pre')"
    /></el-icon>
    <div class="pageNum">
      {{ pageNum }}
    </div>

    <el-icon
      ><ArrowRight
        class="page-btn"
        :disabled="noNext"
        v-click="()=>setChangePageNum('next')"
    /></el-icon>
  </div>
</template>

<script setup>
import { ArrowLeft, ArrowRight } from "@element-plus/icons-vue";
const emit = defineEmits('changePageNum')
const props = defineProps({
  noNext: {
    type: Boolean,
  },
  noPre: {
    type: Boolean,
  },
  pageNum: {
    type: Number,
    default: 1,
  },
  // changePageNum: {
  //   type: Function,
  // },
});

const setChangePageNum = (type)=>{
  emit('changePageNum',type)
}
</script>
<style lang="scss" scoped>
.pagitation-wrapper {
  color: #fff;
  .page-btn {
    background: #2c3138 !important;
    color: #fff !important;
    cursor: pointer;
  }
}
.paginatio {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.paginatio .pageNum {
  margin: 0 15px;
  position: relative;
}

.paginatio .page-btn {
  background: #ececec;
  color: #000;
  cursor: pointer;
}
</style>
