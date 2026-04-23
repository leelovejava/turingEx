<template>
  <div id="loanRulePage">
    <div class="loanRule">
      <assets-head :title="$t('规则')" />
      <div class="container px-8 mt-10">
        <div class="textColor" v-for="(item, index) in ruleList" :key="index">
          <div class="mb-8">{{ item.title }}</div>
          <div class="mb-8 whiteSpace">{{ item.content }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AssetsHead from "@/components/Transform/assets-head/index.vue";

export default {
  name: "loanRule",
  data() {
    return {
      ruleList: [
        { title: this.$t('loanRuleTitle1'), content: this.$t('loanRuleContent1') },
        { title: this.$t('loanRuleTitle2'), content: this.$t('loanRuleContent2') },
        { title: this.$t('loanRuleTitle3'), content: this.$t('loanRuleContent3') },
        { title: this.$t('loanRuleTitle4'), content: this.$t('loanRuleContent4') },
        { title: this.$t('loanRuleTitle5'), content: this.$t('loanRuleContent5') },
        { title: this.$t('loanRuleTitle6'), content: this.$t('loanRuleContent6') },
        { title: this.$t('loanRuleTitle7'), content: this.$t('loanRuleContent7') },
        { title: this.$t('loanRuleTitle8'), content: this.$t('loanRuleContent8') },
        { title: this.$t('loanRuleTitle9'), content: this.$t('loanRuleContent9') },
        { title: this.$t('loanRuleTitle10'), content: this.$t('loanRuleContent10') },
        { title: this.$t('loanRuleTitle11'), content: this.$t('loanRuleContent11') },
        { title: this.$t('loanRuleTitle12'), content: this.$t('loanRuleContent12') },
      ]
    }
  },
  components: { AssetsHead }
}
</script>

<style  lang="scss" scoped>
#loanRulePage {
  font-size: 30px;

  .whiteSpace {
    white-space: pre-line;
  }
}
</style>