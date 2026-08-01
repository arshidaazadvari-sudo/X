package shared.protocol;


import java.util.UUID;

public class Request {

    private String type;
    private String requestId;
    private int userId;
    private Object payload;


    public Request() {
    }

    public Request(String type, Object payload) {
        this.type = type;
        this.requestId = UUID.randomUUID().toString();
        this.payload = payload;
        this.userId = 0;
    }

    public Request(String type, int userId, Object payload) {
        this.type = type;
        this.requestId = UUID.randomUUID().toString();
        this.userId = userId;
        this.payload = payload;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int  userId) {
        this.userId= userId;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}