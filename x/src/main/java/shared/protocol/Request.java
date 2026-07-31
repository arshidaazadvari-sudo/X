package shared.protocol;


import java.util.UUID;

public class Request {

    private String type;
    private String requestId;
    private String token;
    private Object payload;


    public Request() {
    }

    public Request(String type, Object payload) {
        this.type = type;
        this.requestId = UUID.randomUUID().toString();
        this.payload = payload;
        this.token = null;
    }

    public Request(String type, String token, Object payload) {
        this.type = type;
        this.requestId = UUID.randomUUID().toString();
        this.token = token;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}