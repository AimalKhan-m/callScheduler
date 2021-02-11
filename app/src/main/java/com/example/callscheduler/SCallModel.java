package com.example.callscheduler;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "SCallModelsTable")
public class SCallModel implements Parcelable  {

    @PrimaryKey(autoGenerate = true)
    public int  id;
    public String calleeName = "UnKnown";
    public String calleeNumber = "123";
    @TypeConverters(AppUtils.DateConverter.class)
    public Date sCallTime ;
    public Boolean askBeforeCall = false;
    public Boolean allowSpeakerOn = false;
    public String alarmAudioUri = Uri.parse("android.resource://" + "com.example.callscheduler" + "/" + R.raw.default_alarmtone).toString();
    public Boolean isSCallDone = false;


    public SCallModel(){}

//    --------------------------- GETTER / SETTERS ------------------------------------


    protected SCallModel(Parcel in) {
        id = in.readInt();
        calleeName = in.readString();
        calleeNumber = in.readString();
        sCallTime = (Date) in.readSerializable();
        byte tmpAskBeforeCall = in.readByte();
        askBeforeCall = tmpAskBeforeCall == 0 ? null : tmpAskBeforeCall == 1;
        byte tmpAllowSpeakerOn = in.readByte();
        allowSpeakerOn = tmpAllowSpeakerOn == 0 ? null : tmpAllowSpeakerOn == 1;
        alarmAudioUri = in.readString();
        byte tmpIsSCallDone = in.readByte();
        isSCallDone = tmpIsSCallDone == 0 ? null : tmpIsSCallDone == 1;
    }

    public static final Creator<SCallModel> CREATOR = new Creator<SCallModel>() {
        @Override
        public SCallModel createFromParcel(Parcel in) {
            return new SCallModel(in);
        }

        @Override
        public SCallModel[] newArray(int size) {
            return new SCallModel[size];
        }
    };

    public Boolean getSCallDone() {
        return isSCallDone;
    }

    public void setSCallDone(Boolean SCallDone) {
        isSCallDone = SCallDone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCalleeName() {
        return calleeName;
    }

    public void setCalleeName(String calleeName) {
        this.calleeName = calleeName;
    }

    public String getCalleeNumber() {
        return calleeNumber;
    }

    public void setCalleeNumber(String calleeNumber) {
        this.calleeNumber = calleeNumber;
    }

    public java.util.Date getsCallTime() {
        return sCallTime;
    }

    public void setsCallTime(Date sCallTime) {
        this.sCallTime = sCallTime;
    }

    public Boolean getAskBeforeCall() {
        return askBeforeCall;
    }

    public void setAskBeforeCall(Boolean askBeforeCall) {
        this.askBeforeCall = askBeforeCall;
    }

    public Boolean getAllowSpeakerOn() {
        return allowSpeakerOn;
    }

    public void setAllowSpeakerOn(Boolean allowSpeakerOn) {
        this.allowSpeakerOn = allowSpeakerOn;
    }

    public String getAlarmAudioUri() {
        return alarmAudioUri;
    }

    public void setAlarmAudioUri(String alarmAudioUri) {  this.alarmAudioUri = alarmAudioUri;}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(calleeName);
        dest.writeString(calleeNumber);
        dest.writeSerializable(sCallTime);
        dest.writeByte((byte) (askBeforeCall == null ? 0 : askBeforeCall ? 1 : 2));
        dest.writeByte((byte) (allowSpeakerOn == null ? 0 : allowSpeakerOn ? 1 : 2));
        dest.writeString(alarmAudioUri);
        dest.writeByte((byte) (isSCallDone == null ? 0 : isSCallDone ? 1 : 2));
    }
}
