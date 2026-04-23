<template>
  <el-dialog
    title="订单详情"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close="handClose"
    :width="dataForm.direction != 'withdraw' ? '1200px' : '600px'"
    class="transport-dialog"
  >
    <div class="allBox">
      <!-- 左侧详情 -->
      <div class="leftBox">
        <div class="minibox" v-if="dataForm.direction == 'recharge'">
          {{ dataForm.username }}充值<span class="moneyText">{{
            dataForm.coin_amount + dataForm.symbol
          }}</span>
        </div>
        <div class="minibox" v-if="dataForm.direction == 'withdraw'">
          {{ dataForm.username }}提现<span class="moneyText">{{
            dataForm.coin_amount + dataForm.symbol
          }}</span>
        </div>
        <div class="miniMiddle">
          <div class="chetBox">
            <span class="cicle"></span>
            <div class="title">订单信息</div>
            <div class="miniTitle">
              <span class="titLeft">订单号</span>
              <span class="titRight">订单状态</span>
            </div>
            <div class="textTitle">
              <span class="textBush">{{ dataForm.order_no }}</span>
              <span class="textFig yellow" v-if="dataForm.state == '0'"
                >待付款</span
              >
              <span class="textFig yellow" v-if="dataForm.state == '1'"
                >已付款</span
              >
              <span class="textFig red" v-if="dataForm.state == '2'"
                >申诉中</span
              >
              <span class="textFig green" v-if="dataForm.state == '3'"
                >已完成</span
              >
              <span class="textFig yellow" v-if="dataForm.state == '4'"
                >已取消</span
              >
              <span class="textFig red" v-if="dataForm.state == '5'"
                >已超时</span
              >
            </div>
          </div>
          <div class="chetBox">
            <span class="cicle"></span>
            <div class="title">价格</div>
            <div class="miniTitle">
              <span class="titLeft">单价</span>
              <span class="titRight">总价</span>
            </div>
            <div class="textTitle">
              <span class="textBush">{{
                dataForm.symbol_value +
                dataForm.currency +
                "/" +
                dataForm.symbol
              }}</span>
              <span class="textFig green">{{
                dataForm.amount + dataForm.currency
              }}</span>
            </div>
          </div>
          <div class="chetBox">
            <span class="cicle"></span>
            <div class="title">时间</div>
            <div class="miniTitle">
              <span class="titLeft">下单时间</span>
              <span class="titRight" v-if="dataForm.pay_time">付款时间</span>
            </div>
            <div class="textTitle">
              <span class="textBush">{{ dataForm.create_time }}</span>
              <span class="textFigTwo">{{ dataForm.pay_time }}</span>
            </div>
          </div>
          <div class="chetBox">
            <span class="cicle"></span>
            <div class="title">收款信息</div>
            <div class="miniTitle menuBox">
              <span class="titLeft">支付方式类型</span>
              <span class="textmela">{{ dataForm.method_type_name }}</span>
            </div>
            <div class="textTitle menuBox">
              <span class="titLeft">支付方式名称</span>
              <span class="textmela">{{ dataForm.method_name }}</span>
            </div>
            <div class="miniTitle menuBox" style="margin-top: 20px">
              <span class="titLeft">真实姓名</span>
              <span class="textmela">{{ dataForm.real_name }}</span>
            </div>
            <div>
              <div
                class="miniTitle menuBox"
                v-for="(item, index) in list"
                :key="index"
              >
                <span class="titLeft">{{ item.param_name }}</span>
                <span class="textmela">{{ item.param_value }}</span>
              </div>
            </div>

            <!-- <div class="miniTitle" style="margin-top: 20px">
              <span class="titLeft">真实姓名</span>
              <span class="titRight" v-if="dataForm.param_value1">{{ dataForm.param_name1 }}</span>
              <span class="titRight" v-if="dataForm.param_value2">{{ dataForm.param_name2 }}</span>
              <span class="titRight" v-if="dataForm.param_value3">{{ dataForm.param_name3 }}</span>
              <span class="titRight" v-if="dataForm.param_value4">{{ dataForm.param_name4 }}</span>
              <span class="titRight" v-if="dataForm.param_value5">{{ dataForm.param_name5 }}</span>
              <span class="titRight" v-if="dataForm.param_value6">{{ dataForm.param_name6 }}</span>
              <span class="titRight" v-if="dataForm.param_value7">{{ dataForm.param_name7 }}</span>
              <span class="titRight" v-if="dataForm.param_value8">{{ dataForm.param_name8 }}</span>
              <span class="titRight" v-if="dataForm.param_value9">{{ dataForm.param_name9 }}</span>
              <span class="titRight" v-if="dataForm.param_value10">{{ dataForm.param_name10 }}</span>
              <span class="titRight" v-if="dataForm.param_value11">{{ dataForm.param_name11 }}</span>
              <span class="titRight" v-if="dataForm.param_value12">{{ dataForm.param_name12 }}</span>
              <span class="titRight" v-if="dataForm.param_value13">{{ dataForm.param_name13 }}</span>
              <span class="titRight" v-if="dataForm.param_value14">{{ dataForm.param_name14 }}</span>
              <span class="titRight" v-if="dataForm.param_value15">{{ dataForm.param_name15 }}</span>
            </div>
            <div class="textTitle">
              <span class="textBush">{{ dataForm.real_name }}</span>
              <span class="textFigTwo" v-if="dataForm.param_value1">{{ dataForm.param_value1 }}</span>
              <span class="textFigTwo" v-if="dataForm.param_value2">{{ dataForm.param_value2 }}</span>
              <span class="textFigTwo" v-if="dataForm.param_value3">{{ dataForm.param_value3 }}</span>
              <span class="textFigTwo" v-if="dataForm.param_value4">{{ dataForm.param_value4 }}</span>
              <span class="textFigTwo" v-if="dataForm.param_value5">{{ dataForm.param_value5 }}</span>
              <span class="textFigTwo" v-if="dataForm.param_value6">{{ dataForm.param_value6 }}</span>
              <span class="textFigTwo" v-if="dataForm.param_value7">{{ dataForm.param_value7 }}</span>
              <span class="textFigTwo" v-if="dataForm.param_value8">{{ dataForm.param_value8 }}</span>
              <span class="textFigTwo" v-if="dataForm.param_value9">{{ dataForm.param_value9 }}</span>
              <span class="textFigTwo" v-if="dataForm.param_value10">{{ dataForm.param_value10 }}</span>
              <span class="textFigTwo" v-if="dataForm.param_value11">{{ dataForm.param_value11 }}</span>
              <span class="textFigTwo" v-if="dataForm.param_value12">{{ dataForm.param_value12 }}</span>
              <span class="textFigTwo" v-if="dataForm.param_value13">{{ dataForm.param_value13}}</span>
              <span class="textFigTwo" v-if="dataForm.param_value14">{{ dataForm.param_value14 }}</span>
              <span class="textFigTwo" v-if="dataForm.param_value15">{{ dataForm.param_value15 }}</span>
            </div> -->
            <div
              v-if="dataForm.qrcode"
              class="miniTitle"
              style="margin-top: 20px"
            >
              <span class="titLeft">支付二维码</span>
            </div>
            <div v-if="dataForm.qrcode">
              <img :src="dataForm.qrcode" alt="" width="150" height="150" />
            </div>
          </div>
          <div class="chetBox">
            <span class="cicle"></span>
            <div class="title">操作</div>
            <div>
              <el-button
                type="primary"
                size="medium "
                v-if="dataForm.state == '0' && dataForm.direction == 'recharge'"
                @click.stop="passtHandle(row)"
                >手动放行</el-button
              >
              <el-button
                type="primary"
                size="medium "
                v-if="dataForm.state == '0' && dataForm.direction == 'withdraw'"
                @click.stop="passtHandle(row, 'n')"
                >手动转账</el-button
              >
              <el-button
                type="text"
                v-if="dataForm.state == '0'"
                @click.stop="deletOrdeHandle(row)"
                style="margin-left: 70px"
                >取消订单</el-button
              >
            </div>
          </div>
        </div>
      </div>
      <!-- 右侧聊天框 -->
      <div class="rightBox" v-if="dataForm.direction != 'withdraw'">
        <div class="kefu">
          <div
            :isActive="true"
            :w="500"
            :h="620"
            v-on:resizing="resize"
            class="kefu-tuozhuai"
            @activated="activateEv"
            :isResizable="false"
            v-on:dragging="resize"
            :isDrag="false"
          >
            <div class="kefu-title">
              <div id="panel-header" class="header">
                <div class="module1 left-module left-1">
                  <div
                    style="
                      width: 100%;
                      height: 100%;
                      display: flex;
                      justify-content: space-between;
                    "
                  >
                    <div class="module" style="">
                      <div>
                        <span class="nickname">{{ dataForm.username }}</span>
                      </div>

                      <!-- <div>
                          <span class="nickname">{{ params.remarks ? '(' + params.remarks + ')' : '' }}</span>
                        </div> -->

                      <!-- <div class="pointer" style="height: auto;padding-left: 10px;" @click="editFriendRemarks(params)">
                          <img src="~@/assets/img/ri_delete-bin-3-line.png" width="24px" height="25px"/>
                        </div>

                        <div>
                          <span class="nickname">设置备注</span>
                        </div> -->
                    </div>

                    <!-- <div class="module" style="height: auto;display: flex;justify-content: flex-end;">
                        <div class="pointer" style="height: auto;margin-right: 40px;" @click="deleteChat(params)">
                          <i class="el-icon-delete"></i>
                        </div>
                        <div class="pointer" style="height: auto;margin-right: 20px;" @click="close()">
                          <img src="~@/assets/img/clear.png" width="28px" height="28px"/>
                        </div>       
                      </div> -->
                  </div>
                </div>
                <div class="module left-module left-2">
                  <span class="text1">UID:{{ dataForm.usercode }}</span>
                  <span class="text1"
                    >账户类型:<span style="color: #38e1a5">{{
                      dataForm.roleNameDesc
                    }}</span></span
                  >
                  <!-- <span class="text1">推荐人:{{ params.recom_parent_name }}</span> -->
                  <!-- <span class="text1">登录IP:{{ params.loginIp }}</span> -->
                  <span class="text1">订单时间:{{ dataForm.create_time }}</span>
                  <!-- <span class="text1">最后登录时间:{{ params.last_login_time }}</span> -->
                </div>
              </div>
              <!-- <span>在线客服</span> -->
              <!-- <img  src="@/assets/img/Group609.png" @click="show_kefu=false" /> -->
            </div>
            <div class="kegu-nrkegu-nr">
              <chat ref="chat" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <span slot="footer" class="dialog-footer">
      <el-button @click="closeDiog()">取消</el-button>
      <el-button type="primary" @click="closeDiog()">确定</el-button>
    </span>
    <!-- 通过放行 -->
    <passtpn
      v-if="passtpnVisible"
      :append-to-body="true"
      @refreshDataList="closeDiog()"
      ref="passtpnUpdate"
    >
    </passtpn>
    <!-- 取消订单 -->
    <deletMsg
      v-if="deletVisible"
      :append-to-body="true"
      @refreshDataList="closeDiog()"
      ref="deletUpdate"
    >
    </deletMsg>
  </el-dialog>
</template>

<script>
import { Debounce } from "@/utils/debounce";
import passtpn from "./bank-ord-passtpn";
import { encrypt } from "@/utils/crypto";
import VueDragResize from "vue-drag-resize";
import Chat from "@/components/chat2";
import deletMsg from "./bank-ord-delet";
export default {
  data() {
    return {
      hasFreeCondition: 0,
      visible: false,
      addOrUpdateVisible: false,
      passtpnVisible: false,
      deletVisible: false,
      list: [],
      dataForm: {
        id: "",
      },
      row: "",
      roleList: [],
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      dataRule: {
        login_safeword: [
          { required: true, message: "资金密码不能为空", trigger: "blur" },
        ],
      },
      params: {
        username: "昵称",
        usercode: "123123",
        role_name: "工作",
        recom_parent_name: "",
        loginIp: "",
        create_time: "",
        last_login_time: "",
      },
    };
  },
  components: { VueDragResize, Chat, passtpn, deletMsg },
  methods: {
    //拖拽事件
    resize(newRect) {},
    activateEv() {
      // 结局文本框无法输入的问题
      if (this.$refs["chat"] && this.$refs["chat"].$refs["input"]) {
        this.$refs["chat"].$refs["input"].focus();
      }
    },
    init(row) {
      this.row = row || "";
      this.dataForm = { ...row };
      this.visible = true;
      this.formatData(); //过滤支付方式

      if (row.direction !== "withdraw") {
        this.$nextTick(() => {
          this.$refs["chat"].init(this.dataForm);
        });
      }
    },
    concatenateNumber(number) {
      // 在这里执行你的拼接逻辑
      return "dataForm.param_name" + number;
    },
    formatData() {
      let index = 1;
      Object.keys(this.dataForm).forEach((key) => {
        if (key.indexOf("param_name") !== -1) {
          if (this.dataForm[key] && this.dataForm[key].length > 0) {
            this.list.push({
              ["param_name"]: this.dataForm["param_name" + index],
              ["param_value"]: this.dataForm["param_value" + index],
            });
            index++;
          }
        }
      });
    },
    handClose() {
      this.$nextTick(() => {
        this.$refs["chat"].close();
      });
      this.list = []
      this.roleList=[],
      this.$emit("refreshDataList", this.page);
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
    },
    // getC2cManagerInfo() {
    //   //获取详情
    //   this.$http({
    //     url: this.$http.adornUrl("/c2cOrder/detail"),
    //     method: "post",
    //     data: this.$http.adornData(
    //       Object.assign({
    //         order_no: this.dataForm.order_no,
    //       })
    //     ),
    //   }).then(({ data }) => {
    //     if (data.code == 0) {
    //       this.roleList = data.data;
    //     }
    //   });
    // },
    passtHandle(row, n) {
      this.passtpnVisible = true;
      this.$nextTick(() => {
        this.$refs.passtpnUpdate.init(row, n);
      });
    },
    deletOrdeHandle(row) {
      this.deletVisible = true;
      this.$nextTick(() => {
        this.$refs.deletUpdate.init(row);
      });
    },
    // 表单提交
    closeDiog() {
      this.visible = false;
      this.$emit("refreshDataList", this.page);
    },
  },
};
</script>

<style lang="scss" scoped>
.transport-dialog .table-con .el-form-item {
  margin-top: 16px;
  margin-bottom: 16px !important;
}
.allBox {
  width: 100%;
  overflow: hidden;
}
.leftBox {
  width: 550px;
  float: left;
}
.rightBox {
  width: 500px;
  float: right;
  padding: 30px;
  height: 600px;
  // background: yellow;
}
.moneyText {
  font-size: 20px;
}
.minibox {
  height: 50px;
  line-height: 50px;
  letter-spacing: 1px;
  border-bottom: 1px solid #eaecef;
}
.miniMiddle {
  margin-top: 20px;
  overflow: hidden;
  min-height: 800px;
  border-left: 1px solid #eaecef;
}
.cicle {
  display: block;
  width: 10px;
  height: 10px;
  border: 2px solid #1d91ff;
  border-radius: 50%;
  position: absolute;
  left: 13px;
  margin-top: 3px;
}
.chetBox {
  padding-left: 10px;
  margin-bottom: 40px;
}
.title {
  font-size: 17px;
  color: black;
  margin-bottom: 10px;
  font-weight: 540;
}
.titLeft {
  float: left;
  color: #727a89;
  font-size: 14px;
}
.titRight {
  float: right;
  color: #727a89;
}
.textBush {
  float: left;
  font-size: 16px;
  color: black;
}
.textFig {
  float: right;
  font-size: 19px;
}
.textFigTwo {
  float: right;
  font-size: 16px;
  color: black;
}
.textTitle {
  overflow: hidden;
  width: 400px;
}
.miniTitle {
  overflow: hidden;
  margin-bottom: 10px;
  width: 400px;
}
</style>

<style>
#app {
  min-height: 100vh;
  overflow-x: hidden;
}
body {
  margin: 0;
}
/* 客服 */
.service-box {
  bottom: 1px;
  cursor: pointer;
}
.vdr.active:before {
  display: none;
}

/* .item-dropdown-wrapper.el-dropdown-menu,.quotes-dropdown.el-dropdown-menu {
  background: #24282D !important;
  border: 1px solid #24282D !important;
}

.popper__arrow {
  border-bottom-color: #24282D !important;
}

.el-popper[x-placement^=bottom] .popper__arrow::after {
  border-bottom-color: #24282D !important;
} */

.item-dropdown-wrapper .el-dropdown-menu__item:focus,
.item-dropdown-wrapper .el-dropdown-menu__item:not(.is-disabled):hover {
  background-color: transparent !important;
  color: #1d91ff !important;
}

.quotes-dropdown.el-dropdown-menu {
  padding: 0 !important;
}

.quotes-dropdown .el-dropdown-menu__item {
  min-width: 60px !important;
  padding: 6px 13px !important;
}

.quotes-dropdown .el-dropdown-menu__item:hover {
  background: #2c3138 !important;
}
</style>

<style scoped>
.kefu {
  cursor: pointer;
}
.embed-responsive-item {
  width: 100%;
  height: 100%;
  border: none;
}
.kefu-title {
  width: 100%;
  height: 87px;
  position: relative;
  text-align: center;
  background: linear-gradient(
    90.3deg,
    rgba(45, 45, 53, 0.71) 0.21%,
    #23232e 99.83%
  );
  border-radius: 10px 10px 0px 0px;
}
.kegu-nrkegu-nr {
  width: 100%;
  height: 559px;
}
.kefu-title span {
  text-align: center;
  font-style: normal;
  font-weight: 400;
  font-size: 20px;
  line-height: 61px;
  color: #ffffff;
}
.kefu-title img {
  position: absolute;
  width: 30px;
  height: 30px;
  right: 21px;
  top: 16px;
}

::v-deep .kefu-title .date {
  color: #fff !important;
}
.kefu-tuozhuai {
  box-shadow: 0px 0px 20px rgba(0, 0, 0, 0.5);
  outline: none;
  background: #ffffff;
  border-radius: 10px;
}

.kefu-tuozhuai:before {
  position: absolute;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
}
</style>

<style lang="less" scoped>
.pointer {
  cursor: pointer;
}

.header {
  // max-width: 1450px;
  // max-width: 1290px;
  width: auto;
  height: 87px;
  background-color: #2e2f33;
  // display: flex;
}

#panel-header {
  align-items: center;
  justify-content: space-between;
  box-sizing: border-box;
  border-bottom: 1px solid #f5eeee;
  color: #ffffff;

  .module1 {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-shrink: 0;
  }

  .module {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
  }

  .left-1 {
    background: linear-gradient(90deg, #121212 0%, #2f3032 51.04%, #6e6f75 100%),
      linear-gradient(0deg, #393939, #393939);
    // width: 1171px;
    height: 55px;
    // top: 204px;
    // left: 720px;
    border-radius: 0px 10px 0px 0px;
  }

  .left-2 {
    background-color: #2e2f33;
    // width: 1171px
    height: 32px;
    // top: 259px
    // left: 720px
  }

  .left-module {
    padding-right: 0px;

    .nickname {
      // overflow: hidden;
      // white-space: nowrap;
      // text-overflow: ellipsis;
      font-family: PingFang SC;
      font-size: 18px;
      font-weight: 500;
      line-height: 25px;
      letter-spacing: 0em;
      text-align: center;

      padding-left: 10px;
    }

    .text1 {
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      font-family: PingFang SC;
      font-size: 12px;
      font-weight: 400;
      line-height: 17px;
      letter-spacing: 0em;
      text-align: center;

      font-family: PingFang SC;
      font-size: 12px;
      font-weight: 500;
      line-height: 17px;
      letter-spacing: 0em;
      text-align: center;

      padding-left: 10px;
    }
  }

  .center-module {
    flex-direction: column;
    justify-content: center;

    .online {
      color: #cccccc;
      font-weight: 300;
      font-size: 15px;

      &.color {
        color: #1890ff;
      }

      .online-status {
        position: relative;
        top: -1px;
        display: inline-block;
        width: 6px;
        height: 6px;
        vertical-align: middle;
        border-radius: 50%;
        position: relative;
        background-color: #1890ff;
        margin-right: 5px;

        &:after {
          position: absolute;
          top: -1px;
          left: -1px;
          width: 100%;
          height: 100%;
          border: 1px solid #1890ff;
          border-radius: 50%;
          -webkit-animation: antStatusProcessing 1.2s ease-in-out infinite;
          animation: antStatusProcessing 1.2s ease-in-out infinite;
          content: "";
        }
      }
    }

    .keyboard-status {
      height: 20px;
      line-height: 18px;
      font-size: 10px;
      animation: inputfade 600ms infinite;
      -webkit-animation: inputfade 600ms infinite;
    }
  }

  .right-module {
    display: flex;
    justify-content: flex-end;
    align-items: center;

    p {
      cursor: pointer;
      margin: 0 8px;
      font-size: 20px;
      color: #828f95;
      &:active i {
        font-size: 26px;
        transform: scale(1.3);
        transition: ease 0.5s;
        color: red;
      }
    }
  }
}

/* css 动画 */
@keyframes inputfade {
  from {
    opacity: 1;
  }

  50% {
    opacity: 0.4;
  }

  to {
    opacity: 1;
  }
}

@-webkit-keyframes inputfade {
  from {
    opacity: 1;
  }

  50% {
    opacity: 0.4;
  }

  to {
    opacity: 1;
  }
}

@-webkit-keyframes antStatusProcessing {
  0% {
    -webkit-transform: scale(0.8);
    transform: scale(0.8);
    opacity: 0.5;
  }

  to {
    -webkit-transform: scale(2.4);
    transform: scale(2.4);
    opacity: 0;
  }
}

@keyframes antStatusProcessing {
  0% {
    -webkit-transform: scale(0.8);
    transform: scale(0.8);
    opacity: 0.5;
  }

  to {
    -webkit-transform: scale(2.4);
    transform: scale(2.4);
    opacity: 0;
  }
}
.textmela {
  float: right;
  font-size: 16px;
  color: black;
}
.menuBox {
  border-bottom: 1px dashed #ece9e9;
  margin-top: 25px;
}
</style>
