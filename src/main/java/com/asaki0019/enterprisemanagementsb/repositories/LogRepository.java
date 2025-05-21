package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.log.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
}
