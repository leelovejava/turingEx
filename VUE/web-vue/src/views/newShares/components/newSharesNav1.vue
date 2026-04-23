<template>
    <!-- 新股认购 -->
    <div class="newSharesNav1">
        <div class="newSharesNav1-box" v-for="item in tableList" :key="item.uuid">
            <div class="fs-18 font-bold font-Omit" :title="item.name" style="width: 20%;margin-right: 10px;">
                {{ item.name }}
            </div>
            <div class="flex-column-center" style="width: 10%;margin-right: 10px;">
                <p class="fs-12 new-shares-title-color" style="margin-bottom: 5px;">{{t('message.user.zhuangtai')}}</p>
                <p :class="['fs-14',item.status === 2 ? 'isRed' : '']">{{t(`message.user.${getStatus(item.status)}`)}}</p>
            </div>
            <div class="flex-column-center" style="width: 16%;margin-right: 10px;">
                <p class="fs-12 new-shares-title-color" style="margin-bottom: 5px;">{{t('message.user.jiezhiri')}}</p>
                <p class="fs-14">{{item.endSubscribeDate}}</p>
            </div>
            <div class="flex-column-center" style="width: 10%;margin-right: 10px;">
                <p class="fs-12 new-shares-title-color" style="margin-bottom: 5px;">{{t('message.user.chajia')}}</p>
                <p class="fs-14 isRed">{{item.priceDifference}}</p>
            </div>
            <div class="flex-column-center" style="width: 10%;margin-right: 10px;">
                <p class="fs-12 new-shares-title-color" style="margin-bottom: 5px;">{{t('message.user.shijia1')}}</p>
                <p class="fs-14">{{item.marketPrice}}</p>
            </div>
            <div class="flex-column-center" style="width: 12%;margin-right: 10px;">
                <p class="fs-12 new-shares-title-color" style="margin-bottom: 5px;">{{t('message.user.chengxiaojia')}}</p>
                <p class="fs-14" style="color: #1194F7;">{{item.underwritingPrice}}</p>
            </div>
            <div style="width: 10%;margin-right: 10px;">
                <el-progress type="circle" :stroke-width="7" :percentage="item.priceDifferenceValue" color="#F33368" :width="80">
                    <template #default>
                        <p class="fs-12 text-white" style="margin-bottom: 1px;">{{item.priceDifferenceValue}}%</p>
                        <p class="fs-12 text-white font-Omit" style="width: 70%" :title="t('message.user.yichajia')">{{t('message.user.yichajia')}}</p>
                    </template>
                </el-progress>
            </div>
            <div class="flex-column-center align-center" style="width: 12%;">
                <p class="fs-12 new-shares-title-color" style="margin-bottom: 1px;">{{t('message.user.zongchouqianzhang')}}</p>
                <p class="fs-14" style="margin-bottom: 7px;">{{item.subscribeTotalNumber}}</p>
                <div>
                    <button 
                    v-if="item.status === 2 && getShareStatus(item.shareStatus)"
                    :class="['newSharesNav1-box-button','button-text-style','fs-14',item.shareStatus === 2 ? 'isRed' : '']" 
                    @click="toPath(item)">
                    {{ t(`message.user.${getShareStatus(item.shareStatus)}`) }}{{ ' >' }}
                    </button>
                </div>
                
            </div>
        </div>
    </div>
</template>

<script setup>
    import { useRouter } from 'vue-router';
    import { useI18n } from "vue-i18n";

    const { t } = useI18n();
    const router = useRouter();

    const props = defineProps({
        tableList:{
            type:Array,
            default:()=>[]
        }
    })

    // 状态
    const getStatus = computed(()=>{
        return function(value){
            let result = '';
            switch (value) {
                case 1:
                    result = 'weikaishi'
                    break;
                case 2:
                    result = 'kaifangzhong'
                    break;
                case 3:
                    result = 'yijieshu'
                    break;
                default:
                    break;
            }
            return result;
        }
    })

    // 按钮
    const getShareStatus = computed(()=>{
        return function(value){
            let result = '';
            switch (value) {
                case 1:
                    result = 'chouqian'
                    break;
                case 2:
                    result = 'renjiao'
                    break;
                default:
                    result = ''
                    break;
            }
            return result;
        }
    })

    const toPath = (item)=>{
        switch (item.shareStatus) {
            case 1:
                router.push({
                    path:'/newShareSrecord/draw',
                    query:{
                        id:item.uuid,
                    }
                })
                break;
            case 2:
                router.push({
                    path:'/newShareSrecord/pay',
                    query:{
                        id:item.uuid,
                    }
                })
                break;
            default:
                break;
        }
    }
</script>

<style lang="scss" scoped>

    .newSharesNav1{
        :deep(.newSharesNav1-box){
            width: 100%;
            background: #27293B;
            color: #fff;
            display: flex;
            align-items: center;
            padding: 15px 30px;
            border-radius: 14px;
            margin-bottom: 30px;
            &:nth-last-of-type(1){
                margin-bottom: 0;
            }
            .el-progress{
                border-radius: 50%;
                background: #131A2E;
                .el-progress__text{
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                }
            }
            :deep(.el-progress-circle){
                width: 60px;
                height: 60px;
            }
            .newSharesNav1-box-button{
                min-width: 96px;
                height: 32px;
                border-radius: 5px;
                background: #333B50;
                color: #1194F7;
            }
        }
    }
    :deep(.el-progress-circle__track) {
        stroke: #2C344A !important;
    }
    .isRed{
        color: #F33368 !important;
    }
</style>