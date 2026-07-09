package shared.models;

import java.sql.Timestamp;

public class Reply {
    private int id;
    private int userId;
    private int tweetId;
    private Timestamp createdAt;
    private String content;

    public Reply() {}

    public Reply(String content, int userId, int tweetId){
        this.content = content;
        this.userId = userId;
        this.tweetId = tweetId;
    }

    public int getId(){return id; }
    public void setId(int id){this.id = id; }

    public int getUserId(){return userId; }
    public void setUserId(int userId){this.userId = userId; }

    public int getTweetId(){return tweetId; }
    public void setTweetId(int tweetId){this.tweetId = tweetId; }

    public String getContent(){return content; }
    public void setContent(String content){this.content = content; }

    public Timestamp getCreatedAt(){return createdAt; }
    public void setCreatedAt(Timestamp createdAt){this.createdAt = createdAt; }

    @Override
    public String toString(){
        return "Reply{ id : " + id + ", userId : " + userId + ", tweetId : " + tweetId + " }";
    }
}
