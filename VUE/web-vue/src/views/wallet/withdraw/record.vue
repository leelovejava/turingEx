<template>
  <div class="charge-bank pb-20">
    <Headers :title="$t('message.user.tikuanjilu')" />
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
        <el-card v-for="(item, i) in withdrawList" :key="i" class="mb-4">
          <template #header>
            <div class="flex justify-between items-center font-semibold">
              <span class="fs-16"
                >{{ item?.coin?.toUpperCase() }}
                {{ $t("message.user.tikuan") }}</span
              >
              <StateShow :state="item.status" :type="activeType" />
            </div>
          </template>
          <div class="flex">
            <div class="flex-1">
              <p>{{ $t("message.user.network") }}</p>
              <div>{{ item.coin_blockchain?.toUpperCase() }}</div>
            </div>
            <div class="flex-1 flex-address">
              <!-- 提币地址 -->
              <p>{{ $t("message.user.xian4") }}</p>
              <div>{{ item.from }}</div>
            </div>
            <div class="flex-1 flex-address">
              <p>{{ $t("message.user.zhuanchudizhi") }}</p>
              <div>{{ item.to }}</div>
            </div>
            <div class="flex-1">
              <p>
                {{ $t("message.user.xian11") }}({{ item?.coin?.toUpperCase() }})
              </p>
              <div>{{ item.volume }}</div>
            </div>
            <div class="flex-1">
              <p>
                {{ $t("message.user.shouxufei") }}({{
                  item?.coin?.toUpperCase()
                }})
              </p>
              <div>{{ item.fee }}</div>
            </div>
            <div class="flex-1">
              <p class="text-right">{{ $t("message.user.fukuanpingzheng") }}</p>
              <div
                class="text-right cursor-pointer blue-color"
                @click="gotoDetail(item, 'crypto')"
              >
                {{ $t("message.user.dianjichakan") }}
              </div>
            </div>
          </div>
        </el-card>
      </div>
      <div v-else>
        <el-card v-for="(item, i) in withdrawList" :key="i" class="mb-4">
          <template #header>
            <div class="flex justify-between items-center font-semibold">
              <span class="fs-16"
                >{{ $t("message.user.yinhangka") }}
                {{ $t("message.user.tikuan") }}</span
              >
              <StateShow :state="item.state" :type="activeType" />
            </div>
          </template>

          <div class="flex">
            <div class="flex-1">
              <p>{{ $t("message.user.yinhangmingcheng") }}</p>
              <div>{{ item?.param_value1 }}</div>
            </div>
            <div class="flex-1">
              <p>{{ $t("message.user.yinhangkazhanghu") }}</p>
              <div>{{ item?.param_value3 }}</div>
            </div>
            <div class="flex-1">
              <p>{{ $t("message.c2c.bizhong") }}</p>
              <div>{{ item?.currency }}</div>
            </div>
            <div class="flex-1">
              <p>
                {{ $t("message.user.tibi") }}{{ $t("message.c2c.jine") }} ({{
                  item.currency?.toUpperCase()
                }})
              </p>
              <div>{{ item.amount }}</div>
            </div>
            <div class="flex-1">
              <p>
                {{ $t("message.user.shouxufei") }}
                ({{ item.currency?.toUpperCase() }})
              </p>
              <div>{{ item.coin_amount_fee }}</div>
            </div>

            <div class="flex-1">
              <p class="text-right">{{ $t("message.user.fukuanpingzheng") }}</p>
              <div
                class="text-right cursor-pointer blue-color"
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
import { ref, onBeforeMount } from "vue";
import StateShow from "../components/StateShow.vue";
import Headers from "../components/headers.vue";
import { useRouter } from "vue-router";
import Axios2 from "@/api/wallet.js";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const router = useRouter();
const activeType = ref("bank"); //
const withdrawList = ref([]); //充值列表
const pageNum = ref(1);
const tableLength = ref(0);
onBeforeMount(() => {
  getList();
});

const handleChangeType = (type) => {
  activeType.value = type;
  getList();
};

const handleCurrentChange = (val) => {
  console.log(8888);
  pageNum.value = val;
  getList();
};

// 获取列表数据
const getList = () => {
  if (activeType.value === "bank") {
    const params = {
      direction: "withdraw",
      page_no: pageNum.value,
    };
    Axios2.getC2cList(params).then((res) => {
      withdrawList.value = res.data;
      tableLength.value = res.total || 10;
    });
  } else {
    Axios2.getWithdrawList({ page_no: pageNum.value }).then((res) => {
      withdrawList.value = res.data;
      tableLength.value = res.total || 10;
    });
  }
};

const gotoDetail = (item, type) => {
  router.push(`/withdraw/detail?id=${item.order_no}&type=${type}`);
};
</script>
<style lang="scss" scoped>
:deep(.el-card__header) {
  padding: 12px 24px;
}
:deep(.head-content) {
  padding: 24px 0 !important;
  width: 1200px;
  margin: 0 auto;
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
