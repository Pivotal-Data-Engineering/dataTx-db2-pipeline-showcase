package com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.processor;


import com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.MockitoAnnotations.initMocks;

class ExistingAccountFilterTest
{
    @Mock
    AccountRepository accountRepository;

    @BeforeEach
    public void setUp()
    {
        initMocks(this);
    }
    @Test
    public void test_filter()
    throws Exception
    {

        ExistingAccountFilter filter = new ExistingAccountFilter(accountRepository);
        Map<String, Long> map = new HashMap<>();
        Long id = Long.valueOf(1);
        map.put("id", id);

        filter.process(map);

        Mockito.verify(accountRepository).findById(any());

    }

}