package com.example.rdscommon.domain;

import com.example.rdscommon.config.AuditingFields;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "nickname", unique = true),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class UserAccount extends AuditingFields {
    @Id
    @Column(length = 100)
    private String userId;
    @Setter
    @Column(nullable = false)
    private String userPassword;

    @Setter
    @Column(length = 15)
    private String nickname;
    @Setter
    private String memo;

    @Setter
    @Column(length = 100)
    private String interLock;   // sns 계정인 경우 사이트 계정과 연통했을 떄 사용


    protected UserAccount() {}

    private UserAccount(String userId, String userPassword, String nickname, String memo, String createdBy) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.memo = memo;
        this.createdBy = createdBy;
        this.modifiedBy = createdBy;
    }

    public static UserAccount of(String userId, String userPassword, String nickname, String memo) {
        return UserAccount.of(userId, userPassword, nickname, memo, null);
    }

    public static UserAccount of(String userId, String userPassword, String nickname, String memo, String createdBy) {
        return new UserAccount(userId, userPassword, nickname, memo, createdBy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount that)) return false;
        return this.getUserId() != null && this.getUserId().equals(that.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getUserId());
    }

}