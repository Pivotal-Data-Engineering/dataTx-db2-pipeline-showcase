package com.vmware.pivotal.labs.dataTx.kakaconnectjdbcconsumersink;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.connect.data.Decimal;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;

import java.math.BigDecimal;
import java.util.Base64;

/**
 * @author Gregory Green
 */
@EnableBinding(Sink.class)
public class MySink
{
    @StreamListener(Sink.INPUT)
    void sink(Message<String> message)
    throws Exception
    {

        try
        {
            ObjectMapper om = new ObjectMapper();
            JsonNode jn = om.readTree(message.getPayload());
            JsonNode payload = jn.get("payload");

            AccountDto dto = om.readValue(payload.toString(), AccountDto.class);
            String accountID = payload.get("ACCT_ID").asText();

            byte[] values = Base64.getDecoder().decode(accountID);

            BigDecimal decimal = Decimal.toLogical(Decimal.builder(2), values);


            System.out.println("message:" + decimal);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;

        }
    }
}
