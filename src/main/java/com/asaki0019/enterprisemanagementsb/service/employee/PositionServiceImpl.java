package com.asaki0019.enterprisemanagementsb.service.employee;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.entities.employee.Position;
import com.asaki0019.enterprisemanagementsb.enums.ErrorCode;
import com.asaki0019.enterprisemanagementsb.repositories.PositionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 职位管理服务实现类
 */
@Service
public class    PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private static final Logger log = LoggerFactory.getLogger(PositionServiceImpl.class);

    @Autowired
    public PositionServiceImpl(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Override
    public Result<?> getAllPositions(Integer departmentId) {
        log.info("获取职位列表，部门ID：{}", departmentId);
        try {
            List<Position> positions;
            
            if (departmentId != null) {
                // 如果指定了部门ID，过滤该部门的职位
                positions = positionRepository.findAll()
                    .stream()
                    .filter(position -> position.getDepartment() != null && 
                            position.getDepartment().getDepartmentId().equals(departmentId))
                    .collect(Collectors.toList());
            } else {
                // 否则返回所有职位
                positions = positionRepository.findAll();
            }
            
            List<Map<String, Object>> result = positions.stream()
                .map(position -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", position.getPositionId());
                    map.put("title", position.getTitle());
                    map.put("baseSalary", position.getBaseSalary());
                    if (position.getDepartment() != null) {
                        map.put("departmentId", position.getDepartment().getDepartmentId());
                        map.put("departmentName", position.getDepartment().getName());
                    }
                    return map;
                })
                .collect(Collectors.toList());
                
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取职位列表失败", e);
            return Result.failure(ErrorCode.BUSINESS_ERROR, "获取职位列表失败");
        }
    }
} 