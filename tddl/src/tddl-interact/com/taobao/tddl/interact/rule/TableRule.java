package com.taobao.tddl.interact.rule;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.tddl.interact.rule.ruleimpl.DbVirtualNodeRule;
import com.taobao.tddl.interact.rule.ruleimpl.GroovyRule;
import com.taobao.tddl.interact.rule.ruleimpl.TableVirtualNodeRule;
import com.taobao.tddl.interact.rule.ruleimpl.WrappedGroovyRule;
import com.taobao.tddl.interact.rule.util.SimpleNamedMessageFormat;
import com.taobao.tddl.interact.rule.virtualnode.DBTableMap;
import com.taobao.tddl.interact.rule.virtualnode.TableSlotMap;

/**
 * һ���߼��������ֿ�ֱ�
 *
 * @author linxuan
 *
 */
public class TableRule extends VirtualTable {
	Log logger = LogFactory.getLog(TableRule.class);
	public void init() {
		//��Ҫ�������λ��
		super.setExtraPackagesStr(extraPackages);
		initDbIndexes();
		initVnodeMap();
		replaceWithParam(this.dbRules, dbRuleParames != null ? dbRuleParames : ruleParames);
		replaceWithParam(this.tbRules, tbRuleParames != null ? tbRuleParames : ruleParames);
		super.setDbShardRules(convertToRuleArray(dbRules, dbNamePattern,tableSlotMap,dbTableMap,false));
		super.setTbShardRules(convertToRuleArray(tbRules, tbNamePattern,tableSlotMap,dbTableMap,true));
		super.init();
	}

	protected final void initDbIndexes() {
		//TODO
	}

	protected static void replaceWithParam(Object[] rules, String[] params) {
		if (params == null || rules == null) {
			return;
		}
		for (int i = 0; i < rules.length; i++) {
			if (rules[i] instanceof String) {
				rules[i] = replaceWithParam((String) rules[i], params);
			}
		}
	}

	private static String replaceWithParam(String template, String[] params) {
		if (params == null || template == null) {
			return template;
		}
		if (params.length != 0 && params[0].indexOf(":") != -1) {
			// ֻҪparams�ĵ�һ�������к���ð�ţ�����Ϊ��NamedParam
			return replaceWithNamedParam(template, params);
		}
		return new MessageFormat(template).format(params);
	}

	private static String replaceWithNamedParam(String template, String[] params) {
		Map<String, String> args = new HashMap<String, String>();
		for (String param : params) {
			int index = param.indexOf(":");
			if (index == -1) {
				throw new IllegalArgumentException("ʹ�����ֻ���ռλ���滻ʧ�ܣ��������á� params:" + Arrays.asList(params));
			}
			args.put(param.substring(0, index).trim(), param.substring(index + 1).trim());
		}
		return new SimpleNamedMessageFormat(template).format(args);
	}

	protected static List<Rule<String>> convertToRuleArray(Object[] rules,String keyPattern,TableSlotMap tableSlotMap,DBTableMap dbTableMap,boolean isTableRule) {
		List<Rule<String>> ruleList = new ArrayList<Rule<String>>(1);
		if (null == rules) {
			//�����������󲻿���ΪtableRule
			if(tableSlotMap!=null&&dbTableMap!=null&&!isTableRule){
			    ruleList.add(new DbVirtualNodeRule(String.valueOf(""), dbTableMap,extraPackagesStr));
			    return ruleList;
			}else{
			    return null;
			}
		}

		for (Object rule : rules) {
			if (keyPattern != null && keyPattern.length() != 0) {
				ruleList.add(new WrappedGroovyRule(String.valueOf(rule), keyPattern,extraPackagesStr));
			} else {
				if(tableSlotMap!=null&&dbTableMap!=null&&isTableRule){
					ruleList.add(new TableVirtualNodeRule(String.valueOf(rule), tableSlotMap,extraPackagesStr));
				}else{
				    ruleList.add(new GroovyRule<String>(String.valueOf(rule),extraPackagesStr));
				}
			}
		}

		return ruleList;
	}

	public void setDbRuleArray(List<String> dbRules) {
		//�����͸�ΪString[],spring���Զ��Զ��ŷָ�����̬��
		dbRules = trimRuleString(dbRules);
		this.dbRules = dbRules.toArray(new String[dbRules.size()]);
	}

	public void setTbRuleArray(List<String> tbRules) {
		//�����͸�ΪString[],spring���Զ��Զ��ŷָ�����̬��
		tbRules = trimRuleString(tbRules);
		this.tbRules = tbRules.toArray(new String[tbRules.size()]);
	}

	public void setDbRules(String dbRules) {
		if (this.dbRules == null) {
			// ���ȼ���dbRuleArray��
			//this.dbRules = dbRules.split("\\|");
			this.dbRules = new String[] { dbRules.trim() }; //�ϵ�|�ָ�����û������������ɻ���
		}
	}

	public void setTbRules(String tbRules) {
		if (this.tbRules == null) {
			// ���ȼ���tbRuleArray��
			//this.tbRules = tbRules.split("\\|");
			this.tbRules = new String[] { tbRules.trim() }; //�ϵ�|�ָ�����û������������ɻ���
		}
	}

	public void setRuleParames(String ruleParames) {
		if (ruleParames.indexOf('|') != -1) {
			// ������|�߷ָ�,��Ϊ��Щ�������ʽ�л��ж���
			this.ruleParames = ruleParames.split("\\|");
		} else {
			this.ruleParames = ruleParames.split(",");
		}
	}

	public void setRuleParameArray(String[] ruleParames) {
		this.ruleParames = ruleParames;
	}

	public void setDbRuleParames(String dbRuleParames) {
		this.dbRuleParames = dbRuleParames.split(",");
	}

	public void setDbRuleParameArray(String[] dbRuleParames) {
		this.dbRuleParames = dbRuleParames;
	}

	public void setTbRuleParames(String tbRuleParames) {
		this.tbRuleParames = tbRuleParames.split(",");
	}

	public void setTbRuleParameArray(String[] tbRuleParames) {
		this.tbRuleParames = tbRuleParames;
	}

	public void setExtraPackages(List<String> extraPackages) {
		this.extraPackages = extraPackages;
	}
	
	public void setTableSlotKeyFormat(String tableSlotKeyFormat) {
		this.tableSlotKeyFormat = tableSlotKeyFormat;
	}
	
	public void setDbNamePattern(String dbKeyPattern) {
		this.dbNamePattern = dbKeyPattern;
	}
	
	public void setTbNamePattern(String tbKeyPattern) {
		this.tbNamePattern = tbKeyPattern;
	}
	
	public void setAllowReverseOutput(boolean allowReverseOutput) {
		this.allowReverseOutput = allowReverseOutput;
	}

	public boolean isDisableFullTableScan() {
		return disableFullTableScan;
	}

	public void setDisableFullTableScan(boolean disableFullTableScan) {
		this.disableFullTableScan = disableFullTableScan;
	}
	
	public void setNeedRowCopy(boolean needRowCopy) {
		this.needRowCopy = needRowCopy;
	}
	
	public void setUniqueKeys(List<String> uniqueKeys) {
		this.uniqueKeys = uniqueKeys;
	}
	
	public void setAllowFullTableScan(boolean allowFullTableScan) {
		this.allowFullTableScan = allowFullTableScan;
	}
	
	public void setActualTopology(Map<String, Set<String>> actualTopology) {
		this.actualTopology = actualTopology;
	}
	
	public void setOuterContext(Map<Object, Object> outerContext) {
		this.outerContext = outerContext;
	}
	
	public void setTableSlotMap(TableSlotMap tableSlotMap) {
		this.tableSlotMap = tableSlotMap;
	}

	public void setDbTableMap(DBTableMap dbTableMap) {
		this.dbTableMap = dbTableMap;
	}
}