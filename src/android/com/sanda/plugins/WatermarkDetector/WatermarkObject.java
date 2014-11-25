package com.sanda.plugins.WatermarkDetector;

import android.os.Parcel;
import android.os.Parcelable;

public class WatermarkObject implements Parcelable {
	String mData;
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeValue(mData);
		}
	public static final Parcelable.Creator<WatermarkObject> CREATOR = new Parcelable.Creator<WatermarkObject>() {
        public WatermarkObject createFromParcel(Parcel in) {
            return new WatermarkObject(in);
        }
        public WatermarkObject[] newArray(int size) {
            return new WatermarkObject[size];
        }
	 };
	 
	 WatermarkObject(Parcel in) {
	        mData = in.readString();
	    }
	 
	 WatermarkObject() {
	    }
}
