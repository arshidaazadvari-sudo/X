package shared.protocol;


public class Response {

    private String type;
    private String requestId;
    private String status;
    private int code;
    private String message;
    private Object payload;
    public Response() {
    }
    public static Response success(String type, String requestId, Object payload) {
        Response r = new Response();
        r.type = type;
        r.requestId = requestId;
        r.status = "OK";
        r.code = 200;
        r.message = null;
        r.payload = payload;
        return r;
    }

    public static Response success(String type, String requestId, String message, Object payload) {
        Response r = success(type, requestId, payload);
        r.message = message;
        return r;
    }

    public static Response error(String requestId, int code, String message) {
        Response r = new Response();
        r.type = "ERROR";
        r.requestId = requestId;
        r.status = "ERROR";
        r.code = code;
        r.message = message;
        r.payload = null;
        return r;
    }

    public static Response error(String type, String requestId, int code, String message) {
        Response r = error(requestId, code, message);
        r.type = type;
        return r;
    }

    public String getType() {return type;}
    public void setType(String type) {this.type = type;}
    public String getRequestId() {return requestId;}
    public void setRequestId(String requestId) {this.requestId = requestId;}
    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}
    public int getCode() {return code;}
    public void setCode(int code) {this.code = code;}
    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}
    public Object getPayload() {return payload;}
    public void setPayload(Object payload) {this.payload = payload;}
}