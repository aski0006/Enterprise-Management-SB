package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.salary.BonusScheme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 奖金方案Repository
 */
@Repository
public interface BonusSchemeRepository extends JpaRepository<BonusScheme, String> {
    
    /**
     * 根据方案编码查询奖金方案
     * @param schemeCode 方案编码
     * @return 奖金方案
     */
    Optional<BonusScheme> findBySchemeCode(String schemeCode);
    
    /**
     * 根据部门查询奖金方案
     * @param department 部门
     * @return 奖金方案列表
     */
    List<BonusScheme> findByDepartmentOrDepartment(String department, String allDepartment);
    
    /**
     * 根据年份查询奖金方案
     * @param year 年份
     * @return 奖金方案列表
     */
    List<BonusScheme> findByYear(String year);
    
    /**
     * 根据部门和年份查询奖金方案
     * @param department 部门
     * @param allDepartment 全部部门标识
     * @param year 年份
     * @return 奖金方案列表
     */
    List<BonusScheme> findByDepartmentInAndYear(List<String> departments, String year);
    
    /**
     * 分页查询奖金方案
     * @param department 部门
     * @param year 年份
     * @param pageable 分页信息
     * @return 奖金方案分页
     */
    @Query("SELECT b FROM BonusScheme b WHERE " +
           "(:department IS NULL OR :department = '' OR b.department = :department OR b.department = '全部部门') AND " +
           "(:year IS NULL OR :year = '' OR b.year = :year)")
    Page<BonusScheme> findByDepartmentAndYearWithPagination(
            @Param("department") String department,
            @Param("year") String year,
            Pageable pageable);
    
    /**
     * 查询所有不同的年份
     * @return 年份列表
     */
    @Query("SELECT DISTINCT b.year FROM BonusScheme b ORDER BY b.year DESC")
    List<String> findDistinctYears();
} 