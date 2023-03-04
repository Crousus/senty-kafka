package io.flowing.retail.shipping.messages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.handler.annotation.Header;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.flowing.retail.shipping.application.ShippingService;
import io.flowing.retail.shipping.messages.payload.GoodsFetchedEventPayload;

@Component
public class MessageListener {    
  
  @Autowired
  private MessageSender messageSender;

  @Autowired
  private ShippingService shippingService;

  @Transactional
  @KafkaListener(id = "shipping", topics = MessageSender.TOPIC_NAME)
  public void goodsFetchedEventReceived(String messageJson, @Header("type") String messageType) throws Exception {
    if ("GoodsFetchedEvent".equals(messageType)) {
      Message<JsonNode> message = objectMapper.readValue(messageJson, new TypeReference<Message<JsonNode>>(){});
      ObjectNode payload = (ObjectNode) message.getData();
      GoodsFetchedEventPayload payloadEvent = objectMapper //
              .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) //
              .treeToValue(payload, GoodsFetchedEventPayload.class);

      String shipmentId = shippingService.createShipment( //
              payloadEvent.getPickId(), //
              payloadEvent.getCustomer().getName(), //
              payloadEvent.getCustomer().getAddress(), //
              "DHL");

      payload.put("shipmentId", shipmentId);

      messageSender.send( //
              new Message<JsonNode>( //
                      "GoodsShippedEvent", //
                      message.getTraceid(), //
                      payload));
      // as nobody else can send an order completed event I will issue it here
      // Bad smell (order context is missing)
      messageSender.send( //
              new Message<JsonNode>( //
                      "OrderCompletedEvent", //
                      message.getTraceid(), //
                      payload));
    }
  }

  @Autowired
  private ObjectMapper objectMapper;

    
}
