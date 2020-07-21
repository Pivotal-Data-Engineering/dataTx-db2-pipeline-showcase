package com.vmware.pivotal.labs.dataTx.kakaconnectjdbcconsumersink;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author Gregory Green
 */
@Data
@Builder
public class Account
{
    private Long accountId;
    private String accountName;
    private LocalDate updateDt;
}
