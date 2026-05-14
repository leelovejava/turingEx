package com.yami.trading.admin.controller.miner;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson2.JSON;
import com.yami.trading.bean.miner.Miner;
import com.yami.trading.bean.miner.MinerOrder;
import com.yami.trading.bean.model.User;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.*;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.SessionTokenService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yami.trading.service.miner.service.MinerOrderService;
import com.yami.trading.service.miner.service.MinerService;
import com.yami.trading.service.quant.service.QuantPreIncomeService;
import com.yami.trading.bean.quant.QuantPreIncome;


/**
 * 矿池订单
 */
@RestController
@CrossOrigin
public class MinerOrderController {

    private Logger logger = LogManager.getLogger(MinerOrderController.class);
    @Autowired
    protected MinerOrderService minerOrderService;
    @Autowired
    protected MinerService minerService;
    @Autowired
    protected UserService partyService;
    @Autowired
    protected SessionTokenService sessionTokenService;
    @Autowired
    protected SysparaService sysparaService;
    @Autowired
    protected QuantPreIncomeService quantPreIncomeService;

    private final String action = "api/minerOrder!";

    /**
     * 矿池订单 列表
     */
    @RequestMapping(action + "list.action")
    public ResultObject list(HttpServletRequest request) {

        ResultObject resultObject = new ResultObject();
//		resultObject = readSecurityContextFromSession(resultObject);
//		if (!"0".equals(resultObject.getCode())) {
//			return resultObject;
//		}
        try {
            List<Map<String, Object>> datas = null;
//			String partyId = getLoginPartyId();
            String partyId = SecurityUtils.getCurrentUserId();
//			DecimalFormat df = new DecimalFormat("#.##");
            if (StringUtils.isNullOrEmpty(partyId)) {
                resultObject.setData(datas);
                resultObject.setCode("0");
                return resultObject;
            }

            // 状态。0.正常赎回， 1 托管中 ,2提前赎回 (违约)3.取消
            String state = request.getParameter("state");
            String page_no = request.getParameter("page_no");
            int pageNo = 1;
            if (StringUtils.isNotEmpty(page_no)) {
                pageNo = Integer.valueOf(page_no);
            }

            datas = minerOrderService.pagedQuery(pageNo, 40, partyId, state).getRecords();

            for (Map<String, Object> data : datas) {
                int intervalDaysByTwoDate = 0;
                if (null == data.get("stop_time")) {
                    if ("0" != data.get("cycle_close")) {
                        String can_close_time = DateUtils
                                .format(DateUtils.addDay(DateUtils.toDate(data.get("create_time").toString()),
                                        new Double(data.get("cycle_close").toString()).intValue()),
                                        DateUtils.DF_yyyyMMdd);
                        intervalDaysByTwoDate = DateUtils.getIntervalDaysByTwoDate(DateUtils.toDate(can_close_time),
                                new Date());
                    }
                } else {
                    intervalDaysByTwoDate = DateUtils.getIntervalDaysByTwoDate(
                            DateUtils.toDate(data.get("stop_time").toString()), new Date());
                }
                if (intervalDaysByTwoDate < 0) {
                    intervalDaysByTwoDate = 0;
                }
                int runningDays = 0;
                Date runStartDate = null;
                if (data.get("earn_time") != null) {
                    runStartDate = DateUtils.toDate(data.get("earn_time").toString());
                } else if (data.get("create_time") != null) {
                    runStartDate = DateUtils.toDate(data.get("create_time").toString());
                }
                if (runStartDate != null) {
                    runningDays = Math.max(daysBetween(runStartDate, new Date()), 0);
                }
                // 运行天数
                data.put("days", runningDays);
                data.put("runningDays", runningDays);

                DecimalFormat df = new DecimalFormat("#.##");
                data.put("profit", df.format(data.get("profit")));
                data.put("test", null != data.get("test") && "Y".equals(data.get("test").toString()));
                data.put("can_close", intervalDaysByTwoDate <= 0);

                data.put("buyCurrency", "usdt");
                data.put("outputCurrency", "usdt");
                if (data.get("symbol") == null || data.get("symbol").toString().isEmpty()) {
                    data.put("symbol", "BTC/USDT");
                }

                if ((boolean) data.get("test")) {
                    Double minerTestProfit = sysparaService.find("miner_test_profit").getDouble();
                    data.put("daily_profit", minerTestProfit);
                    data.put("daily_rate", minerTestProfit);
                    data.put("cycle", data.get("cycle"));
                    data.put("all_rate", Arith.mul(minerTestProfit, Integer.valueOf(data.get("cycle").toString())));
                } else {
                    double dailyProfitRate = Arith.mul(Double.valueOf(data.get("daily_rate").toString()), 0.01d);
                    double dailyProfitAmount = Arith.mul(dailyProfitRate, Double.valueOf(data.get("amount").toString()));
                    data.put("daily_profit", df.format(dailyProfitAmount));
                    data.put("daily_rate", data.get("daily_rate"));
                    data.put("cycle", data.get("cycle_close"));
                    double all_rate = Arith.mul(30, Double.valueOf(data.get("daily_rate").toString()));
                    data.put("all_rate", df.format(all_rate));
                }
                // 今日收益 / 总收益（最终收益 = 日收益 × 支付金额 × 天数）
                String quantOrderId = data.get("id") != null ? data.get("id").toString()
                        : (data.get("uuid") != null ? data.get("uuid").toString() : null);
                if (quantOrderId != null) {
                    double amount = Double.valueOf(data.get("amount").toString());
                    int days = runningDays;
                    double dailyRate;
                    if ((boolean) data.get("test")) {
                        dailyRate = sysparaService.find("miner_test_profit").getDouble();
                    } else {
                        dailyRate = Arith.mul(Double.valueOf(data.get("daily_rate").toString()), 0.01d);
                    }
                    double dayIncome = Arith.mul(dailyRate, amount);
                    double totalIncome = Arith.mul(dayIncome, days);
                    data.put("day_income", df.format(dayIncome));
                    data.put("total_income", df.format(totalIncome));
                } else {
                    data.put("day_income", "0");
                    data.put("total_income", "0");
                }
                // 倒计时天数
                data.put("countdown", intervalDaysByTwoDate);
            }
            resultObject.setData(datas);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            logger.error("BusinessException:", e);
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", e);
        }
        return resultObject;
    }

    /**
     * 矿机收益统计
     */
    @RequestMapping(action + "listSum.action")
    public Object listSum() {
        ResultObject resultObject = new ResultObject();
        try {
//			String partyId = getLoginPartyId();
            String partyId = SecurityUtils.getCurrentUserId();
            List<Map<String, Object>> data = minerOrderService.findByState(partyId, null);
            MinerOrder order;
            Map<String, Object> one;
            List<Miner> miners = minerService.findAll();
            Miner miner = new Miner();
            Map<String, Object> map = new HashMap<String, Object>();

            // 金额总数
            double amount_sum = 0;
            // 预计今日盈利
            double today_profit = 0;
            // 已获收益
            double aready_profit = 0;
            // 订单数
            double order_sum = 0;
            // 金额总数  换算U
            double amountSumValue = 0;
            // 今日盈利 换算U
            double todayProfitValue = 0;
            // 已获收益 换算U
            double areadyProfitValue = 0;

            String buyCurrency = "";
            String outputCurrency = "";

            Double miner_test_profit = sysparaService.find("miner_test_profit").getDouble();

            if (data != null) {
                for (int i = 0; i < data.size(); i++) {
                    one = data.get(i);

                    order = JSON.parseObject(JSON.toJSONString(one), MinerOrder.class);

                    if ("1".equals(order.getState())) {
                        order_sum = Arith.add(order_sum, 1d);
                        amount_sum = Arith.add(amount_sum, (Double) order.getAmount());
                    }
                    aready_profit = Arith.add(aready_profit, (Double) order.getProfit());
                    for (int j = 0; j < miners.size(); j++) {
                        miner = miners.get(j);
                        if (miner.getUuid().equals(order.getMiner_id()) && "1".equals(order.getState())) {
                            if (miner.getTest().equals("Y")) {
                                today_profit = Arith.add(today_profit, miner_test_profit);
                            } else {
                                double miner_profit = Arith.mul(miner.getDaily_rate(), 0.01d);
                                double get_profit = Arith.mul(miner_profit, (Double) order.getAmount());
                                today_profit = Arith.add(today_profit, get_profit);
                                break;
                            }
                        }
                    }
                }
            }
            DecimalFormat df = new DecimalFormat("#.##");
            map.put("amount_sum", df.format(amount_sum));
            map.put("amountSumValue", df.format(amountSumValue));
            map.put("today_profit", df.format(today_profit));
            map.put("aready_profit", df.format(aready_profit));
            map.put("order_sum", order_sum);
            map.put("buyCurrency", buyCurrency);
            map.put("outputCurrency", outputCurrency);
            map.put("areadyProfitValue", df.format(areadyProfitValue));
            map.put("todayProfitValue", df.format(todayProfitValue));
            resultObject.setData(map);
            resultObject.setCode("0");
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", e);
        }
        return resultObject;
    }

    /**
     * 矿机订单收益记录列表（分页）
     */
    @RequestMapping(action + "incomeList.action")
    public Object incomeList(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        try {
            String order_no = request.getParameter("order_no");
            String pageNoStr = request.getParameter("page_no");
            int pageNo = pageNoStr != null ? Integer.parseInt(pageNoStr) : 1;
            int pageSize = 20;
            MinerOrder order = minerOrderService.findByOrder_no(order_no);
            if (order == null || order.getUuid() == null) {
                resultObject.setData(new ArrayList<>());
                resultObject.setCode("0");
                return resultObject;
            }
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<QuantPreIncome> page =
                quantPreIncomeService.lambdaQuery()
                    .eq(QuantPreIncome::getQuantOrderId, order.getUuid())
                    .eq(QuantPreIncome::getStatus, 1)
                    .orderByDesc(QuantPreIncome::getEndTime)
                    .page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNo, pageSize));
            Map<String, Object> result = new HashMap<>();
            result.put("records", page.getRecords());
            result.put("total", page.getTotal());
            result.put("pages", page.getPages());
            result.put("current", page.getCurrent());
            resultObject.setData(result);
            resultObject.setCode("0");
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", e);
        }
        return resultObject;
    }

    /**
     * 矿池订单 详情
     */
    @RequestMapping(action + "get.action")
    public Object get(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
//		resultObject = readSecurityContextFromSession(resultObject);
//		if (!"0".equals(resultObject.getCode())) {
//			return resultObject;
//		}
        try {
            String order_no = request.getParameter("order_no");
            MinerOrder data = minerOrderService.findByOrder_no(order_no);

            resultObject.setData(bulidData(data));
            resultObject.setCode("0");
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            logger.error("BusinessException:", e);
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", e);
        }
        return resultObject;
    }

    /**
     * 创建买入矿机订单
     */
    @RequestMapping(action + "open.action")
    public Object open(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
//		resultObject = readSecurityContextFromSession(resultObject);
//		if (!"0".equals(resultObject.getCode())) {
//			return resultObject;
//		}
//		String partyId = this.getLoginPartyId();
        String partyId = SecurityUtils.getCurrentUserId();
        try {
            String session_token = request.getParameter("session_token");
            String minerId = request.getParameter("minerId");
            String amount = request.getParameter("amount");
            String symbol = request.getParameter("symbol");
            String cycleParam = request.getParameter("cycle");

            String object = this.sessionTokenService.cacheGet(session_token);
            this.sessionTokenService.del(session_token);
            if ((!partyId.equals(object))) {
                resultObject.setCode("1");
                resultObject.setMsg("请稍后再试");
                return resultObject;
            }

            User party = this.partyService.cacheUserBy(partyId);
//			if (!party.getEnabled()) {
//				resultObject.setCode("506");
//				resultObject.setMsg(error);
//				return resultObject;
//			}

            MinerOrder order = new MinerOrder();
            order.setPartyId(partyId);
            order.setMinerId(minerId);
            order.setAmount(Double.valueOf(amount));
            order.setOrder_no(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
            order.setState("1");
            order.setSymbol(symbol);
            if (cycleParam != null && !cycleParam.isEmpty()) {
                order.setCycle(Integer.parseInt(cycleParam));
            }

            this.minerOrderService.saveCreateNew(order, false);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("order_no", order.getOrder_no());
            resultObject.setData(map);
            resultObject.setCode("0");
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", e);
        }
        return resultObject;
    }

    /**
     * 获取订单
     */
    @RequestMapping(action + "getOpen.action")
    public Object getOpen(HttpServletRequest request) throws IOException {
        ResultObject resultObject = new ResultObject();
//		resultObject = readSecurityContextFromSession(resultObject);
//		if (!"0".equals(resultObject.getCode())) {
//			return resultObject;
//		}
        try {
            String minerId = request.getParameter("minerId");
            String amount_temp = request.getParameter("amount");
            Miner miner = minerService.findById(minerId);
            if (StringUtils.isNullOrEmpty(amount_temp)
                    || !StringUtils.isDouble(amount_temp)
                    || Double.valueOf(amount_temp) < 0) {
                throw new BusinessException("金额错误");
            }

            DecimalFormat df = new DecimalFormat("#.##");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("miner_test", miner.getTest());
            map.put("miner_name", miner.getName());
            map.put("miner_name_en", miner.getName_en());
            map.put("miner_name_cn", miner.getName_cn());
            map.put("buyCurrenc", miner.getBuy_currency());
            map.put("outputCurrency", miner.getOutput_currency());
//			String partyId = getLoginPartyId();
            String partyId = SecurityUtils.getCurrentUserId();
            if (!StringUtils.isNullOrEmpty(partyId)) {
                String session_token = sessionTokenService.savePut(partyId);
                map.put("session_token", session_token);
            }

            Date date = new Date();
            map.put("create_time", date);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            // 把日期往后增加一天.整数往后推
            calendar.add(calendar.DATE, 1);
            date = calendar.getTime();

            // 起息时间
            // 根据对外展示时区设置，修改时间
            Date showEarnTime = DateTimeTools.transferToShowTime(date);
            map.put("earn_time", DateUtils.format(showEarnTime, DateUtils.DF_yyyyMMdd));

            if (miner.getTest().equals("Y")) {
                map.put("amount", 0);
                calendar.add(calendar.DATE, miner.getCycle());
                date = calendar.getTime();
                // 系统时区时间
                Date localStopTime = DateUtils.addDay(date, miner.getCycle());
                // 根据对外展示时区设置，修改时间
                Date showStopTime = DateTimeTools.transferToShowTime(localStopTime);
                map.put("stop_time", DateUtils.format(showStopTime, DateUtils.DF_yyyyMMdd));
                Double minerTestProfit = sysparaService.find("miner_test_profit").getDouble();
                map.put("daily_rate", minerTestProfit);
                map.put("profit_may", String.valueOf(Arith.mul(minerTestProfit, miner.getCycle())));
                map.put("cycle", miner.getCycle());
                map.put("all_rate", Arith.mul(minerTestProfit, miner.getCycle()));
            } else {
                double amount = Double.valueOf(amount_temp);
                map.put("amount", amount);
                map.put("stop_time", null);
                map.put("daily_rate", miner.getDaily_rate());
                double rate = Arith.mul(miner.getDaily_rate(), 0.01d);
                map.put("profit_may", String.valueOf(df.format(Arith.mul(amount, Arith.mul(rate, 30d)))));
                map.put("cycle", miner.getCycle_close());
                double all_rate = Arith.mul(30d, miner.getDaily_rate());
                map.put("all_rate", df.format(all_rate));
            }
            map.put("investment_min", miner.getInvestment_min());
            map.put("minerId", minerId);
            map.put("order_no", DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
            map.put("test", miner.getTest());

            resultObject.setData(map);
            resultObject.setCode("0");
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            logger.error("BusinessException:", e);
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", e.fillInStackTrace());
        }
        return resultObject;
    }


    /**
     * 赎回订单
     */
    @RequestMapping(action + "closOrder.action")
    public Object closOrder(HttpServletRequest request) throws IOException {
        ResultObject resultObject = new ResultObject();
//		resultObject = readSecurityContextFromSession(resultObject);
//		if (!"0".equals(resultObject.getCode())) {
//			return resultObject;
//		}
        try {
            String order_no = request.getParameter("order_no");
            CloseDelayThread lockDelayThread = new CloseDelayThread(order_no, minerOrderService);
            Thread t = new Thread(lockDelayThread);
            t.start();

        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            logger.error("error:", e.fillInStackTrace());
        }
        return resultObject;
    }

    /**
     * 新线程处理，直接拿到订单锁处理完成后退出
     */
    public class CloseDelayThread implements Runnable {
        private String orderNo;
        private MinerOrderService minerOrderService;

        public void run() {
            try {
                // 提前赎回理财产品需要支付违约金
                MinerOrder order = minerOrderService.findByOrder_no(orderNo);

                Miner miner = minerService.findById(order.getMiner_id());

                // 取时间
                Date date_now = new Date();
                double last_days = daysBetween(order.getCreate_time(), date_now);
                if ("1".equals(order.getState()) && last_days >= miner.getCycle_close()) {
                    // 解锁，不扣违约金
                    double default_money = 0;
                    order.setState("2");
                    order.setProfit(Arith.sub(order.getProfit(), default_money));
                    this.minerOrderService.saveClose(order);
                }

            } catch (Exception e) {
                logger.error("error:", e);
            }

        }

        public CloseDelayThread(String orderNo, MinerOrderService minerOrderService) {
            this.orderNo = orderNo;
            this.minerOrderService = minerOrderService;
        }

    }

    /**
     * 构建矿机订单详情数据
     *
     * @param order 矿机订单对象
     * @return 包含订单详情的Map集合
     * @throws ParseException 日期解析异常
     */
    protected Map<String, Object> bulidData(MinerOrder order) throws ParseException {
        System.out.println("bulidData => " + order);
        Miner miner = new Miner();
        miner = minerService.findById(order.getMiner_id());
        System.out.println("miner => " + miner);

        // 初始化小数格式器（保留两位小数）
        DecimalFormat df = new DecimalFormat("#.##");
        Map<String, Object> map = new HashMap<String, Object>();
        
        // ==================== 矿机信息 ====================
        map.put("miner_name", miner.getName());
        map.put("miner_name_en", miner.getName_en());
        map.put("miner_name_cn", miner.getName_cn());
        Double miner_test_profit = sysparaService.find("miner_test_profit").getDouble();

        // 日利率：体验矿机使用测试收益，普通矿机使用配置的日利率
        map.put("daily_rate", miner.getTest().equals("Y") ? miner_test_profit : miner.getDaily_rate());
        
        // ==================== 时间信息 ====================
        Date create_time = order.getCreate_time();
        map.put("create_timeStr", create_time);
        map.put("close_timeStr", order.getClose_time());
        
        // 停止时间展示
        if (miner.getTest().equals("Y")) {
            // 根据对外展示时区设置，修改时间
            Date showStopTime = DateTimeTools.transferToShowTime(order.getStop_time());
            map.put("stop_timeStr", DateUtils.format(showStopTime, DateUtils.DF_yyyyMMdd));
        } else {
            map.put("stop_timeStr", null);
        }

        // 起息时间展示（根据对外展示时区设置）
        Date showEarnTime = DateTimeTools.transferToShowTime(order.getEarn_time());
        map.put("earn_timeStr", DateUtils.format(showEarnTime, DateUtils.DF_yyyyMMdd));

        // 计算剩余天数（到停止时间还有多少天）
        Date date_now = new Date();
        int daysBetween = order.getStop_time() == null ? 0 : daysBetween(date_now, order.getStop_time());
        daysBetween = Math.max(daysBetween, 0);
        map.put("days", daysBetween);
        
        // 计算已运行天数
        int last_days = daysBetween(create_time, date_now);
        map.put("can_close", last_days >= miner.getCycle_close());
        
        // ==================== 订单基本信息 ====================
        map.put("order_no", order.getOrder_no());
        map.put("amount", order.getAmount());
        map.put("id", order.getOrder_no());
        map.put("state", order.getState());
        // 实际收益
        map.put("profit", order.getProfit());

        // 周期天数：体验矿机使用配置周期，普通矿机使用平仓周期
        int cycle = miner.getTest().equals("Y") ? miner.getCycle() : miner.getCycle_close();
        map.put("cycle", cycle);
        
        // 总收益率
        double all_rate = Arith.mul(30, miner.getDaily_rate());
        map.put("all_rate", miner.getTest().equals("Y") ? Arith.mul(miner_test_profit, miner.getCycle()) : df.format(all_rate));

        // ==================== 预期总收益 ====================
        map.put("expected_total_income", order.getExpectedTotalIncome());
        map.put("test", miner.getTest());
        map.put("buyCurrency", miner.getBuy_currency());
        map.put("outputCurrency", miner.getOutput_currency());
        String symbol = order.getSymbol();
        if (symbol == null || symbol.isEmpty()) {
            symbol = (miner.getBuy_currency() + "/" + miner.getOutput_currency()).toUpperCase();
        }
        map.put("symbol", symbol);
        
        // ==================== 今日收益 / 总收益（最终收益 = 日收益 × 支付金额 × 天数） ====================
        String uuid = order.getUuid();
        double totalGeneratedIncome = 0;
        if (uuid != null) {
            double amount = order.getAmount();
            int days = last_days;
            // 使用订单创建时存储的随机日利率（在 daily_rate_start 和 daily_rate_end 之间随机生成）
            double dailyRate = order.getRandom_daily_rate();
            // 将百分比转换为小数（例如 2% → 0.02）
            double dailyRateDecimal = Arith.mul(dailyRate, 0.01d);
            // 计算今日收益
            double dayIncome = Arith.mul(dailyRateDecimal, amount);
            // 计算总收益
            double totalIncome = Arith.mul(dayIncome, days);
            totalGeneratedIncome = totalIncome;
            map.put("day_income", df.format(dayIncome));
            map.put("total_income", df.format(totalIncome));
        } else {
            map.put("day_income", "0");
            map.put("total_income", "0");
        }
        
        // ==================== 资产信息 ====================
        // 总资产 = 本金 + 已产生的收益
        double totalAsset = Arith.add(order.getAmount(), totalGeneratedIncome);
        map.put("total_asset", df.format(totalAsset));
        // 已产生收益
        map.put("generated_income", df.format(totalGeneratedIncome));

        // ==================== 根据订单状态设置数据 ====================
        // 状态 1：已结束/已平仓
        if ("1".equals(order.getState())) {
            map.put("profit", order.getProfit());
            map.put("default_amount", 0);
            map.put("principal_amount", 0);
        }
        // 状态 2：违约/强制平仓
        if ("2".equals(order.getState())) {
            map.put("profit", 0);
            map.put("default_amount", 0);
            map.put("principal_amount", df.format(order.getAmount()));
        }
        // 状态 0：运行中
        if ("0".equals(order.getState())) {
            map.put("profit", order.getProfit());
            map.put("default_amount", 0);
            map.put("principal_amount", df.format(order.getAmount()));
        }

        return map;
    }

    public static int daysBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }


}
