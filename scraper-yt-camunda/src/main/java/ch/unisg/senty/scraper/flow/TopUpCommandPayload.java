package ch.unisg.senty.scraper.flow;

public class TopUpCommandPayload {
  
  private String videoId;
  private String customerId;
  private int tokenAmount;
  
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
  public int getTokenAmount() {
    return tokenAmount;
  }
  public TopUpCommandPayload setTokenAmount(int tokenAmount) {
    this.tokenAmount = tokenAmount;
    return this;
  }
}
