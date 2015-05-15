package bean;

import java.util.Date;
import java.util.UUID;

/**
 * Created by yls on 15-5-14.
 */
public class Crime {
    private UUID    mId;
    private String  mTitle;
    private Date mDate;
    private boolean mSolved;
    public Crime(){
        mId =  UUID.randomUUID();
        mDate = new Date();
    }
    @Override
    public String toString() {
        return mTitle;
    }
    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public UUID getmId() {
        return mId;
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public boolean ismSolved() {
        return mSolved;
    }

    public void setmSolved(boolean mSolved) {
        this.mSolved = mSolved;
    }


}
