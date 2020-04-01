package com.vmware.pivotal.labs.dataTx.db2geodebatch.mapper;

import com.vmware.pivotal.labs.dataTx.db2geodebatch.domain.Account;
import com.vmware.pivotal.labs.dataTx.db2geodebatch.domain.Location;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Gregory Green
 */
public class AccountRowMapper implements RowMapper<Account>
{

    public static final String ACCOUNT_NBR_NM = "ACCOUNT_NBR";
    public static final String LOC_ADDR1 = "LOC_ADDR1";
    public static final String LOC_ADDR2 = "LOC_ADDR2";

    @Override
    public Account mapRow(ResultSet resultSet, int i)
    throws SQLException
    {
        return Account.builder()
                .accountNumber(resultSet.getString(ACCOUNT_NBR_NM))
                .location(Location.builder()
                        .address1(resultSet.getString(LOC_ADDR1))
                        .address2(resultSet.getString(LOC_ADDR2)).build()
                ).build();
    }
}
