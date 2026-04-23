<template>
    <div id="cryptos">
        <div class="loading w-full h-full flex justify-center items-center loading">
            <van-loading />
        </div>
    </div>
</template>

<script>
import { Loading } from 'vant';
export default {
    name: 'LoadingComp',
    components: {
        [Loading.name]: Loading
    }
}
</script>

<style lang="scss" scoped>
#cryptos {
    .loading {
        background: rgba(0, 0, 0, 0.6);
    }
}
</style>