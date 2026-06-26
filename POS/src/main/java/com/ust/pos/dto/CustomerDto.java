package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDto extends CommonDto{
    private String address;
    private Long phoneno;
    private String email;
    private String partytype;
    private AddressDto billing;
    private AddressDto shipping;
}
