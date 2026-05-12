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
    color: $text_color1;
    background: $tab_background;
    border: 1px solid $line_color;
    font-size: 28px;
    font-weight: 600;
    white-space: nowrap;
  }
}

.ex-tabs .active {
    background: $second-bg;
    border-color: $line_color;
    color: $text_color;
    font-weight: 700;
}
</style>
