package com.vmware.pivotal.labs.dataTx.db2geodebatch.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Gregory Green
 */
@Builder
@EqualsAndHashCode
@Getter
public class Account
{

    private String accountNumber;
    private Location location;
}
