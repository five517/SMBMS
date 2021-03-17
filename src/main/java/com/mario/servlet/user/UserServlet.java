package com.mario.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.mario.pojo.Role;
import com.mario.pojo.User;
import com.mario.service.role.RoleService;
import com.mario.service.role.RoleServiceImpl;
import com.mario.service.user.UserService;
import com.mario.service.user.UserServiceImpl;
import com.mario.util.Constants;
import com.mario.util.PageSupport;
import com.mysql.cj.util.StringUtils;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//实现Servlet复用，实现复用需要提取出方法，然后在doGet函数中调用即可
public class UserServlet extends HttpServlet {
    public void init() throws ServletException {
        // Put your code here
    }
    public UserServlet() {
        super();
    }
    public void destroy() {
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getParameter("method");
        System.out.println("method----> " + method);
        if(method != null && method.equals("add")){
            //增加操作
            this.add(request, response);
        }else if(method != null && method.equals("query")){
            //查询用户操作
            this.query(request, response);
        }else if(method != null && method.equals("getrolelist")){
            //查询用户角色表
            this.getRoleList(request, response);
        }else if(method != null && method.equals("ucexist")){
            //查询当前用户编码是否存在
            this.userCodeExist(request, response);
        }else if(method != null && method.equals("deluser")){
            //删除用户
            this.delUser(request, response);
        }else if(method != null && method.equals("view")){
            //通过用户id得到用户
            this.getUserById(request, response,"userview.jsp");
        }else if(method != null && method.equals("modify")){
            //通过用户id得到用户
            this.getUserById(request, response,"usermodify.jsp");
        }else if(method != null && method.equals("modifyexe")){
            //验证用户
            this.modify(request, response);
        }else if(method != null && method.equals("pwdmodify")){
            //验证用户密码
            this.modifyPwd(request, response);
        }else if(method != null && method.equals("savepwd")){
            //更新用户密码
            this.updatePwd(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
    //修改密码
    private void updatePwd(HttpServletRequest req, HttpServletResponse resp){
        //从session中获得用户id,这里的attribute包括了用户的所用信息
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        //获得新密码
        String newpassword = req.getParameter("newpassword");
        boolean flag= false;
        //判断是否有这个用户是否存在，以及新密码不为空
        if(attribute!=null && !StringUtils.isNullOrEmpty(newpassword)){
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(((User) attribute).getId(), newpassword);
            if (flag) {
                req.setAttribute(Constants.SYS_MESSAGE,"修改密码成功，请退出后重新登录");
                //密码修改成功后移除当前session
                req.getSession().removeAttribute(Constants.USER_SESSION);
            }else{
                req.setAttribute(Constants.SYS_MESSAGE,"密码修改失败请重新输入");
            }
        }else{
            req.setAttribute(Constants.SYS_MESSAGE,"新密码设置错误请重新输入");
        }
        try {
            req.getRequestDispatcher("pwdmodify.jsp").forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //验证旧密码，session中可以获得旧密码，不需要重复去数据库中寻找
    private void modifyPwd(HttpServletRequest req, HttpServletResponse resp){
        //从session中获得用户的旧密码,这里的attribute包括了用户的所用信息
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        //从前端输入的页面中获得输入的旧密码
        String oldpassword = req.getParameter("oldpassword");
        //万能的Map
        Map<String, String> resultMap = new HashMap<String,String>();
        if (attribute==null){//取到的session为空，意味着session过期了
            resultMap.put("result","sessionerror");
        }else if (StringUtils.isNullOrEmpty(oldpassword)){//如果输入的旧密码为空
            resultMap.put("result","sessionerror");
        }else{//session不为空，输入的旧密码也不为空，则取出当前旧密码与之比较
            String userPassword = ((User) attribute).getUserPassword();
            if(oldpassword.equals(userPassword)){
                resultMap.put("result","true");
            }else {
                resultMap.put("result","false");
            }
        }
        try {
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.write(JSONArray.toJSONString(resultMap));
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //用户管理模块页面查询
    //怎么处理
    private  void query(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException{
        //接收前端传来的参数
        String queryUserName = req.getParameter("queryname");
        String temp = req.getParameter("queryUserRole");//从前端传回来的用户角色码不知是否为空或者是有效角色码，所以暂存起来
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole=0;
        //先设置一个默认的用户角色码，若temp为空，则将这个传进sql语句中，这是真正放进sql语句中的角色码
        /*
            if(userRole > 0){
                sql.append(" and u.userRole = ?");
                list.add(userRole);
            }
            这句便不会被执行
         */
        //通过UserServiceImpl得到用户列表,用户数
        UserServiceImpl userService = new UserServiceImpl();
        //通过RoleServiceImpl得到角色表
        RoleService roleService = new RoleServiceImpl();
        List<User> userList=null;//用来存储用户列表
        List<Role> roleList=null;//用来存储角色表
        //设置每页显示的页面容量
        int pageSize=Constants.pageSize;
        //设置当前的默认页码
        int currentPageNo=1;
        //输出控制台，显示参数的当前值
        System.out.println("queryUserName servlet--------"+queryUserName);
        System.out.println("queryUserRole servlet--------"+queryUserRole);
        System.out.println("query pageIndex--------- > " + pageIndex);

        //前端传来的参数若不符合查询sql语句，即如果用户不进行设置，值为空会影响sql查询，需要给它们进行一些约束
        if(queryUserName==null){//这里为空，说明用户没有输入要查询的用户名，则sql语句传值为""，%%，会查询所有记录
            queryUserName="";
        }
        if(temp!=null && !temp.equals("")){
            //不为空，说明前端有传来的用户所设置的userCode，更新真正的角色码
            queryUserRole=Integer.parseInt(temp);//强制转换，前端传递的参数都是默认字符串，要转成int类型
        }

        if(pageIndex!=null){//说明当前用户有进行设置跳转页面
            currentPageNo=Integer.valueOf(pageIndex);
        }

        //有了用户名和用户角色后可以开始查询了，所以需要显示当前查询到的总记录条数
        int totalCount = userService.getUserCount(queryUserName, queryUserRole);
        //根据总记录条数以及当前每页的页面容量可以算出，一共有几页，以及最后一页的显示条数
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(totalCount);
        //可显示的总页数
        int totalPageCount=pageSupport.getTotalPageCount();

        //约束首位页，即防止用户输入的页面索引小于1或者大于总页数
        if(currentPageNo<1){
            currentPageNo=1;
        }else if(currentPageNo>totalPageCount){
            currentPageNo=totalPageCount;
        }
        //有了，待查询条件，当前页码，以及每页的页面容量后，就可以给出每页的具体显示情况了
        userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
        roleList = roleService.getRoleList();
        //得到了用户表与角色表以及各种经过处理后的参数，都存进req中
        req.setAttribute("userList",userList);
        req.setAttribute("roleList",roleList);
        req.setAttribute("queryUserName", queryUserName);
        req.setAttribute("queryUserRole", queryUserRole);
        req.setAttribute("totalPageCount", totalPageCount);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPageNo", currentPageNo);
        //将所得到的的所有req参数送回给前端
        req.getRequestDispatcher("userlist.jsp").forward(req,resp);

    }

    //增加用户
    private void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("当前正在执行增加用户操作");
        //从前端得到页面的请求的参数即用户输入的值
        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");
        //String ruserPassword = req.getParameter("ruserPassword");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");
        //把这些值塞进一个用户属性中
        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setAddress(address);
        user.setGender(Integer.valueOf(gender));
        user.setPhone(phone);
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        user.setUserRole(Integer.valueOf(userRole));
        user.setCreationDate(new Date());
        //查找当前正在登陆的用户的id
        user.setCreatedBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        UserServiceImpl userService = new UserServiceImpl();
        Boolean flag = userService.add(user);
        //如果添加成功，则页面转发，否则重新刷新，再次跳转到当前页面
        if(flag){
            resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
        }else{
            req.getRequestDispatcher("useradd.jsp").forward(req,resp);
        }
    }

    //得到用户角色表
    private void getRoleList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        List<Role> roleList = null;
        RoleService roleService = new RoleServiceImpl();
        roleList = roleService.getRoleList();
        //把roleList转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(roleList));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    //判断当前输入用户编码是否可用，即是否与已经存在的编码发生冲突
    private void userCodeExist(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        //先拿到用户的编码
        String userCode = req.getParameter("userCode");
        //用一个hashmap，暂存现在所有现存的用户编码
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(StringUtils.isNullOrEmpty(userCode)){
            //userCode == null || userCode.equals("")
            //如果输入的这个编码为空或者不存在，说明可用
            resultMap.put("userCode", "exist");
        }else{//如果输入的编码不为空，则需要去找一下是否存在这个用户
            UserService userService = new UserServiceImpl();
            User user = userService.selectUserCodeExist(userCode);
            if(null != user){
                resultMap.put("userCode","exist");
            }else{
                resultMap.put("userCode", "notexist");
            }
        }
        //把resultMap转为json字符串以json的形式输出
        //配置上下文的输出类型
        resp.setContentType("application/json");
        //从response对象中获取往外输出的writer对象
        PrintWriter outPrintWriter = resp.getWriter();
        //把resultMap转为json字符串 输出
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();//刷新
        outPrintWriter.close();//关闭流
    }

    //删除用户，需要当前的Id，来找到这个用户然后删除
    private void delUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String id = req.getParameter("uid");
        Integer delId = 0;
        try{
            delId = Integer.parseInt(id);
        }catch (Exception e) {
            // TODO: handle exception
            delId = 0;
        }
        //需要判断是否能删除成功
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(delId <= 0){
            resultMap.put("delResult", "notexist");
        }else{
            UserService userService = new UserServiceImpl();
            if(userService.deleteUserById(delId)){
                resultMap.put("delResult", "true");
            }else{
                resultMap.put("delResult", "false");
            }
        }

        //把resultMap转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    //通过id得到用户信息
    private void getUserById(HttpServletRequest req, HttpServletResponse resp,String url) throws ServletException, IOException{

        String id = req.getParameter("uid");
        if(!StringUtils.isNullOrEmpty(id)){
            //调用后台方法得到user对象
            UserService userService = new UserServiceImpl();
            User user = userService.getUserById(id);
            req.setAttribute("user", user);
            req.getRequestDispatcher(url).forward(req, resp);
        }
    }

    //修改用户信息
    private void modify(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        //需要拿到前端传递进来的参数
        String id = req.getParameter("uid");;
        String userName = req.getParameter("userName");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        //创建一个user对象接收这些参数
        User user = new User();
        user.setId(Integer.valueOf(id));
        user.setUserName(userName);
        user.setGender(Integer.valueOf(gender));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.valueOf(userRole));
        user.setModifyBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        user.setModifyDate(new Date());

        //调用service层
        UserServiceImpl userService = new UserServiceImpl();
        Boolean flag = userService.modify(user);

        //判断是否修改成功来决定跳转到哪个页面
        if(flag){
            resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
        }else{
            req.getRequestDispatcher("usermodify.jsp").forward(req, resp);
        }

    }




}
