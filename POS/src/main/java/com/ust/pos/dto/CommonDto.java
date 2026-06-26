package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommonDto {
    private Long id;
    private String identifier;
    private String description;
    private Boolean status;
    private String createdBy;
    private LocalDateTime createdOn;
    private String modifiedBy;
    private LocalDateTime modifiedOn;
    private boolean success = true;
    private String message;
    private Boolean deleted=false;
}
