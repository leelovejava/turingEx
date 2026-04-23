<template>
  <div id="c2c_settings" class="w-full h-full">
    <div class="flex flex-col w-full h-full">
      <order-nav>
        <template #title>
          {{ $t('通知设置') }}
        </template>
      </order-nav>
      <div class="flex-1 overflow-auto">
        <settings-item :options="options.order" @changeSwitch="changeSwitch">
          <template>
            <h2 class="mb-10 text-24 font-normal">{{ $t('订单') }}</h2>
            <p class="text-20">{{ $t('接收新订单和订单状态变化的消息，请至少开启1项通知。') }}</p>
          </template>
        </settings-item>
        <settings-item class="mt-44" :options="options.appeal" @changeSwitch="changeSwitch">
          <template>
            <h2 class="mb-10 text-24 font-normal">{{ $t('申诉') }}</h2>
            <p class="text-20">{{ $t('接收新申诉和申诉状态变化的消息，请至少开启1项通知。') }}</p>
          </template>
        </settings-item>
        <!--        <settings-item class="mt-44" :options="options.chat" @changeSwitch="changeSwitch">-->
        <!--          <template>-->
        <!--            <h2 class="mb-10 text-24 font-normal">{{$t('聊天')}}</h2>-->
        <!--            <p class="text-20">{{$t('接收聊天消息，请开启此项通知')}}</p>-->
        <!--          </template>-->
        <!--        </settings-item>-->
        <!--        <settings-item class="mt-44" :options="options.safe" @changeSwitch="changeSwitch">-->
        <!--          <template>-->
        <!--            <h2 class="mb-10 text-24 font-normal">{{$t('安全')}}</h2>-->
        <!--            <p class="text-20">{{$t('接收安全与隐私提示等消息，邮件和短信无法手动关闭。')}}</p>-->
        <!--          </template>-->
        <!--        </settings-item>-->
      </div>
    </div>
  </div>
</template>

<script>
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import SettingsItem from "@/views/cryptos/c2cUser/components/SettingsItem.vue";
import otcApi from "@/service/otc.api";

export default {
  name: "c2cNoticeSettings",
  data() {
    return {
      options: {
        // 订单
        order: [
          { title: this.$t('邮件'), checked: true, type: 'mail' },
          { title: this.$t('短信'), checked: false, type: 'sms' },
          // { title: this.$t('APP通知'), checked: false, type: 'app' },
        ],
        // 申诉
        appeal: [
          { title: this.$t('邮件'), checked: false, type: 'mail' },
          { title: this.$t('短信'), checked: false, type: 'sms' },
          // { title: this.$t('APP通知'), checked: false, type: 'app' },
        ],
        // // 聊天
        // chat: [
        //   { title: this.$t('APP通知'), checked: false, type: 'app' },
        // ],
        // // 安全
        // safe: [
        //   { title: this.$t('邮件'), checked: true, disabled: true, type: 'mail' },
        //   { title: this.$t('短信'), checked: true, disabled: true, type: 'sms' },
        //   { title: this.$t('APP通知'), checked: false, type: 'app' },
        // ]
      },
      ctc_user: {},
      order_mail_obj: {},
      order_sms_obj: {},
      order_app_obj: {},
      appeal_mail_obj: {},
      appeal_sms_obj: {},
      appeal_app_obj: {},
      safe_mail_obj: {},
      safe_sms_obj: {},
      safe_app_obj: {},
    }
  },
  components: {
    OrderNav,
    SettingsItem,
  },
  mounted() {
    let ctc_user = this.$route.query.ctc_user
    this.ctc_user = JSON.parse(ctc_user)
    this.init()
  },
  methods: {
    init() {
      this.options.order.forEach(item => {
        if (item.type == 'mail') {
          item.checked = Boolean(this.ctc_user.orderMailNoticeOpen)
        } else if (item.type == 'sms') {
          item.checked = Boolean(this.ctc_user.orderSmsNoticeOpen)
        } else if (item.type == 'app') {
          item.checked = Boolean(this.ctc_user.orderAppNoticeOpen)
        }
      })
      this.options.appeal.forEach(item => {
        if (item.type == 'mail') {
          item.checked = Boolean(this.ctc_user.appealMailNoticeOpen)
        } else if (item.type == 'sms') {
          item.checked = Boolean(this.ctc_user.appealSmsNoticeOpen)
        } else if (item.type == 'app') {
          item.checked = Boolean(this.ctc_user.appealAppNoticeOpen)
        }
      })
      // this.options.chat.forEach(item => {
      //   if (item.type == 'app') {
      //     item.checked = Boolean(this.ctc_user.chat_app_notice_open)
      //   }
      // })
      //
      // this.options.appeal.forEach(item => {
      //   if (item.type == 'app') {
      //     item.checked = Boolean(this.ctc_user.securityAppNoticeOpen)
      //   }
      // })
    },
    ctcUserSet() {
      otcApi.ctcUserSet({
        order_mail_notice_open: Number(this.order_mail_obj.checked),
        order_sms_notice_open: Number(this.order_sms_obj.checked),
        order_app_notice_open: Number(this.order_app_obj.checked),
        appeal_mail_notice_open: Number(this.appeal_mail_obj.checked),
        appeal_sms_notice_open: Number(this.appeal_sms_obj.checked),
        appeal_app_notice_open: Number(this.appeal_app_obj.checked),
        chat_app_notice_open: Number(this.chat_app_obj.checked),
        security_mail_notice_open: Number(this.safe_mail_obj.checked),
        security_sms_notice_open: Number(this.safe_sms_obj.checked),
        security_app_notice_open: Number(this.safe_app_obj.checked),
      }).then(res => {
        console.log(res)
      }).catch(err => {

      })
    },
    changeSwitch() {
      this.order_mail_obj = this.options.order.find(item => item.type == 'mail')
      this.order_sms_obj = this.options.order.find(item => item.type == 'sms')
      this.order_app_obj = this.options.order.find(item => item.type == 'app')
      this.appeal_mail_obj = this.options.appeal.find(item => item.type == 'mail')
      this.appeal_sms_obj = this.options.appeal.find(item => item.type == 'sms')
      this.appeal_app_obj = this.options.appeal.find(item => item.type == 'app')
      this.chat_app_obj = this.options.chat.find(item => item.type == 'app')
      this.safe_mail_obj = this.options.safe.find(item => item.type == 'mail')
      this.safe_sms_obj = this.options.safe.find(item => item.type == 'sms')
      this.safe_app_obj = this.options.safe.find(item => item.type == 'app')
      this.ctcUserSet()
    }
  }
}
</script>

<style scoped></style>
