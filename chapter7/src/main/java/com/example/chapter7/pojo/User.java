package com.example.chapter7.pojo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;

/**
 * @author DX
 * @version 1.0
 * @date 2021/6/17 16:01
 */
@Alias("user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String userName;
    private String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
