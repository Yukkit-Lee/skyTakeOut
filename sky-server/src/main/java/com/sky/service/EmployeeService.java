package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import io.swagger.models.auth.In;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    Integer insertEmp(EmployeeDTO employeeDTO);

    PageResult getEmpInfo(EmployeePageQueryDTO employeePageQueryDTO);

    Integer setAccountStatus(Integer status,long id);

    Integer modifyEmpInfo(EmployeeDTO employeeDTO);

    Employee getEmpInfoById(Integer id);
}
