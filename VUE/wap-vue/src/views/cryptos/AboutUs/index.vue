<template>
  <div id="cryptos">
    <div class="CommonProblem">
      <assets-head :title="serviceTerm ? $t('服务条款') : $t('关于我们')" />
      <div class="CommonProblem-padding">
        <p class="textColor" v-html="content"></p>
      </div>
    </div>
  </div>
</template>
<script>
import assetsHead from "@/components/Transform/assets-head/index.vue";
import { _getAboutUs } from "@/service/user.api"
import { dataTimeEx } from '@/utils/utis'
import { showToast } from 'vant';
export default {
  data() {
    return {
      content: '',
      title: '',
      createTime: '',
      serviceTerm: '',
      dataTimeEx
    }
  },
  components: {
    assetsHead
  },
  mounted() {
    this.serviceTerm = this.$route.query.serviceTerm
    console.log(this.serviceTerm, 'this.serviceTerm')
    this.getCms();
  },
  methods: {
    onClickLeft() {
      this.$router.push('/')
    },
    getCms() {
      _getAboutUs({
        content_code: this.serviceTerm ? '001' : '020',
      }).then((res) => {
        this.content = res.content
        this.title = res.title
        this.createTime = this.dataTimeEx(res.createTime)

      }).catch((error) => {
        if (error.code == 'ECONNABORTED') { showToast(this.$t('网络超时！')); }
        else if (error.msg !== undefined) { showToast(this.$t(error.msg)); }
      });
    }
  }
}
</script>
<style lang="scss" scoped>
#cryptos {
  .CommonProblem {
    width: 100%;
    box-sizing: border-box;
  }

  pre {
    white-space: pre-wrap;
  }

  .CommonProblem-padding {
    padding-left: 35px;
    padding-right: 35px;
    font-weight: 400;
    font-size: 30px;
    line-height: 35px;
    color: $text_color;
  }

  .CommonProblem-title {
    font-style: normal;
    font-weight: 700;
    font-size: 50px;
    line-height: 59px;
    padding-left: 35px;
    padding-right: 35px;
    margin-top: 57px;
    margin-bottom: 22px;
    color: $text_color;
  }
}
</style>
