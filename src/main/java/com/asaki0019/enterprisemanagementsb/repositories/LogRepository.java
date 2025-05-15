package com.asaki0019.enterprisemanagementsb.repositories;

import com.asaki0019.enterprisemanagementsb.entities.log.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findByTimestampBetween(Date start, Date end);

    List<Log> findByUserIdAndTimestampBetween(String userId, Date start, Date end);
}
