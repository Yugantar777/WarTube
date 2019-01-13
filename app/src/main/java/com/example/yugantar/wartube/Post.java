package com.example.yugantar.wartube;

import android.net.Uri;

public class Post{
    private String post;
    private String name;
    private String id;
    private String date;

    public Post(String post, String name,String id,String date) {
        this.post = post;
        this.name = name;
        this.id=id;
        this.date=date;

    }
    public Post(){

    }

    public String getPost() {
        return post;
    }

    public String getName() {
        return name;
    }

    public String getId(){
        return id;
    }

    public String getDate(){
        return date;
    }

}
