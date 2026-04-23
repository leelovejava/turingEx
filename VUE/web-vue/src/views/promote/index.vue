<template>
  <div>
    <div class="css-1877llp" style="margin-top: 64px;">
      <div class="css-15owl46">
        <div class="css-gnqbje">
          <div class="css-o7pwem">
            <div class="css-hp51qo">
              <div class="css-y4fji5"><h1 data-bn-type="text" class="css-46s9y4">
                <span>{{$t('message.home.tui1')}}。</span>&nbsp;<span>{{$t('message.home.tui2')}}。</span>
              </h1>
                <div data-bn-type="text" class="css-3h343h">{{$t('message.home.tui3')}}。</div>
                <a data-bn-type="link" cursor="pointer" @click="dialogVisible4=true" class="css-1ufz5jz" style="display: flex;align-items: center">{{$t('message.home.tui4')}}
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" class="css-sg07c1">
                    <path fill-rule="evenodd" clip-rule="evenodd"
                          d="M12.288 12l-3.89 3.89 1.768 1.767L15.823 12l-1.768-1.768-3.889-3.889-1.768 1.768 3.89 3.89z"
                          fill="currentColor"></path>
                  </svg>
                </a></div>
              <div class="css-1igt9ee">
                <div class="css-1beneqn">
                  <div data-bn-type="text" class="css-1jq4ozh">{{$t('message.home.tui5')}}</div>
                </div>
                <div
                    style="background-color: rgb(254, 246, 216);padding: 12px 16px;border-radius: 4px;box-sizing: border-box;">
                  <div>{{$t('message.home.tui6')}}</div>
                  <div style="margin-top: 6px;font-weight: 600;font-size: 24px;">{{ promote_data.children }}</div>
                  <div class="css-ifm649" style="margin-top: 15px;">
                    <div class="css-8vr7wn">
                      <div data-bn-type="text" class="css-7nipyh">{{$t('message.home.tui7')}}</div>
                      <h5 data-bn-type="text" class="css-8jsrjd" style="margin-top: 6px;">{{
                          promote_data.level_1
                        }}</h5></div>
                    <div class="css-8vr7wn">
                      <div data-bn-type="text" class="css-7nipyh">{{$t('message.home.tui8')}}</div>
                      <h5 data-bn-type="text" class="css-8jsrjd" style="margin-top: 6px;">{{
                          promote_data.level_2
                        }}</h5></div>
                    <div class="css-8vr7wn">
                      <div data-bn-type="text" class="css-7nipyh">{{$t('message.home.tui9')}}</div>
                      <h5 data-bn-type="text" class="css-8jsrjd" style="margin-top: 6px;">{{
                          promote_data.level_3
                        }}</h5></div>
                  </div>


                </div>
                <div class="css-1dn9g81" style="margin-top: 20px;">
                  <div data-bn-type="text" class="css-ey7pe9">{{$t('message.home.tui10')}}</div>
                  <div data-bn-type="text" class="css-1rg3smu">{{ localuser_data.usercode }}</div>
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" @click="copyEvent(1)" fill="none"
                       cursor="pointer" class="css-1ccca2">
                    <path fill-rule="evenodd" clip-rule="evenodd" d="M9 3h11v13h-3V6H9V3zM4 8v13h11V8.02L4 8z"
                          fill="currentColor"></path>
                  </svg>
                </div>
                <div class="css-139jsk0">
                  <div data-bn-type="text" class="css-ey7pe9">{{$t('message.home.tui11')}}</div>
                  <div data-bn-type="text" class="css-1t9x2w1"><span class="css-11eo8t1" style="">{{
                      localuser_data.url
                    }}</span><span></span>
                  </div>
                  <svg xmlns="http://www.w3.org/2000/svg" @click="copyEvent(2)" viewBox="0 0 24 24" fill="none"
                       cursor="pointer" class="css-1ccca2">
                    <path fill-rule="evenodd" clip-rule="evenodd" d="M9 3h11v13h-3V6H9V3zM4 8v13h11V8.02L4 8z"
                          fill="currentColor"></path>
                  </svg>
                </div>
                <div class="css-1iralb9">
                                    <img
                                        src="./referralcard-invitefriendbutton-icon.png"
                                        class="css-kuidie">
                                    <button data-bn-type="button" class=" css-5uzimb" @click="dialogVisible = true">
                                      <div data-bn-type="text" class="css-131en3l">{{$t('message.home.tui12')}}</div>
                                    </button>
                  <button data-bn-type="button" class=" css-sl1k40" @click="dialogVisible2=true">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" class="css-f1b9tf">
                      <path fill-rule="evenodd" clip-rule="evenodd"
                            d="M4 4h7v7H4V4zm0 9h7v7H4v-7zm11 0h-2v4h4v-2h3v-2h-4v2h-1v-2zm5 3h-2v2h-2v2h4v-4zm-5 2h-2v2h2v-2zM13 4h7v7h-7V4zM8.5 6.5h-2v2h2v-2zm-2 9h2v2h-2v-2zm11-9h-2v2h2v-2z"
                            fill="currentColor"></path>
                    </svg>
                  </button>
                </div>
              </div>
            </div>
            <div class="css-153sp45">
              <div class="section css-430j5i" id="Dashboard">
                <div class="css-vurnku">
                  <div class="css-u5xevn">
                    <div class="css-ttgphu">
                      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" class="css-3tndv1">
                        <path d="M4 20H12V12H20V4H4V20Z" fill="url(#paint0_linear)"></path>
                        <path d="M12 28H28V12H20V20H12V28Z" fill="#707A8A"></path>
                        <defs>
                          <linearGradient id="paint0_linear" x1="12" y1="4" x2="12" y2="20"
                                          gradientUnits="userSpaceOnUse">
                            <stop stop-color="#F0B90B"></stop>
                            <stop offset="1" stop-color="#F8D33A"></stop>
                          </linearGradient>
                        </defs>
                      </svg>
                    </div>
                    <h3 data-bn-type="text" class="css-rcthv6">{{$t('message.home.tui13')}}</h3>
                    <div style="flex: 1;"><span @click="tuiguangdakai" style="float: right;">{{$t('message.user.wodekehu')}} ></span></div>
                  </div>
                  <div class="css-196bpvs">
                    <div class="css-15owl46">
                      <div class="css-z2wfdb">
                        <div id="tab-ALL" class="css-ov54vn">
                          <div class=" css-i5mu3w" :class="show_time==''?'active':''" @click="timetabe('')">
                            <div data-bn-type="text" class="css-1cjl26j">{{$t('message.home.tui14')}}</div>
                          </div>
                        </div>
                        <div id="tab-YESTERDAY" class="css-ov54vn">
                          <div class=" css-i5mu3w" :class="show_time=='day'?'active':''" @click="timetabe('day')">
                            <div data-bn-type="text" class="css-1cjl26j">{{$t('message.home.tui15')}}</div>
                          </div>
                        </div>
                        <div id="tab-THIS_WEEK" class="css-ov54vn">
                          <div class=" css-i5mu3w" :class="show_time=='week'?'active':''" @click="timetabe('week')">
                            <div data-bn-type="text" class="css-1cjl26j">{{$t('message.home.tui16')}}</div>
                          </div>
                        </div>
                        <div id="tab-THIS_MONTH" class="css-ov54vn">
                          <div class=" css-i5mu3w" :class="show_time=='month'?'active':''" @click="timetabe('month')">
                            <div data-bn-type="text" class="css-1cjl26j">{{$t('message.home.tui17')}}</div>
                          </div>
                        </div>
                      </div>
                      <div class="css-gnqbje"></div>
                      <div class="css-1bzb8nq"></div>
                      <div class="css-1bzb8nq"></div>
                      <div class="css-1bzb8nq"></div>
                    </div>
                  </div>
                  <div class="css-ktxhrn">
                    <div class="css-mp494r">
                      <div class="css-c3mc5j">
                        <div class="css-1pydvw">
                          <div data-bn-type="text" class="css-8mo9v9">{{$t('message.home.tui18')}}</div>
                          <div class="css-18mqml5">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" class="css-ew2l8i">
                              <path fill-rule="evenodd" clip-rule="evenodd"
                                    d="M12 21a9 9 0 100-18 9 9 0 000 18zm-1.25-5.5V18h2.5v-2.5h-2.5zm0-9.5v7h2.5V6h-2.5z"
                                    fill="currentColor"></path>
                            </svg>
                          </div>
                          <div class="bn-tooltip-box css-1yof1af" data-popper-reference-hidden="true"
                               data-popper-escaped="false" data-popper-placement="top"
                               style="position: absolute; left: 129px; top: 848px; transition: opacity 120ms ease-in-out 0s, transform 120ms ease-in-out 0s; opacity: 0; transform: translate3d(0px, -6px, 0px); visibility: hidden;">
                            {{$t('message.home.tui19')}}
                            <div class="bn-tooltip-arrow css-1u9esp9" data-popper-arrow="true"
                                 style="position: absolute; left: 157px;"></div>
                            <i class="gap-fill"></i></div>
                        </div>
                        <h3 data-bn-type="text" class="css-xw688i">{{ getPromoteData_data.rechargeRecom }} USDT</h3>
                        <div data-bn-type="text" class="css-1f23r63">{{ getPromoteData_data.rechargeRecom }} USDT</div>
                      </div>
                      <div class="css-c3mc5j">
                        <div class="css-1pydvw">
                          <div data-bn-type="text" class="css-8mo9v9">{{$t('message.home.tui20')}}</div>
                          <div class="css-18mqml5">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" class="css-ew2l8i">
                              <path fill-rule="evenodd" clip-rule="evenodd"
                                    d="M12 21a9 9 0 100-18 9 9 0 000 18zm-1.25-5.5V18h2.5v-2.5h-2.5zm0-9.5v7h2.5V6h-2.5z"
                                    fill="currentColor"></path>
                            </svg>
                          </div>
                          <div class="bn-tooltip-box css-1yof1af" data-popper-reference-hidden="true"
                               data-popper-escaped="false" data-popper-placement="top"
                               style="position: absolute; left: 541px; top: 864px; transition: opacity 120ms ease-in-out 0s, transform 120ms ease-in-out 0s; opacity: 0; transform: translate3d(0px, -6px, 0px); visibility: hidden;">
                            {{$t('message.home.tui21')}}。
                            <div class="bn-tooltip-arrow css-1u9esp9" data-popper-arrow="true"
                                 style="position: absolute; left: 123px;"></div>
                            <i class="gap-fill"></i></div>
                        </div>
                        <h3 data-bn-type="text" class="css-xw688i">{{ getPromoteData_data.peopleNum }}</h3>
                      </div>
                      <div class="css-c3mc5j">
                        <div class="css-1pydvw">
                          <div data-bn-type="text" class="css-8mo9v9">{{$t('message.home.tui22')}}</div>
                          <div class="css-18mqml5">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" class="css-ew2l8i">
                              <path fill-rule="evenodd" clip-rule="evenodd"
                                    d="M12 21a9 9 0 100-18 9 9 0 000 18zm-1.25-5.5V18h2.5v-2.5h-2.5zm0-9.5v7h2.5V6h-2.5z"
                                    fill="currentColor"></path>
                            </svg>
                          </div>
                          <div class="bn-tooltip-box css-1yof1af" data-popper-reference-hidden="true"
                               data-popper-escaped="true" data-popper-placement="top"
                               style="position: absolute; left: 143px; top: 996px; transition: opacity 120ms ease-in-out 0s, transform 120ms ease-in-out 0s; opacity: 0; transform: translate3d(0px, -6px, 0px); visibility: hidden;">
                            {{$t('message.home.tui23')}}
                            <div class="bn-tooltip-arrow css-1u9esp9" data-popper-arrow="true"
                                 style="position: absolute; left: 129px;"></div>
                            <i class="gap-fill"></i></div>
                        </div>
                        <h3 data-bn-type="text" class="css-xw688i">{{ getPromoteData_data.rechargeRecom }}</h3>
                      </div>
                    </div>
                  </div>
                  <div data-bn-type="text" class="css-2rxr5c" style="font-size:14px;">{{$t('message.home.tui24')}}
                  </div>
                  <div data-bn-type="text" class="css-1ez451f" style="font-size:14px;">{{$t('message.home.tui25')}}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="yaoqinghaoyou">
      <el-dialog
          width="406px"
          :show-close="false"
          v-model="dialogVisible"
      >
        <div>
          <div class="xuanchaun">
            <div class="xuanchaun9">
              <img src="../../assets/newImages/promote/pop_mobile_turnoff@2x.png" @click="dialogVisible=false" />
            </div>

            <div class="xuanchaun-text">{{$t('message.home.tui26')}}</div>
            <div class="xuanchaun-text2">{{$t('message.home.tui27')}}</div>
          </div>
          <div class="saoma">
            <img src="/src/assets/images/compositeHome/logo.png" style="width: 130px;height: 35px;vertical-align: middle;float: left;margin-left: 17px;margin-top: 57px;" />
            <div>
              <vue-qrcode :value="localuser_data.url" style="width: 88px;height: 88px;vertical-align: middle; float: right;margin-top: 35px;margin-right: 17px;"></vue-qrcode>
            </div>
            <!-- <img :src="localuser_data.usercode_qr" style="" /> -->
          </div>
        </div>
      </el-dialog>
    </div>
    <div class="yaoqinghaoyou">
      <el-dialog
          width="315px"
          v-model="dialogVisible2"

          :show-close="false">
        <div>
          <div class="xuanchaun" style="height: 500px;background-image: none;background: #111216;">
            <div class="xuanchaun9">
              <div style="font-size: 16px;color: #FFFFFF;padding-top: 24px;margin-left: 24px;float: left;">{{$t('message.home.tui28')}}</div>
              <img src="../../assets/newImages/promote/pop_mobile_turnoff@2x2.png" @click="dialogVisible2=false" />
            </div>
            <div>
              <vue-qrcode :value="localuser_data.url" style="width: 100%;padding:24px 24px 0 24px;display: block;box-sizing: border-box;"></vue-qrcode>
            </div>
            <div style="width: 100%;height: 50px;padding-left: 24px;padding-right: 24px; box-sizing: border-box;">
              <div style="background: #FFFFFF;width: 100%;height: 50px;text-align: center;">
                <div>{{$t('message.home.tui29')}}</div>
                <div style="font-weight: bold;margin-top: 2px;">{{localuser_data.usercode}}</div>
              </div>
            </div>
          </div>
        </div>
      </el-dialog>
    </div>
    <div class="wodetuiguang">
      <el-dialog
          :title="$t('message.home.tui30')"
          width="618px"
          :append-to-body="true"
          v-model="dialogVisible3"
          >
        <div class="tuiguang_tab">
          <div :style="level==1?'background: #f5f5f5;':''" @click="tuiguangtabqiehuan('1')">{{$t('message.home.tui31')}}</div>
          <div :style="level==2?'background: #f5f5f5;':''" @click="tuiguangtabqiehuan('2')">{{$t('message.home.tui32')}}</div>
          <div :style="level==3?'background: #f5f5f5;':''" @click="tuiguangtabqiehuan('3')">{{$t('message.home.tui33')}}</div>
        </div>

        <div class="tuiguang_nr">
          <div class="wodetuiguang-biaotou">
            <div class="wodetuiguang-biaotou-text">{{$t('message.user.yonghurenshu')}}</div>
            <div class="wodetuiguang-biaotou-text">{{$t('message.user.zongrenshu')}}</div>
            <div class="wodetuiguang-biaotou-text">{{$t('message.user.shimingrenzheng')}}</div>
          </div>
          <div
              class="list"
              v-infinite-scroll="load"
              infinite-scroll-disabled="disabled">
            <div  v-for="(item,index) in list_data" :key="index" class="list-item">

              <div class="list-item-div">{{ item.username }}</div>
              <div class="list-item-div">{{ item.username }}</div>
              <div class="list-item-div">{{ item.username }}</div>
            </div>
          </div>
          <p v-if="loading" style="text-align: center;">{{$t('message.user.jiazaizhong')}}...</p>
          <p v-if="noMore" style="text-align: center;padding-top: 20px;border-top: 1px solid #eeeeee;">{{$t('message.user.meiyougengduol')}}</p>
        </div>
      </el-dialog>
    </div>
    <!--规则-->
    <div class="guize">
      <el-dialog
          width="840px"
          :show-close="false"
          :append-to-body="true"
          v-model="dialogVisible4"
          >
        <div class="guize-beijing">
          <img v-if="language=='zh-CN'" class="guize-beijing-img" src="../../assets/newImages/promote/Frame402.png" />
          <img v-if="language=='en'" class="guize-beijing-img" src="../../assets/newImages/promote/Frame403.png" />
          <img v-if="language=='CN'" class="guize-beijing-img" src="../../assets/newImages/promote/Frame404.png" />
          <img @click="dialogVisible4=false" class="guanbi" src="../../assets/newImages/promote/pop_mobile_turnoff@2x2.png" />
        </div>
        <div class="guize-text">
          <div class="guize-text1">{{$t('message.home.tui34')}}：</div>
          <div class="guize-text2">{{$t('message.home.tui35')}}</div>
          <div class="guize-text3">{{$t('message.home.tui36')}}</div>
          <div class="guize-text4">{{$t('message.home.tui37')}}</div>
        </div>
        <el-divider></el-divider>
        <div class="guize-fenlei">
          <div class="guize-fenlei1">
            <div class="guize-guize-fenlei-text1">{{$t('message.home.tui38')}}</div>
            <div class="guize-guize-fenlei-text2">{{$t('message.home.tui39')}}</div>
            <div class="guize-guize-fenlei-text3">{{$t('message.home.tui40')}}</div>
            <div class="guize-guize-fenlei-text3">{{$t('message.home.tui41')}}</div>
            <div class="guize-guize-fenlei-text4">{{$t('message.home.tui42')}}</div>
          </div>
          <div class="guize-fenlei2">
            <div class="guize-guize-fenlei-text1">{{$t('message.home.tui43')}}</div>
            <div class="guize-guize-fenlei-text2">{{$t('message.home.tui44')}}</div>
            <div class="guize-guize-fenlei-text3">{{$t('message.home.tui45')}}</div>
            <div class="guize-guize-fenlei-text4">{{$t('message.home.tui46')}}</div>
            <div class="guize-guize-fenlei-text5">{{$t('message.home.tui47')}}</div>
          </div>
          <div class="guize-fenlei3">
            <div class="guize-guize-fenlei-text1">{{$t('message.home.tui48')}}</div>
            <div class="guize-guize-fenlei-text2">{{$t('message.home.tui49')}}</div>
            <div class="guize-guize-fenlei-text3">{{$t('message.home.tui50')}}</div>
            <div class="guize-guize-fenlei-text4">{{$t('message.home.tui51')}}</div>
            <div class="guize-guize-fenlei-text5">{{$t('message.home.tui52')}}</div>
          </div>
        </div>
      </el-dialog>
    </div>
    <footer-view></footer-view>
  </div>

</template>
<script>
import promoteAxios from "@/api/promote.js";
import VueQrcode from 'vue-qrcode';
export default {
  data() {
    return {
      promote_data: {},
      localuser_data: {},
      getPromoteData_data: {},
      show_time: "",
      dialogVisible: false,
      dialogVisible2:false,
      dialogVisible3:false,
      dialogVisible4:false,//规则
      level:'1',
      page_no:1,
      loading: false,
      list_data:[],
      wanbi:false,
      language:'zh-CN',
      img: '',
      imgshow: false,
      qrcodeOptions: {
        margin: 2, // 设置边框大小，以模块单位为单位
      },
    }
  },
  components: {
    VueQrcode, // 注册 vue-qrcode 组件
  },
  created() {
    this.init()
  },
  computed: {
    noMore () {
      return this.wanbi;
    },
    disabled () {
      return this.loading || this.noMore
    }
  },
  mounted() {
    if (localStorage.getItem("localLan") == 'en') {
      this.language = 'en'
    }
    if (localStorage.getItem("localLan") == 'cht') {
      this.language = 'CN'
    }
    if (localStorage.getItem("localLan") == 'zh-CN') {
      this.language = 'zh-CN'
    }
  },
  methods: {
    init() {
      this.promote()
      this.localuser()
      this.getPromoteData()
    },
    load () {
      this.loading = true
      this.page_no++
      this.promote()
    },
    tuiguangdakai(){
      this.dialogVisible3=true
      this.level=1
      this.page_no=1
      this.list_data = []
      this.promote()
    },
    tuiguangtabqiehuan(e){
      this.level = e
      this.page_no = 1
      this.list_data = []
      this.promote()
    },
    timetabe(e) {
      this.show_time = e
      this.getPromoteData()
    },
    promote() {
      promoteAxios.getPromote({
        page_no: this.page_no,
        level: this.level
      }).then((res) => {
        console.log(res);
        this.promote_data = res.data.total
        if (res.data.list.length==0){
          this.loading= false
          this.wanbi = true
          return 123;
        }
        for (let d = 0; d<res.data.list.length; d++){
          this.list_data.push(res.data.list[d])
        }
        if (res.data.list.length!=0){
          this.loading=false
          return  123;
        }
      });
    },
    localuser() {
      var host_url = window.location.host
     promoteAxios.getShare().then((res) => {
        this.localuser_data = res.data
        this.localuser_data.url = host_url+'/#/home?usercode='+this.localuser_data.usercode
      });
    },
    copyEvent(e) {
      if (e === 1) {
        navigator.clipboard.writeText(this.localuser_data.usercode)
            .then(() => {
              this.$message.success(this.$t('message.user.chenggong'));
            });
      }
      if (e === 2) {
        navigator.clipboard.writeText(this.localuser_data.url)
            .then(() => {
              this.$message.success(this.$t('message.user.chenggong'));
            });
      }
    },
    getPromoteData() {
      promoteAxios.getPromoteData({
        type: this.show_time
      }).then((res) => {
        this.getPromoteData_data = res.data
      });
    }
  }
}
</script>
<style scoped>
@import url("font.css");

.guize >>> .el-dialog__header {
  display: none !important;
}
.guize >>> .el-dialog__body {
  padding: 0;
}
.wodetuiguang-biaotou{
  margin-top: 20px;
  width: 100%;
  height: 48px;
  border-top: 1px solid #eeeeee;
  display: flex;
}
.wodetuiguang-biaotou-text{
  flex: 1;
  text-align: center;
  line-height: 48px;
}
.tuiguang_nr{
  width: 100%;
  height: 200px;
  overflow: scroll;
}

.list-item{
  display: flex;
}
.list-item-div{
  flex: 1;
  height: 48px;
  line-height: 48px;
  border-top: 1px solid #eeeeee;
  text-align: center;
}
.guize-beijing{
  width: 100%;
  position: relative;
}
.guize-text{
  width: 100%;
  padding: 24px 19px 54px 19px;
  box-sizing: border-box;
}
.guize-text1{
  font-size: 18px;
  padding-bottom: 19px;
  font-weight: bold;
}
.guize-text2{
  font-size: 14px;
  padding-bottom: 12px;
}
.guize-text3{
  font-size: 14px;
  padding-bottom: 12px;
}
.guize-text4{
  font-size: 13px;
  color: #888888;
}
.guize-guize-fenlei-text1{
  font-size: 16px;
  padding-bottom: 8px;
  font-weight: bold;
}
.guize-guize-fenlei-text2{
  padding-bottom: 5px;
}
.guize-guize-fenlei-text3{
  padding-bottom: 5px;
}
.guize-guize-fenlei-text4{
  padding-bottom: 5px;
}
.tuiguang_tab{
  display: flex;
  justify-content: center;
}
.tuiguang_tab div{
  flex: 1;
  text-align: center;
  padding-top: 12px;
  padding-bottom: 12px;
}
.guize-fenlei{
  width: 100%;
  padding: 45px 19px 70px 19px;
  box-sizing: border-box;
  display: flex;
  justify-content: space-between;
}
.guanbi{
  width: 30px;
  position: absolute;
  top: 15px;
  right: 15px;
}

.guize-beijing-img{
  width: 100%;
  display: block;
}

.yaoqinghaoyou >>> .el-dialog__header {
  display: none !important;
}

.yaoqinghaoyou >>> .el-dialog__body {
  padding: 0;
}

.saoma {
  width: 406px;
  height: 157px;
  background: #2a2e34;
  display: table;
}

.xuanchaun {
  width: 406px;
  display: block;
  height: 473px;
  background-image: url(
  ../../assets/newImages/promote/Slice_1@2x.png);
  background-size: 100% 100%;
}
.xuanchaun9{
  width: 100%;
  height: 40px;
}
.xuanchaun9 img{
  width: 25px;
  height: 25px;
  float: right;
  margin-right: 15px;
  margin-top: 15px;
}
.xuanchaun-text{
  color: #000000;
  font-weight: 500;
  font-size: 50px;
  line-height: 50px;
  text-align: center;
}
.xuanchaun-text2{
  color: #000000;
  font-weight: 500;
  font-size: 50px;
  line-height: 50px;
  text-align: center;
}
:deep(.el-dialog){
  padding: 0;
}
</style>
