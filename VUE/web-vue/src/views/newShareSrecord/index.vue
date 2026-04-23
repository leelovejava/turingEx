<template>
    <div class="newShareSrecord">
        <header class="newShareSrecord-header">
            <div class="newShareSrecord-header-title" >
                <img v-if="uuid" src="@/assets/images/newShares/leftArrow.png" class="newShareSrecord-header-title-img" @click="goBack">
                <p class="fs-24 font-bold text-white">{{ t(`message.user.${navTitle}`) }}</p>
            </div>
            <div class="newShareSrecord-header-info">
                <button class="fs-14 text-white" @click="toPath(navSubtitle.path)" v-if="navSubtitle.title">{{ t(`message.user.${navSubtitle.title}`) }}</button>
            </div>
        </header>
        <div class="newShareSrecord-box">
            <router-view></router-view>
        </div>
    </div>
</template>

<script setup>
    import { useRoute , useRouter } from 'vue-router'
    import { useI18n } from "vue-i18n";

    const { t } = useI18n();
    const route = useRoute ();
    const router = useRouter()
    const navTitle = ref('chouqian')
    const navSubtitle = ref({
        title:'',
        path:'',
        type:1
    })
    const uuid = ref('')
    watch(route,(newVal)=>{
        const { title } = newVal.meta;
        const { id } = newVal.query;
        
        if(title){
            navTitle.value = title
        }

        if(id){
            uuid.value = id;
        }else{
            uuid.value = '';
        }
        
        switch (route.name) {
            // 抽签
            case 'draw':
                navSubtitle.value.title = 'chouqianjilu';
                navSubtitle.value.path = '/newShareSrecord/drawRecord';
                navSubtitle.value.type = 1;
                break;
            // 认缴
            case 'pay':
                navSubtitle.value.title = 'renjiaojilu';
                navSubtitle.value.path = '/newShareSrecord/payRecord';
                navSubtitle.value.type = 2;
                break;
            default:
                navSubtitle.value.title = '';
                navSubtitle.value.path = '';
                break;
        }
    },{immediate:true})
    
    const goBack = ()=>{
        router.go(-1)
    }

    const toPath = (path)=>{
        router.push({
            path,
            query:{
                id:uuid.value,
                // type:navSubtitle.value.type
            }
        })
    }
    
</script>

<style lang="scss" scoped>
    .newShareSrecord{
        width: 100%;
        .newShareSrecord-header{
            height:80px;
            background: #282A3A;
            padding: 0 360px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            .newShareSrecord-header-title{
                display: flex;
                align-items: center;
                .newShareSrecord-header-title-img{
                    width: 14px;
                    height: 24px;
                    margin-right: 18px;
                    cursor: pointer;
                }
            }
            .newShareSrecord-header-info{
                cursor: pointer;
            }
        }
        .newShareSrecord-box{
            min-height: 100vh;
            padding: 30px 360px;
            background: #191B28;
        }
    }
</style>