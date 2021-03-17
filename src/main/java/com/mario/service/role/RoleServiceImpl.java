package com.mario.service.role;

import com.mario.dao.BaseDao;
import com.mario.dao.role.RoleDao;
import com.mario.dao.role.RoleDaoImpl;
import com.mario.pojo.Role;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

public class RoleServiceImpl  implements RoleService{
    //业务层都会调用dao层，所以我们要引入Dao层；
    private RoleDao roleDao;
    public RoleServiceImpl(){ roleDao=new RoleDaoImpl();}
    //方法
    //Service层一般步骤都是获取连接执行操作，返回结果就行
    public List<Role> getRoleList() {
        Connection connection=null;
        List<Role> roleList=null;
        try {
            connection= BaseDao.getConnection();
            roleList = roleDao.getRoleList(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return roleList;
    }
//    @Test
//    public void  test(){
//        RoleServiceImpl roleService = new RoleServiceImpl();
//        List<Role> roleList=null;
//        roleList=roleService.getRoleList();
//        for (Role role : roleList) {
//            System.out.println(role.getRoleName());
//        }
//    }
}
