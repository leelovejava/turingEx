<template>
  <van-tabbar class="h-160" v-model="active" route active-color="#000" inactive-color="#B8BCC5">
    <van-tabbar-item v-for="(item, index) in footer" :key="index" replace :to="item.path">
      <span>{{ item.title }}</span>
      <template #icon="props">
        <img :src="props.active ? item.icon.active : item.icon.inactive" />
      </template>
    </van-tabbar-item>
  </van-tabbar>
</template>

<script>
import { Tabbar, TabbarItem } from 'vant'

export default {
  name: 'OrderFooter',
  data() {
    return {
      active: '',
      footer: [
        {
          title: 'c2c',
          path: '/c2c/receivingBuy',
          icon: {
            active: require('@/assets/image/c2c/c2c2.png'),
            inactive: require('@/assets/image/c2c/c2c1.png'),
          },
        },
        {
          title: '订单',
          path: '/c2c/orderList',
          icon: {
            active: require('@/assets/image/c2c/order2.png'),
            inactive: require('@/assets/image/c2c/order1.png'),
          },
        },
        {
          title: '广告',
          path: '/c2c/advertise',
          icon: {
            active: require('@/assets/image/c2c/advertise2.png'),
            inactive: require('@/assets/image/c2c/advertise1.png'),
          },
        },
        {
          title: '我的',
          path: '/c2c/c2cUser',
          icon: {
            active: require('@/assets/image/c2c/user2.png'),
            inactive: require('@/assets/image/c2c/user1.png'),
          },
        },
      ],
    }
  },
  components: {
    [Tabbar.name]: Tabbar,
    [TabbarItem.name]: TabbarItem,
  },
}
</script>

<style lang="scss" scoped>
::deep(.van-tabbar) {
  box-shadow: 0 -6px 20px rgba(0, 0, 0, 0.05);
}

::deep(.van-tabbar-item__icon) {
  img {
    width: 44px;
    height: 44px;
  }
}


::deep(.van-tabbar-item__text) {
  font-size: 24px;
}
</style>
