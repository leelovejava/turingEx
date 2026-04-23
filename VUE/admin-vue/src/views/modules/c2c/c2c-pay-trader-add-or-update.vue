<template>
  <el-dialog :title="!dataForm.id ? '新增C2C承兑商' : '修改C2C承兑商'" :close-on-click-modal="false" width="1200px"
    :visible.sync="visible" @close = 'handClose'>
    <el-scrollbar class="vertical-scrollbar">
      <div class="scroll-content">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()"
      label-width="140px">
      <el-form-item class="titleDiv" label="基础信息" prop="">
        <el-button type="primary" icon="el-icon-plus" size="small" class="addButton" @click="openUrl()">新增C2C管理员</el-button>
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="C2C管理员" prop="uuid">
            <el-select class="speaInputTwo" v-model="dataForm.c2c_manager_party_id" placeholder="请选择" @change="changeVal()">
              <el-option v-for="item in options" :key="item.id" :label="item.userName" :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="承兑商类型" prop="projectName">
            <el-select class="speaInputTwo" v-model="dataForm.c2c_user_type" placeholder="请选择" @change="changeVal()">
              <el-option v-for="item in c2c_user_type" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="承兑商昵称" prop="nick_name">
            <el-input  v-model="dataForm.nick_name" placeholder="承兑商昵称"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    <!-- 以下是修改类型 -->
      <el-row v-if="dataForm.id">
        <el-col :span="8">
          <el-form-item label="承兑商UID" prop="c2c_user_party_code">
            <el-input  v-model="dataForm.c2c_user_party_code" disabled placeholder="承兑商UID"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="用户UID" prop="user_code">
            <el-input  v-model="dataForm.user_code" disabled placeholder="用户UID"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="用户名" prop="user_name">
            <el-input  v-model="dataForm.user_name" disabled placeholder="用户名"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="dataForm.id">
        <el-col :span="8">
          <el-form-item label="剩余保证金" prop="deposit">
            <el-input  v-model="dataForm.deposit" disabled placeholder="剩余保证金">
              <template v-slot:append>
                <span>USDT</span>
              </template></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="保证金" prop="deposit_open">
            <el-input  v-model="dataForm.deposit_open" disabled placeholder="保证金">
              <template v-slot:append>
                <span>USDT</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 至此 -->
      <el-form-item label="登录人资金密码" prop="login_safeword">
        <el-input class="speaInputTwo" v-model="dataForm.login_safeword" type="password" placeholder="登录人资金密码"></el-input>
      </el-form-item>
      <el-form-item label="承兑商头像" prop="head_img">
        <el-upload class="avatar-uploader" :action="$http.adornUrl('/api/uploadFile')"
          :headers="{ Authorization: $cookie.get('Authorization') }" :show-file-list="false"
          :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
          <img v-if="imageUrl" :src="imageUrl" class="avatar">
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
      </el-form-item>
      <!-- 前端登录信息 -->
      <el-form-item v-if="!dataForm.id" class="titleDiv" label="前端登录信息" prop="">
        <div class="green">
          前端登录信息：只有后台承兑商需要配置，用户承兑商已经注册过了
        </div>
      </el-form-item>
      <el-row v-if="!dataForm.id">
        <el-col :span="8">
          <el-form-item label="前端登录用户名类型" prop="uuid">
            <el-select class="speaInputTwo" v-model="dataForm.type_front" placeholder="请选择" @change="changeVal()">
              <el-option v-for="item in type_front" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="推荐码(选填)" prop="usercode_front">
            <el-input v-model="dataForm.usercode_front" placeholder="推荐码(选填)"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="!dataForm.id">
        <el-col :span="8">
          <el-form-item label="前端登录用户名" prop="username_front">
            <el-input v-model="dataForm.username_front" placeholder="前端登录用户名"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="前端登录密码" prop="password_front">
            <el-input v-model="dataForm.password_front" type="password" placeholder="密码（6-12个字符）"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="前端登录确认密码" prop="re_password_front">
            <el-input v-model="dataForm.re_password_front" type="password" placeholder="确认密码（6-12个字符）"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 统计数据 -->
      <el-form-item class="titleDiv" label="统计数据" prop="">
        <div class="green">
          温馨提示：</br>
          1.【基础设置】是后台配置的，【统计值】是系统每天定时统计的;</br>
          2.“30日成单率”、“30日平均放行时间”、“30日平均付款时间” 3个参数：【基础设置】不为0时，【最终值】=【基础设置】；否则，【最终值】=【统计值】；</br>
          3.其它参数：【最终值】=【基础设置】+【统计值】；
        </div>
      </el-form-item>
      <el-form-item class="titleDivTwo" label="30日成单数" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="thirty_days_order_base">
            <el-input v-model="dataForm.thirty_days_order_base" placeholder="基础设置">
              <template v-slot:append>
                <span>单</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="统计值" prop="thirty_days_order">
            <el-input v-model="dataForm.thirty_days_order" disabled placeholder="统计值">
              <template v-slot:append>
                <span>单</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终值" prop="projectName">
            <el-input v-model="dataForm.thirty_days_order*1+dataForm.thirty_days_order_base*1" disabled placeholder="最终值">
              <template v-slot:append>
                <span>单</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="30日成单率" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="thirty_days_order_ratio_base">
            <el-input v-model="dataForm.thirty_days_order_ratio_base" placeholder="基础设置">
              <template v-slot:append>
                <span>%</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="统计值" prop="thirty_days_order_ratio">
            <el-input v-model="dataForm.thirty_days_order_ratio" disabled placeholder="统计值">
              <template v-slot:append>
                <span>%</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终值" prop="projectName">
            <el-input v-model="dataForm.thirty_days_order_ratio_base==0?dataForm.thirty_days_order_ratio:dataForm.thirty_days_order_ratio_base" disabled placeholder="最终值">
              <template v-slot:append>
                <span>%</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="30日平均放行时间" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="thirty_days_pass_average_time_base">
            <el-input v-model="dataForm.thirty_days_pass_average_time_base" placeholder="基础设置">
              <template v-slot:append>
                <span>分</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="统计值" prop="thirty_days_pass_average_time">
            <el-input v-model="dataForm.thirty_days_pass_average_time" disabled placeholder="统计值">
              <template v-slot:append>
                <span>秒</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终值" prop="projectName">
            <el-input v-model="dataForm.thirty_days_pass_average_time_base==0?dataForm.thirty_days_pass_average_time:dataForm.thirty_days_pass_average_time_base" disabled placeholder="最终值">
              <template v-slot:append>
                <span>秒</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="30日平均付款时间" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="thirty_days_pay_average_time_base">
            <el-input v-model="dataForm.thirty_days_pay_average_time_base" placeholder="基础设置">
              <template v-slot:append>
                <span>分</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="统计值" prop="thirty_days_pay_average_time">
            <el-input v-model="dataForm.thirty_days_pay_average_time" disabled placeholder="统计值">
              <template v-slot:append>
                <span>秒</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终值" prop="projectName">
            <el-input v-model="dataForm.thirty_days_pay_average_time_base==0?dataForm.thirty_days_pay_average_time:dataForm.thirty_days_pay_average_time_base" disabled placeholder="最终值">
              <template v-slot:append>
                <span>秒</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="30日成交量" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="thirty_days_amount_base">
            <el-input v-model="dataForm.thirty_days_amount_base" placeholder="基础设置">
              <template v-slot:append>
                <span>USDT</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="统计值" prop="thirty_days_amount">
            <el-input v-model="dataForm.thirty_days_amount" disabled placeholder="统计值">
              <template v-slot:append>
                <span>USDT</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终值" prop="projectName">
            <el-input v-model="dataForm.thirty_days_amount*1+dataForm.thirty_days_amount_base*1" disabled placeholder="最终值">
              <template v-slot:append>
                <span>USDT</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="买交易量" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="buy_amount_base">
            <el-input v-model="dataForm.buy_amount_base" placeholder="基础设置">
              <template v-slot:append>
                <span>USDT</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="统计值" prop="buy_amount">
            <el-input v-model="dataForm.buy_amount" disabled placeholder="统计值">
              <template v-slot:append>
                <span>USDT</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终值" prop="projectName">
            <el-input v-model="dataForm.buy_amount_base*1+dataForm.buy_amount*1" disabled placeholder="最终值">
              <template v-slot:append>
                <span>USDT</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="卖交易量" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="sell_amount_base">
            <el-input v-model="dataForm.sell_amount_base" placeholder="基础设置">
              <template v-slot:append>
                <span>USDT</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="统计值" prop="sell_amount">
            <el-input v-model="dataForm.sell_amount" disabled placeholder="统计值">
              <template v-slot:append>
                <span>USDT</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终值" prop="projectName">
            <el-input v-model="dataForm.sell_amount_base*1+dataForm.sell_amount*1" disabled placeholder="最终值">
              <template v-slot:append>
                <span>USDT</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="总交易量" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="total_amount_base">
            <el-input v-model="dataForm.total_amount_base" placeholder="基础设置">
              <template v-slot:append>
                <span>USDT</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="统计值" prop="total_amount">
            <el-input v-model="dataForm.total_amount" disabled placeholder="统计值">
              <template v-slot:append>
                <span>USDT</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终值" prop="projectName">
            <el-input v-model="dataForm.total_amount_base*1+dataForm.total_amount*1" disabled placeholder="最终值">
              <template v-slot:append>
                <span>USDT</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="账号创建天数" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="account_create_days_base">
            <el-input v-model="dataForm.account_create_days_base" placeholder="基础设置">
              <template v-slot:append>
                <span>天</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="统计值" prop="account_create_days">
            <el-input v-model="dataForm.account_create_days" disabled placeholder="统计值">
              <template v-slot:append>
                <span>天</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终值" prop="projectName">
            <el-input v-model="dataForm.account_create_days_base*1+dataForm.account_create_days*1" disabled  placeholder="最终值">
              <template v-slot:append>
                <span>天</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="首次交易至今天数" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="first_exchange_days_base">
            <el-input v-model="dataForm.first_exchange_days_base" placeholder="基础设置">
              <template v-slot:append>
                <span>天</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="统计值" prop="first_exchange_days">
            <el-input v-model="dataForm.first_exchange_days" disabled placeholder="统计值">
              <template v-slot:append>
                <span>天</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终值" prop="projectName">
            <el-input v-model="dataForm.first_exchange_days_base*1+dataForm.first_exchange_days*1" disabled placeholder="最终值">
              <template v-slot:append>
                <span>天</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="交易人数" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="exchange_users_base">
            <el-input v-model="dataForm.exchange_users_base" placeholder="基础设置">
              <template v-slot:append>
                <span>人</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="统计值" prop="exchange_users">
            <el-input v-model="dataForm.exchange_users" disabled placeholder="统计值">
              <template v-slot:append>
                <span>人</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终值" prop="projectName">
            <el-input v-model="dataForm.exchange_users_base*1+dataForm.exchange_users*1" disabled placeholder="最终值">
              <template v-slot:append>
                <span>人</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="买成单数" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="buy_success_orders_base">
            <el-input v-model="dataForm.buy_success_orders_base" placeholder="基础设置">
              <template v-slot:append>
                <span>单</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="统计值" prop="buy_success_orders">
            <el-input v-model="dataForm.buy_success_orders" disabled placeholder="统计值">
              <template v-slot:append>
                <span>单</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终值" prop="projectName">
            <el-input v-model="dataForm.buy_success_orders_base*1+dataForm.buy_success_orders*1" disabled placeholder="最终值">
              <template v-slot:append>
                <span>单</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="卖成单数" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="sell_success_orders_base">
            <el-input v-model="dataForm.sell_success_orders_base" placeholder="基础设置">
              <template v-slot:append>
                <span>单</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="统计值" prop="sell_success_orders">
            <el-input v-model="dataForm.sell_success_orders" disabled placeholder="统计值">
              <template v-slot:append>
                <span>单</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终值" prop="projectName">
            <el-input v-model="dataForm.sell_success_orders_base*1+dataForm.sell_success_orders*1" disabled  placeholder="最终值">
              <template v-slot:append>
                <span>单</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="总成单数" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="uuid">
            <el-input v-model="dataForm.total_success_orders_base" placeholder="基础设置">
              <template v-slot:append>
                <span>单</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="统计值" prop="projectName">
            <el-input v-model="dataForm.total_success_orders" disabled placeholder="统计值">
              <template v-slot:append>
                <span>单</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终值" prop="projectName">
            <el-input v-model="dataForm.total_success_orders_base*1+dataForm.total_success_orders*1" disabled placeholder="最终值">
              <template v-slot:append>
                <span>单</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="好评数" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="appraise_good_base">
            <el-input v-model="dataForm.appraise_good_base" placeholder="基础设置">
              <template v-slot:append>
                <span>次</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="统计值" prop="appraise_good">
            <el-input v-model="dataForm.appraise_good" disabled placeholder="统计值">
              <template v-slot:append>
                <span>次</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终值" prop="projectName">
            <el-input v-model="dataForm.appraise_good_base*1+dataForm.appraise_good*1" disabled placeholder="最终值">
              <template v-slot:append>
                <span>次</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="差评数" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="appraise_bad_base">
            <el-input v-model="dataForm.appraise_bad_base" placeholder="基础设置">
              <template v-slot:append>
                <span>次</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="统计值" prop="appraise_bad">
            <el-input v-model="dataForm.appraise_bad" disabled placeholder="统计值">
              <template v-slot:append>
                <span>次</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终值" prop="projectName">
            <el-input v-model="dataForm.appraise_bad_base*1+dataForm.appraise_bad*1" disabled placeholder="最终值">
              <template v-slot:append>
                <span>次</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 验证状态 -->
      <el-form-item class="titleDiv" label="验证状态" prop="">
        <div class="green">温馨提示：
          </br>
          1.【基础设置】是后台配置的，【用户状态】取决于用户前端操作验证认证；</br>
          2.【基础设置】和【用户状态】只要有一个为“已验证”，【最终状态】就为“已验证”；</br>
        </div>
      </el-form-item>
      <el-form-item class="titleDivTwo" label="手机验证状态" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="uuid">
            <el-select class="speaInputTwo" v-model="dataForm.phone_authority_base" placeholder="请选择" @change="changeVal()">
              <el-option v-for="item in optionsTwo" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="用户状态" prop="projectName">
            <el-select class="speaInputTwo" v-model="dataForm.phone_authority" placeholder="请选择" disabled @change="changeVal()">
              <el-option v-for="item in optionsTwo" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终状态" prop="projectName">
            <el-select class="speaInputTwo" v-model="dataForm.phone_authority_base=='Y'||dataForm.phone_authority=='Y'?'Y':'N'" placeholder="请选择" disabled @change="changeVal()">
              <el-option v-for="item in optionsTwo" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="邮箱验证状态" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="uuid">
            <el-select class="speaInputTwo" v-model="dataForm.email_authority_base" placeholder="请选择" @change="changeVal()">
              <el-option v-for="item in optionsTwo" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="用户状态" prop="projectName">
            <el-select class="speaInputTwo" v-model="dataForm.email_authority" placeholder="请选择" disabled @change="changeVal()">
              <el-option v-for="item in optionsTwo" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终状态" prop="projectName">
            <el-select class="speaInputTwo" v-model="dataForm.email_authority_base=='Y'||dataForm.email_authority=='Y'?'Y':'N'" placeholder="请选择" disabled @change="changeVal()">
              <el-option v-for="item in optionsTwo" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="身份认证状态" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="uuid">
            <el-select class="speaInputTwo" v-model="dataForm.kyc_authority_base" placeholder="请选择" @change="changeVal()">
              <el-option v-for="item in optionsTwo" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="用户状态" prop="projectName">
            <el-select class="speaInputTwo" v-model="dataForm.kyc_authority" placeholder="请选择" disabled @change="changeVal()">
              <el-option v-for="item in optionsTwo" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终状态" prop="projectName">
            <el-select class="speaInputTwo" v-model="dataForm.kyc_authority_base=='Y'||dataForm.kyc_authority=='Y'?'Y':'N'" placeholder="请选择" disabled @change="changeVal()">
              <el-option v-for="item in optionsTwo" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="高级认证状态" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="基础设置" prop="uuid">
            <el-select class="speaInputTwo" v-model="dataForm.kyc_highlevel_authority_base" placeholder="请选择" @change="changeVal()">
              <el-option v-for="item in optionsTwo" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="用户状态" prop="projectName">
            <el-select class="speaInputTwo" v-model="dataForm.kyc_highlevel_authority" placeholder="请选择" disabled @change="changeVal()">
              <el-option v-for="item in optionsTwo" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="最终状态" prop="projectName">
            <el-select class="speaInputTwo" v-model="dataForm.kyc_highlevel_authority_base=='Y'||dataForm.kyc_highlevel_authority=='Y'?'Y':'N'" placeholder="请选择" disabled @change="changeVal()">
              <el-option v-for="item in optionsTwo" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="dataForm.remark" type="textarea"  maxlength="500"
            :autosize="{ minRows: 5, maxRows: 5}"
            show-word-limit placeholder="备注"></el-input>
      </el-form-item>
    </el-form>
  </div>
  </el-scrollbar>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { treeDataTranslate } from "@/utils";
import { Debounce } from "@/utils/debounce";
import { encrypt } from '@/utils/crypto'
export default {
  data() {
    return {
      visible: false,
      menuList: [],
      menuListTreeProps: {
        label: "name",
        children: "children",
      },
      imageUrl: "",
      dataForm: {
        id: '',
        deposit:'',//剩余保证金
        deposit_open:'',//保证金
        user_code:'',//用户UID
        user_name:'',//用户名
        account_create_days: 0, //账号创建天数统计值
        account_create_days_base: 0, //账号创建天数基础设置
        appraise_bad: 0, //差评数统计值
        appraise_bad_base: 0, //差评数基础设置
        appraise_good: 0, //好评数统计值
        appraise_good_base: 0, //好评数基础设置
        buy_amount: 0, //买交易量统计值
        buy_amount_base: 0, //	买交易量基础设置
        buy_success_orders: 0, //	买成单数统计值
        buy_success_orders_base: 0, //	买成单数基础设置
        c2c_manager_party_id: "", //	C2C管理员
        c2c_user_party_code: "", //	承兑商uuid
        c2c_user_type: 1, //	承兑商类型
        email_authority: 'N', //	邮箱验证状态统计值
        email_authority_base: 'N', //	邮箱验证状态基础设置
        exchange_users: 0, //	交易人数统计值
        exchange_users_base: 0, //	交易人数基础设置
        first_exchange_days: 0, //	首次交易至今天数基础设置
        first_exchange_days_base: 0, //	首次交易至今天数统计值
        head_img: "", //	承兑商头像
        kyc_authority: 'N', //	身份认证状态统计值
        kyc_authority_base: 'N', //	身份认证状态基础设置
        kyc_highlevel_authority: 'N', //	高级认证状态统计值
        kyc_highlevel_authority_base: 'N', //	高级认证状态基础设置
        login_safeword: "", //	登录人资金密码
        nick_name: "", //	承兑商昵称
        password_front: "", //	前端登录密码
        phone_authority: 'N', //	手机验证状态统计值
        phone_authority_base: 'N', //	手机验证状态基础设置
        re_password_front: "", //	前端登录密码
        remark: "", //	备注
        sell_amount: 0, //	卖交易量统计值
        sell_amount_base: 0, //	卖交易量基础设置
        sell_success_orders: 0, //	卖成单数统计值
        sell_success_orders_base: 0, //	卖成单数基础设置
        thirty_days_amount: 0, //	30日交易量统计值
        thirty_days_amount_base: 0, //	30日交易量基础设置
        thirty_days_order: 0, //	30日成单数统计值
        thirty_days_order_base: 0, //	30日成单数基础设置
        thirty_days_order_ratio: 0, //	30日成单率统计值
        thirty_days_order_ratio_base: 0, //	30日成单率基础设置
        thirty_days_pass_average_time: 0, //	30日平均放行时间统计值
        thirty_days_pass_average_time_base: 0, //	30日平均放行时间基础设置
        thirty_days_pay_average_time: 0, //	30日平均付款时间统计值
        thirty_days_pay_average_time_base: 0, //	30日平均付款时间基础设置
        total_amount: 0, //	总交易量统计值
        total_amount_base: 0, //	总交易量基础设置
        total_success_orders: 0, //	总成单数统计值
        total_success_orders_base: 0, //	总成单数基础设置
        type_front: 1, //	1 手机 2.邮箱
        usercode_front: "", //	推荐码
        username_front: "", //	前端登录用户名
      },
      options: [],
      type_front: [
        {
          label: "手机",
          value: 1,
        },
        {
          label: "邮箱",
          value: 2,
        },
      ],
      c2c_user_type: [
        {
          label: "后台承兑商",
          value: 1,
        },
        // {
        //   label: "用户承兑商",
        //   value: 2,
        // },
      ],
      optionsTwo: [
        {
          label: "未验证",
          value: 'N',
        },
        {
          label: "已验证",
          value: 'Y',
        },
      ],
      dataRule: {
        roleName: [
          { required: true, message: "角色名称不能为空", trigger: "blur" },
          {
            pattern: /\s\S+|S+\s|\S/,
            message: "请输入正确的角色名称",
            trigger: "blur",
          },
        ],
        login_safeword: [
          { required: true, message: "资金密码不能为空", trigger: "blur" },
        ],
        username_front: [
          { required: true, message: "前端登录用户名不能为空", trigger: "blur" },
        ],
        password_front: [
          { required: true, message: "前端登录密码不能为空", trigger: "blur" },
        ],
        re_password_front: [
          { required: true, message: "前端确认登录密码不能为空", trigger: "blur" },
        ],
        head_img: [
          { required: true, message: "承兑商头像不能为空", trigger: "blur" },
        ],
        nick_name: [
          { required: true, message: "承兑商昵称不能为空", trigger: "blur" },
        ],
        remark: [
          {
            required: false,
            pattern: /\s\S+|S+\s|\S/,
            message: "输入格式有误",
            trigger: "blur",
          },
        ],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(id,img) {
      this.dataForm.id = id || '';
      this.getDesc();
      if(id){
        this.imageUrl = img
        this.getC2cManagerInfo()
      }
      
      this.visible = true;
    },
    handleAvatarSuccess(res, file) {
      if(res.code == 0){
        this.dataForm.head_img = res.data.path;
        this.imageUrl = URL.createObjectURL(file.raw);
        this.$refs["dataForm"].validateField('head_img')
      }else{
        this.$message({
          type: 'error',
          message: data.msg
        }); 
      }


    },
    beforeAvatarUpload(file) {
      // const isJPG = file.type === 'image/jpeg';
      const isLt2M = file.size / 1024 / 1024 < 10;

      // if (!isJPG) {
      //   this.$message.error('上传头像图片只能是 JPG 格式!');
      // }
      if (!isLt2M) {
        this.$message.error("上传图片大小不能超过 10MB!");
      }
      // return isJPG && isLt2M;
      return isLt2M;
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    getDesc() {
      //获取C2C管理员
      this.$http({
        url: this.$http.adornUrl("/c2cUser/getAllC2cManager"),
        method: "get",
        params: this.$http.adornParams(Object.assign({})),
      }).then(({ data }) => {
        if (data.code == 0) {
          this.options = data.data;
        }
      });
    },
    getC2cManagerInfo() {
      //获取详情
      this.$http({
        url: this.$http.adornUrl("/c2cUser/getDesc"),
        method: "get",
        params: this.$http.adornParams(Object.assign({
          id:this.dataForm.id
        })),
      }).then(({ data }) => {
        if (data.code == 0) {
          this.dataForm = data.data;
        }
      });
    },
    handClose(){
        this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
        this.imageUrl = ''
     this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate() // 清除表单验证
          })
      },
      openUrl(){
        this.visible = false;
        this.$router.push({ path: '/sys-config-system-user-sys-config' })
      },
        // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
              const formData = new FormData();
              const objDate = {...this.dataForm}
            // 添加需要提交的表单字段及其值到 FormData 对象
            for (let key in objDate) {
              if(key == 'login_safeword'){
                console.log(objDate[key])
                objDate[key] = encrypt(objDate[key])
                
              }
              formData.append(key, objDate[key]);
            }
            if(this.dataForm.id){//修改
           // 发送表单数据到后台接口
           this.$http({
              url: this.$http.adornUrl(`/c2cUser/update`),
              method: 'post',
              data: formData,
              headers: {
                'Content-Type': 'multipart/form-data' // 设置请求头为 multipart/form-data
              }
            })
            .then(response  => {
              if(response.data.code==0){
                this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.visible = false;
                  this.$emit("refreshDataList");
                },
              });
              }else{
                  this.$message({
                  message:response.data.msg,
                  type: "error",
                  duration: 1500,
                  onClose: () => {
                  },
                });
              }
            })
            .catch(error => {
            });
            }else{
           // 发送表单数据到后台接口
           this.$http({
              url: this.$http.adornUrl(`/c2cUser/add`),
              method: 'post',
              data: formData,
              headers: {
                'Content-Type': 'multipart/form-data' // 设置请求头为 multipart/form-data
              }
            })
            .then(response  => {
              if(response.data.code==0){
                this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.visible = false;
                  this.$emit("refreshDataList");
                },
              });
              }else{
                  this.$message({
                  message:response.data.msg,
                  type: "error",
                  duration: 1500,
                  onClose: () => {
                  },
                });
              }
            })
            .catch(error => {
            });
            }
 
          }
        });
    }),
    // 表单提交
    // dataFormSubmit: Debounce(function () {
    //   this.$refs["dataForm"].validate((valid) => {
    //     if (valid) {
    //       this.$http({
    //         url: this.$http.adornUrl(`/c2cUser/add`),
    //         method:"post",
    //         data: 
    //         //this.dataForm
    //         this.$http.adornData({
    //           //this.dataForm,
    //           account_create_days: this.dataForm.account_create_days, //账号创建天数统计值
    //           account_create_days_base: this.dataForm.account_create_days_base, //账号创建天数基础设置
    //           appraise_bad: this.dataForm.appraise_bad, //差评数统计值
    //           appraise_bad_base: this.dataForm.appraise_bad_base, //差评数基础设置
    //           appraise_good: this.dataForm.appraise_good, //好评数统计值
    //           appraise_good_base: this.dataForm.appraise_good_base, //好评数基础设置
    //           buy_amount: this.dataForm.buy_amount, //买交易量统计值
    //           buy_amount_base: this.dataForm.buy_amount_base, //	买交易量基础设置
    //           buy_success_orders: this.dataForm.buy_success_orders, //	买成单数统计值
    //           buy_success_orders_base: this.dataForm.buy_success_orders_base, //	买成单数基础设置
    //           c2c_manager_party_id: this.dataForm.c2c_manager_party_id, //	C2C管理员
    //           c2c_user_party_code: this.dataForm.c2c_user_party_code, //	承兑商uuid
    //           c2c_user_type: this.dataForm.c2c_user_type, //	承兑商类型
    //           email_authority: this.dataForm.email_authority, //	邮箱验证状态统计值
    //           email_authority_base: this.dataForm.email_authority_base, //	邮箱验证状态基础设置
    //           exchange_users: this.dataForm.exchange_users, //	交易人数统计值
    //           exchange_users_base: this.dataForm.exchange_users_base, //	交易人数基础设置
    //           first_exchange_days: this.dataForm.first_exchange_days, //	首次交易至今天数基础设置
    //           first_exchange_days_base: this.dataForm.first_exchange_days_base, //	首次交易至今天数统计值
    //           head_img: this.dataForm.head_img, //	承兑商头像
    //           kyc_authority: this.dataForm.kyc_authority, //	身份认证状态统计值
    //           kyc_authority_base: this.dataForm.kyc_authority_base, //	身份认证状态基础设置
    //           kyc_highlevel_authority: this.dataForm.kyc_highlevel_authority, //	高级认证状态统计值
    //           kyc_highlevel_authority_base: this.dataForm.kyc_highlevel_authority_base, //	高级认证状态基础设置
    //           login_safeword: encrypt(this.dataForm.login_safeword), //	登录人密码
    //           nick_name: this.dataForm.nick_name, //	承兑商昵称
    //           password_front: encrypt(this.dataForm.password_front), //	前端登录密码
    //           phone_authority: this.dataForm.phone_authority, //	手机验证状态统计值
    //           phone_authority_base: this.dataForm.phone_authority_base, //	手机验证状态基础设置
    //           re_password_front: encrypt(this.dataForm.re_password_front), //	前端登录密码
    //           remark: this.dataForm.remark, //	备注
    //           sell_amount: this.dataForm.sell_amount, //	卖交易量统计值
    //           sell_amount_base: this.dataForm.sell_amount_base, //	卖交易量基础设置
    //           sell_success_orders: this.dataForm.sell_success_orders, //	卖成单数统计值
    //           sell_success_orders_base: this.dataForm.sell_success_orders_base, //	卖成单数基础设置
    //           thirty_days_amount: this.dataForm.thirty_days_amount, //	30日交易量统计值
    //           thirty_days_amount_base: this.dataForm.thirty_days_amount_base, //	30日交易量基础设置
    //           thirty_days_order: this.dataForm.thirty_days_order, //	30日成单数统计值
    //           thirty_days_order_base: this.dataForm.thirty_days_order_base, //	30日成单数基础设置
    //           thirty_days_order_ratio: this.dataForm.thirty_days_order_ratio, //	30日成单率统计值
    //           thirty_days_order_ratio_base: this.dataForm.thirty_days_order_ratio_base, //	30日成单率基础设置
    //           thirty_days_pass_average_time: this.dataForm.thirty_days_pass_average_time, //	30日平均放行时间统计值
    //           thirty_days_pass_average_time_base: this.dataForm.thirty_days_pass_average_time_base, //	30日平均放行时间基础设置
    //           thirty_days_pay_average_time: this.dataForm.thirty_days_pay_average_time, //	30日平均付款时间统计值
    //           thirty_days_pay_average_time_base: this.dataForm.thirty_days_pay_average_time_base, //	30日平均付款时间基础设置
    //           total_amount: this.dataForm.total_amount, //	总交易量统计值
    //           total_amount_base: this.dataForm.total_amount_base, //	总交易量基础设置
    //           total_success_orders: this.dataForm.total_success_orders, //	总成单数统计值
    //           total_success_orders_base: this.dataForm.total_success_orders_base, //	总成单数基础设置
    //           type_front: this.dataForm.type_front, //	1 手机 2.邮箱
    //           usercode_front: this.dataForm.usercode_front, //	推荐码
    //           username_front: this.dataForm.username_front, //	前端登录用户名
    //         }),
    //       }).then(({ data }) => {
    //         if(data.code == 0){
    //           this.$message({
    //           message: "操作成功",
    //           type: "success",
    //           duration: 1500,
    //           onClose: () => {
    //             this.visible = false;
    //             this.$emit("refreshDataList");
    //           },
    //         });
    //         }else{
    //           this.$message({
    //           message: data.msg,
    //           type: "error",
    //           duration: 1500,
    //           onClose: () => {
    //           },
    //         });
    //         }

    //       });
    //     }
    //   });
    // }),
  },
};
</script>
<style scoped>
.titleDiv {
  margin: 30px 0;
  border-bottom: 1px solid #e6e6e6;
}

.titleDivTwo {
  height: 40px;
  border-left: 3px solid #1c4efa;
  background: #f4f7ff;
}

.addButton {
  float: right;
  margin-right: 20px;
}

.speaInputTwo {
  width: 250px;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.vertical-scrollbar .scroll-content {
  max-height: 600px;
  overflow-y: auto;
  overflow-x: hidden;
}
.vertical-scrollbar .scroll-content::-webkit-scrollbar {
  width: 4px;
}

.vertical-scrollbar .scroll-content::-webkit-scrollbar-track {
  background-color: #f1f1f1;
}

.vertical-scrollbar .scroll-content::-webkit-scrollbar-thumb {
  background-color: #888;
  border-radius: 4px;
}

.vertical-scrollbar .scroll-content::-webkit-scrollbar-thumb:hover {
  background-color: #555;
}
</style>
