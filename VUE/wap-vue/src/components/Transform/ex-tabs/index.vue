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
    font-size: 28px;
    display: flex;
    margin-top: 52px;
    justify-content: center;
    align-items: center;

    .ex-tab {
        text-align: center;
        flex: 1;
        padding: 10px 17px !important;
        border-radius: 4px;
        color: #787E8C;
    }
}

.ex-tabs .active {
    background: $btn_main;
    color: $white;
}
</style>