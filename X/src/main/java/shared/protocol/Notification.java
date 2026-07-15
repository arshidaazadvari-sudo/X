package shared.protocol;

public class Notification {

    private NotificationType type;

    private String details;

    public Notification (NotificationType nt, String det) {
        this.type = nt;
        this.details = det;
    }

    public NotificationType getType() {
        return type;
    }

    public String getDetails() {
        return details;
    }
}
