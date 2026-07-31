package shared.models;

import java.sql.Timestamp;

public class Follow {
    private int followerId;
    private int followeeId;
    private Timestamp createdAt;

    public Follow() {}

    public Follow(int followerId, int followeeId){
        this.followeeId = followeeId;
        this.followerId = followerId;
    }

    public int getFollowerId(){return followerId; }
    public void setFollowerId(int followerId){this.followerId = followerId; }

    public int getFolloweeId(){return followerId; }
    public void setFolloweeIdI(int followeeId){this.followeeId = followeeId; }

    public Timestamp getCreatedAt(){return createdAt; }
    public void setCreatedAt(Timestamp createdAt){this.createdAt = createdAt; }

    @Override
    public String toString(){
        return "Follow{ followerId : " + followerId + ", followeeId : " + followeeId + " }";
    }
}
