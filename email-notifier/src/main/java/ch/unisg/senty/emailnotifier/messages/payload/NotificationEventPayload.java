package ch.unisg.senty.emailnotifier.messages.payload;

public class NotificationEventPayload {
  
  private String notificationID;

  private String emailContent;
  
  public String getNotificationID() {
    return notificationID;
  }

  public String getEmailContent() {
    return emailContent;
  }

  public void setNotificationID(String notificationID) {
    this.notificationID = notificationID;
  }

  public void setEmailContent(String emailContent) {
    this.emailContent = emailContent;
  }

}
