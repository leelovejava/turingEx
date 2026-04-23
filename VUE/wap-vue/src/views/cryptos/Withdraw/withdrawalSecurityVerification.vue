<template>
  <div id="withdrawalSecurityVerificationPage">
    <div id="withdraw_verify" class="withdraw_verify">
      <assets-head />
      <div class="content">
        <div class="title textColor">{{ $t('安全验证') }}</div>
        <div>
          <p>{{ $t('请输入资金密码') }}</p>
          <div class="iptbox inputBackground">
            <input class="inputBackground textColor" type="password" :placeholder="$t('请输入密码')" v-model="password">
          </div>
        </div>
        <div class="mt-40" v-if="this.isGoogleInput">
          <p>{{ $t('请输入谷歌验证码') }}</p>
          <div class="iptbox inputBackground">
            <input class="inputBackground textColor" type="password" :placeholder="$t('请输入谷歌验证码')" v-model="googleCode">
          </div>
        </div>
        <div class="btn btnMain" @click="confirm">{{ $t('提交') }}</div>
        <div style="color:$btn_main; margin-top:10px;"><span @click="$router.push('/resetVerify?type=0')">{{
          $t('资金密码不可用?')
        }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import assetsHead from "@/components/Transform/assets-head/index.vue";
import { Form, Field, CellGroup, showToast } from 'vant';
import { _widtGetSessionToken, _withdrawApply, _ctcOrderPass, _ctcOrderPayFinish } from "@/service/withdraw.api.js"
import { _getIsGoogleAuth } from "@/service/user.api.js"

export default {
  name: "withdrawalSecurityVerification",
  components: {
    assetsHead,
    [Form.name]: Form,
    [Field.name]: Field,
    [CellGroup.name]: CellGroup,
  },
  data() {
    return {
      password: '',
      data: null,
      sessionToken: '',
      googleCode: '',
      isGoogleInput: false,
      type: ''
    }
  },
  created() {
    // _getIsGoogleAuth({ code: 'google_auth_secret_open' }).then(res => {
    //   this.isGoogleInput = res.data.google_auth_secret_open === '1'
    // })
    this.data = this.$route.query
    if (this.$route.query.type) {
      this.type = this.$route.query.type
    }
    this.getToken()
  },
  methods: {
    getToken() {
      _widtGetSessionToken().then((res) => {
        this.sessionToken = res.session_token;
      });
    },
    onSubmit(values) {
      console.log('submit', values);
    },
    confirm(data) {
      if (this.type === 'sell' || this.type === 'buy') {
        if (!this.password) {
          showToast(this.$t('请输入资金密码'));
          return
        } else {
          if (this.type === 'buy') {
            _ctcOrderPayFinish({ order_no: this.$store.state.c2c.order_no, safe_password: this.password }).then((res) => {

              this.$router.push({ path: "/cryptos/paymentDetail" });
            })
          } else {
            _ctcOrderPass({
              order_no: this.$store.state.c2c.order_no,
              safe_password: this.password,
            }).then((res) => {
              this.isLoading = false;
              this.$router.replace({
                path: "/cryptos/tradeSuccessSell",
              });
            });
          }

        }
      } else {
        if (!this.password) {
          showToast(this.$t('请输入资金密码'));
          return
        }
        _withdrawApply({
          session_token: this.sessionToken,
          amount: this.data.amount,
          from: this.data.from,
          safeword: this.password,
          channel: this.data.channel
        }).then((res) => {

          this.$router.push({
            path: "/cryptos/withdraw/withdrawSumbit"
          });

        }).catch(err => {
          //console.log(err)
          if (err.code == 105) {
            showToast(this.$t('当前还需交易%s,才可提币', { 'MONEY': err.msg }));
          } else if (err.code === 'ECONNABORTED') {
            showToast(this.$t('网络超时！'))
          } else if (err.msg !== undefined) {
            showToast(this.$t(err.msg))
          }
          this.getToken()
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
#withdrawalSecurityVerificationPage {
  font-size: 28px;

  .withdraw_verify {
    width: 100%;
    box-sizing: border-box;
  }

  .cl {
    color: dodgerblue;
  }

  .title {
    font-weight: 700;
    font-size: 52px;
    margin-top: 54px;
    margin-bottom: 56px;
  }

  .content {
    padding: 0 32px;

    p {
      color: $text_color1;
      ;
      font-size: 30px;
      margin-bottom: 18px;
    }

    .iptbox {
      height: 88px;
      margin-top: 16px;
      padding: 0 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      border-radius: 8px;

      input {
        flex: 1;
        height: 100%;
        border: none;
      }

      span {
        color: $color_main;
      }
    }
  }

  .btn {
    color: $text_color;
    height: 88px;
    line-height: 88px;
    text-align: center;
    font-size: 32px;
    margin-top: 178px;
    border-radius: 10px;
  }
}
</style>