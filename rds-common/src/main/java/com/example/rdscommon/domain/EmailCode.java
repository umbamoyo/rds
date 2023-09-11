package com.example.rdscommon.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter @Setter @ToString
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class EmailCode {
    @Id
    @Column(length = 100)
    private String userId;

    @Column(nullable = false)
    private String code;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column
    private LocalDateTime createdAt;

    public EmailCode(String userId, String code, LocalDateTime createdAt) {
        this.userId = userId;
        this.code = code;
        this.createdAt = createdAt;
    }

    public static EmailCode of(String userId, String code) {
        return EmailCode.of(userId, code, null);
    }

    public static EmailCode of(String userId, String code, LocalDateTime createdAt) {
        return new EmailCode(userId,code,createdAt);
    }


}
