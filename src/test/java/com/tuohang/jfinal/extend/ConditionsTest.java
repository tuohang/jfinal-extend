package com.tuohang.jfinal.extend;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.tuohang.jfinal.extend.core.Condition;

public class ConditionsTest {

	Condition condi = null;

	@Before
	public void beforeTest() {
		// 实例化一个Conditions对象
		// condi = new Conditions(Conditions.FUZZY);
		// 构造方法没有传参表示的是所有字段都按照等于来处理，除非对字段有特别的查询类型设置
		condi = new Condition();
	}

	@Test
	public void test1() {
		condi.setFiledQuery(Condition.FUZZY, "name", "field2"); // 字段name、field2按照模糊查询
		condi.setFiledQuery(Condition.NOT_EQUAL, "filed3"); // 字段filed3按照不等于查询
	}

	@Test
	public void test2() {
		Date startdate = new Date();
		Date enddate = new Date();
		// 针对字段field进行时间范围查询
		condi.setValueQuery(Condition.GREATER_EQUAL, "field", startdate); // 开始时间
		condi.setValueQuery(Condition.LESS_EQUAL, "field", enddate); // 结束时间
	}

	@Test
	public void test3() {
		// 生成条件语句的方法也很简单:如果对象为Model
		// 参数说明：model就是你前台获取到的过滤条件的Model对象
		// alias 表示的是你那条SQL中某张表的别名

		// condi.modelToCondition(model, "alias");
		// 例如： select * from tb_test t inner join tb_dual d on t.id = d.tid
		// where 1 = 1 根据test表的值过滤
		// condi.modelToCondition(testModel, "t"); //t表示的上面tb_test表的别名
	}

}
