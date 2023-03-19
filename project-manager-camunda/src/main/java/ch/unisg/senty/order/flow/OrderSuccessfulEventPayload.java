package ch.unisg.senty.order.flow;

public class OrderSuccessfulEventPayload {
  
  private String orderId;

  public String getOrderId() {
    return orderId;
  }

  public OrderSuccessfulEventPayload setOrderId(String orderId) {
    this.orderId = orderId;
    return this;
  }
}
