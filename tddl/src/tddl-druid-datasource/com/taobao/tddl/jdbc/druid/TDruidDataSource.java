package com.taobao.tddl.jdbc.druid;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.common.lang.StringUtil;
import com.taobao.tddl.common.standard.atom.AtomDbStatusEnum;
import com.taobao.tddl.common.standard.atom.AtomDbTypeEnum;
import com.taobao.tddl.jdbc.druid.common.DruidConstants;
import com.taobao.tddl.jdbc.druid.exception.DruidAlreadyInitException;
import com.taobao.tddl.jdbc.druid.listener.DruidDbStatusListener;

/**
 * 动态数据源，支持数据源参数动态修改
 * 
 * @author qihao
 * 
 */
public class TDruidDataSource extends AbstractTDruidDataSource {

	protected static Log logger = LogFactory.getLog(TDruidDataSource.class);

	private static Map<String, DruidDsConfHandle> cacheConfHandleMap = new HashMap<String, DruidDsConfHandle>();

	private volatile DruidDsConfHandle dsConfHandle = new DruidDsConfHandle();
	

	@Override
	public void init(String appName, String dsKey, String unitName) throws Exception {
		setAppName(appName);
		setDbKey(dsKey);
		setUnitName(unitName);
		init();
	}
	
	public void init() throws Exception {
		String dbName = DruidConstants.getDbNameStr(this.getUnitName(), this.getAppName(), this.getDbKey());
		synchronized (cacheConfHandleMap) {
			DruidDsConfHandle cacheConfHandle = cacheConfHandleMap.get(dbName);
			if (null == cacheConfHandle) {
				//初始化config的管理器
				this.dsConfHandle.init();
				cacheConfHandleMap.put(dbName, dsConfHandle);
				logger.info("create new TAtomDsConfHandle dbName : " + dbName);
			} else {
				dsConfHandle = cacheConfHandle;
				logger.info("use the cache TAtomDsConfHandle dbName : " + dbName);
			}
		}
	}

	/**
	 * 清除掉所有数据源
	 */
	public static void cleanAllDataSource() {
		synchronized (cacheConfHandleMap) {
			for (DruidDsConfHandle handles : cacheConfHandleMap.values()) {
				try {
					handles.destroyDataSource();
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
			cacheConfHandleMap.clear();
		}
	}

	/**
	 * 刷新数据源
	 */
	public void flushDataSource() {
		this.dsConfHandle.flushDataSource();
	}

	/**销毁数据源，慎用
	 * @throws Exception 
	 */
	public void destroyDataSource() throws Exception {
		String dbName = DruidConstants.getDbNameStr(this.getUnitName(), this.getAppName(), this.getDbKey());
		synchronized (cacheConfHandleMap) {
			this.dsConfHandle.destroyDataSource();
			cacheConfHandleMap.remove(dbName);
		}
	}

	public String getAppName() {
		return this.dsConfHandle.getAppName();
	}

	public String getDbKey() {
		return this.dsConfHandle.getDbKey();
	}

	public void setAppName(String appName) throws DruidAlreadyInitException {
		this.dsConfHandle.setAppName(StringUtil.trim(appName));
	}

	public void setDbKey(String dbKey) throws DruidAlreadyInitException {
		this.dsConfHandle.setDbKey(StringUtil.trim(dbKey));
	}

	public void setUnitName(String unitName){
		this.dsConfHandle.setUnitName(unitName);
	}
	
	public String getUnitName(){
		return this.dsConfHandle.getUnitName();
	}

	public AtomDbStatusEnum getDbStatus() {
		return this.dsConfHandle.getStatus();
	}

	public void setDbStatusListeners(List<DruidDbStatusListener> dbStatusListeners) {
		this.dsConfHandle.setDbStatusListeners(dbStatusListeners);
	}

	public void setSingleInGroup(boolean isSingleInGroup) {
		this.dsConfHandle.setSingleInGroup(isSingleInGroup);
	}

	/**=======以下是设置本地优先的配置属性，如果设置了会忽略推送的配置而使用本地的配置=======*/
	public void setPasswd(String passwd) throws DruidAlreadyInitException {
		this.dsConfHandle.setLocalPasswd(passwd);
	}

	public void setDriverClass(String driverClass) throws DruidAlreadyInitException {
		this.dsConfHandle.setLocalDriverClass(driverClass);
	}

	public AtomDbTypeEnum getDbType() {
		return this.dsConfHandle.getDbType();
	}

	public void setSorterClass(String sorterClass) throws DruidAlreadyInitException {
		this.dsConfHandle.setLocalSorterClass(sorterClass);
	}

	public void setConnectionProperties(Map<String, String> map) throws DruidAlreadyInitException {
		this.dsConfHandle.setLocalConnectionProperties(map);
	}

	protected DataSource getDataSource() throws SQLException {
		return this.dsConfHandle.getDataSource();
	}
}
