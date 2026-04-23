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
    props: {

    },
    components: {

    },
    data() {
        return {
            active: 0,
            listArr: [
                {
                    name: this.$t('热门')
                },
                {
                    name: this.$t('涨幅榜')
                },
                {
                    name: this.$t('跌幅榜')
                },
                {
                    name: this.$t('24h成交额')
                }
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
            {
                name: this.$t('热门')
            },
            {
                name: this.$t('涨幅榜')
            },
            {
                name: this.$t('跌幅榜')
            },
            {
                name: this.$t('24h成交额')
            }
        ]
    },
}
</script>

<style lang="scss" scoped>
.ex-tabs {
    font-size: 22px;
    display: flex;
    margin-top: 22px;
    justify-content: center;
    align-items: center;
    background: linear-gradient(180deg, #202a40 0%, #171f31 100%);
    border-radius: 18px;
    padding: 5px;
    gap: 6px;
    border: 1px solid rgba(124, 140, 176, 0.22);
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.04);

    .ex-tab {
        text-align: center;
        flex: 1;
        height: 36px;
        line-height: 36px;
        border-radius: 13px;
        color: #9aa6c3;
        background: transparent;
        font-weight: 500;
        white-space: nowrap;
    }
}

.ex-tabs .active {
    background: linear-gradient(180deg, #ffffff 0%, #edf1f8 100%);
    color: #151d2d;
    font-weight: 700;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.22);
}
</style>
