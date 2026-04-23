<template>
  <div v-if="isShow">
    <div class="css-vp41bv">
      <div class="css-1sirj0f">
        <div class="css-tolw2i"></div>
        <div class="css-16wz1c4">
          <div class="css-hv39bs">
            <div class="css-kewrbm">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 96 96"
                fill="none"
                class="css-1udqo2t"
              >
                <path
                  fill-rule="evenodd"
                  clip-rule="evenodd"
                  d="M88 48c0 22.091-17.909 40-40 40S8 70.091 8 48 25.909 8 48 8s40 17.909 40 40z"
                  fill="url(#general-warning_svg__paint0_linear)"
                ></path>
                <path
                  d="M48 19c16.016 0 29 12.984 29 29S64.016 77 48 77 19 64.016 19 48s12.984-29 29-29z"
                  fill="url(#general-warning_svg__paint1_linear)"
                ></path>
                <path
                  d="M45 66h6v-6h-6v6zM51 54V30h-6v24h6z"
                  fill="#14151A"
                ></path>
                <defs>
                  <linearGradient
                    id="general-warning_svg__paint0_linear"
                    x1="8"
                    y1="48"
                    x2="102.5"
                    y2="48"
                    gradientUnits="userSpaceOnUse"
                  >
                    <stop stop-color="#F0B90B" stop-opacity="0"></stop>
                    <stop offset="1" stop-color="#F0B90B"></stop>
                  </linearGradient>
                  <linearGradient
                    id="general-warning_svg__paint1_linear"
                    x1="77"
                    y1="48"
                    x2="19"
                    y2="48"
                    gradientUnits="userSpaceOnUse"
                  >
                    <stop stop-color="#F0B90B"></stop>
                    <stop offset="1" stop-color="#F8D33A"></stop>
                  </linearGradient>
                </defs>
              </svg>
            </div>
            <div class="modal-desc css-1ixmqv3">
              <div class="css-rdetsy">
                {{ $t("message.user.anquanjieshao") }}
              </div>
            </div>
            <div class="btn-group css-1tr0qpm">
              <button data-bn-type="button" class="css-fzd2zp" @click="cancel">
                <div class="css-1c82c04">
                  {{ $t("message.user.quxiao") }}
                </div></button
              ><button
                data-bn-type="button"
                class="css-13s4ljd"
                @click="keepRegist"
              >
                <div class="css-1c82c04">
                  {{ $t("message.user.jixu") }}
                </div>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";

const { t } = useI18n();

const router = useRouter();
const isShow = ref(false);

const keepRegist = () => {
  router.push("/resetpwd");
};

const cancel = () => {
  isShow.value = false;
};
</script>
<style lang="css" scoped>
@import url("@/assets/css/login/forgotpwd.css");
</style>
