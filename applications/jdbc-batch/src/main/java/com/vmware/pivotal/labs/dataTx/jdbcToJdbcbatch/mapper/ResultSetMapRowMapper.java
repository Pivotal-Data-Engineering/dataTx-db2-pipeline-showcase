package com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.mapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gregory Green
 */
@Component
public class ResultSetMapRowMapper implements RowMapper<Map<String, ?>>
{
    @Override
    public Map<String, ?> mapRow(ResultSet resultSet, int rowNumber)
    throws SQLException
    {
        ResultSetMetaData meta = null;

        meta = resultSet.getMetaData();

        int columnCount = meta.getColumnCount();

        String columnName;

        HashMap<String, Object> rowMap = new HashMap<>();

        for (int i = 0; i < columnCount; i++)
        {

            columnName = meta.getColumnName(i + 1);

            if (columnName == null)
                columnName = String.valueOf(i+1);

            Object value = resultSet.getObject(i + 1);
            rowMap.put(columnName, value);
        }

        return rowMap;

    }
}
