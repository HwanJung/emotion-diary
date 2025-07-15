package hwan.diary.controller;

public class DiaryForm {
    public long uid;
    public String text;
    public String image_url;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
