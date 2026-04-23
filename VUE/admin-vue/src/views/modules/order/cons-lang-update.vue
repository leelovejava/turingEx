<template>
  <el-dialog
    :title="!id ? '支付方式类型 多语言配置' : '支付方式模板 多语言配置'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close="close()"
    @open="open()"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="80px"
    >
      <el-form-item prop="userName">
        <div class="green">
          1.多语言格式: 语言和翻译“##”分隔，不同语言“&&“分隔，如
          zh-CN##账户号&&en##Account Number
        </div>
        <div class="green">{{ "2.语种: " + languageIntro }}</div>
        <div v-if="!id" class="green">
          {{ "3.支付方式类型: " + methodTypeIntro }}
        </div>
        <div class="green">
          示例：ru##Все банковские карты&&pt##Todos os cartões
          bancários&&en##All bank cards&&Korean##모든 은행
          카드&&CN##所有銀行卡&&fr##Toutes les cartes
          bancaires&&zh-CN##所有银行卡&&Japanese##すべての銀行カード&&es##Todas
          las tarjetas bancarias&&th##บตรธนาคารทงหมดิind
        </div>
        <div></div>
      </el-form-item>
      <template v-if="!id">
        <div class="langAllBox" v-for="(item, index) in setArr" :key="index">
          <div class="lanOneBox">
            <span class="lanSpan speaInput">{{ "类型 " + item.typeId }}</span>
            <el-input class="lanInput" v-model="item.name" disabled></el-input>
          </div>
          <div class="lanOneBox">
            <span class="lanSpan">{{ "类型 " + item.typeId + "多语言" }}</span>
            <el-input
              class="lanInput"
              type="textarea"
              v-model="item.trans"
            ></el-input>
            <el-button
              class="lanButton"
              type="primary"
              size="small"
              @click="savePmtTranslate(item.name, item.trans)"
              >保存</el-button
            >
          </div>
        </div>
      </template>
      <!-- 修改多语言 -->
      <template v-if="id">
        <div class="langAllBox">
          <div class="lanOneBox">
            <span class="lanSpan speaInput">支付方式图片</span>
            <img :src="roleList.methodImgUrl" width="100" alt="" />
          </div>
          <div class="lanOneBox">
            <span class="lanSpan speaInput">{{ "支付方式类型 " }}</span>
            <el-input
              class="lanInput"
              v-model="methodTypeName"
              disabled
            ></el-input>
          </div>
          <div class="lanOneBox">
            <span class="lanSpan speaInput">{{ "支付方式名称 " }}</span>
            <el-input
              class="lanInput"
              v-model="roleList.methodName"
              disabled
            ></el-input>
          </div>
          <div class="lanOneBox">
            <span class="lanSpan">{{ "支付方式名称多语言" }}</span>
            <el-input
              class="lanInput"
              type="textarea"
              v-model="roleList.methodNameLangTrans"
            ></el-input>
            <el-button
              class="lanButton"
              type="primary"
              size="small"
              @click="
                saveTranslate(
                  roleList.methodName,
                  '支付方式名称',
                  roleList.methodNameLangTrans
                )
              "
              >保存</el-button
            >
          </div>
          <!-- 参数1 -->
          <div v-if="roleList.paramName1" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput">{{ "参数名1(必填)" }}</span>
              <el-input
                class="lanInput"
                v-model="roleList.paramName1"
                disabled
              ></el-input>
            </div>
            <div class="lanOneBox">
              <span class="lanSpan nasspan">{{ "参数名1多语言" }}</span>
              <el-input
                class="lanInput"
                type="textarea"
                v-model="roleList.paramName1LangTrans"
              ></el-input>
              <el-button
                class="lanButton"
                type="primary"
                size="small"
                @click="
                  saveTranslate(
                    roleList.paramName1,
                    '参数名1',
                    roleList.paramName1LangTrans
                  )
                "
                >保存</el-button
              >
            </div>
          </div>
          <!-- 参数2 -->
          <div v-if="roleList.paramName2" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ "参数名2" }}</span>
              <el-input
                class="lanInput"
                v-model="roleList.paramName2"
                disabled
              ></el-input>
            </div>
            <div class="lanOneBox">
              <span class="lanSpan nasspan">{{ "参数名2多语言" }}</span>
              <el-input
                class="lanInput"
                type="textarea"
                v-model="roleList.paramName2LangTrans"
              ></el-input>
              <el-button
                class="lanButton"
                type="primary"
                size="small"
                @click="
                  saveTranslate(
                    roleList.paramName2,
                    '参数名2',
                    roleList.paramName2LangTrans
                  )
                "
                >保存</el-button
              >
            </div>
          </div>
          <!-- 参数3 -->
          <div v-if="roleList.paramName3" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ "参数名3" }}</span>
              <el-input
                class="lanInput"
                v-model="roleList.paramName3"
                disabled
              ></el-input>
            </div>
            <div class="lanOneBox">
              <span class="lanSpan nasspan">{{ "参数名3多语言" }}</span>
              <el-input
                class="lanInput"
                type="textarea"
                v-model="roleList.paramName3LangTrans"
              ></el-input>
              <el-button
                class="lanButton"
                type="primary"
                size="small"
                @click="
                  saveTranslate(
                    roleList.paramName3,
                    '参数名3',
                    roleList.paramName3LangTrans
                  )
                "
                >保存</el-button
              >
            </div>
          </div>
          <!-- 参数4 -->
          <div v-if="roleList.paramName4" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ "参数名4" }}</span>
              <el-input
                class="lanInput"
                v-model="roleList.paramName4"
                disabled
              ></el-input>
            </div>
            <div class="lanOneBox">
              <span class="lanSpan nasspan">{{ "参数名4多语言" }}</span>
              <el-input
                class="lanInput"
                type="textarea"
                v-model="roleList.paramName4LangTrans"
              ></el-input>
              <el-button
                class="lanButton"
                type="primary"
                size="small"
                @click="
                  saveTranslate(
                    roleList.paramName4,
                    '参数名4',
                    roleList.paramName4LangTrans
                  )
                "
                >保存</el-button
              >
            </div>
          </div>
          <!-- 参数5 -->
          <div v-if="roleList.paramName5" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ "参数名5" }}</span>
              <el-input
                class="lanInput"
                v-model="roleList.paramName5"
                disabled
              ></el-input>
            </div>
            <div class="lanOneBox">
              <span class="lanSpan nasspan">{{ "参数名5多语言" }}</span>
              <el-input
                class="lanInput"
                type="textarea"
                v-model="roleList.paramName5LangTrans"
              ></el-input>
              <el-button
                class="lanButton"
                type="primary"
                size="small"
                @click="
                  saveTranslate(
                    roleList.paramName5,
                    '参数名5',
                    roleList.paramName5LangTrans
                  )
                "
                >保存</el-button
              >
            </div>
          </div>
          <!-- 参数6 -->
          <div v-if="roleList.paramName6" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ "参数名6" }}</span>
              <el-input
                class="lanInput"
                v-model="roleList.paramName6"
                disabled
              ></el-input>
            </div>
            <div class="lanOneBox">
              <span class="lanSpan nasspan">{{ "参数名6多语言" }}</span>
              <el-input
                class="lanInput"
                type="textarea"
                v-model="roleList.paramName6LangTrans"
              ></el-input>
              <el-button
                class="lanButton"
                type="primary"
                size="small"
                @click="
                  saveTranslate(
                    roleList.paramName6,
                    '参数名6',
                    roleList.paramName6LangTrans
                  )
                "
                >保存</el-button
              >
            </div>
          </div>
          <!-- 参数7 -->
          <div v-if="roleList.paramName7" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ "参数名7" }}</span>
              <el-input
                class="lanInput"
                v-model="roleList.paramName7"
                disabled
              ></el-input>
            </div>
            <div class="lanOneBox">
              <span class="lanSpan nasspan">{{ "参数名7多语言" }}</span>
              <el-input
                class="lanInput"
                type="textarea"
                v-model="roleList.paramName7LangTrans"
              ></el-input>
              <el-button
                class="lanButton"
                type="primary"
                size="small"
                @click="
                  saveTranslate(
                    roleList.paramName7,
                    '参数名7',
                    roleList.paramName7LangTrans
                  )
                "
                >保存</el-button
              >
            </div>
          </div>
          <!-- 参数8 -->
          <div v-if="roleList.paramName8" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ "参数名8" }}</span>
              <el-input
                class="lanInput"
                v-model="roleList.paramName8"
                disabled
              ></el-input>
            </div>
            <div class="lanOneBox">
              <span class="lanSpan nasspan">{{ "参数名8多语言" }}</span>
              <el-input
                class="lanInput"
                type="textarea"
                v-model="roleList.paramName8LangTrans"
              ></el-input>
              <el-button
                class="lanButton"
                type="primary"
                size="small"
                @click="
                  saveTranslate(
                    roleList.paramName8,
                    '参数名8',
                    roleList.paramName8LangTrans
                  )
                "
                >保存</el-button
              >
            </div>
          </div>
          <!-- 参数9 -->
          <div v-if="roleList.paramName9" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ "参数名9" }}</span>
              <el-input
                class="lanInput"
                v-model="roleList.paramName9"
                disabled
              ></el-input>
            </div>
            <div class="lanOneBox">
              <span class="lanSpan nasspan">{{ "参数名9多语言" }}</span>
              <el-input
                class="lanInput"
                type="textarea"
                v-model="roleList.paramName9LangTrans"
              ></el-input>
              <el-button
                class="lanButton"
                type="primary"
                size="small"
                @click="
                  saveTranslate(
                    roleList.paramName9,
                    '参数名9',
                    roleList.paramName9LangTrans
                  )
                "
                >保存</el-button
              >
            </div>
          </div>
          <!-- 参数10 -->
          <div
            v-if="
              roleList.paramName10LangTrans !== null && roleList.paramName10
            "
            class="langAllBox"
          >
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ "参数名10" }}</span>
              <el-input
                class="lanInput"
                v-model="roleList.paramName10"
                disabled
              ></el-input>
            </div>
            <div class="lanOneBox">
              <span class="lanSpan nasspan">{{ "参数名10多语言" }}</span>
              <el-input
                class="lanInput"
                type="textarea"
                v-model="roleList.paramName10LangTrans"
              ></el-input>
              <el-button
                class="lanButton"
                type="primary"
                size="small"
                @click="
                  saveTranslate(
                    roleList.paramName10,
                    '参数名10',
                    roleList.paramName10LangTrans
                  )
                "
                >保存</el-button
              >
            </div>
          </div>
          <!-- 参数11 -->
          <div v-if="roleList.paramName11" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ "参数名11" }}</span>
              <el-input
                class="lanInput"
                v-model="roleList.paramName11"
                disabled
              ></el-input>
            </div>
            <div class="lanOneBox">
              <span class="lanSpan nasspan">{{ "参数名11多语言" }}</span>
              <el-input
                class="lanInput"
                type="textarea"
                v-model="roleList.paramName11LangTrans"
              ></el-input>
              <el-button
                class="lanButton"
                type="primary"
                size="small"
                @click="
                  saveTranslate(
                    roleList.paramName11,
                    '参数名11',
                    roleList.paramName11LangTrans
                  )
                "
                >保存</el-button
              >
            </div>
          </div>
          <!-- 参数12 -->
          <div v-if="roleList.paramName12" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ "参数名12" }}</span>
              <el-input
                class="lanInput"
                v-model="roleList.paramName12"
                disabled
              ></el-input>
            </div>
            <div class="lanOneBox">
              <span class="lanSpan nasspan">{{ "参数名12多语言" }}</span>
              <el-input
                class="lanInput"
                type="textarea"
                v-model="roleList.paramName12LangTrans"
              ></el-input>
              <el-button
                class="lanButton"
                type="primary"
                size="small"
                @click="
                  saveTranslate(
                    roleList.paramName12,
                    '参数名12',
                    roleList.paramName12LangTrans
                  )
                "
                >保存</el-button
              >
            </div>
          </div>
          <!-- 参数13 -->
          <div v-if="roleList.paramName13" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ "参数名13" }}</span>
              <el-input
                class="lanInput"
                v-model="roleList.paramName13"
                disabled
              ></el-input>
            </div>
            <div class="lanOneBox">
              <span class="lanSpan nasspan">{{ "参数名13多语言" }}</span>
              <el-input
                class="lanInput"
                type="textarea"
                v-model="roleList.paramName13LangTrans"
              ></el-input>
              <el-button
                class="lanButton"
                type="primary"
                size="small"
                @click="
                  saveTranslate(
                    roleList.paramName13,
                    '参数名13',
                    roleList.paramName13LangTrans
                  )
                "
                >保存</el-button
              >
            </div>
          </div>
          <!-- 参数14 -->
          <div v-if="roleList.paramName14" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ "参数名14" }}</span>
              <el-input
                class="lanInput"
                v-model="roleList.paramName14"
                disabled
              ></el-input>
            </div>
            <div class="lanOneBox">
              <span class="lanSpan nasspan">{{ "参数名14多语言" }}</span>
              <el-input
                class="lanInput"
                type="textarea"
                v-model="roleList.paramName14LangTrans"
              ></el-input>
              <el-button
                class="lanButton"
                type="primary"
                size="small"
                @click="
                  saveTranslate(
                    roleList.paramName14,
                    '参数名14',
                    roleList.paramName14LangTrans
                  )
                "
                >保存</el-button
              >
            </div>
          </div>
          <!-- 参数15 -->
          <div v-if="roleList.paramName15" class="langAllBox">
            <div class="lanOneBox">
              <span class="lanSpan speaInput nasspan">{{ "参数名15" }}</span>
              <el-input
                class="lanInput"
                v-model="roleList.paramName15"
                disabled
              ></el-input>
            </div>
            <div class="lanOneBox">
              <span class="lanSpan nasspan">{{ "参数名15多语言" }}</span>
              <el-input
                class="lanInput"
                type="textarea"
                v-model="roleList.paramName15LangTrans"
              ></el-input>
              <el-button
                class="lanButton"
                type="primary"
                size="small"
                @click="
                  saveTranslate(
                    roleList.paramName15,
                    '参数名15',
                    roleList.paramName15LangTrans
                  )
                "
                >保存</el-button
              >
            </div>
          </div>
        </div>
      </template>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">关闭</el-button>
      <!-- <el-button type="primary" @click="dataFormSubmit()">确定</el-button> -->
      <!-- <el-button type="primary" @click="visible = false">确定</el-button> -->
    </span>
  </el-dialog>
</template>

<script>
import { isEmail, isMobile } from "@/utils/validate";
import { Debounce } from "@/utils/debounce";
import { encrypt } from "@/utils/crypto";
export default {
  data() {
    var validatePassword = (rule, value, callback) => {
      if (!this.dataForm.id && !/\S/.test(value)) {
        callback(new Error("密码不能为空"));
      } else {
        callback();
      }
    };
    var validateEmail = (rule, value, callback) => {
      if (!isEmail(value)) {
        callback(new Error("邮箱格式错误"));
      } else {
        callback();
      }
    };
    var validateMobile = (rule, value, callback) => {
      if (!isMobile(value)) {
        callback(new Error("手机号格式错误"));
      } else {
        callback();
      }
    };
    return {
      visible: false,
      roleList: {},
      languageIntro: "",
      methodTypeIntro: "",
      methodTypeName: "",
      setArr: [
        {
          name: "银行卡",
          trans: "",
          transTypeId: "trans_1",
          typeId: "1",
        },
      ],
      id: "",
      dataForm: {
        parentsUseCode: "",
        remarks: "",
        username: "",
        password: "",
        email: "",
        mobile: "",
        status: 1,
      },
      options: [
        {
          label1: "正常",
          value1: true,
        },
        {
          label1: "限制登录",
          value1: false,
        },
      ],
      optionsTwo: [
        {
          label2: "正常",
          value2: true,
        },
        {
          label2: "业务锁定(登录不受影响,锁定后无法购买订单和提现)",
          value2: false,
        },
      ],
      optionsThree: [
        {
          label3: "正常",
          value3: true,
        },
        {
          label3: "限制提现",
          value3: false,
        },
      ],
      dataRule: {
        username: [
          { required: true, message: "用户名不能为空", trigger: "blur" },
          // { pattern: /\s\S+|S+\s|\S/, message: '请输入正确的用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: "密码不能为空", trigger: "blur" },
        ],
        // email: [
        //   { required: true, message: '邮箱不能为空', trigger: 'blur' },
        //   { validator: validateEmail, trigger: 'blur' }
        // ],
        // mobile: [
        //   { required: true, message: '手机号不能为空', trigger: 'blur' },
        //   { validator: validateMobile, trigger: 'blur' }
        // ]
      },
    };
  },
  created() {
    //this.getPmtLang();
  },
  methods: {
    initTwo(id, methodTypeName) {
      console.log("id" + id);
      this.dataForm.remarks = "";
      this.id = id || "";
      this.methodTypeName = methodTypeName || "";
      // if (row) {
      //   this.dataForm.username = row.userName;
      //   // this.options.value1 = row.loginAuthority
      //   // this.optionsTwo.value2 = row.enabled
      //   // this.optionsThree.value3 = row.withdrawAuthority
      //   this.dataForm.remarks = row.remarks;
      // }
      this.visible = true;
      this.$nextTick(() => {
        //this.$refs.dataForm.resetFields()
      });
    },
    open() {
      if (this.id) {
        this.getDesc();
      } else {
        this.getPmtLang();
      }
    },
    close() {
      this.languageIntro = "";
      this.setArr = [];
    },
    // closeDiag(){
    //   this.setArr = []
    // },
    resClear() {
      this.dataForm = {
        parentsUseCode: "",
        remarks: "",
        username: "",
        password: "",
        email: "",
        mobile: "",
      };
    },
    getPmtLang() {
      //新增列表
      this.$http({
        url: this.$http.adornUrl("/paymentMethodConfig/getPmtLang"),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              current: this.pageIndex,
              size: this.pageSize,
            },
            {
              prodName: this.dataForm.prodName,
            }
          )
        ),
      }).then(({ data }) => {
        if (data.code == 0) {
          (this.languageIntro = data.data.languageIntro),
            (this.methodTypeIntro = data.data.methodTypeIntro),
            (this.setArr = data.data.methodTypeList);
          console.log(data.data.methodTypeList);
          console.log(this.setArr);
        } else {
          this.$message({
            message: data.msg,
            type: "error",
          });
        }
        console.log(data);
      });
    },
    getDesc() {
      //修改列表
      this.$http({
        url: this.$http.adornUrl("/paymentMethodConfig/getDesc"),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              id: this.id,
              current: this.pageIndex,
              size: this.pageSize,
            },
            {
              prodName: this.dataForm.prodName,
            }
          )
        ),
      }).then(({ data }) => {
        console.log(data);
        this.roleList = data.data;
        console.log(this.roleList);
        if (data.code == 0) {
          this.languageIntro = data.data.languageIntro;
          // (this.methodTypeIntro = data.data.methodTypeIntro),
          // (this.setArr = data.data.methodTypeList);
          // console.log(data.data.methodTypeList);
          // console.log(this.setArr);
        } else {
          this.$message({
            message: data.msg,
            type: "error",
          });
        }
        console.log(data);
      });
    },
    savePmtTranslate(name, trans, done) {
      //新增多语言保存
      this.$http({
        url: this.$http.adornUrl("/paymentMethodConfig/savePmtTranslate"),
        method: "post",
        data: this.$http.adornData(
          Object.assign({
            name: name,
            trans: trans,
          })
        ),
      }).then(({ data }) => {
        if (data.code == 0) {
          this.$message({
            message: "保存成功",
            type: "success",
          });
          this.$emit("refreshDataListTwo");
        } else {
          this.$message({
            message: data.msg,
            type: "error",
          });
        }
        if (done) {
          done();
        }
      });
    },
    saveTranslate(content, contentName, langTrans) {
      //修改多语言保存
      this.$http({
        url: this.$http.adornUrl("/paymentMethodConfig/saveTranslate"),
        method: "post",
        data: this.$http.adornData(
          Object.assign({
            content: content,
            contentName: contentName,
            langTrans: langTrans,
            id: this.id,
          })
        ),
      }).then(({ data }) => {
        if (data.code == 0) {
          this.$message({
            message: "保存成功",
            type: "success",
          });
          this.$emit("refreshDataListTwo");
        } else {
          this.$message({
            message: data.msg,
            type: "error",
          });
        }
        if (done) {
          done();
        }
      });
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          if (this.id) {
            this.$http({
              url: this.$http.adornUrl(`/userData/update`), //修改
              method: "post",
              data: this.$http.adornData({
                enabled: this.optionsTwo.value2,
                loginAuthority: this.options.value1,
                withdrawAuthority: this.optionsThree.value3,
                userId: this.roleList.userId,
                remarks: this.dataForm.remarks,
              }),
            }).then(({ data }) => {
              if (data.code == 0) {
                this.$message({
                  message: "操作成功",
                  type: "success",
                  duration: 1500,
                  onClose: () => {
                    this.resClear();
                    this.visible = false;
                    this.$emit("refreshDataListTwo");
                  },
                });
              } else {
                this.$message({
                  message: data.msg,
                  type: "error",
                });
              }
            });
          } else {
            this.$http({
              url: this.$http.adornUrl(`/userData/add`), //新增
              method: "post",
              data: this.$http.adornData({
                username: this.dataForm.username,
                enabled: this.optionsTwo.value2,
                loginAuthority: this.options.value1,
                parentsUseCode: this.dataForm.parentsUseCode,
                password: encrypt(this.dataForm.password),
                remarks: this.dataForm.remarks,
              }),
            }).then(({ data }) => {
              if (data.code == 0) {
                this.$message({
                  message: "操作成功",
                  type: "success",
                  duration: 1500,
                  onClose: () => {
                    this.resClear();
                    this.visible = false;
                    this.$emit("refreshDataList");
                  },
                });
              } else {
                this.$message({
                  message: data.msg,
                  type: "error",
                });
              }
            });
          }
        }
      });
    }),
  },
};
</script>
<style scoped>
.langAllBox {
  overflow: hidden;
  margin: 10px 0;
}
.lanOneBox {
  overflow: hidden;
  margin-bottom: 20px;
}
.lanInput {
  width: 70%;
}
.lanSpan,
.lanInput,
.lanButton {
  float: left;
}
.lanSpan {
  width: 100px;
  margin-right: 3%;
}
.lanInput {
  margin-right: 2%;
}
.speaInput {
  margin-right: 3%;
}
.lanOneBox {
  line-height: 31px;
}
.nasspan {
  width: 100px;
  margin-right: 3%;
}
</style>
