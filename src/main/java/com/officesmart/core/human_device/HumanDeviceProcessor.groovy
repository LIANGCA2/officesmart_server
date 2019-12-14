package com.officesmart.core.human_device

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.component.paho.PahoMessage
import org.springframework.stereotype.Component

@Component
class HumanDeviceProcessor implements Processor  {

    @Override
    void process(Exchange exchange) throws Exception {
        println exchange.getMessage(PahoMessage.class).getMqttMessage().getPayload().toString()
    }

}
