package com.tuohang.jfinal.extend.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

/**
 * Jfinal工具类通用方法
 * @author Lims
 * @date 2015年9月11日
 * @version 1.0
 */
public class Common {
	/**
	 * 将Model类转换为Map modelToMap
	 * 
	 * @param 参数说明
	 * @return 返回对象
	 * @Exception 异常对象
	 */
	public static Map<String, Object> modelToMap(Model<?> model) {
		Map<String, Object> map = new HashMap<String, Object>();
		String[] names = model.getAttrNames();
		for (String str : names) {
			map.put(str, model.get(str));
		}
		return map;
	}

	/**
	 * 将Record转换成Map recordToMap
	 * 
	 * @param 参数说明
	 * @return 返回对象
	 * @Exception 异常对象
	 */
	public static Map<String, Object> recordToMap(Record record) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (record != null) {
			String[] columns = record.getColumnNames();
			for (String col : columns) {
				map.put(col, record.get(col));
			}
		}
		return map;
	}

	/**
	 * 判断对象或对象数组中每一个对象是否为空: 对象为null，字符序列长度为0，集合类、Map为empty
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isNullOrEmpty(Object obj) {
		if (obj == null) {
			return true;
		} else if (obj instanceof String && (obj.equals(""))) {
			return true;
		} else if (obj instanceof Short && ((Short) obj).shortValue() == 0) {
			return true;
		} else if (obj instanceof Integer && ((Integer) obj).intValue() == 0) {
			return true;
		} else if (obj instanceof Double && ((Double) obj).doubleValue() == 0) {
			return true;
		} else if (obj instanceof Float && ((Float) obj).floatValue() == 0) {
			return true;
		} else if (obj instanceof Long && ((Long) obj).longValue() == 0) {
			return true;
		} else if (obj instanceof Boolean && !((Boolean) obj)) {
			return true;
		} else if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
			return true;
		} else if (obj instanceof Map && ((Map) obj).isEmpty()) {
			return true;
		} else if (obj instanceof Object[] && ((Object[]) obj).length == 0) {
			return true;
		}
		return false;
	}

}
