package com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.processor;

import com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.repository.AccountEntity;
import com.vmware.pivotal.labs.dataTx.jdbcToJdbcbatch.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * @author Gregory Green
 */
@AllArgsConstructor
@Component
public class ExistingAccountFilter implements ItemProcessor<Map<String, ?>, Map<String, ?>>
{
    private AccountRepository accountRepository;

    @Override
    @Transactional(propagation =)
    public Map<String, ?> process(Map<String, ?> accountMap)
    throws Exception
    {
        Optional<AccountEntity> foundOptional = accountRepository.findById((Long) accountMap.get("id"));

        if (!foundOptional.isPresent())
            return accountMap; //new record

        return null;
    }
}
