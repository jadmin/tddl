package com.taobao.tddl.common.jdbc.conurl;

import java.text.MessageFormat;

import com.alibaba.common.lang.StringUtil;
import com.taobao.tddl.interact.rule.bean.DBType;

/**MYSQL����Դ���ӵ�ַ�ֻ࣬Ҫ����ҪIP,PORT,DBNAME�����renderURL�����������ӵ��ַ���
 * @author qihao
 *
 */
public class MySqlConnectionURL extends  ConnectionURL{

	private String pramStr;
	
	private static MessageFormat urlFormat=new MessageFormat("jdbc:mysql://{0}:{1}/{2}");
	
	public String renderURL() {
		String url=urlFormat.format(new String[] {this.getIp(),this.getPort(),this.getDbName() });
		if(StringUtil.isNotBlank(this.getPramStr())){
			url=url+"?"+pramStr;
		}
		return url;
	}

	public DBType getDbType() {
		return DBType.MYSQL;
	}

	public String getPramStr() {
		return pramStr;
	}

	public void setPramStr(String pramStr) {
		this.pramStr =  StringUtil.trim(pramStr);
	}
}