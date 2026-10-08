package com.sky.mapper;

import com.sky.dto.EmployeeDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     *
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);


    /**
     * 插入新增员工
     *
     * @param employee  员工对象，包含用户名、姓名、性别、部门等信息
     * @return 受影响的行数, 1即为插入成功, 0即为失败
     */
    int insertNewEmp(Employee employee);

}
