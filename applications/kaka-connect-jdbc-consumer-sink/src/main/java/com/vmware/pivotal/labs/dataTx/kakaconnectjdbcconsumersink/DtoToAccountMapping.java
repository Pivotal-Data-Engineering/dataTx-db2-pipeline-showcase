package com.vmware.pivotal.labs.dataTx.kakaconnectjdbcconsumersink;

import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Timestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author Gregory Green
 */
public class DtoToAccountMapping
{

    public Account map(AccountDto dto)
    {
        BigDecimal decimal =
                Decimal.toLogical(Decimal.schema(2).schema(),
                        dto.getAccountId());


        Date date = Timestamp.toLogical(Timestamp.SCHEMA,
                dto.getUpdateDate().longValue());


        return Account.builder()
                .accountId(Long.valueOf(decimal.toBigInteger().longValue()))
                .accountName(dto.getAccountName())
                .updateDt(Instant.ofEpochMilli(date.getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate())
        .build();


    }
}
