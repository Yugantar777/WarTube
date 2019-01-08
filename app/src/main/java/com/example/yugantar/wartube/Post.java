package com.example.yugantar.wartube;

import android.net.Uri;

public class Post{
    private String post;
    private String name;
    private String id;

    public Post(String post, String name,String id) {
        this.post = post;
        this.name = name;
        this.id=id;

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

}
