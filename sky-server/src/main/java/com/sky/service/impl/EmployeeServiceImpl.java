package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private JwtProperties jwtProperties;
    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对

        // Spring提供的工具类直接转换MD5
        password=DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }


    @Override
    public Integer insertEmp(EmployeeDTO employeeDTO) {
        //  1.现针对前端传过来的数据做校验
        //  .....
        Employee newEmp=new Employee();

        //从DTO对象直接get过来属性（直接拷贝属性 前提是属性名相同）
        BeanUtils.copyProperties(employeeDTO,newEmp);

        //service层进行校验属性添加:
        newEmp.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));

        newEmp.setStatus(StatusConstant.ENABLE);

        newEmp.setCreateTime(LocalDateTime.now());
        newEmp.setUpdateTime(LocalDateTime.now());

        newEmp.setCreateUser(BaseContext.getCurrentId());
        newEmp.setUpdateUser(BaseContext.getCurrentId());

        int msgCode = employeeMapper.insertNewEmp(newEmp);

        return  msgCode;
    }

    @Override
    public PageResult getEmpInfo(EmployeePageQueryDTO employeePageQueryDTO) {
        //指定DTO默认数值
        if(employeePageQueryDTO.getPageSize()==0)
            employeePageQueryDTO.setPageSize(10);

        //利用PageHelper 开始分页查询(底层通过threadLocal传递参数拼接limit)
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());

        //PageHelper要求mapper层返回一个Page对象
        Page<Employee> empInfoList = employeeMapper.getEmpInfoList(employeePageQueryDTO);

        PageResult pageResult=new PageResult(empInfoList.getTotal(),empInfoList.getResult());
        return pageResult;
    }

    @Override
    public Integer setAccountStatus(Integer status, long id) {

        //用builder 创建一个只填了部分字段的 Employee
        Employee empBuild = Employee.builder()
                .status(status)
                .id(id)
                .updateTime(LocalDateTime.now())
                .build();

        //配合mapper接口动态SQL更新对应字段即可
        return employeeMapper.updateEmpInfo(empBuild);
    }

    @Override
    public Integer modifyEmpInfo(EmployeeDTO employeeDTO) {
        Employee updateEmp=new Employee();
        BeanUtils.copyProperties(employeeDTO,updateEmp);

        updateEmp.setUpdateTime(LocalDateTime.now());
        updateEmp.setUpdateUser(BaseContext.getCurrentId());

        return employeeMapper.updateEmpInfo(updateEmp);
    }

    @Override
    public Employee getEmpInfoById(Integer id) {
        Employee emp=employeeMapper.getEmpInfoById(id);
        emp.setPassword("******");  //前端回显把密码隐藏
        return emp;
    }

}
