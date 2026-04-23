<template>
    <div class="contentCom-item">
        <div class="flex items-center text-30 textColor justify-between222">
            <img v-if="state" src="../../assets/image/idImg/kfc.png" class="img2" alt="" />
            <img v-else src="../../assets/image/idImg/nokfc.png" class="img2" alt="" />
            <span class="ml-24 textColor title">{{ contentObj.title }}</span>
        </div>
        <div class="flex items-center  text-30 justify-between justify-between222" v-for="(item, index) in contentObj.list"
            :key="index">
            <span class="text-grey text">{{ item.left }}</span>
            <span class="textColo text">{{ item.right }}</span>
        </div>
    </div>
</template>

<script setup>
const props = defineProps({
    contentObj: {
        type: Object,
        default() {
            return {
                title: '',
                list: [
                    {
                        left: '',
                        right: ''
                    }
                ]
            }
        }
    },
    state: {
        type: Boolean,
        default: false
    }
})
</script>

<style lang="scss" scoped>
.contentCom-item {
    font-size: 40px;

    .title {
        font-size: 14px;
    }

    .text {
        font-size: 14px;
    }
}

.img2 {
    width: 20px;
    height: 20px;
}

.ml-24 {
    margin-left: 1rem;
}

.justify-between222 {
    margin-top: 2rem;
    line-height: 2rem;
    height: 2rem;
}

.justify-between222 span {
    font-size: 1rem;
}
</style>