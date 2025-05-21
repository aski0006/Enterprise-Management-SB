package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.employee.Department;
import com.asaki0019.enterprisemanagementsb.entities.employee.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, Integer> {
    Position findByTitle(String title);
    
    /**
     * 根据职位名称和部门查询职位
     * @param title 职位名称
     * @param department 部门
     * @return 职位
     */
    Position findByTitleAndDepartment(String title, Department department);
}
