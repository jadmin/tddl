package com.taobao.tddl.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import com.alibaba.common.lang.StringUtil;
import com.taobao.monitor.MonitorLog;
import com.taobao.tddl.common.ConfigServerHelper.AbstractDataListener;
import com.taobao.tddl.common.ConfigServerHelper.DataListener;
import com.taobao.tddl.common.ConfigServerHelper.TDDLConfigKey;
import com.taobao.tddl.common.monitor.SnapshotValuesOutputCallBack;
import com.taobao.tddl.common.monitor.stat.AbstractStatLogWriter.LogCounter;
import com.taobao.tddl.common.monitor.stat.BufferedLogWriter;
import com.taobao.tddl.common.monitor.stat.Log4jLogWriter;
import com.taobao.tddl.common.monitor.stat.MinMaxAvgLogWriter;
import com.taobao.tddl.common.monitor.stat.SoftRefLogWriter;
import com.taobao.tddl.common.util.BoundedConcurrentHashMap;
import com.taobao.tddl.common.util.NagiosUtils;
import com.taobao.tddl.common.util.TStringUtil;

public class Monitor {
	public static final String KEY1 = "TDDL";
	//public static final String KEY1_TABLE = "TDDL_TABLE|";
	//public static final String KEY2_EXEC_SQL = "TDDL_SQL|";
	public static final String KEY2_SYNC = "Sync";
	public static final String KEY2_SYNC_CONTEXT_SQL = "SyncServerContextSql"; //added by huali������ͬ��������ͬ����ʱ�����ø�context��sql�������صĴ����NPE 
	public static final String KEY3_BatchUpdateSyncLog = "BatchUpdateSyncLog";
	public static final String KEY3_BatchDeleteSyncLog = "BatchDeleteSyncLog";
	public static final String KEY3_SyncLogFetched = "SyncLogFetched";
	public static final String KEY3_ReplicationTasksAccepted = "ReplicationTasksAccepted";
	public static final String KEY3_UpdateSlaveRow_dup_all = "UpdateSlaveRow_dup_all";
	public static final String KEY3_PARSE_SQL = "PARSE_SQL_SUCCESS";
	public static final String KEY3_TAIR_HIT_RATING = "TAIR_HIT_RATING";
	public static final String KEY3_GET_DB_AND_TABLES = "GET_DB_ANDTABLES_SUCCESS";//���¼���������õ�ʱ����ܺ�ʱ
	/**
	 * ִ��sql����ʱ�䣬�����������ݿ��ִ��ʱ�����ʱ��
	 */
	public static final String KEY3_EXECUTE_A_SQL_SUCCESS = "EXECUTE_A_SQL_SUCCESS";
	/**
	 * �ܹ�ִ���˼����⣬������
	 */
	public static final String KEY3_EXECUTE_A_SQL_SUCCESS_DBTAB = "EXECUTE_A_SQL_SUCCESS_DBTAB";
	/**
	 * ִ��sql����ʱ�䣬�����������ݿ��ִ��ʱ�����ʱ��
	 */
	public static final String KEY3_EXECUTE_A_SQL_TIMEOUT = "EXECUTE_A_SQL_TIMEOUT";

	public static final String KEY3_EXECUTE_A_SQL_TIMEOUT_DBTAB = "EXECUTE_A_SQL_TIMEOUT_DBTAB";

	public static final String KEY3_EXECUTE_A_SQL_EXCEPTION = "EXECUTE_A_SQL_WITH_EXCEPTION";

	public static final String KEY3_EXECUTE_A_SQL_EXCEPTION_DBTAB = "EXECUTE_A_SQL_WITH_EXCEPTION_DBTAB";

	public static final String KEY2_REPLICATION_SQL = "TDDL_REPLICATION_SQL|";

	/**
	 * ���Ƶ��ӿ�ɹ�������д��ʱ����ܺķ�ʱ��
	 */
	public static final String KEY3_COPY_2_SLAVE_SUCCESS = "COPY_2_SLAVE_SUCCESS";

	/**
	 * ��¼���������񵽸�����ʼ��ִ��֮�������ĵ�ʱ��
	 */
	public static final String KEY3_COPY_2_SLAVE_SUCCESS_TIME_CONSUMING_IN_THREADPOOL = "COPY_2_SLAVE_SUCCESS_TIME_CONSUMING_IN_THREADPOOL";
	/**
	 * ���Ƶ��ӿⳬʱ��Ҫ��¼��ѯ+д��sql���ķѵ�ʱ�䡣
	 */
	public static final String KEY3_COPY_2_SLAVE_TIMEOUT = "COPY_2_SLAVE_TIMEOUT";

	/**
	 * ��¼���������񵽸�����ʼ��ִ��֮�������ĵ�ʱ��
	 */
	public static final String KEY3_COPY_2_SLAVE_TIMEOUT_TIME_CONSUMING_IN_THREADPOOL = "COPY_2_SLAVE_TIMEOUT_TIME_CONSUMING_IN_THREADPOOL";
	/**
	 * ���Ƶ��ӿ��쳣��������������ͻ��Ϊ���³ɹ����������
	 */
	public static final String KEY3_COPY_2_SLAVE_EXCEPTION = "COPY_2_SLAVE_EXCEPTION";

	public static final String KEY3_COPY_2_SLAVE_EXCEPTION_TIME_CONSUMING_IN_THREADPOOL = "COPY_2_SLAVE_EXCEPTION_TIME_CONSUMING_IN_THREADPOOL";

	/**
	 * ʹ��syncCenter�����Ƴɹ��ܺķ�ʱ�䣨����syncCenterǰ����syncCenter������Ӧ��
	 */
	public static final String KEY3_SYNC_VIA_CENTER_SUCCESS = "SYNC_VIA_CENTER_SUCCESS";
	/**
	 * ʹ��syncCenter�����Ƴ�ʱʱ�䣨����syncCenterǰ����syncCenter������Ӧ��
	 */
	public static final String KEY3_SYNC_VIA_CENTER_TIMEOUT = "SYNC_VIA_CENTER_TIMEOUT";
	/**
	 * ʹ��syncCenter�����Ƴ�ʱʱ�������ڶ����еĵȴ�ʱ��
	 */
	public static final String KEY3_SYNC_VIA_CENTER_TIMEOUT_TIME_IN_QUEUE = "SYNC_VIA_CENTER_TIMEOUT_TIME_IN_QUEUE";
	/**
	 * value1��ʹ��syncCenter���ڳ�ʱ��waitForResponseTimeout��,û�еȵ�server���صĴ�����value2���ܴ���
	 */
	public static final String KEY3_SYNC_VIA_CENTER_NO_RESPONSE = "SYNC_VIA_CENTER_NO_RESPONSE";
	
	/**
	 * TDDL ���ݿ⣨��Ͱ��������
	 */
	public static final String KEY3_CONN_NUMBER = "CONN_NUM";
	/**
	 * TDDL ���ݿ⣨��Ͱ����������ʱ��
	 */
	public static final String KEY3_CONN_BLOCKING = "CONN_BLOCKING";
	
	/** changyuan.lh: TDDL ͳ����־ */
	private static final BufferedLogWriter bufferedStatLogWriter = new BufferedLogWriter(
			/* XXX: ��¼�и�����־�� SQL ������־, Key ������ SQL ������ͬ */
			1024, 4096, new Log4jLogWriter(LoggerInit.TDDL_Statistic_LOG));
	private static final BufferedLogWriter atomBufferedStatLogWriter = new BufferedLogWriter(
			/* XXX: ��¼����� SQL ִ�м�¼, Key ������ SQL x ���������� x ���������� */
			2048, 131072, new Log4jLogWriter(LoggerInit.TDDL_Atom_Statistic_LOG));
	private static final BufferedLogWriter matrixBufferedStatLogWriter = new BufferedLogWriter(
			/* XXX: ��¼�߼����Լ�������/������ �� SQL ִ�м�¼, Key ������� SQL x ���������� x ���������� */
			2048, 131072, new Log4jLogWriter(LoggerInit.TDDL_Matrix_Statistic_LOG));
	private static final SoftRefLogWriter connRefStatLogWriter = new SoftRefLogWriter(
			/* XXX: ��¼ Atom ���ӳ��Լ�ҵ���Ͱ�����������¼, Key ������������� x ҵ���Ͱ���� */
			false, new MinMaxAvgLogWriter(", ", LoggerInit.TDDL_Conn_Statistic_LOG));
	
	/**
	 * ��log��ʱ��
	 */
	public static final String KEY3_WRITE_LOG_SUCCESS = "WRITE_LOG_SUCCESS";

	public static final String KEY3_WRITE_LOG_EXCEPTION = "WRITE_LOG_EXCEPTION";

	private static final Log logger = LogFactory.getLog(Monitor.class);
	private static final Logger log = LoggerInit.TDDL_MD5_TO_SQL_MAPPING;
	private static final BoundedConcurrentHashMap<String, String> sqlToMD5Map = new BoundedConcurrentHashMap<String, String>();
	private static MD5Maker md5Maker = MD5Maker.getInstance();
	public static volatile String APPNAME = "TDDL";

	public enum RECORD_TYPE {
		RECORD_SQL, MD5, NONE
	}

	private static volatile RECORD_TYPE recordType = RECORD_TYPE.RECORD_SQL;
	private static volatile int left = 0; //�����������ٸ��ַ�
	private static volatile int right = 0;//�����������ٸ��ַ�
	private static volatile String[] excludsKeys = null;
	private static volatile String[] includeKeys = null; //������
	public static volatile Boolean isStatRealDbInWrapperDs = null;
	//modify by junyu,2012-3-28
	public static volatile boolean isStatAtomSql = true; //Ĭ�ϲ���ӡsql��־
	public static volatile int sqlTimeout=500; //Ĭ�ϳ�ʱ500����
	public static volatile int atomSamplingRate=100;//ֵֻ��Ϊ0-100,��־�Ĳ���Ƶ��
	public static volatile int statChannelMask = 7; //��λ������|BufferedStatLogWriter|StatMonitor
	public static volatile int dumpInterval = -1;
	public static volatile int cacheSize = -1;
	static {
		init();
	}

	//private static AsynWriter<String> inputWriter;
	private static void init() {
		// changyuan.lh: ��ʼ�� TDDL ��־, BufferedStatLogWriter �Ƶ�����
		LoggerInit.initTddlLog();

		if ("TDDL".equals(APPNAME)) {
			logger.warn("��ָ��TDDL�����appName�򲻶���");
			return;
		}
		//DATA_ID_TDDL_CLIENT_CONFIG = DATA_ID_PREFIX + "{0}_tddlconfig"
		//String tddlconfigDataId = ConfigServerHelper.DATA_ID_PREFIX + APPNAME + "_tddlconfig";
		//Object firstFetchedConfigs = ConfigServerHelper.subscribePersistentData(tddlconfigDataId, tddlConfigListener);
		Object firstFetchedConfigs = ConfigServerHelper.subscribeTDDLConfig(APPNAME, tddlConfigListener);
		if (firstFetchedConfigs == null) {
			logger.warn("No tddlconfig received, use default");
		}
		//inputWriter = new AsynWriter<String>(commalog);
		//inputWriter.init();
	}

	public static interface GlobalConfigListener {
		void onConfigReceive(Properties p);
	}

	private static final Set<GlobalConfigListener> globalConfigListeners = new HashSet<GlobalConfigListener>(0);

	public static void addGlobalConfigListener(GlobalConfigListener listener) {
		globalConfigListeners.add(listener);
	}
	public static void removeGlobalConfigListener(GlobalConfigListener listener) {
		globalConfigListeners.remove(listener);
	}

	private static final DataListener tddlConfigListener = new AbstractDataListener() {
		public void onDataReceive(Object data) {
			Properties p = ConfigServerHelper.parseProperties(data, "[tddlConfigListener]");
			if (p == null) {
				logger.warn("Empty tddlconfig");
				return;
			}
			try {
				for (Map.Entry<Object, Object> entry : p.entrySet()) {
					String key = ((String) entry.getKey()).trim();
					String value = ((String) entry.getValue()).trim();
					switch (TDDLConfigKey.valueOf(key)) {
					case statKeyRecordType: {
						RECORD_TYPE old = recordType;
						recordType = RECORD_TYPE.valueOf(value);
						logger.warn("statKeyRecordType switch from [" + old + "] to [" + recordType + "]");
						break;
					}
					case statKeyLeftCutLen: {
						int old = left;
						left = Integer.valueOf(value);
						logger.warn("statKeyLeftCutLen switch from [" + old + "] to [" + left + "]");
						break;
					}
					case statKeyRightCutLen: {
						int old = right;
						right = Integer.valueOf(value);
						logger.warn("statKeyRightCutLen switch from [" + old + "] to [" + right + "]");
						break;
					}
					case statKeyExcludes: {
						String[] old = excludsKeys;
						excludsKeys = value.split(",");
						logger.warn("statKeyExcludes switch from " + Arrays.toString(old) + " to [" + value + "]");
						break;
					}
					case statKeyIncludes: {
						String[] old = includeKeys;
						includeKeys = value.split(",");
						logger.warn("statKeyIncludes switch from " + Arrays.toString(old) + " to [" + value + "]");
						break;
					}
					case StatRealDbInWrapperDs: {
						boolean old = isStatRealDbInWrapperDs;
						isStatRealDbInWrapperDs = Boolean.valueOf(value);
						logger.warn("StatRealDbInWrapperDs switch from [" + old + "] to [" + value + "]");
						break;
					}
					case StatChannelMask: {
						int old = statChannelMask;
						statChannelMask = Integer.valueOf(value);
						logger.warn("statChannelMask switch from [" + old + "] to [" + value + "]");
						break;
					}
					case statDumpInterval: {
						int old = dumpInterval;
						dumpInterval = Integer.valueOf(value);
						statMonitor.setFlushInterval(dumpInterval);
						bufferedStatLogWriter.setFlushInterval(dumpInterval);
						logger.warn("statDumpInterval switch from [" + old + "] to [" + value + "]");
						break;
					}
					case statCacheSize: {
						int old = cacheSize;
						cacheSize = Integer.valueOf(value);
						statMonitor.setMaxKeySize(cacheSize);
						bufferedStatLogWriter.setMaxKeySize(cacheSize);
						logger.warn("statCacheSize switch from [" + old + "] to [" + value + "]");
						break;
					}
					case statAtomSql: {
						boolean old = isStatAtomSql;
						isStatAtomSql = Boolean.parseBoolean(value);
						logger.warn("isStatAtomSql switch from [" + old + "] to [" + value + "]");
						break;
					}
					case sqlExecTimeOutMilli:{
						int old = sqlTimeout;
						sqlTimeout=Integer.valueOf(value);
						logger.warn("sqlTimeout switch from [" + old + "] to [" + value + "]");
						break;
					}
					case atomSqlSamplingRate:{
						int old=atomSamplingRate;
						if(old>0){
							int rate=0;
							if(Integer.valueOf(value) % 100==0){
								rate=100;
							}else{
								rate=Integer.valueOf(value) % 100;//�������100,ȡ����
							}
							atomSamplingRate=rate;
							logger.warn("atomSqlSamplingRate switch from [" + old + "] to [" + atomSamplingRate + "]");
						}else{
							logger.warn("atomSqlSamplingRate will not change,because the value got is nagetive!old value is:"+old);
						}
					}
					default:
						logger.warn("Not cared TDDLConfigKey:" + key);
					}
				}
			} catch (Exception e) {
				logger.error("[tddlConfigListener.onDataReceive]", e);
			}
			
			for (GlobalConfigListener listener : globalConfigListeners) {
				listener.onConfigReceive(p);
			}
		}
	};
	//public static final StatMonitor statMonitor = StatMonitor.getInstance();
	public static final StatMonitor statMonitor = StatMonitor.getInstance();

	private static void addMonitor(String key1, String key2, String key3, long value1, long value2) {
		//һ��ʱ���ڲ���־���ʧ���ʺ�ƽ����Ӧʱ��
		if (KEY3_WRITE_LOG_SUCCESS.equals(key3)) {
			statMonitor.addStat(key1, "", NagiosUtils.KEY_INSERT_LOGDB_FAIL_RATE, 0);
			statMonitor.addStat(key1, "", NagiosUtils.KEY_INSERT_LOGDB_TIME_AVG, value1);
		} else if (KEY3_WRITE_LOG_EXCEPTION.equals(key3)) {
			statMonitor.addStat(key1, "", NagiosUtils.KEY_INSERT_LOGDB_FAIL_RATE, 1);
		}
		//һ��ʱ�����и��Ƶ�ʧ���ʺ�ƽ����Ӧʱ��
		else if (KEY3_COPY_2_SLAVE_SUCCESS.equals(key3)) {
			statMonitor.addStat(key1, "", NagiosUtils.KEY_REPLICATION_FAIL_RATE, 0);
			statMonitor.addStat(key1, "", NagiosUtils.KEY_REPLICATION_TIME_AVG, value1);
		} else if (KEY3_WRITE_LOG_EXCEPTION.equals(key3)) {
			statMonitor.addStat(key1, "", NagiosUtils.KEY_REPLICATION_FAIL_RATE, 1);
		}
	}

	public static String buildTableKey1(String virtualTableName) {
		//return KEY1_TABLE+virtualTableName;
		return "" + virtualTableName; //��֤������null
	}

	/**
	 * ��¼sql
	 * ����¼sql
	 * ��¼ǰ��ȡsql
	 * ��¼���ȡsql
	 * ��¼md5
	 * 
	 * �������
	 * 
	 * @param sql
	 * @return
	 */
	public static String buildExecuteSqlKey2(String sql) {
		if (sql == null) {
			return "null";
		}
		switch (recordType) {
		case RECORD_SQL:
			String s = TStringUtil.fillTabWithSpace(sql);
			if (left > 0) {
				s = StringUtil.left(s, left);
			}
			if (right > 0) {
				s = StringUtil.right(s, right);
			}
			return s;
		case MD5:
			String s1 = TStringUtil.fillTabWithSpace(sql);
			if (left > 0) {
				s1 = StringUtil.left(s1, left);
			}
			if (right > 0) {
				s1 = StringUtil.right(s1, right);
			}
			String md5 = sqlToMD5Map.get(s1);
			if (md5 != null) {
				return md5;
			} else {
				String sqlmd5 = md5Maker.getMD5(s1);
				StringBuilder sb = new StringBuilder();
				sb.append("[md5]").append(sqlmd5).append(" [sql]").append(s1);
				log.warn(sb.toString());
				sqlToMD5Map.put(s1, sqlmd5);
				return sqlmd5;
			}
		case NONE:
			return "";
		default:
			throw new IllegalArgumentException("������Ҫ��ļ�¼log����! " + recordType);
		}

	}

	public static String buildExecuteDBAndTableKey1(String realDSKey, String realTable) {
		StringBuilder sb = new StringBuilder();
		sb.append(KEY1).append("|").append(realDSKey).append("|").append(realTable);
		return sb.toString();
	}

	/**
	 * ���ݸ��ƹ�������Ҫ�õ���sql��key
	 * 
	 * @param sql
	 * @return
	 */
	public static String buildReplicationSqlKey2(String sql) {
		return buildExecuteSqlKey2(sql);
	}

	/**
	 * @param key1 һ�����߼�������appname��
	 * @param key2 һ����SQL
	 * @param key3 һЩ�ɹ���ʧ�ܡ���ʱ�������ʵȱ�־
	 * @param value1 ִ��ʱ��
	 * @param value2 ����
	 */
	public static void add(String key1, String key2, String key3, long value1, long value2) {
		if (isExclude(key1, key2, key3)) {
			return;
		}
		if ((statChannelMask & 4) == 4) { // 100
			MonitorLog.addStat(key1, "", key3, value1, value2); // ������־��ʱ����
		}
		if ((statChannelMask & 2) == 2) { // 010
			bufferedStatLogWriter.stat(key2, key1, key3, value2, value1); //
		}
		if ((statChannelMask & 1) == 1) { // 001
			addMonitor(key1, key2, key3, value1, value2); // ƽ����Ӧʱ��ȶ�̬���Nagois
		}
	}
	
	public static void atomSqlAdd(String key1,String key2,String key3,String key4,String key5,String key6,long value1,long value2){
		// changyuan.lh XXX: ���˳����  key1(sql), key2(group), attach1, attach2, attach3, key3(flag)
		atomBufferedStatLogWriter.write(new Object[] { key2, key1, key3 }, 
				new Object[] { key2, key1, key4, key5, key6, key3 }, new long[] { value2, value1 });
	}
	
	public static void matrixSqlAdd(String key1,String key2,String key3,long value1,long value2){
		matrixBufferedStatLogWriter.stat(key2, key1, key3, value2, value1);
	}
	
	// ���һ��ͳ�ƶ���, ���ÿ���ֱ������
	public static LogCounter connStat(String obj1, String obj2, String obj3) {
		Object[] objs = new Object[] { obj1, obj2, obj3 };
		return connRefStatLogWriter.getCounter(objs, objs);
	}
	
	private final static PositiveAtomicCounter pc=new PositiveAtomicCounter();
	public static boolean isSamplingRecord(){
		int ra=pc.incrementAndGet()%100;
		if(ra<Monitor.atomSamplingRate){
			return true;
		}else{
			return false;
		}
	}

	private static boolean isExclude(String key1, String key2, String key3) {
		if (excludsKeys == null || excludsKeys.length == 0)
			return false;
		for (String exclude : excludsKeys) {
			if (key1.indexOf(exclude) != -1 || key2.indexOf(exclude) != -1 || key3.indexOf(exclude) != -1)
				return true;
		}
		return false;
	}

	public static boolean isInclude(String sql) {
		if (includeKeys != null && includeKeys.length != 0) { // ���ڰ�����
			boolean discard = true;
			for (String whiteItem : includeKeys) {
				if (sql.indexOf(whiteItem) != -1) {
					discard = false;
					break;
				}
			}
			if (discard) {
				return false; // ���ڰ������У��������־���Լ�����־��
			}
		}
		return true;
	}

	public static void setAppName(String appname) {
		if (appname != null) {
			APPNAME = appname;
			init();
		}
	}

	public static synchronized void addSnapshotValuesCallbask(SnapshotValuesOutputCallBack callbackList) {
		statMonitor.addSnapshotValuesCallbask(callbackList);
	}

	public static synchronized void removeSnapshotValuesCallback(SnapshotValuesOutputCallBack callbackList) {
		statMonitor.removeSnapshotValuesCallback(callbackList);
	}
}