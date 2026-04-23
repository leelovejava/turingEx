<template>
  <div class="deliveryContract">
<!--    头部-->
    <ContractHeader :title="title" :symbol="symbol" :price="price" :range="range" :selectIndex="'2'"></ContractHeader>
<!--    全仓-->
    <FullWarehouse></FullWarehouse>
<!--仓位-->
    <position></position>
  </div>
</template>
<script>
import ContractHeader from '@/components/contract-header/index.vue'
import FullWarehouse from './FullWarehouse.vue'
import position from './position.vue'
export default {
  data() {
    return {
      title:this.$t('交割'),
      symbol:'BTC',
      price:'22637.22',
      range:'-3.81%',
    }
  },
  components: { ContractHeader,FullWarehouse,position},
  mounted() {
  },
  methods: {
    onClickLeft() {
      this.$router.push('/')
    },
  }
}
</script>
<style lang="scss" scoped>
.deliveryContract{
  background: $light-grey;
}
</style>
