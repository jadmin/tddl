package com.taobao.tddl.rule.ruleengine.exception;

import java.sql.SQLException;

public class RuleRuntimeExceptionWrapper extends RuntimeException{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 5542737179921749267L;
	final SQLException sqlException;
	public RuleRuntimeExceptionWrapper(SQLException throwable) {
		super(throwable);
		this.sqlException = throwable;
	}
	public SQLException getSqlException() {
		return sqlException;
	}
	
}
