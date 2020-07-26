package com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.repository;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Gregory Green
 */
@Entity
@Data
@Builder
public class AccountEntity
{
    @Id
    private String accountId;

    private String name;
}
