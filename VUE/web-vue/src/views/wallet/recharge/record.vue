<template>
  <div class="charge-bank pb-20">
    <Headers :title="$t('message.user.chongzhi') + $t('message.user.jilu')" />
    <div class="py-4 content">
      <!-- bank 和 usdt 选择-->
      <div class="flex items-center mb-4">
        <div
          class="mr-4 cursor-pointer"
          @click="handleChangeType('bank')"
          :class="[activeType === 'bank' ? 'activeBtn' : '']"
        >
          {{ $t("message.user.yinhanghuobi") }}
        </div>
        <div
          class="cursor-pointer"
          @click="handleChangeType('crypto')"
          :class="[activeType === 'crypto' ? 'activeBtn' : '']"
        >
          {{ $t("message.user.shuzihuobi") }}
        </div>
      </div>
      <!-- 列表 -->
      <div v-if="activeType === 'crypto'">
        <el-card v-for="(item, i) in rechargeList" :key="i" class="mb-4">
          <template #header>
            <div class="flex justify-between items-center font-semibold">
              <span class="fs-16"
                >{{ item?.symbol?.toUpperCase() }}
                {{ $t("message.user.chongzhi") }}</span
              >

              <StateShow :state="item.status" :type="activeType"></StateShow>
            </div>
          </template>
          <div class="flex">
            <div class="flex-1">
              <p>{{ $t("message.user.network") }}</p>
              <div>{{ item.blockchain_name?.toUpperCase() }}</div>
            </div>
            <div class="flex-1 flex-address">
              <p>{{ $t("message.user.chongzhidizhi") }}</p>
              <div>{{ item.address }}</div>
            </div>
            <div class="flex-1 flex-address">
              <p>{{ $t("message.user.zhuanchudizhi") }}</p>
              <div>{{ item.from }}</div>
            </div>
            <div class="flex-1 text-center">
              <p>
                {{ $t("message.user.RechargeAmount") }}({{
                  item?.symbol?.toUpperCase()
                }})
              </p>
              <div>{{ item.volume }}</div>
            </div>
            <div class="flex-1">
              <p class="text-right">{{ $t("message.user.fukuanpingzheng") }}</p>
              <div
                class="blue-color text-right cursor-pointer"
                @click="gotoDetail(item, 'crypto')"
              >
                {{ $t("message.user.dianjichakan") }}
              </div>
            </div>
          </div>
        </el-card>
      </div>
      <div v-else>
        <el-card v-for="(item, i) in rechargeList" :key="i" class="mb-4">
          <template #header>
            <div class="flex justify-between items-center font-semibold">
              <span class="fs-16">{{
                $t("message.user.yinhangkachongzhi")
              }}</span>

              <StateShow :state="item.state" :type="activeType"></StateShow>
            </div>
          </template>

          <div class="flex">
            <!-- TODO -->
            <div class="flex-1">
              <p>{{ $t("message.user.nation") }}</p>
              <div>{{ item?.nationality }}</div>
            </div>
            <div class="flex-1">
              <p>{{ $t("message.user.FrenchCurrency") }}</p>
              <div>{{ item.currency }}</div>
            </div>
            <div class="flex-1">
              <p>{{ $t("message.user.shijian") }}</p>
              <div>{{ item.create_time }}</div>
            </div>
            <div class="flex-1 text-center">
              <p>
                {{ $t("message.user.RechargeAmount") }}({{
                  item.currency?.toUpperCase()
                }})
              </p>
              <div>{{ item.amount }}</div>
            </div>
            <div class="flex-1">
              <p class="text-right">{{ $t("message.user.fukuanpingzheng") }}</p>
              <div
                class="blue-color text-right cursor-pointer"
                @click="gotoDetail(item, 'bank')"
              >
                {{ $t("message.user.dianjichakan") }}
              </div>
            </div>
          </div>
        </el-card>
      </div>
      <!-- 分页 -->
      <el-pagination
        class="pagination-box"
        v-model:current-page="pageNum"
        :default-page-size="10"
        layout="total, prev, pager, next, jumper"
        :total="tableLength"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import Headers from "../components/headers.vue";
import StateShow from "../components/StateShow.vue";
import { ref, onBeforeMount } from "vue";
import { useRouter } from "vue-router";
import Axios2 from "@/api/wallet.js";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const router = useRouter();
const activeType = ref("bank"); //
const rechargeList = ref([]); //充值列表
const pageNum = ref();
const tableLength = ref(0);
onBeforeMount(() => {
  getList();
});

const handleChangeType = (type) => {
  activeType.value = type;
  getList();
};

const handleCurrentChange = (val) => {
  pageNum.value = val;
  getList();
};
const getList = () => {
  if (activeType.value === "bank") {
    const params = {
      direction: "recharge",
      page_no: pageNum.value,
    };
    Axios2.getC2cList(params).then((res) => {
      rechargeList.value = res.data;
      tableLength.value = res.total || 10;
    });
  } else {
    Axios2.getCrypotRechargeList({ page_no: pageNum.value }).then((res) => {
      rechargeList.value = res.data;
      tableLength.value = res.total || 10;
    });
  }
};

const gotoDetail = (item, type) => {
  router.push(`/recharge/detail?id=${item.order_no}&type=${type}`);
};
</script>
<style lang="scss" scoped>
:deep(.head-content) {
  padding: 24px 0 !important;
  width: 1200px;
  margin: 0 auto;
}

:deep(.el-card__header) {
  padding: 12px 24px;
}

.charge-bank {
  font-size: 15px;

  .content {
    width: 1200px;
    margin: 0 auto;
    .activeBtn {
      background: #2555f8;
      border-radius: 100px;
      padding: 8px 24px;
      color: #fff;
    }

    // label的样式
    p {
      color: #868d9a;
      margin-bottom: 8px;
      font-size: 12px;
    }
  }
}

.usd-input {
  height: 50px;
  font-size: 14px;
}

.flex-address {
  flex-basis: 242px;
}
</style>
