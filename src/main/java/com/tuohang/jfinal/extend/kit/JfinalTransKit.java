package com.tuohang.jfinal.extend.kit;

import com.jfinal.plugin.activerecord.DbKit;


/**
 * JFINAL手动事务
 * @author Lims
 * @date 2015年9月17日
 * @version 1.0
 */
public class JfinalTransKit {
	public static void beginTran() {
		try {
			DbKit.getConfig().setThreadLocalConnection(
					DbKit.getConfig().getConnection());
			DbKit.getConfig().getThreadLocalConnection().setAutoCommit(false);
		} catch (Exception e) {
			throw (new RuntimeException(e));
		}
	}

	public static void commit() {
		try {
			DbKit.getConfig().getThreadLocalConnection().commit();
			DbKit.getConfig().getThreadLocalConnection().setAutoCommit(true);
			DbKit.getConfig().setThreadLocalConnection(null);
		} catch (Exception e) {
			throw (new RuntimeException(e));
		}
	}

	public static void beginTran(String configName) {
		try {
			DbKit.getConfig(configName).setThreadLocalConnection(
					DbKit.getConfig(configName).getConnection());
			DbKit.getConfig(configName).getThreadLocalConnection()
					.setAutoCommit(false);
		} catch (Exception e) {
			throw (new RuntimeException(e));
		}
	}

	public static void commit(String configName) {
		try {
			DbKit.getConfig(configName).getThreadLocalConnection().commit();
			DbKit.getConfig(configName).getThreadLocalConnection()
					.setAutoCommit(true);

			DbKit.getConfig(configName).setThreadLocalConnection(null);
		} catch (Exception e) {
			throw (new RuntimeException(e));
		}
	}

	public static void rollback() {
		try {
			DbKit.getConfig().getThreadLocalConnection().rollback();
			DbKit.getConfig().setThreadLocalConnection(null);
		} catch (Exception e) {
			throw (new RuntimeException(e));
		}
	}

	public static void rollback(String configName) {
		try {
			DbKit.getConfig(configName).getThreadLocalConnection().rollback();
			DbKit.getConfig(configName).setThreadLocalConnection(null);
		} catch (Exception e) {
			throw (new RuntimeException(e));
		}
	}
}
