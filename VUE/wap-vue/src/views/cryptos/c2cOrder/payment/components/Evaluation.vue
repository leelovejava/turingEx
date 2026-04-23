<template>
  <div class="text-center">
    <div class="pt-80 pb-50 text-28">
      <slot name="desc"></slot>
    </div>
    <div class="flex justify-between px-72 ">
      <div class="relative w-320 h-80 box-border py-16 border-radius">
        好评
        <div class="circle flex justify-center items-center">
          <img class="w-32 h-32" src="~@/assets/image/c2c/el_thumbs-up.png" alt="">
        </div>
      </div>
      <div class="relative w-320 h-80 box-border py-16 border-radius">
        差评
        <div class="circle flex justify-center items-center">
          <img class="w-32 h-32" src="~@/assets/image/c2c/el_thumbs-up(2).png" alt="">
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Evaluation"
}
</script>

<style lang="scss" scoped>
.border-radius {
  border-radius: 60px;
  background: #EAEBEE;
}

.circle {
  position: absolute;
  top: 4px;
  left: 4px;
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: #fff;
}
</style>