<template>
    <div class="login">
        <van-loading color="#1194F7" class="loading-box" v-if="isLoading"/>
        <div class="top" @click="onRoute('/my/index')"><img src="../../assets/image/icon-close.png" alt="" /></div>
        <div class="title textColor">{{ $t('login') }}</div>
        <div class="flex login-tab">
            <div class="textColor1" :class="activeIndex == 0 ? 'active' : ''" @click="changeIndex(0)">{{
                $t('account')
            }}
            </div>
            <div class="textColor1" :class="activeIndex == 1 ? 'active' : ''" @click="changeIndex(1)">{{ $t('email') }}
            </div>
            <div class="textColor1" :class="activeIndex == 2 ? 'active' : ''" @click="changeIndex(2)">{{
                $t('phoneNum')
            }}
            </div>
        </div>
        <ExInput :label="getRegType(activeIndex, true)" :placeholderText="getRegType(activeIndex, false)" v-model="username"
            :dialCode="dialCode" @selectArea="onSelectArea" :area="isArea" :icon="icon" />
        <ExInput style="padding-bottom:0!important;" :label="$t('password')" :placeholderText="$t('entryPassword')"
            v-model="password" typeText="password" />
        <div class="forget colorMain" @click="$router.push('/forget')">{{ $t('forgetPassword') }}</div>
        <van-button class="w-full" style="margin-top:50px;" type="primary" @click="verifyLogin">{{ $t('login') }}
        </van-button>
        <div class="noTips textColor">{{ $t('noAccount') }}<span class="colorMain" @click="$router.push('/register')">
                {{ $t('goRegister') }}</span>
        </div>
        <nationality-list ref='controlChildRef' :title="$t('selectArea')" @getName="getName"></nationality-list>
    </div>
</template>

<script setup>
import ExInput from "@/components/ex-input/index.vue";
import { _loginUser } from "@/service/login.api";
import { _exchangerateuserconfig } from "@/service/trade.api";
import { GET_USERINFO } from '@/store/types.store'
import { useUserStore } from '@/store/user';
import { useI18n } from 'vue-i18n'
import nationalityList from '../authentication/components/nationalityList.vue'
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { showToast } from "vant";
import store from '@/store/store'

const router = useRouter()
const { t } = useI18n()
const onRoute = (path) => {
    if (path == 'back') {
        router.go(-1)
    } else {
        router.push(path)
    }
}

let username = ref('')
let password = ref('')
let activeIndex = ref(0)
let isArea = ref(false)
let dialCode = ref(0)
let icon = ref('')
const type = ref(3)

const getRegType = (activeIndex, bFlag) => {
    switch (activeIndex) {
        case 0:
            return bFlag ? t('account') : t('entryAccount');
        case 1:
            return bFlag ? t('email') : t('entryEmail');
        case 2:
            return bFlag ? t('phoneNum') : t('entryPhone');
    }
}
const controlChildRef = ref(null)
const isLoading = ref(false)

const onSelectArea = () => {
    controlChildRef.value.open();
}

//获取到当前选中国家的code值
const getName = (params) => {
    icon.value = params.code;
    dialCode.value = params.dialCode;
}

const changeIndex = (index) => {
    activeIndex.value = index;
    switch (index) {
        case 0:
        case 1: {
            isArea.value = false;
            break;
        }
        case 2: {
            isArea.value = true;
            break;
        }
    }
}

const verifyLogin = () => {
    switch (activeIndex.value) {
        case 0:
            {
                type.value = 3;
                break;
            }
        case 1:
            {
                type.value = 2;
                break;
            }
        case 2:
            {
                type.value = 1;
                break;
            }
    }
    if (username.value == '') {
        switch (activeIndex.value) {
            case 0:
                {
                    showToast(t('entryAccount'));
                    break;
                }
            case 1:
                {
                    showToast(t('entryEmail'));
                    break;
                }
            case 2:
                {
                    showToast(t('entryPhone'));
                    break;
                }
        }
        return false
    }
    if (password.value == '') {
        showToast(t('entryPassword'));
        return false
    }
    loginUser()
}

const userStore = useUserStore();
const loginUser = () => {
    isLoading.value = true
    _loginUser({
        userName: (activeIndex.value == 0 || activeIndex.value == 1) ? username.value : `${dialCode.value}${username.value}`,
        passWord: password.value,
        type: type.value
    }).then((res) => {
        isLoading.value = false
        userStore[GET_USERINFO](res)
        store.commit('user/SET_USERINFO', res)
        router.push('/')
    }).catch((res) => {
        isLoading.value = false
        console.log(res)
    })
}
</script>

<style lang="scss" scoped>
:deep(.van-loading) {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
}

.login {
    width: 100%;
    padding: 15px;
    font-size: 13px;
    box-sizing: border-box;
}

.top {
    padding-left: 9px;
    padding-top: 9px;

    img {
        width: 18px;
        height: 18px;
    }
}

.title {
    font-weight: 700;
    font-size: 26px;
    margin-top: 27px;
    margin-bottom: 33px;
}

.login-tab {
    margin-bottom: 20px;

    div {
        padding: 0 20px;
        height: 34px;
        line-height: 34px;
        text-align: center;
        border-radius: 4px;
        margin-right: 10px;
    }

    .active {
        background: $US_tabActice_background;
        color: $color_main;
    }
}

.forget {
    font-size: 12px;
    line-height: 14px;
    margin-top: 30px;
}

.noTips {
    margin-top: 22px;
}
</style>