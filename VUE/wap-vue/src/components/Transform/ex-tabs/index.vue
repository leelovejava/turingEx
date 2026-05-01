<template>
    <div class="ex-tabs">
        <div class="ex-tab" v-for="(item, index) in listArr" :key="index" @click="changeTab(index)"
            :class="active == index ? 'active' : ''">
            {{ item.name }}
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            active: 0,
            listArr: [
                { name: this.$t('热门') },
                { name: this.$t('涨幅榜') },
                { name: this.$t('跌幅榜') },
                { name: this.$t('24h成交额') }
            ],
        }
    },
    methods: {
        changeTab(index) {
            this.active = index;
            this.$emit('tabs', index)
        },
    },
    activated() {
        this.listArr = [
            { name: this.$t('热门') },
            { name: this.$t('涨幅榜') },
            { name: this.$t('跌幅榜') },
            { name: this.$t('24h成交额') }
        ]
    },
}
</script>

<style lang="scss" scoped>
.ex-tabs {
  display: flex;
  margin-top: 12px;
  gap: 8px;

  .ex-tab {
    flex: 1;
    height: 58px;
    line-height: 58px;
    text-align: center;
    border-radius: 29px;
    color: #8f96a8;
    background: rgba(255, 255, 255, 0.08);
    border: 1px solid rgba(255, 255, 255, 0.12);
    font-size: 28px;
    font-weight: 600;
    white-space: nowrap;
  }
}

.ex-tabs .active {
    background: #f4f5f7;
    border-color: #f4f5f7;
    color: #191f2b;
    font-weight: 700;
}
</style>
