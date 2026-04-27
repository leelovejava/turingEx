package com.yami.trading.admin.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yami.trading.common.util.ApplicationUtil;
import com.yami.trading.common.util.DateUtils;
import com.yami.trading.common.util.StringUtils;
import com.yami.trading.common.util.ThreadUtils;
import com.yami.trading.service.syspara.SysparaService;
import com.yami.trading.sys.model.SysLog;
import com.yami.trading.sys.service.SysLogService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BackupUtil {

    private static String KEY = "Roj6#@08SDF87323FG00%jjsd";
    /**
     * 局部备份
     */
    public static String TYPE_PART = "part";
    /**
     * 全局备份
     */
    public static String TYPE_ALL = "all";
    /**
     * 局部+全局备份
     */
    public static String TYPE_BOTH = "both";

    private static Endecrypt endecrypt = new Endecrypt();

    public static void backup(SysLogService sysLogService, SysparaService sysparaService) {
        try {
            log.info("进入全局备份 backup...");
            boolean backup_all_button = sysparaService.find("backup_all_button").getBoolean();
            if (!backup_all_button) {
                return;
            }
            /**
             * 保留n天的备份，之前清除
             */
            String backup_stay_days = sysparaService.find("backup_stay_days").getSvalue();
            String backupType = TYPE_ALL;
            log.info("开始备份...");

            DateFormat sdf = DateUtils.createDateFormat(Constants.DB_BACKUP_TIME_FORMAT);
            String backupName = ApplicationUtil.getProperty("db.database.name") + "_" + sdf.format(new Date());

            executeLinuxCmd("mkdir " + ApplicationUtil.getProperty("db.backup.path"));
            // 开始备份sql
            String backupDB = backupDB(backupName);

            String backResult = executeLinuxCmd(backupDB);
            log.info(backResult);
            // 开始压缩zip
            String zip = zip(backupName);
            log.info(zip);
            String zipResult = executeLinuxCmd(zip);
            log.info(zipResult);
            // 保留n天,删除之前的zip

            String clearBackup = clearBackup(backup_stay_days, backupType);
            log.info(clearBackup);
            String clearBackupResult = executeLinuxCmd(clearBackup);
            log.info(clearBackupResult);
            ThreadUtils.sleep(2000);// 等待备份完成

            handleSftp(sysparaService, ApplicationUtil.getProperty("db.backup.path") + "/" + backupName + ".zip",
                    backupType, backup_stay_days);
            log.info("备份完成...");
        } catch (Exception e) {
            // TODO: handle exception
            log.error(DateUtils.format(new Date(), DateUtils.DF_yyyyMMddHHmmss) + " backup fail e:", e);
            SysLog entity = new SysLog();
            entity.setCreateDate(new Date());
            entity.setOperation("数据库备份失败  e:" + e);
            sysLogService.save(entity);
        }
    }


    /**
     * 备份文件发送到配置的服务
     *
     * @param filePath       指定文件目录
     * @param sysparaService
     */
    public static void handleSftp(SysparaService sysparaService, String filePath, String backupType,
                                  String backupStayDays) throws Exception {
//		String backup_server_param="[{\"ip\":\"127.0.0.1\",\"port\":\"22\",\"username\":\"root\",\"password\":\"g5xkwp8ET2WCTbtb5aAxeq2%2FQsWR3j35MbsW2bpXDhp0NsW%2BNNfzSA%3D%3D\",\"path\":\"/backup\",\"type\":\"part\"},{\"ip\":\"127.0.0.1\",\"port\":\"22\",\"username\":\"root\",\"password\":\"g5xkwp8ET2WCTbtb5aAxeq2%2FQsWR3j35MbsW2bpXDhp0NsW%2BNNfzSA%3D%3D\",\"path\":\"/backup\",\"type\":\"part\"}]";
        String backup_server_param = sysparaService.find("backup_server_param").getSvalue();
        if (StringUtils.isEmptyString(backup_server_param)) {
            return;
        }
        JSONArray jsonArray = JSON.parseArray(backup_server_param);
        Iterator<Object> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            JSONObject next = (JSONObject) iterator.next();

            String type = next.getString("type");
            if (!type.equals(TYPE_BOTH) && !backupType.equals(type)) {
                continue;
            }
            String sftpIp = next.getString("ip");
            int sftpPort = next.getInteger("port");
            String sftpUsername = next.getString("username");
            String sftpPassword = endecrypt.get3DESDecrypt(next.getString("password"), KEY);
            String sftpBackupPath = next.getString("path");
            try {
                sftp(filePath, sftpIp, sftpPort, sftpUsername, sftpPassword, sftpBackupPath, backupStayDays, backupType);
            }catch (Exception e) {
                // TODO: handle exception
                log.error("ip:"+sftpIp+" sftp fail. e:",e);
            }
        }
    }


    /**
     * 备份文件发送到指定服务
     *
     * @param filePath
     * @param sftpIp
     * @param sftpPort
     * @param sftpUsername
     * @param sftpPassword
     * @param sftpBackupPath
     * @throws Exception
     */
    public static void sftp(String filePath, String sftpIp, int sftpPort, String sftpUsername, String sftpPassword,
                            String sftpBackupPath, String backupStayDays, String backupType) throws Exception {
        SFTPUtil sftp = new SFTPUtil(sftpUsername, sftpPassword, sftpIp, sftpPort);
        //1.传输中途中断，2.未连接上
        if(TYPE_ALL.equals(backupType)) {//全局备份
            int times = 0;//次数
            long start = System.currentTimeMillis();
            long end = System.currentTimeMillis();
            long limit = 1000 * 60 * 5;
            while(true) {
                try {
                    sftp.login(SFTPUtil.SFTP);
                    sftp.upload(sftpBackupPath, filePath);
                    break;
                }catch (Exception e) {
                    // TODO: handle exception
                    times++;
                    end = System.currentTimeMillis();
                    if(times>5||(end-start)>limit) {//尝试5分钟，或尝试次数大于五
                        log.error("尝试时间大于五分钟，或尝试次数大于五，无法传输成功",times);
                        throw new RuntimeException(e);
                    }else {
                        log.error("全局备份传输失败，尝试第{}次。。。",times);
                    }
                }finally {
                    sftp.logout();
                }

            }
        }else {
            sftp.login(SFTPUtil.SFTP);
            sftp.upload(sftpBackupPath, filePath);
            sftp.logout();
        }

        // 保留n天删除之前的zip
        String clearBackup = clearBackup(backupStayDays, backupType);
        log.info("sftp:" + clearBackup);
        String clearBackupResult = SFTPUtil.execCmd(sftpIp, sftpUsername,sftpPort, sftpPassword, clearBackup);
        log.info(clearBackupResult);
    }


    /**
     * 清除超时备份
     *
     * @param days 备份保留时间
     * @return
     */
    public static String clearBackup(String days, String backupType) {
//		find /backup/test -mtime +2 -type f -name *.txt -exec rm {} \;
//		StringBuffer cmdBuf = new StringBuffer("find /backup -mmin +120 -type f -name *.zip -exec rm {} \\;");
        StringBuffer cmdBuf = new StringBuffer();
        cmdBuf.append(" find ").append(ApplicationUtil.getProperty("db.backup.path"));
        cmdBuf.append(" -mtime ").append("+" + days);
        String dbName = ApplicationUtil.getProperty("db.database.name") + "_part_";
        if (backupType.equals(TYPE_ALL)) {// 优先处理part，全局的匹配了再处理
            dbName = ApplicationUtil.getProperty("db.database.name") + "_";
        }
        cmdBuf.append(" -type f -name '" + dbName + "*.zip' -exec rm {} \\; ");

        return cmdBuf.toString();
    }


    public static String zip(String backupName) {
//		String zip="zip -m /home/demo7.zip /home/demo7.sql";
        StringBuffer cmdBuf = new StringBuffer();
        cmdBuf.append("zip"); // 客户端命令

        cmdBuf.append(" -m ").append(ApplicationUtil.getProperty("db.backup.path")).append("/").append(backupName)
                .append(".zip");
        cmdBuf.append(" ").append(ApplicationUtil.getProperty("db.backup.path")).append("/").append(backupName)
                .append(".sql");
        return cmdBuf.toString();
    }


    public static String backupDB(String backupName) {
        StringBuffer cmdBuf = new StringBuffer();
        cmdBuf.append(DBTools.formatDBClientCmd("mysqldump", "exe")); // 客户端命令

        cmdBuf.append(" -h ").append(ApplicationUtil.getProperty("db.ip"));
        cmdBuf.append(" -P ").append(ApplicationUtil.getProperty("db.port"));
        cmdBuf.append(" -u").append(ApplicationUtil.getProperty("db.username"));
        cmdBuf.append(" -p")
                .append("'" + ApplicationUtil.getProperty("db.password", KEY) + "'");
        cmdBuf.append(" ").append(ApplicationUtil.getProperty("db.database.name"));
        cmdBuf.append(" > ")//
//        .append(DBTools.formatRuntimeCmdPath(realPath)) // 处理空格
                .append(ApplicationUtil.getProperty("db.backup.path")).append("/").append(backupName).append(".sql");
        return cmdBuf.toString();
    }


    public static String executeLinuxCmd(String cmd) {
//        System.out.println("got cmd job : " + cmd);
        Runtime run = Runtime.getRuntime();
        try {
            Process process = null;
            if(cmd.indexOf(">")!=-1||cmd.indexOf("|")!=-1||cmd.indexOf("\\")!=-1) {
                String[] command = { "/bin/sh", "-c", cmd};
                process = run.exec(command);
            }else {
                process = run.exec(cmd);
            }
//            Process process = run.exec(cmd);
            InputStream in = process.getInputStream();
//            BufferedReader bs = new BufferedReader(new InputStreamReader(in));
            // System.out.println("[check] now size \n"+bs.readLine());
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[8192];
            for (int n; (n = in.read(b)) != -1;) {
                out.append(new String(b, 0, n));
            }
//            System.out.println("job result [" + out.toString() + "]");
            in.close();
            // process.waitFor();
            process.destroy();
            return out.toString();
        } catch (IOException e) {
            log.error("exec cmd fail e:",e);
            e.printStackTrace();
        }
        return null;
    }

}
