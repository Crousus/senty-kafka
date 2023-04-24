package ch.unisg.senty.order.flow.orderplaced;

public class TopUpCommandPayload {
  
  private String videoId;
  private String customerId;
  private String tokenAmount;
  
  public String getVideoId() {
    return videoId;
  }
  public TopUpCommandPayload setVideoId(String videoId) {
    this.videoId = videoId;
    return this;
  }
  public String getCustomerId() {
    return customerId;
  }
  public TopUpCommandPayload setCustomerId(String customerId) {
    this.customerId = customerId;
    return this;
  }
  public String getTokenAmount() {
    return tokenAmount;
  }
  public TopUpCommandPayload setTokenAmount(String tokenAmount) {
    this.tokenAmount = tokenAmount;
    return this;
  }
}
