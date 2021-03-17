package com.mario.dao.bill;

import com.mario.pojo.Bill;


import java.sql.Connection;
import java.util.List;

public interface BillDao {
    /**
     * 增加订单
     * @param connection
     * @param bill
     * @return
     * @throws Exception
     */
    public int add(Connection connection, Bill bill)throws Exception;


    /**
     * 通过查询条件获取供应商列表-模糊查询-getBillList
     * @param connection
     * @param bill
     * @return
     * @throws Exception
     */
    public List<Bill> getBillList(Connection connection, Bill bill)throws Exception;

    /**
     * 通过delId删除Bill
     * @param connection
     * @param delId
     * @return
     * @throws Exception
     */
    public int deleteBillById(Connection connection, String delId)throws Exception;


    /**
     * 通过billId获取Bill
     * @param connection
     * @param id
     * @return
     * @throws Exception
     */
    public Bill getBillById(Connection connection, String id)throws Exception;

    /**
     * 修改订单信息
     * @param connection
     * @param bill
     * @return
     * @throws Exception
     */
    public int modify(Connection connection, Bill bill)throws Exception;

    /**
     * 根据供应商ID查询订单数量
     * @param connection
     * @param providerId
     * @return
     * @throws Exception
     */
    public int getBillCountByProviderId(Connection connection, String providerId)throws Exception;
}
