package com.ust.pos.dto;

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
    private Boolean status = true;
    private String description;
    private LocalDateTime createdOn;
    private String createdBy;
    private String modifiedBy;
    private LocalDateTime modifiedOn;
    private Boolean deleted = false;
}