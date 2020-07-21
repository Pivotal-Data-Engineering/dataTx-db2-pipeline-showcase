package com.vmware.pivotal.labs.dataTx.kakaconnectjdbcconsumersink;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.connect.data.Schema;

/**
 * @author Gregory Green
 */
@Data
@NoArgsConstructor
public class AccountConnectMessage
{
    private AccountDto accountDto;
    private Schema schema;
}
