<template>
  <div id="cryptos" class="trader-agreement-page">
    <assets-head :title="t('traderAgreementNavTitle')" :backFunc="goBack" />

    <div class="agreement-content">
      <h1 class="title">Exchange Copy Trading Trader Agreement</h1>

      <section class="section">
        <h2 class="section-title">1. Agreement Background</h2>
        <p class="paragraph">
          WHEREAS the Copy Trader intends to follow the trading signals or recommendations shared by the Analyst for strategy copying, and the Analyst Team agrees to provide related trading services to the Copy Trader in a lawful and compliant manner; NOW, THEREFORE, the parties, adhering to the principles of good faith and mutual benefit, hereby enter into this Agreement.
        </p>
      </section>

      <section class="section">
        <h2 class="section-title">2. Service Content</h2>
        <ul class="list">
          <li class="list-item">
            The Analyst Team provides trading signals or strategies to the Copy Trader, which may be executed automatically or manually in the Copy Trader's account.
          </li>
          <li class="list-item">
            The Analyst Team does not have authority over funds allocation in the Copy Trader's account; it provides trading recommendations or signals only.
          </li>
          <li class="list-item">
            Copy Trading may include, but is not limited to, markets such as Forex, Futures, Cryptocurrency, or Stocks, subject to confirmation by the Copy Trader.
          </li>
        </ul>
      </section>

      <section class="section">
        <h2 class="section-title">3. Rights and Obligations</h2>
        <div class="sub-section">
          <h3 class="sub-section-title">Copy Trader's Rights & Obligations:</h3>
          <ul class="sub-list">
            <li class="sub-list-item">
              Has the right to independently decide whether or not to follow the Analyst Team's trading strategies.
            </li>
            <li class="sub-list-item">
              Bears the risks and a portion of the losses (10%) arising from copy trading.
            </li>
            <li class="sub-list-item">
              Pays the Analyst Team's service fees (if applicable) as agreed and on time.
            </li>
            <li class="sub-list-item">
              Is strictly prohibited from disclosing the Analyst Team's strategy content or trading data without authorization.
            </li>
          </ul>
        </div>
        <div class="sub-section">
          <h3 class="sub-section-title">Analyst Team's Rights & Obligations:</h3>
          <ul class="sub-list">
            <li class="sub-list-item">Provides genuine and complete trading strategies or signals.</li>
            <li class="sub-list-item">
              Is liable for a portion of losses (90%) incurred by the Copy Trader resulting from following the Analyst's shared trading signals or recommendations.
            </li>
            <li class="sub-list-item">
              Guarantees non-encroachment or misappropriation of the Copy Trader's account funds.
            </li>
            <li class="sub-list-item">Maintains strict confidentiality of the Copy Trader's information.</li>
            <li class="sub-list-item">
              Ensures a collateral deposit of $5,000,000.00 (Five Million US Dollars) is maintained.
            </li>
          </ul>
        </div>
      </section>

      <section class="section">
        <h2 class="section-title">4. Profit Sharing & Fees</h2>
        <ul class="list">
          <li class="list-item">
            Profit sharing ratio is 15% (Service fees may be structured as a fixed fee or charged per trade/per month).
          </li>
          <li class="list-item">
            In the event of losses, the Analyst Team bears 90% of the loss amount as compensation; the Copy Trader shall actively cooperate in post-trade analysis.
          </li>
          <li class="list-item">Fee Payment Method: External wallet.</li>
          <li class="list-item">Payment Cycle: Monthly Settlement.</li>
        </ul>
      </section>

      <section class="section">
        <h2 class="section-title">5. Risk Disclosure</h2>
        <p class="paragraph">
          BOTH PARTIES explicitly acknowledge that trading markets involve risks, which may result in the loss of a portion of the principal capital. The Copy Trader must fully understand these trading risks, make independent decisions, and bear the corresponding consequences (a loss amounting to 10% of the total loss amount).
        </p>
      </section>

      <section class="section">
        <h2 class="section-title">6. Agreement Termination</h2>
        <ul class="list">
          <li class="list-item">
            The Copy Trader may apply to the Platform to exit the Copy Trading service at any time.
          </li>
          <li class="list-item">
            The Platform reserves the right to immediately terminate the Copy Trader's eligibility upon detection of irregular trading, risk events, or other improper conduct.
          </li>
          <li class="list-item">
            Upon termination of this Agreement, the Platform will cease copy trading services and complete profit/loss settlement within a reasonable timeframe.
          </li>
        </ul>
      </section>

      <section class="section">
        <h2 class="section-title">7. Governing Law</h2>
        <p class="paragraph">
          This Agreement shall be governed by and construed in accordance with the laws of the jurisdiction where the Platform is registered. Any disputes arising hereunder shall first be resolved through mutual consultation. Should consultation fail, the dispute shall be submitted to the exclusive jurisdiction of the courts located in the Platform's place of registration.
        </p>
      </section>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import assetsHead from '@/components/Transform/assets-head/index.vue'

defineOptions({ name: 'TraderAgreementPage' })

const router = useRouter()
const { t } = useI18n()

function goBack() {
  router.back()
}
</script>

<style lang="scss" scoped>
.trader-agreement-page {
  min-height: 100vh;
  background: $main_background;
  padding-bottom: 32px;
  box-sizing: border-box;
}

.agreement-content {
  width: 100%;
  max-width: 920px;
  margin: 0 auto;
  padding: 20px 18px 40px;
  background: $main2_background;
  color: $text_color;
  border: 1px solid $line_color;
  border-radius: 12px;
  box-sizing: border-box;
  font-family: Inter, Avenir, Helvetica, Arial, sans-serif;
}

.title {
  margin: 8px 0 28px;
  font-size: 32px;
  font-weight: 700;
  line-height: 1.35;
  text-align: center;
  color: $text_color;
}

.section {
  margin-top: 28px;

  &:first-of-type {
    margin-top: 0;
  }
}

.section-title {
  margin: 0 0 14px;
  font-size: 26px;
  font-weight: 700;
  line-height: 1.4;
  color: $text_color;
}

.paragraph {
  margin: 0;
  font-size: 24px;
  font-weight: 400;
  line-height: 1.5;
  color: $text_color1;
}

.list,
.sub-list {
  margin: 0;
  padding-left: 28px;
  font-size: 24px;
  line-height: 1.5;
  color: $text_color1;
}

.list-item,
.sub-list-item {
  margin-bottom: 12px;

  &:last-child {
    margin-bottom: 0;
  }
}

.sub-section {
  margin-top: 20px;

  &:first-child {
    margin-top: 0;
  }
}

.sub-section-title {
  margin: 0 0 12px;
  font-size: 24px;
  font-weight: 700;
  line-height: 1.4;
  color: $text_color;
}

@media (min-width: 1024px) {
  .agreement-content {
    margin-top: 8px;
    padding: 32px 40px 48px;
    border-radius: 12px;
    box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  }

  .title {
    font-size: 34px;
  }

  .section-title {
    font-size: 28px;
  }

  .paragraph,
  .list,
  .sub-list {
    font-size: 25px;
  }
}
</style>
