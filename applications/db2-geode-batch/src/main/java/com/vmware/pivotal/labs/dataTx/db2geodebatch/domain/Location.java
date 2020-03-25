package com.vmware.pivotal.labs.dataTx.db2geodebatch.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Gregory Green
 */
@Data
@Builder
@EqualsAndHashCode
public class Location
{

    private String address1;
    private String address2;

}
