package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommonDto extends PaginationDto {

    private Long id;
    private String identifier;
    private String message;
    private boolean success = true;
    private boolean status;
    private String description;
    private String createdBy;
    private LocalDateTime createdOn;
    private String modifiedBy;
    private LocalDateTime modifiedOn;
}

