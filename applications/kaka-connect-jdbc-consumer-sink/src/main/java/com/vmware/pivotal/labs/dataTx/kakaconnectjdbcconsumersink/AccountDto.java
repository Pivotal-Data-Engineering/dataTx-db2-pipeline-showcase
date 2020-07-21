package com.vmware.pivotal.labs.dataTx.kakaconnectjdbcconsumersink;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author Gregory Green
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
public class AccountDto
{
    @JsonProperty("ACCT_ID")
    private byte[] accountId;

    //
    @JsonProperty("ACCT_NM")
    private String accountName;

    //UPDATE_DT
    @JsonProperty("UPDATE_DT")
    private BigInteger updateDate;
}
