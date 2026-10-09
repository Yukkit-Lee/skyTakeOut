package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")   //knife4j 注解
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }


    @PostMapping
    @ApiOperation(value = "新增员工")
    public Result insertNewEmp(@RequestBody EmployeeDTO employeeDTO) {
        Integer code = employeeService.insertEmp(employeeDTO);
        return code == 1 ? Result.success(code) : Result.error(code.toString());
    }

    @GetMapping("/page")
    @ApiOperation(value = "员工分页查询")
//    public Result showEmpByPage(@RequestParam(required = false) String name,
//                                @RequestParam(defaultValue = "1") Integer page,
//                                @RequestParam(defaultValue = "10") Integer pageSize) {
//        EmployeePageQueryDTO employeePageQueryDTO=new EmployeePageQueryDTO();
//
//        employeePageQueryDTO.setPage(page);
//        employeePageQueryDTO.setPageSize(pageSize);
//        if(name!=null) employeePageQueryDTO.setName(name);
//
//        employeeService
//        return Result.success();
//    }
    public Result<PageResult> showEmpByPage(EmployeePageQueryDTO employeePageQueryDTO) {
        PageResult empInfoPageRes = employeeService.getEmpInfo(employeePageQueryDTO);
        return Result.success(empInfoPageRes);
    }
}
