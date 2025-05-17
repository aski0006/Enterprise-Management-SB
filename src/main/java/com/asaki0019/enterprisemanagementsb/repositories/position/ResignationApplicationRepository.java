package com.asaki0019.enterprisemanagementsb.repositories.position;

import com.asaki0019.enterprisemanagementsb.entities.position.ResignationApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResignationApplicationRepository extends JpaRepository<ResignationApplication, Integer> {
    List<ResignationApplication> findByNameContainingIgnoreCase(String name);

}