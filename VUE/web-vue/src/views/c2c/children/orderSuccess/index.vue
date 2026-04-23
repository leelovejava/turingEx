<template>
  <div class="w-full o-buy">
    <otc-order-header :state="detailInfo?.state*1" />
    <div
      class="main"
      v-loading="loadingPop"
      element-loading-spinner="el-icon-loading"
      element-loading-background="#171a1e"
    >
      <!-- 您正在向卖家购买USDT -->
      <div class="main-title">
        <div class="title-center">
          <div class="title-left">
            <p v-if="infoProcess == 0 || infoProcess == 1" class="buying">
              {{ isBuy ? $t("message.c2c.tip52") : $t("message.c2c.tip190")
              }}{{ detailInfo.c2cUserNickName
              }}{{ isBuy ? $t("message.c2c.goumai") : $t("message.c2c.chushou")
              }}{{ detailInfo.symbol&&detailInfo.symbol.toUpperCase() }}
            </p>
            <p v-if="infoProcess == 2" class="buying">
              <span>{{ $t("message.c2c.tip53") }}</span>
            </p>
            <p
              v-if="infoProcess === 3"
              class="buying"
              style="display: flex; align-items: center"
            >
              <span>{{ $t("message.c2c.tip54") }}</span>
              <img
                src="@/assets/images/c2c/orderSuccess/success-green.png"
                style="width: 20px; height: 20px"
                alt=""
              />
            </p>
            <p
              v-if="infoProcess === 4 || infoProcess === 5"
              class="buying"
              style="display: flex; align-items: center"
            >
              <span>{{ $t("message.c2c.tip55") }}</span>
              <img
                src="@/assets/images/c2c/orderSuccess/Group1879.png"
                style="width: 20px; height: 20px"
                alt=""
              />
            </p>
            <div v-if="infoProcess === 0" class="ordering">
              <span v-if="isBuy">{{ $t("message.c2c.tip56") }}</span>
              <span v-else>{{ $t("message.c2c.tip57") }}</span>
              <span class="time" style="margin-left: 22px">
                <span class="bg-time">{{ hour.substring(0, 1) }}</span>
                <span class="bg-time">{{ hour.substring(1, 2) }}</span>
                <span style="margin: 0 2.5px">:</span>
                <span class="bg-time">{{ minute }}</span>
                <span class="bg-time">{{ secound }}</span>
              </span>
            </div>
            <div v-if="infoProcess === 1 && isBuy" class="ordering">
              <span>{{ $t("message.c2c.tip58") }}</span>
            </div>
            <div v-if="infoProcess === 1 && !isBuy" class="ordering">
              <span>{{ $t("message.c2c.tip59") }}</span>
            </div>
            <div v-if="infoProcess === 3" class="ordering">
              <span
                >{{ $t("message.c2c.tip60")
                }}{{
                  isBuy ? $t("message.c2c.goumai") : $t("message.c2c.chushou")
                }}
                <p class="price">
                  {{
                    `${
                      detailInfo.symbol == "usdt"
                        ? Math.floor(detailInfo.coinAmount * 100) / 100
                        : Math.floor(detailInfo.coinAmount * 1000000) / 1000000
                    } ${detailInfo.symbol && detailInfo.symbol.toUpperCase()}`
                  }}
                </p>
              </span>
            </div>
            <div
              v-if="(infoProcess === 4 || infoProcess === 5) && isBuy"
              class="ordering"
            >
              <span v-if="infoProcess === 4">{{
                $t("message.c2c.tip61")
              }}</span>
              <span v-else>{{ $t("message.c2c.tip62") }}</span>
            </div>
            <div v-if="infoProcess === 5 && !isBuy" class="ordering">
              <span>{{ $t("message.c2c.tip63") }}</span>
            </div>
          </div>
          <div class="title-right">
            <div class="order-c" style="margin-bottom: 15px">
              <span style="color: #727a89">{{
                $t("message.c2c.dingdanhao")
              }}</span>
              <span style="margin: 0 10px">{{ detailInfo.orderNo }}</span>
              <img
                @click="handleCopy(detailInfo.orderNo)"
                src="@/assets/images/c2c/orderSuccess/Group1884.png"
                style="width: 15px; height: 15px"
                alt=""
              />
            </div>
            <div class="order-c">
              <span style="color: #727a89">{{
                $t("message.c2c.chuangjianshijian")
              }}</span>
              <span style="margin: 0 10px">{{
                formatTime(detailInfo.createTime)
              }}</span>
            </div>
          </div>
        </div>
      </div>
      <!-- 买币流程 -->
      <div class="main-center">
        <div class="main-left">
          <!-- 步骤条 -->
          <div
            v-if="infoProcess === 0 || infoProcess === 1 || infoProcess === 3"
            class="main-top"
          >
            <el-steps :active="activeIndex">
              <el-step
                :title="
                  isBuy ? $t('message.c2c.tip64') : $t('message.c2c.tip65')
                "
              ></el-step>
              <el-step
                :title="
                  isBuy ? $t('message.c2c.tip66') : $t('message.c2c.tip67')
                "
              ></el-step>
              <el-step :title="$t('message.c2c.jiaoyiwancheng')"></el-step>
            </el-steps>
            <p v-if="infoProcess === 1 && isBuy" class="confirm">
              {{ $t("message.c2c.tip68") }}
            </p>
          </div>
          <!-- 具体步骤 -->
          <div class="main-main">
            <img
              v-if="infoProcess === 0 && isBuy"
              src="@/assets/images/c2c/orderSuccess/Group1896.png"
              style="width: 12px; height: 560px; margin-right: 18px"
              alt=""
            />
            <div class="main-info">
              <p v-if="infoProcess === 0 && isBuy" class="info-o">
                {{ $t("message.c2c.tip69") }}
              </p>
              <p v-if="(infoProcess !== 0 && isBuy) || !isBuy" class="info-o">
                {{ $t("message.c2c.dingdanxinxi") }}
              </p>
              <div class="total">
                <div class="t-box">
                  <p
                    style="color: #727a89; font-size: 15px; margin-bottom: 10px"
                  >
                    {{ $t("message.c2c.zongjia") }}
                  </p>
                  <p
                    class="price"
                    :style="{ color: isBuy ? '#4ea372' : '#BD3F4D' }"
                  >
                    {{ fiatCurrency.currencySymbol }}{{ detailInfo.amount }}
                  </p>
                </div>
                <div class="t-box">
                  <p
                    style="color: #727a89; font-size: 15px; margin-bottom: 10px"
                  >
                    {{ $t("message.hangqing.jiage") }}
                  </p>
                  <p class="price">
                    {{ fiatCurrency.currencySymbol }}
                    {{ detailInfo.symbolValue }}
                  </p>
                </div>
                <div class="t-box">
                  <p
                    style="color: #727a89; font-size: 15px; margin-bottom: 10px"
                  >
                    {{ $t("message.c2c.shuliang") }}
                  </p>
                  <p class="price" v-if="detailInfo.coinAmount">
                    {{
                      `${
                        detailInfo.symbol == "usdt"
                          ? Math.floor(detailInfo.coinAmount * 100) / 100
                          : Math.floor(detailInfo.coinAmount * 1000000) /
                            1000000
                      } ${detailInfo.symbol && detailInfo.symbol.toUpperCase()}`
                    }}
                  </p>
                </div>
              </div>
              <div class="info-buy">
                <div class="buy-t">
                  <div
                    v-if="infoProcess === 0 && isBuy"
                    style="display: flex; align-items: center"
                  >
                    <span style="font-weight: bold">{{
                      $t("message.c2c.tip70")
                    }}</span>
                    <img
                      src="@/assets/images/c2c/orderSuccess/Group1879.png"
                      style="width: 14px; height: 14px"
                      alt=""
                    />
                  </div>
                </div>
                <div class="allType">
                  <div
                    v-if="infoProcess === 0 || infoProcess === 1"
                    class="flex align-center"
                  >
                    <span style="font-weight: bold; margin-right: 12px">{{
                      $t("message.c2c.zhifufangshi")
                    }}</span>
                    <img
                      src="@/assets/images/c2c/orderSuccess/Group1879.png"
                      width="14"
                    />
                  </div>
                  <div
                    v-if="infoProcess === 0 || infoProcess === 1"
                    class="flex align-center"
                  >
                    <span>{{ $t("message.user.chakanquanbu") }}</span>
                    <img
                      src="@/assets/images/c2c/orderSuccess/Group1900.png"
                      width="16"
                    />
                  </div>
                </div>

                <p
                  v-if="infoProcess === 0 && isBuy"
                  class="flex align-center mt-4"
                  style="font-size: 14px; color: #1a6ebd"
                >
                  <img
                    src="@/assets/images/c2c/orderSuccess/Group1878.png"
                    class="mr-4"
                    width="14"
                  />
                  <span>{{ TITLE }} {{ $t("message.c2c.tip71") }}</span>
                </p>
                <div
                  v-if="infoProcess === 0 || infoProcess === 1"
                  class="card"
                  style="margin-top: 13px"
                >
                  <div class="card-left">
                    <div class="card-b">
                      <span class="shu"></span>
                      <span>{{ detailInfo.methodName }}</span>
                    </div>
                  </div>
                  <!-- 支付方式详细信息 -->
                  <div class="card-right">
                    <!-- 姓名 -->
                    <div class="mr18" v-if="detailInfo.realName">
                      <p style="color: #727a89">
                        {{ $t("message.c2c.xingming") }}
                      </p>
                      <p class="bankTitle">
                        <span>{{ detailInfo.realName }}</span>
                        <img
                          @click="handleCopy(detailInfo.realName)"
                          src="@/assets/images/c2c/orderSuccess/Group1884.png"
                          class="ml-4"
                          width="14"
                        />
                      </p>
                    </div>

                    <div class="mr18" v-if="detailInfo.paramValue1">
                      <p style="color: #727a89">{{ detailInfo.paramName1 }}</p>
                      <p class="bankTitle">
                        <span>{{ detailInfo.paramValue1 }}</span>
                        <img
                          @click="handleCopy(detailInfo.paramValue1)"
                          src="@/assets/images/c2c/orderSuccess/Group1884.png"
                          class="ml-4"
                          width="14"
                        />
                      </p>
                    </div>
                    <div class="mr18" v-if="detailInfo.paramValue2">
                      <p style="color: #727a89">{{ detailInfo.paramName2 }}</p>
                      <p class="bankTitle">
                        <span>{{ detailInfo.paramValue2 }}</span>
                        <img
                          @click="handleCopy(detailInfo.paramValue2)"
                          src="@/assets/images/c2c/orderSuccess/Group1884.png"
                          class="ml-4"
                          width="14"
                        />
                      </p>
                    </div>
                    <div class="mr18" v-if="detailInfo.paramValue3">
                      <p style="color: #727a89">{{ detailInfo.paramName3 }}</p>
                      <p class="bankTitle">
                        <span>{{ detailInfo.paramValue3 }}</span>
                        <img
                          @click="handleCopy(detailInfo.paramValue)"
                          src="@/assets/images/c2c/orderSuccess/Group1884.png"
                          class="ml-4"
                          width="14"
                        />
                      </p>
                    </div>
                  </div>
                </div>
                <div
                  v-if="
                    infoProcess === 3 || infoProcess === 4 || infoProcess === 5
                  "
                  class="card-3"
                  style="margin-top: 13px"
                >
                  <div
                    v-if="
                      ((infoProcess === 5 || infoProcess === 3) && !isBuy) ||
                      ((infoProcess === 4 || infoProcess === 3) && isBuy)
                    "
                    class="card-b"
                  >
                    <span class="shu"></span>
                    <span>{{ detailInfo.methodName }}</span>
                  </div>
                  <span v-if="infoProcess === 5 && isBuy">{{
                    $t("message.c2c.tip72")
                  }}</span>
                </div>
                <p v-if="infoProcess === 0 && isBuy" style="font-weight: bold">
                  {{ $t("message.c2c.tip73") }}
                </p>
                <p
                  v-if="infoProcess === 2 && !isBuy"
                  class="flex align-center mt-12"
                >
                  <span style="font-weight: bold; margin-right: 12px">{{
                    $t("message.c2c.tip73")
                  }}</span>
                  <img
                    src="@/assets/images/c2c/orderSuccess/Group1879.png"
                    width="14"
                  />
                </p>
              </div>
            </div>
          </div>
          <!-- 通知对方 -->
          <div class="main-confirm">
            <span
              v-if="infoProcess === 0 && isBuy"
              class="confirm-w"
              @click="confirmPay"
              >{{ $t("message.c2c.tip74") }}</span
            >
            <span
              v-if="infoProcess === 1 && !isBuy"
              class="confirm-w"
              style="width: 140px; height: 42px"
              @click="confirmSellDialog = true"
              >{{ $t("message.c2c.tip75") }}</span
            >
            <span
              v-if="infoProcess === 1"
              class="confirm-q"
              style="margin-right: 20px"
              >{{ $t("message.c2c.tip76") }}{{ doubthour.substring(0, 1)
              }}{{ doubthour.substring(1, 2) }}:{{ doubtminute }}:{{
                doubtsecound
              }}）</span
            >
            <span
              v-if="infoProcess === 3 || isShenshu"
              class="confirm-q"
              style="margin-right: 20px; cursor: pointer"
              @click="doubtDialog = true"
              >{{ $t("message.c2c.tip77") }}</span
            >
            <span
              v-if="infoProcess === 3"
              style="cursor: pointer"
              @click="openUrl('/wallet/walletOverview')"
              class="confirm-q"
              >{{ $t("message.c2c.chankanzhican") }}</span
            >
            <span v-if="infoProcess === 5 && !isBuy" class="confirm-q">{{
              $t("message.c2c.kefuzhichi")
            }}</span>
          </div>
          <!-- faq -->
          <div class="main-faq">
            <p v-if="(!isBuy && infoProcess !== 5) || isBuy" class="faq-title">
              FAQ
            </p>

            <div>
              <div
                class="faq-box"
                v-for="(item, index) in fqaList"
                :key="index"
                @click="openFqa(item)"
              >
                <div class="faq-col">
                  <img
                    src="@/assets/images/c2c/orderSuccess/Group1904.png"
                    alt=""
                  />
                  <span>{{ item.title }}</span>
                </div>
                <template v-if="item.isShow">
                  <div
                    class="faq-text"
                    v-for="(item1, index1) in item.list"
                    :key="index1"
                  >
                    {{ item1 }}
                  </div>
                </template>
              </div>
            </div>
          </div>
        </div>
        <!--右边聊天窗 -->
        <div class="main-right">
          <div class="huihua">
            <!-- 头部 -->
            <div class="hui-top">
              <div class="hui-avag">
                <img
                  :src="c2c_user.headImg"
                  style="width: 35px; height: 35px; margin-right: 15px"
                  alt=""
                />
                <div>
                  <p class="font-bold mb-2">{{ c2c_user.nickName }}</p>
                  <div class="flex items-center">
                    <span>{{ $t("message.c2c.renzhengguangaofang") }}</span>
                    <img
                      src="@/assets/images/c2c/orderSuccess/Group1160.png"
                      style="width: 12px; height: 12px; margin: 0 20px 0 4px"
                      alt=""
                    />
                    <span
                      class="cursor-pointer"
                      style="color: #1b82e4"
                      @click="doubtDialog = true"
                      >{{ $t("message.c2c.jubao") }}</span
                    >
                  </div>
                </div>
              </div>

              <div class="hui-dan">
                <div style="margin: 0 100px 0 50px">
                  <p class="mb-2">
                    {{ $t("message.c2c.tip98") }}
                  </p>
                  <p>{{ c2c_user.thirtyDaysOrder }}</p>
                </div>
                <div>
                  <p class="mb-2">
                    {{ $t("message.c2c.tip99") }}
                  </p>
                  <p>{{ c2c_user.thirtyDaysOrderRatio }} %</p>
                </div>
              </div>
            </div>
            <!-- 聊天内容 -->
            <div class="hui-center" ref="scroll">
              <div class="hui-xia" v-if="detailInfo.direction">
                {{ detailInfo.direction == "buy" ? fixStrBuy() : fixStrSell() }}
              </div>
              <div class="hui-dui">
                <div
                  class="hui-li"
                  v-for="(item, index) in msgList"
                  :key="index"
                >
                  <div class="list-tiem">{{ item.createtime }}</div>
                  <p
                    class="hui-n"
                    :class="[
                      item.send_receive === 'receive' ? 'bg-ta' : '',
                      item.send_receive === 'receive'
                        ? 'left-is-ta'
                        : 'right-is-ta',
                    ]"
                    v-if="item.type === 'text'"
                  >
                    {{ item.content }}
                  </p>
                  <img
                    :class="[
                      item.send_receive === 'receive' ? 'bg-ta' : '',
                      item.send_receive === 'receive'
                        ? 'left-is-ta'
                        : 'right-is-ta',
                    ]"
                    class="content-img"
                    v-else
                    :src="item.content"
                  />
                </div>
              </div>
            </div>
            <!-- 发送聊天信息 -->
            <div class="hui-bottom">
              <img
                src="@/assets/images/c2c/orderSuccess/Group1862.png"
                alt=""
                @click="handleSendImg"
              />
              <!-- 图片 -->
              <input
                ref="upload"
                type="file"
                hidden
                @change="onImageFileChanged"
                accept="image/png, image/gif, image/jpeg, image/bmp, image/x-icon"
              />
              <el-input
                v-model="message"
                :placeholder="$t('message.c2c.tip152')"
              />
              <img
                @click="handleSend('text', message)"
                src="@/assets/images/c2c/orderSuccess/Group1859.png"
                alt=""
              />
            </div>
          </div>
        </div>
      </div>
    </div>
    <footer-view class="w-full" style="background: #171a1e" />
    <!-- sell 确认放行 -->
    <el-dialog
      :title="buyProcess === 1 ? $t('message.c2c.tip153') : ''"
      v-model="confirmSellDialog"
      width="470px"
    >
      <div class="dia">
        <div v-if="buyProcess === 1">
          <div style="text-align: center; margin: 0 80px 66px">
            <img
              src="@/assets/images/c2c/orderSuccess/Group1986.png"
              style="width: 50px; height: 50px; margin-top: 15px"
              alt=""
            />
            <p style="font-weight: bold; font-size: 20px; margin: 15px 0 21px">
              {{ $t("message.c2c.querenfangxing") }}
            </p>
            <p style="font-size: 14px">
              {{ $t("message.c2c.tip187") }}
            </p>
          </div>
          <div class="radio-main" style="margin-bottom: 5px">
            <el-radio-group v-model="sellRadio">
              <el-radio :label="1">{{ $t("message.c2c.tip188") }}</el-radio>
            </el-radio-group>
          </div>
        </div>
        <div v-else class="dia-main">
          <p class="dia-main-title">
            <img
              src="@/assets/images/c2c/orderSuccess/Group1878.png"
              style="width: 14px; height: 14px; margin-right: 9px"
              alt=""
            />
            <span>{{ $t("message.c2c.wentixishi") }} </span>
          </p>
          <p class="ml23">{{ $t("message.c2c.tip189") }}</p>
          <p class="dia-pass">{{ $t("message.user.zijinmima") }}</p>
          <div>
            <el-input
              :placeholder="$t('message.c2c.tip106')"
              v-model="fundPassword"
              clearable
              show-password
            />
          </div>
        </div>
        <div v-if="buyProcess === 1" class="dia-btn">
          <div class="cencel" style="width: 195px">
            {{ $t("message.jiaoyi.quxiao") }}
          </div>
          <el-button
            class="confirm"
            :class="!sellRadio ? 'is-co' : ''"
            style="width: 195px"
            :disabled="!sellRadio"
            @click="buyProcess = 2"
            >{{ $t("message.user.queren") }}</el-button
          >
        </div>
        <div v-else class="confirmBuy" @click="nextBuy">
          {{ $t("message.c2c.tip75") }}
        </div>
      </div>
    </el-dialog>
    <!-- 取消订单 -->
    <el-dialog
      :title="$t('message.c2c.quxiaodingdan')"
      v-model="cancelOrderDialog"
      width="540px"
    >
      <div class="dia">
        <div class="dia-main">
          <p class="dia-main-title">
            <img
              src="@/assets/images/c2c/orderSuccess/Group1878.png"
              style="width: 14px; height: 14px; margin-right: 9px"
              alt=""
            />
            <span>{{ $t("message.c2c.wentixishi") }}</span>
          </p>
          <p class="ml23" style="margin-bottom: 12px">
            {{ $t("message.c2c.tip100") }}
          </p>
          <p class="ml23" style="margin-bottom: 12px">
            {{ $t("message.c2c.tip101") }}
          </p>
          <p class="ml23" style="margin-bottom: 12px">
            {{ $t("message.c2c.tip102") }}
          </p>
        </div>
        <p style="font-weight: bold; margin-bottom: 13px">
          {{ $t("message.c2c.tip103") }}
        </p>
        <div class="radio-main" style="margin-bottom: 67px">
          <el-radio-group v-model="radio">
            <el-radio
              v-for="(item, index) in cancelTextList"
              :key="index"
              :label="item.label"
              >{{ item.text }}</el-radio
            >
          </el-radio-group>
        </div>
        <div class="dia-btn">
          <div
            class="cencel"
            style="cursor: pointer"
            @click="cancelOrderDialog = false"
          >
            {{ $t("message.c2c.quxiao") }}
          </div>
          <el-button
            class="confirm"
            :class="!radio ? 'is-co' : ''"
            :disabled="!radio"
            @click="cancelOrder"
            >{{ $t("message.c2c.queren") }}</el-button
          >
        </div>
      </div>
    </el-dialog>
    <!--buy 确认付款 -->
    <el-dialog
      :title="$t('message.c2c.querenfukuan')"
      v-model="confirmBuyDialog"
      width="540px"
    >
      <div class="dia">
        <p v-if="buyProcess === 1" class="dia-t">
          {{ $t("message.c2c.tip104") }}
        </p>
        <div v-if="buyProcess === 1" class="dia-main">
          <div class="shu-t">
            <span class="shu"></span>
            <span>{{ detailInfo.methodName }}</span>
          </div>
          <div class="dia-card">
            <span style="color: #484d56">{{ detailInfo.paramName1 }}</span>
            <span>{{ detailInfo.paramValue1 }}</span>
          </div>
          <div class="dia-card">
            <span style="color: #484d56">{{ detailInfo.paramName2 }}</span>
            <span>{{ detailInfo.paramValue2 }}</span>
          </div>
          <div class="dia-card">
            <span style="color: #484d56">{{
              $t("message.c2c.zhenshixingming")
            }}</span>
            <span>{{ detailInfo.realName }}</span>
          </div>
        </div>
        <!-- 输入资金密码 -->
        <div v-else class="dia-main">
          <p class="dia-main-title">
            <img
              src="@/assets/images/c2c/orderSuccess/Group1878.png"
              style="width: 14px; height: 14px; margin-right: 9px"
              alt=""
            />
            <span>{{ $t("message.home.wenxintishi") }}</span>
          </p>
          <p class="ml23">
            {{ $t("message.c2c.tip105") }}
          </p>
          <p class="dia-pass">{{ $t("message.c2c.zijinmima") }}</p>
          <div>
            <el-input
              :placeholder="$t('message.c2c.tip106')"
              v-model="safe_password"
              clearable
              show-password
            />
          </div>
        </div>
        <div v-if="buyProcess === 1" class="dia-ti">
          <img
            src="@/assets/images/c2c/orderSuccess/Group1878.png"
            style="width: 16px; height: 16px; margin-right: 10px"
            alt=""
          />
          <span
            ><span style="font-weight: bold">{{
              $t("message.c2c.tishi")
            }}</span>
            {{ $t("message.c2c.tip107") }}{{ TITLE
            }}{{ $t("message.c2c.tip108") }}</span
          >
        </div>
        <div v-if="buyProcess === 1" class="dia-q">
          <el-checkbox v-model="checked" size="mini"
            >{{ $t("message.c2c.tip109") }}{{ TITLE
            }}{{ $t("message.c2c.tip110") }}</el-checkbox
          >
        </div>
        <div v-if="buyProcess === 1" class="dia-btn">
          <div class="cencel" @click="confirmBuyDialog = false">
            {{ $t("message.c2c.quxiao") }}
          </div>
          <el-button
            class="confirm"
            :class="!checked ? 'is-co' : ''"
            :disabled="!checked"
            @click="buyProcess = 2"
            >{{ $t("message.c2c.queren") }}</el-button
          >
        </div>
        <!--  确认付款按钮 -->
        <div v-else class="confirmBuy" @click="pay_order">
          {{ $t("message.c2c.tip111") }}
        </div>
      </div>
    </el-dialog>
    <!-- 订单疑问 -->
    <el-dialog
      :title="$t('message.c2c.tip77')"
      v-model="doubtDialog"
      width="540px"
    >
      <div class="dia">
        <div class="top-center">
          <div class="top-center-title">
            <i class="el-icon-warning warning-icon"></i
            >{{ $t("message.c2c.tip112") }}
          </div>
          <div class="top-center-text">{{ $t("message.c2c.tip113") }}</div>
          <div class="top-center-onlie" @click="doubtDialog = false">
            {{ $t("message.c2c.zaixianliaotian") }}
          </div>
        </div>
        <div class="dia-title">{{ $t("message.c2c.shenshu") }}</div>
        <div class="dia-text">{{ $t("message.c2c.tip114") }}</div>
        <div class="radio-main">
          <el-radio-group v-model="radio1" @change="openTips">
            <el-radio
              v-for="(item, index) in doubtList"
              :key="index"
              :label="item.label"
              >{{ item.label }}</el-radio
            >
          </el-radio-group>
        </div>
      </div>
    </el-dialog>
    <!-- 提示 -->
    <el-dialog v-model="tipDialog" width="470px">
      <img class="tip-img" src="@/assets/images/c2c/orderSuccess/tip.png" />
      <div class="dig-title">{{ $t("message.c2c.tishi") }}</div>
      <div class="dig-text">{{ $t("message.c2c.tip115") }}</div>
      <div class="dig-text">{{ $t("message.c2c.tip116") }}</div>
      <div class="dig-text">{{ $t("message.c2c.tip117") }}</div>
      <template #footer>
        <span class="dialog-footer">
          <el-button class="but fonnter-but" type="primary" @click="doubtFun">{{
            $t("message.user.queding")
          }}</el-button>
        </span>
      </template>
    </el-dialog>
    <!-- 申诉提交 -->
    <el-dialog
      :title="$t('message.c2c.tijiaoshenshu')"
      v-model="submitDoubtDialog"
      width="540px"
    >
      <div class="shendu-tip">
        <div class="shendu-tip-item">
          {{ $t("message.c2c.tip118") }}
        </div>
        <div class="shendu-tip-item">
          {{ $t("message.c2c.tip119") }}
        </div>
      </div>
      <div class="shendu-from">
        <div class="shendu-from-title">{{ $t("message.c2c.tip120") }}</div>
        <el-select
          style="width: 100%"
          class="mb-20"
          v-model="radio1"
          disabled
          :placeholder="$t('message.c2c.qingxuanze')"
        >
          <el-option
            v-for="item in doubtList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
        <div class="shendu-from-title">
          {{ $t("message.c2c.shenshumiaosu") }}
        </div>
        <el-input
          class="mb-20"
          rows="4"
          type="textarea"
          :placeholder="$t('message.c2c.tip121')"
          v-model="description"
          maxlength="500"
          show-word-limit
        >
        </el-input>
        <div class="shendu-from-title">
          {{ $t("message.c2c.tip122") }}{{ $t("message.c2c.shenshumiaosu") }}
        </div>
        <div class="shendu-from-text">{{ $t("message.c2c.tip123") }}</div>
      </div>
      <div class="" style="margin-top: 40px">
        <el-upload
          :headers="uploadHeader"
          class="avatar-uploader mb-20"
          :action="`${nowUrl}/api/api/uploadFile`"
          accept=".jpg,.jpeg,.png,.gif.JPG,.JPEG,.PNG,.GIF"
          :show-file-list="false"
          :on-success="handelDoucumentsFront"
          :on-error="onErrorUpload"
          :before-upload="beforeAvatarUpload"
        >
          <img v-if="oneImg" :src="oneImg" class="avatar" />
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
      </div>
      <div class="shendu-from-title">{{ $t("message.c2c.lianxiren") }}</div>
      <el-input
        v-model="name"
        :placeholder="$t('message.c2c.tip124')"
      ></el-input>
      <div class="shendu-from-title">{{ $t("message.c2c.tip125") }}</div>
      <el-input
        v-model="phone"
        :placeholder="$t('message.c2c.tip126')"
      ></el-input>
      <template #footer>
        <span class="dialog-footer1">
          <el-button class="but" @click="submitDoubtDialog = false">{{
            $t("message.user.quxiao")
          }}</el-button>
          <el-button class="but" type="primary" @click="c2cAppealFun()">{{
            $t("message.user.queding")
          }}</el-button>
        </span>
      </template>
    </el-dialog>
    <!--  -->
    <el-dialog class="my_security" v-model="sumbitSuccess" width="520px" center>
      <div class="text-center">
        <img
          src="@/assets/images/c2c/orderSuccess/success-green.png"
          width="85px"
          height="85px"
        />
        <p style="font-size: 20px; margin-top: 20px; color: #000" class="mb-20">
          <b>{{ $t("message.user.tijiaochenggong") }}</b>
        </p>
        <p class="mb-20" style="color: #000">
          {{ $t("message.c2c.tip127") }}
        </p>
        <p class="mb-20" style="color: #000">
          {{ $t("message.c2c.tip128") }}
        </p>
      </div>
      <div class="bind_btn">
        <el-button class="but" type="primary" @click="sumbitSuccess = false">{{
          $t("message.user.queren")
        }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
/* eslint-disable */
import { mapState } from "pinia";
import dayjs from "dayjs";
import FooterView from "@/components/layout/footerView.vue";
import Axios from "@/api/c2c.js";
import { _uploadImage } from "@/api/chat.js";
import { nowUrl } from "@/utils";
import OtcOrderHeader from "@/views/c2c/components/otc-order-header/index.vue";
import { useUserStore } from "@/store/user";
import { signatureGenerate } from "@/utils/signatureUtil";

export default {
  name: "orderSuccess",
  components: {
    FooterView,
    OtcOrderHeader,
  },
  data() {
    return {
      nowUrl,
      isBuy: true, //true表示buy，false表示sell
      sellRadio: null,
      radio: null,
      infoProcess: 0, // 0未付款/1已付款/2申诉中/3已完成/4已取消/5已超时  从接口获取
      buyProcess: 1, //1 2
      checked: true,
      confirmBuyDialog: false,
      confirmSellDialog: false,
      cancelOrderDialog: false,
      detailInfo: {},
      fiatCurrency: {},
      interval: null,
      minute: "",
      hour: "",
      secound: "",
      time: 0,
      safe_password: "",
      activeIndex: 0,
      cancelTextList: [
        { label: 1, text: this.$t("message.c2c.tip129") },
        { label: 2, text: this.$t("message.c2c.tip130") },
        { label: 3, text: this.$t("message.c2c.tip131") },
        { label: 4, text: this.$t("message.c2c.tip132") },
        { label: 5, text: this.$t("message.c2c.qita") },
      ],
      c2c_user: {},
      msgList: [],
      message: "",
      loading: false,
      lastMsgId: "",
      doubtDialog: false,
      radio1: null,
      doubtList: [
        { value: 1, label: this.$t("message.c2c.tip134") },
        { value: 2, label: this.$t("message.c2c.tip135") },
        { value: 3, label: this.$t("message.c2c.tip136") },
      ],
      tipDialog: false,
      submitDoubtDialog: false,
      description: "",
      oneImg: "",
      certificateImg: "",
      isShenshu: false,
      name: "",
      phone: "",
      sumbitSuccess: false,
      timer: null,
      isTime: true,
      time2: 600,
      doubtminute: "",
      doubthour: "",
      doubtsecound: "",
      loadingPop: false,
      fqaList: [
        {
          title: this.$t("message.c2c.tip158"),
          list: [this.$t("message.c2c.tip159"), this.$t("message.c2c.tip160")],
          isShow: false,
        },
        {
          title: this.$t("message.c2c.tip161"),
          list: [this.$t("message.c2c.tip162")],
          isShow: false,
        },
        {
          title: this.$t("message.c2c.tip163"),
          list: [this.$t("message.c2c.tip183"), this.$t("message.c2c.tip184")],
          isShow: false,
        },
        {
          title: this.$t("message.c2c.tip185"),
          list: [
            this.$t("message.c2c.tip164"),
            this.$t("message.c2c.tip165"),
            this.$t("message.c2c.tip166"),
            this.$t("message.c2c.tip167"),
            this.$t("message.c2c.tip168"),
            this.$t("message.c2c.tip169"),
            this.$t("message.c2c.tip170"),
          ],
          isShow: false,
        },
        {
          title: this.$t("message.c2c.tip171"),
          list: [this.$t("message.c2c.tip172"), this.$t("message.c2c.tip173")],
          isShow: false,
        },
        {
          title: this.$t("message.c2c.tip174"),
          list: [
            this.$t("message.c2c.tip175"),
            this.$t("message.c2c.tip176"),
            "0",
            this.$t("message.c2c.tip177"),
            this.$t("message.c2c.tip178"),
            this.$t("message.c2c.tip179"),
          ],
          isShow: false,
        },
        {
          title: this.$t("message.c2c.tip180"),
          list: [this.$t("message.c2c.tip181"), this.$t("message.c2c.tip182")],
          isShow: false,
        },
      ],
      fundPassword: "", //资金密码
      uploadHeader: {
        tissuePaper: "",
        sign: "",
        systemRandom: "",
      },
      timeOutTimer1:null,
      TITLE:''
    };
  },

  watch: {
    "$route.query.id"() {
      this.loadingPop = true;
      this.initParams();
    },
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
  created() {
    if (this.existToken) {
      this.getC2cOrderDetail();
      this.timer = setInterval(() => {
        this.getC2cOrderDetail();
      }, 2000);
    } else {
      this.$router.push("/");
    }
    this.initParams();
  },
  unmounted() {
    clearInterval(this.timer);
    clearInterval(this.interval);

    if(this.timeOutTimer1){
      clearTimeout(this.timeOutTimer1)
      this.timeOutTimer1 = null
    }
  },
  methods: {
    formatTime(time) {
      return dayjs(time).format("YY-MM-DD HH:mm:ss");
    },

    initParams() {
      this.isBuy = this.$route.query.isBuy == 1;
      //TODO 什么情况下
      if (this.$route.query.fiatCurrency) {
        this.fiatCurrency = JSON.parse(this.$route.query.fiatCurrency);
      }
    },
    // 通知对方付款
    confirmPay() {
      this.safe_password = "";
      this.confirmBuyDialog = true;
    },
    //复制
    handleCopy(context) {
      navigator.clipboard.writeText(context).then(() => {
        this.$message.success(this.$t("message.user.fuzhichenggong"));
      });
    },
    // 时间显示
    showTime() {
      if (this.msgList.length > 0) {
        return this.msgList[0].createtime;
      }
      return false;
    },
    //上传付款凭证
    handelDoucumentsFront(res, file) {
      const { path, httpUrl } = res.data;
      this.oneImg = httpUrl;
      this.certificateImg = path;
    },

    onErrorUpload() {
      this.$message.warning(this.$t("message.user.shangchuan3"));
    },
    beforeAvatarUpload(file) {
      const { timestamp, signature, systemRandom } = signatureGenerate();
      this.uploadHeader.tissuePaper = timestamp;
      this.uploadHeader.sign = signature;
      this.uploadHeader.systemRandom = systemRandom;
      let types = [
        "image/jpeg",
        "image/jpg",
        "image/gif",
        "image/bmp",
        "image/png",
      ];
      const isImage = types.includes(file.type);
      const isJPG = file.type === "image/jpeg";
      const isLt5M = file.size / 1024 / 1024 < 5;

      if (!isImage) {
        this.$message.error(this.$t("message.user.shangchuan1"));
      }
      if (!isLt5M) {
        this.$message.error(this.$t("message.user.shangchuan2"));
      }
      return isImage && isLt5M;
    },
    fixStrSell() {
      const state = parseInt(this.detailInfo?.state) || 0;
      const stateToStrArr = [
        "tip65",
        "qingfangxing",
        "shenshuzhong",
        "yiwancheng",
        "yiquxiao",
        "yichaoshi",
      ];
      const str = stateToStrArr[state]
        ? this.$t(`message.c2c.${stateToStrArr[state]}`)
        : "";
      return str;
    },
    fixStrBuy() {
      const state = parseInt(this.detailInfo?.state) || 0;
      const stateToStrArr = [
        "daifukuan",
        "tip137",
        "shenshuzhong",
        "yiwancheng",
        "yiquxiao",
        "yichaoshi",
      ];
      const str = stateToStrArr[state]
        ? this.$t(`message.c2c.${stateToStrArr[state]}`)
        : "";
      return str;
    },
    cancelOrder() {
      let obj = {
        order_no: this.detailInfo.orderNo,
        remark: this.cancelTextList[this.radio - 1].text,
      };
      Axios.order_cancel(obj).then((res) => {
        if (res.code == "0") {
          this.cancelOrderDialog = false;
          this.$message.success(this.$t("message.c2c.tip138"));
        }
      });
    },
    nextBuy() {
      if (!this.fundPassword) {
        this.$message.error(this.$t("message.c2c.tip106"));
        return;
      }
      let obj = {
        order_no: this.detailInfo.orderNo,
        safe_password: this.fundPassword,
      };
      Axios.c2cOrderOrderPass(obj).then((res) => {
        if (res.code == "0") {
          this.$message.success(this.$t("message.home.caozuochenggong"));
          this.confirmSellDialog = false;
          this.fundPassword = "";
        }
      });
    },
    //申诉提交
    c2cAppealFun() {
      if (!this.certificateImg) {
        this.$message.error(this.$t("message.c2c.tip139"));
        return;
      }
      if (!this.phone) {
        this.$message.error(this.$t("message.c2c.tip140"));
        return;
      }
      let obj = {
        order_no: this.detailInfo.orderNo,
        reason: this.radio1, //理由
        description: this.description, //描述
        name: this.name, //名字
        phone: this.phone, //电话号
        img: this.certificateImg, // 图片
      };
      Axios.c2cAppealApply(obj).then((res) => {
        if (res.code == "0") {
          this.submitDoubtDialog = false;
          this.sumbitSuccess = true;
        }
      });
    },
    fetchChatList(message_id = "") {
      let obj = {
        messageId: message_id,
        orderNo: this.detailInfo.orderNo,
      };
      // 获取消息列表
      Axios.getOtcOnlinechat(obj).then((res) => {
        let data = res.data;
        this.scrollToBottom();
        if (!this.lastMsgId) {
          this.lastMsgId = data.length && data[data.length - 1]["id"];
        }
        if (data.length) {
          if (message_id) {
            // 加载更多
            this.lastMsgId = data[data.length - 1]["id"];
            this.msgList = [...data.reverse(), ...this.msgList];
          } else {
            this.msgList = [...this.msgList, ...data.reverse()];
            let hash = {};
            this.msgList = this.msgList.reduce(function (preVal, curVal) {
              hash[curVal.id]
                ? " "
                : (hash[curVal.id] = true && preVal.push(curVal));
              return preVal;
            }, []);
          }
          if (data.length < 10) {
            this.finished = true;
          }
        }
      });
    },

    scrollToBottom() {
      const doms = document.getElementsByClassName("hui-center")[0];
      this.timeOutTimer1 = setTimeout(() => {
        this.$nextTick(() => {
          doms.scrollTop = doms.scrollHeight;
        });
      }, 1000);
    },
    // 选中图片
    onImageFileChanged(e) {
      const file = e.target.files[0];
      this.afterRead(file);
    },
    // 文件上传
    afterRead(file) {
      this.loading = true;
      _uploadImage(file)
        .then((data) => {
          this.loading = false;
          this.handleSend("img", data.httpUrl);
        })
        .catch(() => {
          this.loading = false;
        });
    },

    handleSendImg() {
      this.$refs["upload"].click();
    },
    openFqa(item) {
      item.isShow = !item.isShow;
    },
    //获取订单详情
    getC2cOrderDetail() {
      Axios.c2cOrderGetDetail({
        order_no: this.$route.query.id,
      }).then((res) => {
        if (res.code == "0") {
          this.loadingPop = false;
          this.detailInfo = res.data;
          this.infoProcess = parseInt(this.detailInfo?.state);
          this.detailInfo.autoCancelTimeRemain;
          this.time = parseInt(this.detailInfo.autoCancelTimeRemain);
          if (this.isTime) {
            this.countTime();
            this.countTimeTwo();
          }
          if (this.detailInfo?.state == 0) {
            this.activeIndex = 1;
          }
          if (this.detailInfo?.state == 1) {
            this.activeIndex = 2;
          }
          if (this.detailInfo?.state == 3) {
            this.activeIndex = 3;
          }
          this.getc2cUser();
          this.fetchChatList();
        }
      });
    },
    //获取承兑商信息
    getc2cUser() {
      let obj = {
        c2c_user_id: this.detailInfo.c2cUserId,
      };
      Axios.getC2cUser(obj)
        .then((res) => {
          if (res.code == "0") {
            this.c2c_user = res.data["c2c_user"];
          }
        })
        .catch((err) => {
          clearInterval(this.timer);
        });
    },
    //我已确认付款
    pay_order() {
      if (!this.safe_password) {
        this.$message.error(this.$t("message.c2c.tip141"));
        return;
      }
      let obj = {
        order_no: this.detailInfo.orderNo,
        safe_password: this.safe_password,
      };
      Axios.pay_finish(obj).then((res) => {
        if (res.code == "0") {
          if (this.isBuy) {
            this.infoProcess = 1;
            this.confirmBuyDialog = false;
            this.$message.success(this.$t("message.c2c.fukuanchenggong"));
          } else {
            this.infoProcess = 2;
            this.confirmSellDialog = false;
            this.$message.success(this.$t("message.home.caozuochenggong"));
          }
        }
      });
    },
    handleSend(type = "text", content = "") {
      // 发送消息, img 也当消息text
      if (!content) {
        this.$message.error(this.$t("message.c2c.tip142"));
        return;
      }
      Axios.sendOtcOnlinechat({
        orderNo: this.detailInfo.orderNo,
        type,
        content,
      }).then((data) => {
        this.message = "";
        this.scrollToBottom();
        this.fetchChatList();
      });
    },
    //倒计时 时分秒
    countTime() {
      if (this.detailInfo.autoCancelTimeRemain == 0) {
        this.hour = "00";
        this.minute = "0";
        this.secound = "0";
        return;
      }
      this.isTime = false;
      // 用户输入任意秒数, 函数计算该毫秒数对应的时分秒, 并返回
      let _this = this;
      function getTime(time) {
        // 转换为式分秒
        let h = parseInt((time / 60 / 60) % 24);
        h = h < 10 ? "0" + h : h;
        let m = parseInt((time / 60) % 60);
        m = m < 10 ? "0" + m : m;
        let s = parseInt(time % 60);
        s = s < 10 ? "0" + s : s;
        _this.hour = h;
        _this.minute = m;
        _this.secound = s;
        // 作为返回值返回
        // return [h, m, s]
      }
      // 传入用户输入的数据
      this.interval = setInterval(() => {
        if (this.time <= 0) {
          clearInterval(this.interval);
          this.hour = "0";
          this.minute = "0";
          this.secound = "0";
        }
        getTime(this.time);
        this.time--;
      }, 1000);
    },
    //倒计时 时分秒
    countTimeTwo() {
      this.isTime = false;
      // 用户输入任意秒数, 函数计算该毫秒数对应的时分秒, 并返回
      let _this = this;
      function getTime(time) {
        // 转换为式分秒
        let h = parseInt((time / 60 / 60) % 24);
        h = h < 10 ? "0" + h : h;
        let m = parseInt((time / 60) % 60);
        m = m < 10 ? "0" + m : m;
        let s = parseInt(time % 60);
        s = s < 10 ? "0" + s : s;
        _this.doubthour = h;
        _this.doubtminute = m;
        _this.doubtsecound = s;
        // 作为返回值返回
        // return [h, m, s]
      }
      // 传入用户输入的数据
      this.interval = setInterval(() => {
        if (this.time2 <= 0) {
          this.isShenshu = true;
          clearInterval(this.interval);
          this.doubthour = "0";
          this.doubtminute = "0";
          this.doubtsecound = "0";
        }
        getTime(this.time2);
        this.time2--;
      }, 1000);
    },
    openUrl(val) {
      this.$router.push(val);
    },
    openTips() {
      this.doubtDialog = false;
      this.tipDialog = true;
    },
    doubtFun() {
      this.tipDialog = false;
      this.submitDoubtDialog = true;
    },
    openDown() {
      this.$router.push("/c2c/user");
    },
  },
};
</script>

<style lang="scss" scoped>
.left-is-ta {
  float: left;
}

.right-is-ta {
  float: right;
}

.bg-ta {
  background: #eff7ff !important;
}

.dia {
  font-size: 14px;
  color: #000;

  .confirmBuy {
    background: #1d91ff;
    height: 40px;
    line-height: 40px;
    text-align: center;
    border-radius: 5px;
    color: #fff;
    cursor: pointer;
  }

  .dia-t {
    margin-bottom: 20px;
  }

  .dia-main {
    background: #fafafa;
    border-radius: 15px;
    padding: 30px 21px;
    box-sizing: border-box;
    font-size: 15px;
    margin-bottom: 30px;

    .dia-pass {
      font-size: 14px;
      font-weight: bold;
      color: #000;
      margin-bottom: 9px;
    }

    .dia-main-title {
      display: flex;
      align-items: center;
      font-size: 16px;
      font-weight: bold;
      color: #000;
      margin-bottom: 10px;
    }

    .ml23 {
      margin-left: 23px;
      font-size: 14px;
      margin-bottom: 70px;
    }

    .shu-t {
      margin-bottom: 4px;

      .shu {
        width: 3px;
        height: 12px;
        background: #e6ba41;
        border-radius: 10px;
        margin-right: 8px;
        display: inline-block;
      }

      font-weight: bold;
    }

    .dia-card {
      display: flex;
      justify-content: space-between;
      margin-top: 22px;
    }
  }

  .dia-ti {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
  }

  .dia-q {
    margin-bottom: 28px;
  }

  .dia-btn {
    display: flex;
    justify-content: space-between;

    div {
      width: 238px;
      height: 40px;
      font-size: 16px;
      line-height: 40px;
      text-align: center;
      border-radius: 5px;
    }

    .cencel {
      background: #eaecef;
      color: #727a89;
    }

    .confirm {
      background: #1d91ff;
      color: #ffffff;
    }
  }
}

.card-b {
  width: 100%;
  height: 38px;
  display: flex;
  align-items: center;
  background: #fafafa;
  border-radius: 3px;
  font-size: 15px;
  font-weight: bold;

  .shu {
    width: 3px;
    height: 12px;
    background: #e6ba41;
    border-radius: 10px;
    margin: 0 8px 0 16px;
  }
}

.main {
  .main-title {
    background: #fafafa;

    .title-center {
      width: 1200px;
      height: 110px;
      margin: 0 auto;
      padding: 28px 0 32px;
      position: relative;
      box-sizing: border-box;
      display: flex;
      justify-content: space-between;

      .title-left {
        .buying {
          color: #000;
          font-size: 20px;
          font-weight: bold;
          margin-bottom: 10px;
        }

        .ordering {
          font-size: 14px;
          display: flex;
          align-items: center;

          .time {
            display: flex;
            align-items: center;

            .bg-time {
              width: 24px;
              height: 24px;
              line-height: 24px;
              text-align: center;
              display: inline-block;
              background: #1d91ff;
              border-radius: 4px;
              margin: 0 2.5px;
              color: #fff;
              font-size: 14px;
            }
          }
        }
      }

      .title-right {
        color: #000;
        font-weight: bold;
        text-align: right;

        .order-c {
          display: flex;
          align-items: center;
          justify-content: right;
          font-size: 14px;
        }
      }
    }
  }

  .main-center {
    width: 1200px;
    margin: 0 auto;
    padding: 30px 0 60px;
    display: flex;

    .main-left {
      width: 760px;
      margin-right: 40px;

      .main-top {
        border-bottom: 1px solid #eaecef;
        padding: 20px 0;
        position: relative;

        .confirm {
          color: #1a6ebd;
          font-size: 12px;
          position: absolute;
          padding: 2px 5px;
          height: 30px;
          left: 40px;
          top: -15px;
          text-align: center;
          line-height: 30px;
          background: #eff7ff;
        }
      }

      .main-main {
        padding: 24px 0 27px;
        display: flex;

        .main-info {
          .info-o {
            font-size: 16px;
            font-weight: bold;
            margin-bottom: 22px;
          }
          .total {
            display: flex;
            margin-bottom: 33px;

            .t-box {
              height: 62px;
              margin-right: 120px;

              .price {
                font-size: 20px;
                font-weight: 600;
              }
            }
          }

          .info-buy {
            .buy-t {
              display: flex;
              justify-content: space-between;
            }

            .card {
              height: 280px;
              border: 1px solid #eaecef;
              border-radius: 5px;
              display: flex;
              margin-bottom: 15px;

              .card-left {
                width: 254px;
                box-sizing: border-box;
                padding: 20px 16px;
                border-right: 1px solid #eaecef;
              }

              .card-right {
                padding: 20px 36px;
                font-size: 15px;

                .mr18 {
                  margin-bottom: 18px;
                }
              }
            }

            .card-3 {
              padding: 20px;
              background: #fafafa;
              border-radius: 3px;

              .card-b {
                width: 150px;
                text-align: center;
                height: 30px;
                border: 0.8px solid #e3e4e6;
                border-radius: 3px;
              }
            }
          }
        }
      }

      .main-confirm {
        font-size: 14px;
        font-weight: bold;
        padding-bottom: 44px;
        border-bottom: 1px solid #eaecef;
        display: flex;

        .confirm-w {
          width: 190px;
          height: 42px;
          line-height: 42px;
          text-align: center;
          color: #fff;
          background: #1d91ff;
          border-radius: 5px;
          margin-right: 50px;
          display: inline-block;
          cursor: pointer;
        }

        .confirm-q {
          line-height: 42px;
          color: #1a6ebd;
          cursor: pointer;
        }
      }
      .main-faq {
        min-height: 300px;
        padding-top: 38px;

        .faq-box {
          cursor: pointer;
        }

        .faq-title {
          font-size: 18px;
          font-weight: bold;
          margin-bottom: 24px;
        }

        .faq-col {
          display: flex;
          font-weight: bold;
          margin-bottom: 20px;
          align-items: center;

          img {
            width: 20px;
            height: 20px;
            margin-right: 20px;
          }
        }

        .faq-text {
          font-size: 14px;
          color: #727a89;
          line-height: 25px;
          margin-bottom: 20px;
        }
      }
    }

    .main-right {
      font-size: 12px;

      .huihua {
        margin-top: 31px;
        width: 400px;
        height: 660px;
        box-shadow: 0px 0px 3px rgba(0, 0, 0, 0.2);
        border-radius: 10px;
        box-sizing: border-box;
        padding-bottom: 60px;
        position: relative;

        .hui-top {
          background: #f5f5f5;
          padding: 21px;
          border-radius: 10px 10px 0px 0px;
          box-sizing: border-box;

          .hui-avag {
            display: flex;
            margin-bottom: 15px;
          }

          .hui-dan {
            display: flex;
            font-weight: bold;
          }
        }

        .hui-center {
          padding: 20px;
          text-align: center;
          box-sizing: border-box;
          height: 470px;
          overflow: auto;

          .hui-xia {
            width: 356px;
            height: 35px;
            border: 1px solid #eaecef;
            border-radius: 8px;
            line-height: 35px;
            margin-bottom: 27px;
          }

          .hui-dui {
            .hui-li {
              // display: flex;
              // justify-content: right;
              overflow: hidden;
              margin-bottom: 10px;

              .hui-n {
                padding: 11px 14px;
                background: #f5f5f5;
                border-radius: 8px 8px 8px 0px;
                display: initial;
              }
            }
          }
        }

        .hui-bottom {
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          display: flex;
          align-items: center;
          height: 60px;
          padding: 18px;
          box-sizing: border-box;
          border-top: 1px solid #eaecef;
          background: #fff;

          img {
            width: 32px;
            height: 32px;
          }
        }
      }
    }
  }
}

:deep(.el-radio__inner:hover) {
  border-color: #1d91ff;
}

:deep(.el-radio__input + .el-radio__label) {
  color: #000000;
  width: 450px;
  display: block;
  word-break: break-all;
  word-wrap: break-word;
}

:deep(.el-radio) {
  white-space: inherit;
  display: flex;
  align-items: center;
}

:deep(.el-radio__input.is-checked) .el-radio__inner {
  border-color: #1d91ff;
  background: #1d91ff;
}

:deep(.radio-main) .el-radio {
  margin: 15px 0;
}

:deep(.dia) .el-button {
  width: 238px;
  height: 40px;
  font-size: 16px;
  text-align: center;
  border-radius: 5px;
}

:deep(.is-co) {
  background: #eaecef !important;
  color: #b8bdc5 !important;
}

:deep(.dia) .el-checkbox__input + .el-checkbox__label {
  color: #000;
  font-size: 15px;
}

:deep(.dia) .el-checkbox__inner::after {
  height: 9px;
  left: 5px;
}

:deep(.dia) .el-checkbox__input.is-focus .el-checkbox__inner,
:deep(.dia) .el-checkbox__input .el-checkbox__inner:hover {
  border-color: #1d91ff;
}

:deep(.dia) .el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: #1d91ff;
  border-color: #1d91ff;
}

:deep(.dia) .el-checkbox__input .el-checkbox__inner {
  width: 16px;
  height: 16px;
}

:deep(.el-dialog__title) {
  color: #000 !important;
  font-weight: bold !important;
}

:deep(.hui-bottom) .el-input .el-input__inner {
  border: none !important;
}

:deep(.main-left) .el-step__title.is-finish {
  color: #1d91ff;
}

:deep(.main-left) .el-step__head.is-finish {
  color: #1d91ff;
  border-color: #1d91ff;
}

:deep(.main-left) .el-step__head .is-text {
  background: #eaecef;
  color: #c0c4cc;
}

:deep(.el-step__head.is-process) {
  color: #c0c4cc;
  border-color: #c0c4cc !important;
}

:deep(.el-step__title.is-process) {
  color: #c0c4cc;
}

:deep(.main-left) .el-step__head.is-finish .is-text {
  background: #1d91ff;
  color: #fff;
}

.bankTitle {
  display: flex;
  align-items: center;
  margin-top: 3px;
}

.content-img {
  width: 150px;
  height: 150px;
}

.top-center {
  background: #fafafa;
  border-radius: 15px;
  padding: 30px 20px;
}

.warning-icon {
  color: #1d91ff;
  font-size: 20px;
  margin-right: 10px;
}

.top-center-title {
  font-size: 16px;
  font-weight: bold;
}

.top-center-text {
  line-height: 25px;
  margin-top: 20px;
}

.top-center-onlie {
  text-align: center;
  color: #1a6ebd;
  font-weight: 600;
  margin-top: 30px;
  cursor: pointer;
}

.dia-title {
  font-size: 16px;
  font-weight: bold;
  color: #000;
  margin-top: 30px;
}

.dia-text {
  color: #727a89;
  padding: 20px 0px;
}

.dig-title {
  font-size: 24px;
  font-weight: bold;
  color: #000;
  text-align: center;
  margin-top: 30px;
}

.dig-text {
  font-size: 16px;
  margin-top: 20px;
  color: #000;
  margin-bottom: 20px;
}

.tip-img {
  width: 60px;
  height: 60px;
  margin: 0 auto;
  display: block;
}

.fonnter-but {
  width: 100%;
}

.shendu-tip {
  padding-top: 20px;
  padding-bottom: 10px;
  background: #eff7ff;
  padding-left: 20px;
  padding-right: 20px;
}

.shendu-tip-item {
  color: #727a89;
  padding-bottom: 10px;
}

.shendu-from-title {
  color: #000;
  padding: 10px 0px;
}

.mb-20 {
  margin-bottom: 20px;
}

.dialog-footer1 {
  display: flex;
  justify-content: space-between;
}

.dialog-footer1 .but {
  width: 260px;
}

.bind_btn .but {
  width: 100%;
}

.my_security {
  color: #000;
}

.allType {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
}

.el-dropdown-menu {
  background: #24282d !important;
}

.el-dropdown-menu__item {
  color: #fff;
}

.el-dropdown-menu__item:hover {
  background: #24282d;
  color: #fff;
}

.list-tiem {
  line-height: 30px;
  margin-bottom: 20px;
}

:deep(.el-loading-spinner) i {
  color: #fff !important;
  font-size: 40px;
}
</style>
