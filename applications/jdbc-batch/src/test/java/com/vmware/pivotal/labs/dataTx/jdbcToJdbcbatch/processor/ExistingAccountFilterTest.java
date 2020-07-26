package com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.processor;


import com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.MockitoAnnotations.initMocks;

class ExistingAccountFilterTest
{
    private ExistingAccountFilter subject;


    @Mock
    AccountRepository accountRepository;

    @BeforeEach
    public void setUp()
    {
        initMocks(this);
        subject = new ExistingAccountFilter(accountRepository);
    }

    @Test
    public void test_filter()
    throws Exception
    {


        Map<String, Long> map = new HashMap<>();
        Long id = Long.valueOf(1);
        map.put("id", id);

        subject.process(map);

        Mockito.verify(accountRepository).findById(any());

    }

    @Test
    public void test_filter_empty_returns_null()
            throws Exception
    {

        assertNull(subject.process(new HashMap<>()));

        assertNull(subject.process(null));

    }

}