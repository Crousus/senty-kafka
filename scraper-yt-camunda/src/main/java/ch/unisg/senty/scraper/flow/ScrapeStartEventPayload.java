package ch.unisg.senty.scraper.flow;

public class ScrapeStartEventPayload {
  
  private String orderId;

  public String getOrderId() {
    return orderId;
  }

  public ScrapeStartEventPayload setOrderId(String orderId) {
    this.orderId = orderId;
    return this;
  }
}
