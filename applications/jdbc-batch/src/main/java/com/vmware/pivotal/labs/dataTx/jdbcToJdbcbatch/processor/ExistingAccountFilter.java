package com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.processor;

import com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.repository.AccountEntity;
import com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.repository.AccountRepository;
import lombok.AllArgsConstructor;
import nyla.solutions.core.exception.CommunicationException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * @author Gregory Green
 */
@AllArgsConstructor
@Component
@Profile("!test")
public class ExistingAccountFilter implements ItemProcessor<Map<String, ?>, Map<String, ?>>
{
    private static  int count = -0;
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public Map<String, ?> process(Map<String, ?> accountMap)
    throws Exception
    {
        if(accountMap == null || accountMap.isEmpty())
            return null;

        if(count < 2)
        {

            count++;
            throw new CommunicationException(); //TEST for retry
        }
        String id = (String) accountMap.get("TEST1");
        Optional<AccountEntity> foundOptional = accountRepository.findById(id);

        if (!foundOptional.isPresent())
            return accountMap; //new record

        return null;
    }
}
