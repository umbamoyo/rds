package com.example.rdscommon.domain;

import com.example.rdscommon.config.AuditingTimeFields;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Table(indexes = {
        @Index(columnList = "jti", unique = true)
})
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
public class RefreshToken extends AuditingTimeFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Setter
    @Column(nullable = false, length = 400, unique = true)
    private String refreshToken;

    @Setter
    @Column(nullable = false)
    private String jti;

    @Setter
    @Column(nullable = false)
    private String ip;

    @Setter
    @Column(nullable = false)
    private String os;

    @Setter
    @Column(nullable = false)
    private String browser;

    public RefreshToken(String userId, String refreshToken, String jti, String ip, String os, String browser) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.jti = jti;
        this.ip = ip;
        this.os = os;
        this.browser = browser;
    }

    public static RefreshToken of(String userId, String refreshToken, String jti, String ip, String os, String browser){
        return new RefreshToken(userId, refreshToken, jti, ip, os, browser);
    }
}
