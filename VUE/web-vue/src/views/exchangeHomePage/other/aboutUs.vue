<template>
  <div>
    <div class="text-title">{{ $t("message.user.aboutus") }}</div>
    <div class="content-view-box text-content">
      <p>{{ $t("message.user.aboutusonetext") }}</p>
      <p>{{ $t("message.user.aboutustwotext") }}</p>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {};
  },
};
</script>

<style scoped>
.text-title {
  text-align: center;
  font-size: 18px;
  padding: 30px 0;
  font-weight: bold;
}

.text-content > p {
  margin: 20px 0;
}
</style>
