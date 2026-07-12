package shared.models;

import java.sql.Timestamp;

public class Like {
     private int userId;
     private int tweetId;
     private Timestamp createdAt;

     public Like() {}

    public Like(int userId, int tweetId){
          this.userId = userId;
          this.tweetId = tweetId;
    }

    public int getUserId(){return userId; }
    public void setUserId(int userId){this.userId = userId; }

    public int getTweetId(){return tweetId; }
    public void setTweetId(int tweetId){this.tweetId = tweetId; }

    public Timestamp getCreatedAt(){return createdAt; }
    public void setCreatedAt(Timestamp createdAt){this.createdAt = createdAt; }

    @Override
    public String toString(){
        return "Like{ userId : " + userId + ", tweetId : " + tweetId + " }";
    }
}
