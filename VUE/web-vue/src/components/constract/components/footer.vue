<template>
  <!-- 最下面横条 -->
  <div class="overbottom">
    <span class="overtitle">
      <img src="@/assets/images/commonTrade/wifi.png" />
      {{ $t("message.home.xitonglianjiewending") }}
    </span>

    <span class="twoj">
      <i class="el-icon-right"></i>
      <i class="el-icon-back"></i>
    </span>
    <div
      v-for="(item, index) in footerLeftList"
      class="item"
      data-type="COIN"
      data-id="AGLD"
      :key="index"
    >
      <span class="name">{{ item.name }}</span>
      <span
        :class="[
          'text-end',
          item.change_ratio >= 0 ? 'color-up' : 'color-down',
        ]"
        >{{ addAndSubtr(item.change_ratio) }}</span
      >
      <span class="name">{{ item.close }}</span>
    </div>

    <div class="overright">
      <span>
        <i class="el-icon-bell"></i>
        {{ $t("message.home.announcement") }}
      </span>
      <span>
        <i class="el-icon-download"></i>
        {{ $t("message.home.xiazai") }}
      </span>
      <span>
        <i class="el-icon-chat-dot-square"></i>
        {{ $t("message.home.service") }}
      </span>
    </div>
  </div>
</template>
<script setup>
import { addAndSubtr } from "@/utils/index";
const props = defineProps({
  footerLeftList: {
    type: Array,
  },
});
</script>
