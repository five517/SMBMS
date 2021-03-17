package com.mario.dao.user;

import com.mario.dao.BaseDao;
import com.mario.pojo.User;
import com.mysql.cj.util.StringUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao
{
    //得到要登录的用户
    public User getLoginUser(Connection connection, String userCode) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        User user = null;

        if (connection!=null){
            String sql = "select * from smbms_user where userCode=?";
            Object[] params = {userCode};
            //System.out.println(userPassword);
            rs = BaseDao.execute(connection,sql,params,rs,pstm);
            if (rs.next()){
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreationDate(rs.getTimestamp("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getTimestamp("modifyDate"));
            }
            BaseDao.closeResource(null,pstm,rs);
        }
        return user;
    }
    //修改当前用户密码
    //增删改都会影响数据库的变化，所以是返回int类型，说明有几行受到了影响
    public int updatePwd(Connection connection, int id, String userPassword) throws SQLException {
        int updateRows=0;
        PreparedStatement pstm = null;
        if(connection!=null){
            String Sql="UPDATE `smbms_user` SET `userPassword`=? WHERE `id`=? ";
            Object []params={userPassword,id};
            updateRows=BaseDao.execute(connection,Sql,params,pstm);
        }
        BaseDao.closeResource(null,pstm,null);
        return updateRows;
    }
    //根据用户输入的名字或者角色id来查询计算用户数量
    public int getUserCount(Connection connection, String userName, int userRole) throws Exception {
        int count=0;
        PreparedStatement pstm = null;
        ResultSet rs=null;
        if (connection!=null) {
            StringBuffer sql=new StringBuffer();
            sql.append("SELECT COUNT(*) AS count FROM `smbms_user` u,`smbms_role` r WHERE u.`userRole`=r.`id`");
            ArrayList<Object> list = new ArrayList<Object>();//存放可能会放进sql里的参数，就是用来替代?的params
            if(!StringUtils.isNullOrEmpty(userName)){
                sql.append(" and u.username like ?");
                list.add("%"+userName+"%");//模糊查询，index:0
            }
            if(userRole>0){
                sql.append(" and u.userRole = ?");
                list.add(userRole);//index:1
            }
            Object[] params = list.toArray();//转换成数组
            System.out.println("当前的sql语句为------------>"+sql);
            rs = BaseDao.execute(connection, sql.toString(), params, rs, pstm);
            if(rs.next()){
                count=rs.getInt("count");

            }
            BaseDao.closeResource(null,pstm,rs);

        }
        return count;
    }


    //通过用户输入的条件查询用户列表
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) throws Exception {
        List<User> userList = new ArrayList<User>();
        PreparedStatement pstm=null;
        ResultSet rs=null;
        if(connection!=null){
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.userRole = r.id");
            List<Object> list = new ArrayList<Object>();
            if(!StringUtils.isNullOrEmpty(userName)){
                sql.append(" and u.userName like ?");
                list.add("%"+userName+"%");
            }
            if(userRole > 0){
                sql.append(" and u.userRole = ?");
                list.add(userRole);
            }
            sql.append(" order by creationDate DESC limit ?,?");
            currentPageNo = (currentPageNo-1)*pageSize;
            list.add(currentPageNo);
            list.add(pageSize);

            Object[] params = list.toArray();
            System.out.println("sql ----> " + sql.toString());
            rs = BaseDao.execute(connection,sql.toString(),params,rs,pstm);
            while(rs.next()){
                User _user = new User();
                _user.setId(rs.getInt("id"));
                _user.setUserCode(rs.getString("userCode"));
                _user.setUserName(rs.getString("userName"));
                _user.setGender(rs.getInt("gender"));
                _user.setBirthday(rs.getDate("birthday"));
                _user.setPhone(rs.getString("phone"));
                _user.setUserRole(rs.getInt("userRole"));
                _user.setUserRoleName(rs.getString("userRoleName"));
                userList.add(_user);
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return userList;
    }
    //增加用户信息
    public int add(Connection connection, User user) throws Exception {
        PreparedStatement pstm=null;
        int updateNum=0;
        if(connection!=null) {
            String sql = "insert into smbms_user (userCode,userName,userPassword," +
                    "userRole,gender,birthday,phone,address,creationDate,createdBy) " +
                    "values(?,?,?,?,?,?,?,?,?,?)";
            Object[] params = {user.getUserCode(), user.getUserName(), user.getUserPassword(),
                    user.getUserRole(), user.getGender(), user.getBirthday(),
                    user.getPhone(), user.getAddress(), user.getCreationDate(), user.getCreatedBy()};
            updateNum = BaseDao.execute(connection, sql, params, pstm);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateNum;
    }

    //根据用户id删除该用户
    public int deleteUserById(Connection connection, Integer delId) throws Exception {
        PreparedStatement pstm=null;
        int deleteNum=0;
        if(connection!=null){
            String sql="DELETE FROM `smbms_user` WHERE id=?";
            Object[] params={delId};
            deleteNum=BaseDao.execute(connection,sql,params,pstm);
            BaseDao.closeResource(null,pstm,null);
        }
        return deleteNum;
    }

    //通过userId查看当前用户信息
    public User getUserById(Connection connection, String id) throws Exception {
        PreparedStatement pstm=null;
        ResultSet rs=null;
        User user = new User();
        if(connection!=null){
            String sql="select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.id=? and u.userRole = r.id";
            Object[] params={id};
            rs = BaseDao.execute(connection, sql, params, rs, pstm);
            while(rs.next()){
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreationDate(rs.getTimestamp("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getTimestamp("modifyDate"));
                user.setUserRoleName(rs.getString("userRoleName"));
            }
            BaseDao.closeResource(null,pstm,rs);
        }
        return user;
    }

    //修改用户的信息
    public int modify(Connection connection, User user) throws Exception {
        int updateNum = 0;
        PreparedStatement pstm = null;
        if(null != connection){
            String sql = "update smbms_user set userName=?,"+
                    "gender=?,birthday=?,phone=?,address=?,userRole=?,modifyBy=?,modifyDate=? where id = ? ";
            Object[] params = {user.getUserName(),user.getGender(),user.getBirthday(),
                    user.getPhone(),user.getAddress(),user.getUserRole(),user.getModifyBy(),
                    user.getModifyDate(),user.getId()};
            updateNum = BaseDao.execute(connection, sql,params,pstm);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateNum;
    }

}

