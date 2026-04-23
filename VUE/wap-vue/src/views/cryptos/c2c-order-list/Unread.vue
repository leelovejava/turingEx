<template>
  <div id="cryptos">
    <div class="flex flex-col w-full h-full">
      <order-nav class="w-full" :back="false" @back="hiddenUnread">
        <template #title>
          {{ $t('未读消息') }}
        </template>
      </order-nav>
      <div class="w-full flex-1 overflow-auto">
        <van-list v-model="loading" :finished="finished" :finished-text="$t('已经全部加载完毕')" @load="onLoad">
          <items v-for="(item, index) in list" :key="index" :items="item" />
        </van-list>
      </div>
    </div>
  </div>
</template>

<script>
import {
  List,
} from "vant";
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import Items from "../orderList/items.vue";

export default {
  name: "Unread",
  props: ['data'],
  data() {
    return {
      list: [],
      loading: false,
      finished: false,
    }
  },
  methods: {
    hiddenUnread() {
      this.$emit('back')
    },
    onLoad() {
      // 异步更新数据
      // setTimeout 仅做示例，真实场景中一般为 ajax 请求
      setTimeout(() => {
        for (let i = 0; i < this.data.length; i++) {
          this.list.push(this.data[i]);
        }

        // 加载状态结束
        this.loading = false;

        // 数据全部加载完成
        if (this.list.length >= 10) {
          this.finished = true;
        }
      }, 1000);
    },
  },
  components: {
    [List.name]: List,
    OrderNav,
    Items,
  }
}
</script>

<style scoped></style>