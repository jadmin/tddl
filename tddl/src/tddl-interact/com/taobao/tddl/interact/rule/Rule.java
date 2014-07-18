package com.taobao.tddl.interact.rule;

import java.util.Map;
import java.util.Set;

import com.taobao.tddl.interact.sqljep.Comparative;

/**
 * ���ֿ�����������
 * ����һ��columnA��columnB?����columnBû�У���ȡcolumnB������ֵ���������Ϣ��ã�ȫ��ɨ��
 * �������columnA��columnC��
 * ��sqlֻ����columnA�����߹���һ
 * ��sqlֻ����columnA��columnC�����߹����
 * 
 * ˳��+�������ƥ�䣬��ƥ�������У��Ҳ����ٰ�ȥ����ѡ��֮��ƥ��
 * 
 * @author linxuan
 *
 */
public interface Rule<T> {

	public class RuleColumn {
		/**
		 * ��optional==true����ѡ��ruleʱ��sql���Բ��������С���ʱ�Ը���ֵ��������
		 */
		public final boolean optional; //
		/**
		 * sql�е������������Ǵ�д��������setter��ʾ�����óɴ�д��
		 */
		public final String key;

		public RuleColumn(String name, boolean optional) {
			this.key = name.toUpperCase();
			this.optional = optional;
		}
	}

	/**
	 * @return ���������Ҫ����
	 */
	Map<String, RuleColumn> getRuleColumns();

	Set<RuleColumn> getRuleColumnSet(); //ͬ��

	/**
	 * ��ֵ����ֵ
	 * @param columnValues ��ֵ�ԡ�������getRuleColumns��ͬ��
	 * @param dynamicExtraContext ��̬�Ķ�������������ThreadLocal�д���ı���ǰ׺
	 * @return ����һ����ֵ�Լ�����
	 */
	T eval(Map<String/*����*/, Object/*��ֵ*/> columnValues, Object outerContext);

	/**
	 * �Ƚ���ƥ��
	 * @param sqlArgs ��SQL��ȡ�����ıȽ��� 
	 * getRuleColumns�����ı�ѡ�У�optional=false��������sqlArgs�����С���ѡ�п���û��
	 *     key��  String����
	 *     value: sql�а�������ȡ���ıȽ���Comparative���Ѿ����˲���
	 * @param ctx ����ִ�е������ġ����ڹ�������ִ��ʱ��������Ҫ��Ϣ�Ĵ��ݡ�����EnumerativeRule��˵���ڿ�������й�����ʱ��
	 *            ����ÿһ��������ֵ���棬ִ�б�����ִ��ʱ����������ֵ�������Ϣ���Ըò������롣
	 * @param outerCtx ��̬�Ķ�������������ThreadLocal�д���ı���ǰ׺
	 * @return ������������͵õ����������������ݡ�
	 */
	Map<T, ? extends Object> calculate(Map<String/*����*/, Comparative> sqlArgs, Object ctx, Object outerCtx);

	/**
	 * ������ÿ�������Ӧ�ĵõ��ý��������ֵ����㣩����
	 */
	Set<T> calculateNoTrace(Map<String/*����*/, Comparative/*�Ƚ���*/> sqlArgs, Object ctx, Object outerCtx);
	
    T calculateVnodeNoTrace(String key, Object ctx, Object outerCtx);
}