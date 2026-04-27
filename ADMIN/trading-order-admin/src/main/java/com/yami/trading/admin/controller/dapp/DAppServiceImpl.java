package com.yami.trading.admin.controller.dapp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yami.trading.bean.constans.WalletConstants;
import com.yami.trading.bean.data.domain.Realtime;
import com.yami.trading.bean.model.*;
import com.yami.trading.bean.syspara.domain.Syspara;
import com.yami.trading.common.constants.Constants;
import com.yami.trading.common.exception.BusinessException;
import com.yami.trading.common.util.*;
import com.yami.trading.security.common.bo.UserInfoInTokenBO;
import com.yami.trading.security.common.enums.SysTypeEnum;
import com.yami.trading.security.common.manager.PasswordCheckManager;
import com.yami.trading.security.common.manager.PasswordManager;
import com.yami.trading.security.common.manager.TokenStore;
import com.yami.trading.service.WalletService;
import com.yami.trading.service.data.DataService;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.service.system.LogService;
import com.yami.trading.service.user.UserDataService;
import com.yami.trading.service.user.UserRecomService;
import com.yami.trading.service.user.UserService;
import com.yami.trading.sys.model.SysUser;
import com.yami.trading.sys.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.utils.Convert;

@Service
@Transactional
public class DAppServiceImpl implements DAppService {
	@Autowired
	private UserService userService;
	@Autowired
	protected SysparaService sysparaService;
	@Autowired
	private WalletService walletService;
	@Autowired
	private LogService logService;
	@Autowired
	private UserRecomService userRecomService;
	@Autowired
	private UserDataService userDataService;
//	@Autowired
//	private AutoMonitorWalletService autoMonitorWalletService;
	//todo
//	private AutoMonitorWithdrawService autoMonitorWithdrawService;
	@Autowired
	private SysUserService sysUserService;
//	private RoleService roleService;
//	private AutoMonitorDAppLogService autoMonitorDAppLogService;
//	private AutoMonitorPoolDataService autoMonitorPoolDataService;
//	private MiningConfigService miningConfigService;
//	private MiningService miningService;
	@Autowired
	protected DataService dataService;
//	protected ActivityOrderService activityOrderService;
//	@Autowired
//	protected TelegramBusinessMessageService telegramBusinessMessageService;
//	@Autowired
//	protected DAppUserDataSumService dAppUserDataSumService;
//	@Autowired
//	protected DAppAccountService dAppAccountService;
//	@Autowired
//	protected Erc20RemoteService erc20RemoteService;
//	@Autowired
//	protected Erc20Service erc20Service;
//	@Autowired
//	protected EtherscanRemoteService etherscanRemoteService;
//	@Autowired
//	protected AutoMonitorAddressConfigService autoMonitorAddressConfigService;
//	@Autowired
//	protected NodeRpcBusinessService nodeRpcBusinessService;
//	protected AutoMonitorWithdrawCollectionService autoMonitorWithdrawCollectionService;
//	protected PledgeOrderService pledgeOrderService;
//	protected AutoMonitorPoolMiningDataService autoMonitorPoolMiningDataService;
//	@Autowired
//	protected EtherscanService etherscanService;
	
	private Logger logger = LoggerFactory.getLogger(DAppServiceImpl.class);

	@Autowired
	private TokenStore tokenStore;
	@Autowired
	private PasswordCheckManager passwordCheckManager;
	@Autowired
	private PasswordManager passwordManager;
	@Autowired
	private PasswordEncoder passwordEncoder;

	// todo
//	@Override
//	public List<MiningPledgeConfig> getPledgeConfig() {
//		MiningConfig config = miningConfigService.getHoldConfig();
//		return getRecomRate(config);
//	}
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public User saveLogin(String from, String code, String ip) {

		User user = userService.findByUserName(from);

		// 已经存在的用户，直接返回登录成功
		if (user != null) {
			// 登录日志
			Log log = new Log();
			log.setCategory(Constants.LOG_CATEGORY_SECURITY);
			log.setLog("用户登录,ip[" + ip + "]");
			log.setUserId(user.getUserId());
			log.setUsername(from);
			logService.save(log);
			//todo
//			User.setLogin_ip(ip);
//			User.setLast_loginTime(new Date());
//			this.UserService.update(User);


			return user;
		}

		// 第一次登录，则自动注册
		User User_reco = this.userService.findUserByUserCode(code);
		// todo
//		if ("true".equals(sysparaService.find("register_need_usercode").getSvalue())) {
//			if (User_reco == null || !User_reco.getEnabled()) {
//				throw new BusinessException("code error");
//			}
//		}

		int ever_user_level_num = this.sysparaService.find("ever_user_level_num").getInteger();
		int ever_user_level_num_custom = this.sysparaService.find("ever_user_level_num_custom").getInteger();

		//注册
		String username = from;
		String password = "68j33h456";
		String usercode = "";
		// 注册类型：1/手机；2/邮箱；3/用户名；
		int type = 5;

		user = userService.register(username, passwordEncoder.encode(password), usercode, type,false);



		/**
		 * 登录日志
		 */
		Log log = new Log();

		log.setCategory(Constants.LOG_CATEGORY_SECURITY);
		log.setLog("用户登录,ip[" + ip + "]");
		log.setUserId(user.getUserId().toString());
		log.setUsername(from);
		logService.save(log);
		return user;
	}

	@Override
	public int check(String address) {
		return 0;
	}

	@Override
	public int saveApprove(String from, String to) {
		return 0;
	}

	@Override
	public Double getBalance(String from) {
		return null;
	}

	@Override
	public void saveExchange(String UserId, String address, double value) {

	}

	@Override
	public void saveExchangeCollection(String from) {

	}

	@Override
	public List<Map<String, Object>> getExchangeLogs(int pageNo, int pageSize, String address, String action) {
		return null;
	}

	@Override
	public Map<String, Object> poolData() {
		return null;
	}

	@Override
	public Map<String, Object> poolMiningData() {
		return null;
	}

	@Override
	public Map<String, Object> getProfit(String from) {
		return null;
	}

	@Override
	public void approveAdd(String from, String hash, boolean status) {

	}

	@Override
	public Map<String, String> getActivity(String from, String UserId) {
		return null;
	}

	@Override
	public void saveActivity(String from, String activityId) {

	}

	@Override
	public Map<String, Object> share(User User) {
		return null;
	}

	@Override
	public double exchangeFee(String from, double volume) {
		return 0;
	}

	@Override
	public List<Map<String, Object>> getNoticeLogs() {
		return null;
	}

	@Override
	public Map<String, Object> getApproveGasAbout(String from) {
		return null;
	}

	@Override
	public boolean checkNodeAddress(String address) {
		return false;
	}

	@Override
	public int checkApproveChainBlock(User User) {
		return 0;
	}

	@Override
	public String ownApproveAddress(String from) {
		return null;
	}

	@Override
	public void checkChainApprove(User User) {

	}


}
