package com.mario.service.bill;

import com.mario.pojo.Bill;

import java.util.List;

public interface BillService {
    /**
     * 增加订单
     * @param bill
     * @return
     */
    public boolean add(Bill bill);


    /**
     * 通过条件获取订单列表-模糊查询-billList
     * @param bill
     * @return
     */
    public List<Bill> getBillList(Bill bill);

    /**
     * 通过billId删除Bill
     * @param delId
     * @return
     */
    public boolean deleteBillById(String delId);


    /**
     * 通过billId获取Bill
     * @param id
     * @return
     */
    public Bill getBillById(String id);

    /**
     * 修改订单信息
     * @param bill
     * @return
     */
    public boolean modify(Bill bill);
}
