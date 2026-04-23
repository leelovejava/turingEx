<template>
  <div id="loanPage">
    <div class="loan">
      <assets-head :title="$t('助力贷')">
        <div class="right flex items-center">
          <img src="@/assets/image/loan/rule.png" alt="rule-img" class="w-12 h-12 mr-5"
            @click="$router.push('/cryptos/loanRule')" />
          <img src="@/assets/image/loan/history.png" alt="exchange-img" class="w-12 h-12"
            @click="$router.push('/cryptos/loanHistory')" />
        </div>
      </assets-head>

      <!--   container -->
      <div class="container px-8">
        <!--      title-->
        <div class="py-8">
          <span style="color: #e35461">{{
            this.$t("经过平台审核，您可向平台申请一笔借款！")
          }}</span>
        </div>
        <!--    list  -->
        <div class="loanList">
          <div class="flex justify-between py-8 border-b-color">
            <div>
              <span class="grayText">{{ $t("期望借款金额") }}</span>
            </div>
            <div class="flex align-center">
              <!--            <input class="font-semibold textColor border-none text-right mr-14 mainBackground" v-model="loanAmount"  disabled="disabled" @input="inputAmunt" />-->
              <span class="mr-4 font-semibold textColor">{{ loanData.max_quota || 0 }}</span>
              <span class="font-semibold textColor"></span>
            </div>
          </div>
          <div class="flex justify-between py-8 border-b-color">
            <div>
              <span class="grayText">{{ $t("还款周期") }}</span>
            </div>
            <div class="flex items-center">
              <!--            <div class="font-semibold textColor relative">-->
              <div class="font-semibold mr-4 textColor relative" @click="isSelectDay = !isSelectDay">
                <span>{{ loanData.term }} {{ $t("天") }}</span>
                <!--              <div class="selectDay"  v-show="isSelectDay">-->
                <!--                <div class="mb-20 border-b-white" v-for="(s,index) in selectDayList" :key="index"  @click="selectDay(s)">-->
                <!--                  <span>{{s}}</span>-->
                <!--                </div>-->
                <!--              </div>-->
              </div>
              <div class="w-6 h-6">
                <img src="@/assets/image/loan/right.png" alt="right-img" class="w-full" />
              </div>
            </div>
          </div>
          <div class="flex justify-between py-8 border-b-color">
            <div>
              <span class="grayText">{{ $t("日利率") }}</span>
            </div>
            <div class="flex align-center">
              <span class="font-semibold textColor">{{ loanData.daily_rate * 1 * 100 || 0 }}%
              </span>
            </div>
          </div>
          <div class="flex justify-between py-8 border-b-color">
            <div>
              <span class="grayText">{{ $t("还款方式") }}</span>
            </div>
            <div class="flex align-center">
              <span class="font-semibold textColor text-right">{{ $t("到期一次还款") }}</span>
            </div>
          </div>
          <div class="flex justify-between py-8 border-b-color">
            <div>
              <span class="grayText">{{ $t("利息") }}</span>
            </div>
            <div class="flex align-center">
              <!--            借款金额*日利率*借款天数-->
              <!-- <span class="font-semibold textColor">{{$bigDecimal.multiply(+loanData.maxQuota , +loanData.dailyRate) * +this.loanData.term || 0 }} USDT</span> -->
              <span class="font-semibold textColor">{{
                (+loanData.max_quota, +loanData.daily_rate) * +this.loanData.term || 0
              }}
                </span>
            </div>
          </div>
          <div class="flex justify-between py-8 border-b-color">
            <div>
              <span class="grayText">{{ $t("放款机构") }}</span>
            </div>
            <div class="flex align-center">
              <span class="font-semibold textColor">{{ loanData.lending_name }}</span>
            </div>
          </div>
        </div>
        <!--      上传区域-->
        <div class="uploadImg">
          <div class="mb-10 textColor">
            <span>{{ $t("信用放款(请确保图片清晰可见)") }}</span>
          </div>
          <div class="upload">
            <div class="flex mt-8 mb-16 justify-between">
              <div class="flex-1 flex flex-col text-center justify-center items-center">
                <div class="upload-wrap">
                  <!--                    <img src="@/assets/image/kyc/0.png" alt="" class="w-full"-->
                  <!--                         v-if="[1, 2].includes(status) && frontFile.length === 0" />-->
                  <van-uploader v-model="frontFile" :max-count="1" :disabled="isUpload" :after-read="afterRead"
                    @click-upload="onClickUpload('frontFile')" />
                </div>
                <div class="mt-8 text-26" style="color: #868d9a">
                  {{ $t("证件正面") }}
                </div>
              </div>
              <div class="flex-1 flex flex-col text-center justify-center items-center">
                <div class="upload-wrap">
                  <!--                    <img src="@/assets/image/kyc/1.png" alt="" class="w-full"-->
                  <!--                         v-if="[1, 2].includes(status) && reverseFile.length === 0" />-->
                  <van-uploader v-model="reverseFile" :max-count="1" :disabled="isUpload" :after-read="afterRead"
                    @click-upload="onClickUpload('reverseFile')" />
                </div>
                <div class="mt-8 text-26" style="color: #868d9a">
                  {{ $t("证件反面") }}
                </div>
              </div>
            </div>
            <div class="flex mt-8 mb-16 justify-between">
              <div class="flex-1 flex flex-col text-center justify-center items-center">
                <div class="upload-wrap">
                  <!--                    <img src="@/assets/image/kyc/2.png" alt="" class="w-full"-->
                  <!--                         v-if="[1, 2].includes(status) && fileList.length === 0" />-->
                  <van-uploader v-model="fileList" :max-count="1" :disabled="isUpload" :after-read="afterRead"
                    @click-upload="onClickUpload('fileList')" />
                </div>
                <div class="mt-8 text-26" style="color: #868d9a">
                  {{ $t("handCredent") }}
                </div>
              </div>
              <div class="flex-1 flex flex-col text-center justify-center items-center">
                <div class="upload-wrap">
                  <img src="@/assets/image/loan/handId.png" alt="" class="w-full" />
                </div>
                <div class="mt-8 text-26" style="color: #868d9a">
                  {{ $t("拍摄示例") }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!--      确认按钮-->
        <div class="confirmBtn btnMain w-full py-8 text-center text-white font-normal text-32" @click="submit()">
          <span>{{ $t("确定") }}</span>
        </div>
      </div>
      <van-popup v-model:show="isSelectDay" position="bottom" :round="true">
        <ul class="main2_background">
          <li v-for="(day, index) in loanDeployList" class="text-center py-8" :key="index" @click="selectDay(day)">
            <span>{{ day.term }}</span>
          </li>
        </ul>
      </van-popup>
    </div>
  </div>
</template>

<script>
import AssetsHead from "@/components/Transform/assets-head/index.vue";
import { Popup, Uploader } from "vant";
import { _getLoan, _loanApply } from "@/service/fund.api";
import { _uploadImage } from "@/service/upload.api.js";
import { showToast } from "vant";
// import {debounce} from "@/utils/utis";

export default {
  name: "loanIndex",
  components: {
    AssetsHead,
    [Uploader.name]: Uploader,
    [Popup.name]: Popup,
  },
  methods: {
    getLoan() {
      _getLoan().then((data) => {
        this.loanDeployList = data;
        this.loanDeployList.sort((a, b) => {
          return +a.term - +b.term;
        });
        this.loanData = this.loanDeployList[0];
      });
    },
    submit() {
      // console.log('this.frontFile',this.frontFile)
      if (!this.frontFile.length || !this.reverseFile.length || !this.fileList.length) {
        showToast(this.$t("请上传完整证件信息"));
        return;
      }
      console.log(this.loanData);
      _loanApply({
        // ...this.loanData,
        term: this.loanData.term,
        quota: this.loanData.max_quota, //借贷金额 取max
        dailyRate: this.loanData.daily_rate,
        lendingInstitution: this.loanData.lending_institution, //放款机构名字
        lendingName: this.loanData.lending_name,
        repayment: this.loanData.repayment, //还款方式
        repayCycle: this.loanData.repay_cycle, //还款日期
        symbol: "USDT",
        frontFile: this.frontFile[0].resURL,
        reverseFile: this.reverseFile[0].resURL,
        fileList: this.fileList[0].resURL,
      })
        .then((res) => {
          // console.log('res',res)
          this.$router.push("/cryptos/loanHistory");
        })
        .catch((err) => {
          showToast(this.$t(err.msg));
        });
    },
    // //防抖
    // inputAmunt(){
    //   this.loanAmount = this.loanAmount.replace(/[^0-9]/g, '')
    //   this.debounceFn()
    // },
    // debounceFn: debounce(function () {
    //   this.getInterest()
    // }, 500),
    // getInterest(){
    //   this.interest = +this.loanAmount * +this.loanRate
    // },
    selectDay(data) {
      this.loanData = data;
      // this.getInterest()
      this.isSelectDay = false;
    },
    onClickUpload(type) {
      this.curFile = type;
    },
    afterRead(file) {
      /// 处理文件
      // console.log(file)
      file.status = "uploading";
      file.message = this.$t("上传中...");
      this.isUpload = true;
      _uploadImage(file)
        .then((data) => {
          file.status = "success";
          file.message = this.$t("上传成功");
          file.resURL = data;
          this[this.curFile] = [file];
          this.isUpload = false;
        })
        .catch((err) => {
          file.status = "failed";
          file.message = this.$t("图片上传失败");
          this.isUpload = false;
        });
    },
  },
  mounted() {
    this.getLoan();
    // this.getInterest()
  },
  data() {
    return {
      loanData: {},
      loanDeployList: [],
      frontFile: [],
      reverseFile: [],
      fileList: [],
      curFile: "frontFile",
      // resultArr: ['small-success_' + this.$t('已申请未审核'), 'identifing_' + this.$t('审核中'), 'small-success_' + this.$t('审核通过'), 'icon-close_' + this.$t('审核未通过')], // 0 好像是未提交
      // status:'',
      isSelectDay: false,
      isUpload: false,
    };
  },
};
</script>

<style lang="scss" scoped>
#loanPage {
  font-size: 30px;

  .loan {
    width: 100%;
    box-sizing: border-box;
  }

  .uploadImg {
    margin-top: 60px;

    .upload-wrap {
      width: 356px;
      height: 288px;
      display: flex;
      justify-content: center;
      align-items: center;

      :deep(.van-uploader__upload) {
        width: 356px !important;
        height: 288px !important;
      }
    }
  }

  .selectDay {
    position: absolute;
    left: -70px;
    background: #f5f5f5;
    color: #333;
    box-sizing: border-box;

    div {
      padding: 20px 70px;
    }

    div:last-child {
      border-bottom: none;
      margin-bottom: 0;
    }
  }

  .confirmBtn {
    border-radius: 8px;
  }

  .grayText {
    color: #868e9b;
  }
}
</style>
