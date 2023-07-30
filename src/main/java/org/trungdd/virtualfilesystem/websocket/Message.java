package org.trungdd.virtualfilesystem.websocket;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Message {

    private String status;
    private String curDir;
    private List<String> infoes;

    public Message() {
        this.infoes = new ArrayList<>();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurDir() {
        if (curDir == null) {
            curDir = "";
        }
        return curDir;
    }

    public void setCurDir(String curDir) {
        this.curDir = curDir;
    }

    public List<String> getInfoes() {
        if (infoes.isEmpty()) {
            infoes.add("");
        }
        return infoes;
    }

    public void setInfoes(List<String> infoes) {
        this.infoes = infoes;
    }

    public void pushInfo(String info) {
        this.infoes.add(info);
    }

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this).toString();
    }
}
