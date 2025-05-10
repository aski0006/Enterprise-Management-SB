package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.position.OnboardingApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OnboardingApplicationRepository extends JpaRepository<OnboardingApplication, Integer> {
    @Query("SELECT oa FROM OnboardingApplication oa WHERE LOWER(CONCAT(oa.firstName, ' ', oa.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<OnboardingApplication> findByNameContaining(String name);
}