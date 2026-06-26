package com.ust.pos.dto;

import jakarta.persistence.Column;
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

    @Column(updatable = false)
    private String createdBy;

    @Column(updatable = false)
    private LocalDateTime createdOn;

    private String modifiedBy;
    private LocalDateTime modifiedOn;
}
