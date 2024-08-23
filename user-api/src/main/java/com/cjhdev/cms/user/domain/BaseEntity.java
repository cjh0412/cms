package com.cjhdev.cms.user.domain;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
// Auditing을 위한 엔터티 상속 어노테이션
@MappedSuperclass
// @EntityListeners : JPA CRUL 이벤트가 발생시 특정 코드 실행
// AuditingEntityListener.class :  @CreatedDate, LastModifiedDate, CreatedBy, LastModifiedBy 를 자동으로 넣어주는 기능
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;
}
