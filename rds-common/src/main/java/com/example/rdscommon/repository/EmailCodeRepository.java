package com.example.rdscommon.repository;

import com.example.rdscommon.domain.EmailCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailCodeRepository extends JpaRepository<EmailCode, String > {
}
