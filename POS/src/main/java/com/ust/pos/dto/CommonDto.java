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
        private boolean status=true;
        private String modifiedBy;
        private String createdBy;
        private LocalDateTime modifiedOn;
        private LocalDateTime createdOn;
        private boolean deleted=false;

}
