<template>
    <div class="advancedCtf">
        <fx-header @back="loginOut">
            <template #title>
                {{ $t('advancedCertification') }}
            </template>
        </fx-header>
        <div class="flex items-center justify-center pt-5 pb-5" v-if="status != 0">
            <img src="@/assets/image/kyc/advStatus1.png" v-if="status == 1" alt="" class="advStatus" />
            <img src="@/assets/image/kyc/advStatus2.png" v-if="status == 2" alt="" class="advStatus" />
            <img src="@/assets/image/kyc/advStatus3.png" v-if="status == 3" alt="" class="advStatus" />
            <span class="text-32 textColor">{{ fixState(status) }}</span>
        </div>
        <div class="error-title text-32  py-5 px-5" v-if="status == 3">{{ errMsg }}</div>
        <div class="t2">
            <ExInput :label="$t('realName')" :disabled="true" :clearBtn="false" :placeholderText="$t('entryRealName')"
                v-model="name" />
            <ExInput :label="$t('workAddress')" :max="25" :disabled="disabled()" :clearBtn="!disabled()"
                :placeholderText="$t('verifyKyc1')" v-model="address" />
            <ExInput :label="$t('familyAddress')" :max="25" :disabled="disabled()" :clearBtn="!disabled()"
                :placeholderText="$t('verifyKyc2')" v-model="familyAddress" />
        </div>
        <div class="diviLine"></div>
        <div class="t2">
            <ExInput :label="$t('relationshipwithme')" :max="25" :disabled="disabled()" :clearBtn="!disabled()"
                :placeholderText="$t('verifyKyc3')" v-model="relationMy" />
            <ExInput :label="$t('relativeName')" :max="25" :disabled="disabled()" :clearBtn="!disabled()"
                :placeholderText="$t('verifyKyc4')" v-model="relativesName" />
            <ExInput :label="$t('relativeAddress')" :max="25" :disabled="disabled()" :clearBtn="!disabled()"
                :placeholderText="$t('verifyKyc5')" v-model="relativesAddress" />
            <ExInput :label="$t('relativePhone')" :max="25" :disabled="disabled()" :clearBtn="!disabled()"
                :placeholderText="$t('verifyKyc6')" v-model="relativesPhone" />
            <div class="btnMain text-white text-32 rounded-lg text-center t3" @click="debounceSubmit"
                v-if="status == 0 || status == 3">
                <span v-if="status == 0">{{ $t('Apply') }}</span>
                <span v-if="status == 3">{{ $t('restApply') }}</span>
            </div>
        </div>
    </div>
</template>

<script setup>
import { _getKycHighLevel, _kycHighLevelApply } from "@/service/user.api.js";
import { onMounted, ref } from 'vue';
import { useRouter } from "vue-router";
import { showToast } from "vant"
import ExInput from "@/components/ex-input/index.vue";
import { useI18n } from "vue-i18n";
import { debounce } from '@/utils/index'
const { t } = useI18n()
const router = useRouter()

const status = ref('')
const name = ref('')
const address = ref('')
const familyAddress = ref('')
const relationMy = ref('')
const relativesName = ref('')
const relativesAddress = ref('')
const relativesPhone = ref('')
const errMsg = ref('')
onMounted(() => {
    getInfo();
})
const disabled = () => {
    return ![0, 3, ''].includes(status.value)
}
const getInfo = () => {
    _getKycHighLevel()
        .then(res => {
            status.value = res.status || 0
            name.value = res.realName
            address.value = res.work_place
            familyAddress.value = res.home_place
            relationMy.value = res.relatives_relation
            relativesName.value = res.relatives_name
            relativesAddress.value = res.relatives_place
            relativesPhone.value = res.relatives_phone
            errMsg.value = res.msg
        })
}
// import ExInput from "@/components/ex-input";
// export default {


const fixState = (status) => {
    let str = ''
    if (status == 1) {
        str = t('reviewing')
    } else if (status == 2) {
        str = t('certified')
    } else if (status == 3) {
        str = t('Authenticationfailed')
    }
    return str;
}
const loginOut = () => {
    router.push('/certificationCenter')
}

const debounceSubmit = debounce(function () {
    submit()
}, 500)

const submit = () => {
    if (address.value == '' || address.value == null) {
        showToast(t('verifyKyc7'))
        return false;
    }
    if (familyAddress.value == '' || familyAddress.value == null) {
        showToast(t('verifyKyc8'))
        return false;
    }
    if (relationMy.value == '' || relationMy.value == null) {
        showToast(t('verifyKyc9'))
        return false;
    }
    if (relativesName.value == '' || relativesName.value == null) {
        showToast(t('verifyKyc10'))
        return false;
    }
    if (relativesAddress.value == '' || relativesAddress.value == null) {
        showToast(t('verifyKyc11'))
        return false;
    }
    if (relativesPhone.value == '' || relativesPhone.value == null) {
        showToast(t('verifyKyc12'))
        return false;
    }
    _kycHighLevelApply({
        work_place: address.value,
        home_place: familyAddress.value,
        relatives_relation: relationMy.value,
        relatives_name: relativesName.value,
        relatives_place: relativesAddress.value,
        relatives_phone: relativesPhone.value,
    }).then(res => {
        router.push('/verified')
        showToast(t('submitSuccess'));
    }).catch(err => {
        if (err.code === 'ECONNABORTED') { showToast('网络超时！'); }
        else if (err.msg !== undefined) { showToast(this.err.msg); }
    })
}
</script>

<style lang="scss" scoped>
.advancedCtf {
    width: 100%;
    box-sizing: border-box;
    padding-bottom: 50px;
}

.t2 {
    padding-left: 2rem;
    padding-right: 2rem;
}

.t3 {
    border-radius: 0.5rem;
    height: 36px;
    line-height: 36px;
}

.advStatus {
    width: 20px;
    height: 20px;
    margin-right: 10px;
}


.error-title {
    word-wrap: break-word;
    word-break: break-all;
    color: $red;
    text-align: center;
}
</style>