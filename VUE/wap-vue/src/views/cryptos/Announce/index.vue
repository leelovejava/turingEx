<template>
  <div id="cryptos">
    <div class="announce">
      <assets-head :title="$t('公告')" />
      <div class="content">
        <div class="mb-12 tabBackground py-11 px-8 rounded-lg" v-for="(item, index) in list" :key="index"
          @click="toDetail(item.uuid)">
          <div class="textColor text-32">{{ item.title }}</div>
          <div class="mt-16 flex justify-between text-grey text-28">
            <div>{{ item.startTime.substring(0, 10) }}</div>
            <div class="flex items-center">
              <span class="mr-6">{{ $t('详情') }}</span>
              <img src="../../../assets/image/userCenter/more.png" alt="" class="w-7 h-7" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import assetsHead from "@/components/Transform/assets-head/index.vue";
import { _getNewsList1 } from '@/service/user.api'
import { dataTimeEx } from '@/utils/utis'
export default {
  props: {

  },
  components: {
    assetsHead
  },
  data() {
    return {
      dataTimeEx,
      list: []
    }
  },
  mounted() {
    this.getNewsList();
  },
  methods: {
    getNewsList() {
      _getNewsList1({
        language: this.$i18n.locale,
      }).then(res => {
        this.list = res
        this.list = this.list.sort((a, b) => b.startTime - a.startTime);
      })
    },
    toDetail(id) {
      this.$router.push({ path: '/cryptos/announceDetail', query: { id } })
    }
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  .announce {
    width: 100%;
    box-sizing: border-box;
  }

  .content {
    padding: 40px 32px;
  }

  .title {
    text-decoration: underline;
  }
}
</style>