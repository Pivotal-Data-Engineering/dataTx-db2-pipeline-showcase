package com.vmware.pivotal.labs.dataTx.db2geodebatch.mapper;

import com.vmware.pivotal.labs.dataTx.db2geodebatch.domain.Account;
import com.vmware.pivotal.labs.dataTx.db2geodebatch.domain.Location;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

public class AccountRowMapperTest
{

    @Test
    void test_row_mapper()
    throws SQLException
    {
        Account expected = Account.builder().accountNumber("12232")
        .location(Location.builder()
        .address1("12323 street")
        .address2("jersey city nj")
        .build()).build();

        AccountRowMapper rowMapper = new AccountRowMapper();

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(AccountRowMapper.ACCOUNT_NBR_NM))
                .thenReturn(expected.getAccountNumber());
        when(resultSet.getString(AccountRowMapper.LOC_ADDR1))
                .thenReturn(expected.getLocation().getAddress1());
        when(resultSet.getString(AccountRowMapper.LOC_ADDR2))
                .thenReturn(expected.getLocation().getAddress2());


        Account actual = rowMapper.mapRow(resultSet,0);
        assertEquals(actual,expected);


    }
}