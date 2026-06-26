package com.ust.pos.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommonDto {
    private Long id;
    private String identifier;
    private String message;
    private boolean success = true;
    private boolean status = true;
    @Column(updatable = false)
    private String createdBy;
    @Column(updatable = false)
    private LocalDateTime createdOn;
    private String modifiedBy;
    private LocalDateTime modifiedOn;
    @Column(nullable = false)
    private boolean deleted = false;
}