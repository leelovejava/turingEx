package com.yami.trading.admin.controller.finance;

import com.yami.trading.bean.finance.Finance;
import com.yami.trading.bean.finance.FinanceOrder;
import com.yami.trading.bean.model.User;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.Arith;
import com.yami.trading.common.util.DateUtil;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.RandomUtil;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.common.web.ResultObject;
import com.yami.trading.security.common.util.SecurityUtils;
import com.yami.trading.service.SessionTokenService;
import com.yami.trading.service.finance.service.FinanceOrderService;
import com.yami.trading.service.finance.service.FinanceService;
import com.yami.trading.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

/**
 * 理财订单
 */
@RestController
@CrossOrigin
@Slf4j
public class FinanceOrderController {

//	private Logger logger = LogManager.getLogger(FinanceOrderController.class);

    @Autowired
    protected FinanceOrderService financeOrderService;
    @Autowired
    protected FinanceService financeService;
    @Autowired
    protected UserService partyService;
    @Autowired
    protected SessionTokenService sessionTokenService;

    private final String action = "/api/financeOrder!";

    /**
     * 托管订单
     */
    @RequestMapping(action + "list.action")
    public Object list(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
//		resultObject = readSecurityContextFromSession(resultObject);
//		if (!"0".equals(resultObject.getCode())) {
//			return resultObject;
//		}
        try {
            List<FinanceOrder> data = new ArrayList<>();
//			String partyId = getLoginPartyId();
            String partyId = SecurityUtils.getCurrentUserId();
            if (StringUtils.isNullOrEmpty(partyId)) {
                resultObject.setData(data);
                resultObject.setCode("0");
                return resultObject;
            }
            String page_no = request.getParameter("page_no");
            int pageNo = 1;
            if (StringUtils.isNotEmpty(page_no)) {
                pageNo = Integer.valueOf(page_no);
            }
            // 状态。0.正常赎回， 1 托管中 ,2提前赎回 (违约)3.取消
            String state = request.getParameter("state");
            data = financeOrderService.pagedQuery(pageNo, 10, partyId, state).getRecords();
            // 如果不为空 则从理财产品中获取日利率 图片
            if (data == null) {
                resultObject.setData(data);
                resultObject.setCode("0");
                return resultObject;
            }

            List<Finance> finances = financeService.findAll();
            Finance finance = new Finance();

            for (int i = 0; i < data.size(); i++) {
                FinanceOrder financeOrder = data.get(i);

                for (int j = 0; j < finances.size(); j++) {
                    finance = finances.get(j);
                    if (finance.getUuid().equals(financeOrder.getFinanceId())) {
                        break;
                    }
                }
                // 取时间
                Date date_now = new Date();
                int days = daysBetween(date_now, financeOrder.getStopTime());
                if (days < 0) {
                    days = 0;
                }
                financeOrder.setName(finance.getName());
                financeOrder.setNameCn(finance.getNameCn());
                financeOrder.setNameEn(finance.getNameEn());
                financeOrder.setDays(days);
                financeOrder.setCloseTimeStr(DateUtils.format(financeOrder.getCloseTime(), DateUtils.DF_yyyyMMdd));
                financeOrder.setCreateTimeStr(DateUtils.format(financeOrder.getCreateTime(), DateUtils.DF_yyyyMMdd));
                financeOrder.setDailyRate(finance.getDailyRate() + " ~ " + finance.getDailyRateMax());
                String path = finance.getImg();
                financeOrder.setImg(path);
            }

            resultObject.setData(data);
            resultObject.setCode("0");
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Exception t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            log.error("error:", t);
        }

        return resultObject;

    }

    /**
     * 收益统计
     */
    @RequestMapping(action + "listSum.action")
    public Object listSum() {

        ResultObject resultObject = new ResultObject();

        try {
            String partyId = SecurityUtils.getCurrentUserId();
            List<FinanceOrder> data = financeOrderService.findByState(partyId, "1");
            FinanceOrder order;
            List<Finance> finances = financeService.findAll();
            Finance finance = new Finance();
            Map<String, Object> map = new HashMap<String, Object>();

            // 金额总数
            double amount_sum = 0;
            // 预计今日盈利
            double today_profit = 0;
            // 已获收益
            double aready_profit = 0;
            // 订单数
            double order_sum = 0;

            if (data != null) {
                for (int i = 0; i < data.size(); i++) {
                    order = data.get(i);
                    order_sum = Arith.add(order_sum, 1d);
                    amount_sum = Arith.add(amount_sum, order.getAmount());
                    aready_profit = Arith.add(aready_profit, order.getProfit());
                    for (int j = 0; j < finances.size(); j++) {
                        finance = finances.get(j);
                        if (finance.getUuid().equals(order.getFinanceId())) {
                            double finance_profit = Arith.mul(finance.getDailyRate(), 0.01d);
                            double get_profit = Arith.mul(finance_profit, order.getAmount());
                            today_profit = Arith.add(today_profit, get_profit);
                            break;
                        }
                    }
                }
            }
            // map.put("amount_sum", amount_sum);
            // map.put("today_profit", today_profit);
            // map.put("aready_profit", aready_profit);
            // map.put("order_sum", order_sum);

            map.put("amount_sum", new BigDecimal(amount_sum).setScale(3, RoundingMode.FLOOR).toPlainString());
            map.put("amountSumValue", new BigDecimal(amount_sum).setScale(3, RoundingMode.FLOOR).toPlainString());
            map.put("today_profit", new BigDecimal(today_profit).setScale(3, RoundingMode.FLOOR).toPlainString());
            map.put("aready_profit", new BigDecimal(aready_profit).setScale(3, RoundingMode.FLOOR).toPlainString());
            map.put("order_sum", new BigDecimal(order_sum).setScale(3, RoundingMode.FLOOR).toPlainString());
            map.put("buyCurrency", "usdt");
            map.put("outputCurrency", "usdt");
            map.put("amountSumValue", new BigDecimal(amount_sum).setScale(3, RoundingMode.FLOOR).toPlainString());
            map.put("areadyProfitValue", new BigDecimal(aready_profit).setScale(3, RoundingMode.FLOOR).toPlainString());
            map.put("todayProfitValue", new BigDecimal(today_profit).setScale(3, RoundingMode.FLOOR).toPlainString());

            resultObject.setData(map);
            resultObject.setCode("0");
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            log.error("error:", t);
        }

        return resultObject;
    }

    /**
     * 理财托管订单-详情
     */
    @RequestMapping(action + "get.action")
    public Object get(HttpServletRequest request) {

        ResultObject resultObject = new ResultObject();
        resultObject = readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        try {
            String order_no = request.getParameter("order_no");
            FinanceOrder data = financeOrderService.findByOrder_no(order_no);
            System.out.println("FinanceOrder data => " + data);
            // 如果不为空 则从理财产品中获取日利率 图片
            resultObject.setData(bulidData(data));
            resultObject.setCode("0");
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            log.error("error:", t);
        }

        return resultObject;
    }

    /**
     * 创建买入理财产品订单
     */
    @RequestMapping(action + "open.action")
    public Object open(HttpServletRequest request) {

        ResultObject resultObject = new ResultObject();
        resultObject = readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
//		String partyId = this.getLoginPartyId();
        String partyId = SecurityUtils.getCurrentUserId();
        try {
            String session_token = request.getParameter("session_token");
            String financeId = request.getParameter("financeId");
            String amount = request.getParameter("amount");

            Object object = this.sessionTokenService.cacheGet(session_token);
            this.sessionTokenService.del(session_token);
            if ((object == null) || (!partyId.equals((String) object))) {
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

            FinanceOrder order = new FinanceOrder();
            order.setPartyId(partyId);
            order.setFinanceId(financeId);
            order.setAmount(Double.valueOf(amount));
            order.setOrderNo(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
            order.setState("1");

            this.financeOrderService.saveCreate(order);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("order_no", order.getOrderNo());
            resultObject.setData(map);
            resultObject.setCode("0");
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            log.error("error:", t);
        }
        return resultObject;
    }

    /**
     * 获取订单
     */
    @RequestMapping(action + "getOpen.action")
    public Object getOpen(HttpServletRequest request) {

        ResultObject resultObject = new ResultObject();
        resultObject = readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        try {

            String financeId = request.getParameter("financeId");
            String amount_temp = request.getParameter("amount");

            Finance finance = financeService.findById(financeId);

            if (StringUtils.isNullOrEmpty(amount_temp)
                    || !StringUtils.isDouble(amount_temp)
                    || Double.valueOf(amount_temp) < 0) {
                throw new BusinessException("金额错误");
            }

            if (finance == null) {
                throw new BusinessException("id错误");
            }

            Double amount = Double.valueOf(amount_temp);

            Map<String, Object> map = new HashMap<String, Object>();
            if (finance != null) {
                String path = finance.getImg();
                map.put("img", path);
            }

            map.put("finance_name", finance.getName());
            map.put("finance_name_en", finance.getNameEn());
            map.put("finance_name_cn", finance.getNameCn());
            map.put("finance_name_kn", finance.getNameKn());
            map.put("finance_name_jn", finance.getNameJn());
            map.put("cycle", finance.getCycle());
            String partyId = SecurityUtils.getCurrentUserId();
            if (!StringUtils.isNullOrEmpty(partyId)) {
                String session_token = sessionTokenService.savePut(partyId);
                map.put("session_token", session_token);
            }

            map.put("amount", amount);
            // 取时间
            Date date = new Date();

            map.put("create_time", DateUtils.format(date, DateUtils.DF_yyyyMMddHHmmss));
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(calendar.DATE, 1);// 把日期往后增加一天.整数往后推
            date = calendar.getTime(); //

            // 起息时间
            map.put("earn_time", DateUtils.format(date, DateUtils.DF_yyyyMMdd));
            calendar.add(calendar.DATE, finance.getCycle() - 1);// 把日期往后增加周期.整数往后推
            date = calendar.getTime();

            // 截止时间
            map.put("stop_time", DateUtils.format(date, DateUtils.DF_yyyyMMdd));

            // 区间
            map.put("investment_min", finance.getInvestmentMin());
            map.put("investment_max", finance.getInvestmentMax());
            DecimalFormat df = new DecimalFormat("#.##");
            map.put("daily_rate", df.format(finance.getDailyRate()) + "~" + df.format(finance.getDailyRateMax()));
            map.put("daily_rate_max", finance.getDailyRateMax());
            map.put("financeId", financeId);
            double rate = Arith.mul(finance.getDailyRate(), 0.01d);
            double rateMax = Arith.mul(finance.getDailyRateMax(), 0.01d);
            map.put("profit_may", Arith.mul(amount, Arith.mul(rate, finance.getCycle())) + "~"
                    + Arith.mul(amount, Arith.mul(rateMax, finance.getCycle())));
            map.put("order_no", DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
            map.put("buyCurrency", finance.getBuyCurrency());
            map.put("outputCurrency", finance.getOutputCurrency());

            resultObject.setData(map);
            resultObject.setCode("0");
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            log.error("error:", t);
        }
        return resultObject;
    }

    /**
     * 赎回理财产品订单
     */
    @RequestMapping(action + "closOrder.action")
    public Object closOrder(HttpServletRequest request) {

        ResultObject resultObject = new ResultObject();
        resultObject = readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        try {
            String id = request.getParameter("id");
            FinanceOrder order = financeOrderService.findById(id);
            CloseDelayThread lockDelayThread = new CloseDelayThread(id, order.getOrderNo(), financeOrderService);
            Thread t = new Thread(lockDelayThread);
            t.start();
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
        } catch (Throwable t) {
            resultObject.setCode("1");
            resultObject.setMsg("程序错误");
            log.error("error:", t);
        }
        return resultObject;
    }

    /**
     * 新线程处理，直接拿到订单锁处理完成后退出
     */
    public class CloseDelayThread implements Runnable {
        private String id;
        private String order_no;
        private FinanceOrderService financeOrderService;

        public void run() {
            try {
                while (true) {
                    if (FinanceOrderLock.add(order_no)) {
                        /**
                         * 提前赎回理财产品需要支付违约金
                         */
                        FinanceOrder order = financeOrderService.findById(id);
                        Finance finance = financeService.findById(order.getFinanceId());
                        // order.setDays(daysBetween(order.getEarn_time(), order.getStop_time()));
                        if ("1".equals(order.getState())) {
                            /**
                             * 扣除违约金
                             */
                            Date date_now = new Date();// 取时间
                            double last_days = daysBetween(date_now, order.getStopTime());
                            if (last_days <= 0) {
                                last_days = 1;
                            }
                            double default_ratio = Arith.mul(finance.getDefaultRatio(), 0.01d);
                            default_ratio = Arith.mul(default_ratio, last_days);
                            double breach_amount = Arith.mul(order.getAmount(), default_ratio);
                            order.setProfit(Arith.sub(0, breach_amount));
                            order.setState("2");

                            this.financeOrderService.saveClose(order);

                        }
                        /**
                         * 处理完退出
                         */
                        break;
                    }
                    ThreadUtils.sleep(500);

                }

            } catch (Throwable t) {
                log.error("error:", t);
            } finally {
                FinanceOrderLock.remove(order_no);
            }

        }

        public CloseDelayThread(String id, String order_no, FinanceOrderService financeOrderService) {
            this.id = id;
            this.order_no = order_no;
            this.financeOrderService = financeOrderService;
        }

    }

    public Map<String, Object> bulidData(FinanceOrder order) throws ParseException {

        System.out.println("order => " + order);

        Finance finance = new Finance();
        finance = financeService.findById(order.getFinanceId());

        DecimalFormat df = new DecimalFormat("#.##");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("img", finance.getImg());
        map.put("daily_rate", df.format(finance.getDailyRate()) + "~" + df.format(finance.getDailyRateMax()));
        map.put("daily_rate_min", finance.getDailyRate());
        map.put("daily_rate_max", finance.getDailyRateMax());
        map.put("create_timeStr", DateUtils.format(order.getCreateTime(), DateUtils.DF_yyyyMMddHHmmss));
        map.put("close_timeStr", DateUtils.format(order.getCloseTime(), DateUtils.DF_yyyyMMddHHmmss));
        /**
         * 此处将截止时间传回
         */
        map.put("stop_timeStr", DateUtils.format(order.getStopTime(), DateUtils.DF_yyyyMMdd));
        map.put("earn_timeStr", DateUtils.format(order.getEarnTime(), DateUtils.DF_yyyyMMdd));
        Date date_now = new Date();// 取时间
        int days = daysBetween(date_now, order.getStopTime());
        if (days < 0) {
            days = 0;
        }
        map.put("days", days);
        double rate = Arith.mul(finance.getDailyRate(), 0.01d);
        double rateMax = Arith.mul(finance.getDailyRateMax(), 0.01d);
        map.put("profit_may", Arith.mul(order.getAmount(), Arith.mul(rate, finance.getCycle())) + "~"
                + Arith.mul(order.getAmount(), Arith.mul(rateMax, finance.getCycle())));

//		{{((data.daily_rate.split(' ~ ')[0]) / 1 * form.amount / 100).toFixed(2)}} ~
//				{{((data.daily_rate.split(' ~ ')[1]) / 1 * form.amount / 100).toFixed(2)}}

        map.put("order_no", order.getOrderNo());
        map.put("amount", order.getAmount());
        map.put("cycle", order.getCycle());
        map.put("id", order.getUuid());
        map.put("name", finance.getName());
        map.put("name_en", finance.getNameEn());
        map.put("name_cn", finance.getNameCn());
        map.put("name_kn", finance.getNameKn());
        map.put("name_jn", finance.getNameJn());
        map.put("buyCurrency", finance.getBuyCurrency());
        map.put("outputCurrency", finance.getOutputCurrency());

        map.put("state", order.getState());
        if ("1".equals(order.getState())) {

            map.put("profit", order.getProfit());

            double last_days = daysBetween(date_now, order.getStopTime());
            if (last_days <= 0) {
                last_days = 1;
            }
            log.info("last_days = " + last_days);
            log.info("finance.getDefault_ratio() = " + finance.getDefaultRatio());
            double default_ratio = Arith.mul(finance.getDefaultRatio(), 0.01d);
            log.info("default_ratio 1 = " + default_ratio);
            default_ratio = Arith.mul(default_ratio, last_days);
            log.info("default_ratio 2 = " + default_ratio);
            double default_amount = Arith.mul(order.getAmount(), default_ratio);
            log.info("default_amount = " + default_amount);
            log.info("order.getAmount() = " + order.getAmount());
            map.put("default_amount", default_amount);


            double principal_amount = Arith.sub(order.getAmount(), default_amount);
            if (principal_amount < 0) {
                principal_amount = 0;
            }
            map.put("principal_amount", principal_amount);

        }
        if ("2".equals(order.getState())) {
            map.put("profit", 0);
            map.put("default_amount", df.format(Arith.sub(0, order.getProfit())));
            map.put("principal_amount", df.format(Arith.add(order.getAmount(), order.getProfit())));
        }
        if ("0".equals(order.getState())) {
            map.put("profit", order.getProfit());
            map.put("default_amount", 0);
            map.put("principal_amount", df.format(Arith.add(order.getAmount(), order.getProfit())));
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

    public ResultObject readSecurityContextFromSession(ResultObject resultObject) {
//		String partyId = SecurityUtils.getCurrentSysUserId();
        String partyId = SecurityUtils.getCurrentUserId();
        if (StringUtils.isNullOrEmpty(partyId)) {
            resultObject.setCode("403");
            resultObject.setMsg("请重新登录");
            return resultObject;
        }
        return resultObject;
    }

}
