package com.model;

import org.json.JSONException;
import org.json.JSONObject;

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
    private Photo mPhoto;

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";
    private static final String JSON_PHOTO = "photo";

    public Crime(){
        mId =  UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        if (json.has(JSON_TITLE)) {
            mTitle = json.getString(JSON_TITLE);
        }
        if (json.has(JSON_PHOTO))
            mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
        mSolved = json.getBoolean(JSON_SOLVED);
        mDate = new Date(json.getLong(JSON_DATE));
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

    public Photo getmPhoto() {
        return mPhoto;
    }

    public void setmPhoto(Photo mPhoto) {
        this.mPhoto = mPhoto;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put(JSON_ID,getmId());
        js.put(JSON_TITLE,getmTitle());
        js.put(JSON_DATE,getmDate());
        js.put(JSON_SOLVED,ismSolved());
        if (mPhoto != null)
            js.put(JSON_PHOTO, mPhoto.toJSON());
        return  js;
    }
}
