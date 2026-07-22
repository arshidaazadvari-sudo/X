package client.network;

import shared.protocol.Notification;
import shared.protocol.NotificationType;

import java.io.ObjectInputStream;
import java.util.List;

public class ResponseListener implements Runnable {

    private final ObjectInputStream in;

    public static Notification authNotif;

    public static List<Notification> notifications;

    public ResponseListener(ObjectInputStream inputStream) { this.in = inputStream; }

    @Override
    public void run() {
        try {
            Object obj;
            while ((obj = in.readObject()) != null) {
                // deciding what to do with the received message from server based on its type

                if (obj instanceof Notification) {
                    Notification new_notif = (Notification) obj;

                    if (new_notif.getType().equals(NotificationType.LOGIN_ERROR) ||
                            new_notif.getType().equals(NotificationType.REGISTER_ERROR) ||
                            new_notif.getType().equals(NotificationType.AUTH_SUCCESS)) {

                        authNotif = new_notif;
                    }
                    else {
                        notifications.add(new_notif);
                    }
                }
            }
        }
        catch (Exception e) {
            //
        }
    }
}
