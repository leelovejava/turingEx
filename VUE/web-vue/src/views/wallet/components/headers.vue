<template>
  <div class="headbg">
    <div class="head-content flex">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 24 24"
        fill="none"
        class="css-146agw4"
        @click="gotoPrePage"
      >
        <path
          fill-rule="evenodd"
          clip-rule="evenodd"
          d="M11.934 12l3.89 3.89-1.769 1.767L8.398 12l1.768-1.768 3.89-3.889 1.767 1.768-3.889 3.89z"
          fill="currentColor"
        ></path>
      </svg>
      <div>{{ props.title }}</div>
    </div>
  </div>
</template>
<script setup>
import { useRouter } from "vue-router";
const props = defineProps({
  title: {
    type: String,
    default: "",
  },
});
const router = useRouter();
const gotoPrePage = () => {
  router.go(-1);
};
</script>
<style lang="scss" scoped>
.headbg {
  background: #fafafb;
  font-weight: 600;
  font-size: 24px;
  .head-content {
    padding: 24px 16px 24px 360px;
    svg {
      width: 36px;
      cursor: pointer;
    }
  }
}
</style>
