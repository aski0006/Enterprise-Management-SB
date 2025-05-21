package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.performance.PerformanceLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 绩效等级仓库接口
 */
@Repository
public interface PerformanceLevelRepository extends JpaRepository<PerformanceLevel, Integer> {
    
    /**
     * 根据等级查询绩效等级
     * @param level 等级
     * @return 绩效等级
     */
    Optional<PerformanceLevel> findByLevel(String level);
    
    /**
     * 根据等级名称查询绩效等级
     * @param name 等级名称
     * @return 绩效等级
     */
    Optional<PerformanceLevel> findByName(String name);
    
    /**
     * 根据分数查询对应的绩效等级
     * @param score 分数
     * @return 绩效等级
     */
    @Query("SELECT pl FROM PerformanceLevel pl WHERE :score BETWEEN pl.minScore AND pl.maxScore")
    Optional<PerformanceLevel> findByScore(@Param("score") Integer score);
} 