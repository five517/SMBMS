package com.mario.dao.bill;

import com.mario.dao.BaseDao;
import com.mario.pojo.Bill;
import com.mysql.cj.util.StringUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class BillDaoImpl  implements BillDao{

    //根据用户输入的值，新增订单表
    public int add(Connection connection, Bill bill) throws Exception {
        int updateNum=0;
        PreparedStatement pstm=null;
        if(connection!=null){
            String sql = "insert into smbms_bill (billCode,productName,productDesc," +
                    "productUnit,productCount,totalPrice,isPayment,providerId,createdBy,creationDate) " +
                    "values(?,?,?,?,?,?,?,?,?,?)";
            Object[] params = {bill.getBillCode(),bill.getProductName(),bill.getProductDesc(),
                    bill.getProductUnit(),bill.getProductCount(),bill.getTotalPrice(),bill.getIsPayment(),
                    bill.getProviderId(),bill.getCreatedBy(),bill.getCreationDate()};
            updateNum = BaseDao.execute(connection, sql, params,pstm);
            BaseDao.closeResource(null, pstm, null);
            System.out.println("dao--------修改行数 " + updateNum);
        }
        return updateNum;
    }

    //根据用户输入的值条件，查询订单表
    public List<Bill> getBillList(Connection connection, Bill bill) throws Exception {
        List<Bill> billList = new ArrayList<Bill>();
        PreparedStatement pstm=null;
        ResultSet rs=null;
        if(connection!=null){
            StringBuffer sql=new StringBuffer();
            sql.append("SELECT b.*,p.proName AS providerName FROM smbms_bill b, smbms_provider p WHERE b.providerId = p.id");
            List<Object> list = new ArrayList<Object>();//用来暂存用户的输入
            if(!StringUtils.isNullOrEmpty(bill.getProductName())){//判断用户是否输入商品名称
                sql.append(" AND b.`productName` LIKE ?");
                list.add("%"+bill.getProductName()+"%");
            }
            if(bill.getProviderId()>0){//判断是否选择了供应商
                sql.append(" AND p.`providerId`=?");
                list.add(bill.getProviderId());
            }if(bill.getIsPayment()>0){//判断是否选择了是否付款
                sql.append(" AND b.`isPayment`=?");
                list.add(bill.getIsPayment());
            }
            Object[] params=list.toArray();
            rs= BaseDao.execute(connection, sql.toString(), params, rs, pstm);
            System.out.println("当前的sql是----->"+sql.toString());
            while(rs.next()){
                Bill _bill = new Bill();//创建一个bill对象存储查询到的属性
                _bill.setId(rs.getInt("id"));
                _bill.setBillCode(rs.getString("billCode"));
                _bill.setProductName(rs.getString("productName"));
                _bill.setProductDesc(rs.getString("productDesc"));
                _bill.setProductUnit(rs.getString("productUnit"));
                _bill.setProductCount(rs.getBigDecimal("productCount"));
                _bill.setTotalPrice(rs.getBigDecimal("totalPrice"));
                _bill.setIsPayment(rs.getInt("isPayment"));
                _bill.setProviderId(rs.getInt("providerId"));
                _bill.setProviderName(rs.getString("providerName"));
                _bill.setCreationDate(rs.getTimestamp("creationDate"));
                _bill.setCreatedBy(rs.getInt("createdBy"));
                billList.add(_bill);
            }
            BaseDao.closeResource(null,pstm,rs);
        }

        return billList;
    }

    //根据订单id删除该订单
    public int deleteBillById(Connection connection, String delId) throws Exception {
        int delNum=0;
        PreparedStatement pstm=null;
        if(connection!=null){
            String sql="DELETE FROM `smbms_bill` WHERE id=?";
            Object[] params={delId};
            delNum = BaseDao.execute(connection, sql, params, pstm);
            BaseDao.closeResource(null,pstm,null);
        }
        return delNum;
    }

    //通过订单id得到该订单的所有信息（正确）
    public Bill getBillById(Connection connection, String id) throws Exception {
        // TODO Auto-generated method stub
        Bill bill = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        if(null != connection){
            String sql = "select b.*,p.proName as providerName from smbms_bill b, smbms_provider p " +
                    "where b.providerId = p.id and b.id=?";
            Object[] params = {id};
            rs = BaseDao.execute(connection,sql, params,rs,pstm);
            if(rs.next()){
                bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setBillCode(rs.getString("billCode"));
                bill.setProductName(rs.getString("productName"));
                bill.setProductDesc(rs.getString("productDesc"));
                bill.setProductUnit(rs.getString("productUnit"));
                bill.setProductCount(rs.getBigDecimal("productCount"));
                bill.setTotalPrice(rs.getBigDecimal("totalPrice"));
                bill.setIsPayment(rs.getInt("isPayment"));
                bill.setProviderId(rs.getInt("providerId"));
                bill.setProviderName(rs.getString("providerName"));
                bill.setModifyBy(rs.getInt("modifyBy"));
                bill.setModifyDate(rs.getTimestamp("modifyDate"));
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return bill;
    }

    //根据用户传递输入的值修改订单表
    public int modify(Connection connection, Bill bill) throws Exception {
        int modifyNum=0;
        PreparedStatement pstm=null;
        if(connection!=null){
            String sql = "update smbms_bill set productName=?," +
                    "productDesc=?,productUnit=?,productCount=?,totalPrice=?," +
                    "isPayment=?,providerId=?,modifyBy=?,modifyDate=? where id = ? ";
            Object[] params = {bill.getProductName(),bill.getProductDesc(),
                    bill.getProductUnit(),bill.getProductCount(),bill.getTotalPrice(),bill.getIsPayment(),
                    bill.getProviderId(),bill.getModifyBy(),bill.getModifyDate(),bill.getId()};
            modifyNum=BaseDao.execute(connection,sql,params,pstm);
            BaseDao.closeResource(null,pstm,null);
        }
        return modifyNum;
    }

    //通过供应商id得到该供应商总订单数(正确）
    public int getBillCountByProviderId(Connection connection, String providerId) throws Exception {
        int billcount=0;
        PreparedStatement pstm=null;
        ResultSet rs=null;
        if(connection!=null){
            String sql="SELECT COUNT(1) as billCount FROM `smbms_bill` WHERE `providerId`=?";
            Object[] params={providerId};
            rs = BaseDao.execute(connection, sql, params, rs, pstm);
            while(rs.next()){
                billcount=rs.getInt("billCount");
            }
            BaseDao.closeResource(null,pstm,rs);
        }
        return billcount;
    }
//    @Test
//    public void test(){
//        BillDaoImpl billDao = new BillDaoImpl();
//        Connection connection=BaseDao.getConnection();
//        int billList=0;
//        try {
//            billList=billDao.deleteBillById(connection,"19");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        BaseDao.closeResource(connection,null,null);
//        System.out.println(billList);
//    }
}
