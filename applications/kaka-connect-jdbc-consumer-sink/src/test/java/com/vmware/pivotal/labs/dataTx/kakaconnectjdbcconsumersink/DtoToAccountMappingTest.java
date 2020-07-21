package com.vmware.pivotal.labs.dataTx.kakaconnectjdbcconsumersink;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Base64;

class DtoToAccountMappingTest
{

    @Test
    public void test_mapping()
    {
        //Apr 28, 2020, 12:00:00 AM
        LocalDate date = LocalDate.of(2020,4,27);

        DtoToAccountMapping mapper = new DtoToAccountMapping();
        AccountDto expected = AccountDto.builder()
                .accountName("1")
                .updateDate(BigInteger.valueOf(1588032000000L))
                .accountId(Base64.getDecoder().decode("ZA=="))
                .build();

        Account actual = mapper.map(expected);

        assertNotNull(actual);
        assertEquals(expected.getAccountName(),actual.getAccountName());
        assertEquals(1L,actual.getAccountId());
        assertEquals(date,actual.getUpdateDt());

    }

}