package com.alipay.simplehbase.myrecord.test.hql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.alipay.simplehbase.exception.SimpleHBaseException;
import com.alipay.simplehbase.myrecord.MyRecord;
import com.alipay.simplehbase.myrecord.MyRecordRowKey;
import com.alipay.simplehbase.myrecord.test.TestMyRecord;

public class TestSpecial extends TestMyRecord {

    //hbase默认值为空字节数组。
    @Test
    public void defaultIsEmptyBytes() {
        put("id=0,name=aaa");
        put("id=1");
        put("id=2,name=ccc");

        String hql = "select where name less #name#";
        Map<String, Object> para = new HashMap<String, Object>();

        para.put("name", "ccc");
        List<MyRecord> myRecordList = simpleHbaseClient.findObjectListByRawHql(
                new MyRecordRowKey(0), new MyRecordRowKey(100), MyRecord.class,
                hql, para);
        Assert.assertTrue(myRecordList.size() == 2);
    }

    //var参数必须有值。
    @Test(expected = SimpleHBaseException.class)
    public void nullParaValue() {
        String hql = "select where name less #name#";
        Map<String, Object> para = new HashMap<String, Object>();
        simpleHbaseClient.findObjectListByRawHql(new MyRecordRowKey(0),
                new MyRecordRowKey(100), MyRecord.class, hql, para);
    }

    //constant参数必须有值。
    @Test(expected = SimpleHBaseException.class)
    public void nullParaConstant() {
        String hql = "select where age less \"\"";
        Map<String, Object> para = new HashMap<String, Object>();
        simpleHbaseClient.findObjectListByRawHql(new MyRecordRowKey(0),
                new MyRecordRowKey(100), MyRecord.class, hql, para);
    }

    //不能处理负数。
    @Test
    public void cannotHandleNegative() {
        put("id=0,age=100");
        put("id=1,age=0");
        put("id=2,age=-100");

        String hql = "select where age greaterequal \"0\"";
        List<MyRecord> myRecordList = simpleHbaseClient.findObjectListByRawHql(
                new MyRecordRowKey(0), new MyRecordRowKey(100), MyRecord.class,
                hql, null);
        //should be 2, but result is 3.
        Assert.assertTrue(myRecordList.size() == 3);
    }
}
