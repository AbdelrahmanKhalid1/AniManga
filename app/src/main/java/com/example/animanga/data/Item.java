package com.example.animanga.data;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class Item implements Parcelable {

    private String itemId;
    private String name;
    private String pic;
    private boolean status;
    private String link;
    private String desc;
    private Boolean category;

    public Item() {

    }


    private Item(Parcel in) {
        itemId = in.readString();
        name = in.readString();
        pic = in.readString();
        status = in.readByte() != 0;
        link = in.readString();
        desc = in.readString();
        byte tmpCategory = in.readByte();
        category = tmpCategory == 0 ? null : tmpCategory == 1;
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    @Exclude //3shan mttketbsh fl documnect 3shan kda hbt'a data mtQraraaa
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setStatusByString(String str) {
        if (isDone(str)) {
            status = true;
        } else {
            status = false;
        }
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Exclude
    public Boolean getCategory() {
        return category;
    }

    public void setCategory(Boolean category) {
        this.category = category;
    }

    public void setCategory(String str) {
        if (isAnime(str)) {
            category = true;
        } else {
            category = false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemId);
        dest.writeString(name);
        dest.writeString(pic);
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeString(link);
        dest.writeString(desc);
        dest.writeByte((byte) (category == null ? 0 : category ? 1 : 2));
    }

    private boolean isAnime(String str) {
        return (str.equals("ANIME"));
    }

    private boolean isDone(String str) {
        return (str.equals("Done"));
    }

    public String isDoneToText(boolean t) {
        if (t) {
            return "Done";
        }
        return "Undone";
    }

}
