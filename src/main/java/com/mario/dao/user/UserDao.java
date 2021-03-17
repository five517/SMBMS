package com.mario.dao.user;

import com.mario.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    //得到登录的用户
    //,String userPassword
    public User getLoginUser(Connection connection, String userCode) throws SQLException;

    //修改当前用户密码
    //增删改都会影响数据库的变化，所以是返回int类型，说明有几行受到了影响
    public int updatePwd(Connection connection,int id,String userPassword)throws SQLException;

    //根据用户输入的名字或者角色id来查询计算用户数量
    public int getUserCount(Connection connection, String userName, int userRole)throws Exception;

    //通过用户输入的条件查询用户列表
    public List<User> getUserList(Connection connection,String userName,int userRole,int currentPageNo,int pageSize) throws  Exception;

    //增加用户信息
    public  int add(Connection connection,User user) throws Exception;

    //通过用户id删除用户信息
    public int deleteUserById(Connection connection, Integer delId)throws Exception;

    //通过userId查看当前用户信息
    public User getUserById(Connection connection, String id)throws Exception;
    //修改用户信息
    public int modify(Connection connection, User user)throws Exception;


}
