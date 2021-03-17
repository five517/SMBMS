package com.mario.dao.provider;

import com.mario.pojo.Provider;

import java.sql.Connection;
import java.util.List;

public interface ProviderDao {
    /**
     * 增加供应商
     * @param connection
     * @param provider
     * @return
     * @throws Exception
     */
    public int add(Connection connection, Provider provider)throws Exception;


    /**
     * 通过供应商名称、编码获取供应商列表-模糊查询-providerList
     * @param connection
     * @param proName
     * @return
     * @throws Exception
     */
    public List<Provider> getProviderList(Connection connection, String proName, String proCode)throws Exception;

    /**
     * 通过proId删除Provider
     * @param delId
     * @return
     * @throws Exception
     */
    public int deleteProviderById(Connection connection, String delId)throws Exception;


    /**
     * 通过proId获取Provider
     * @param connection
     * @param id
     * @return
     * @throws Exception
     */
    public Provider getProviderById(Connection connection, String id)throws Exception;

    /**
     * 修改用户信息
     */
    public int modify(Connection connection, Provider provider)throws Exception;
}
