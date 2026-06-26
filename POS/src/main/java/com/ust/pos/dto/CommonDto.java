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
    private boolean status;
    private boolean success = true;
    private String createdBy;
    private LocalDateTime createdOn;
    private String modifiedBy;
    private LocalDateTime modifiedOn;
    @Column(nullable = false)
    private boolean deleted = false;
}