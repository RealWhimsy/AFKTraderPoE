package de.realwhimsy.afktraderpoe.datamodel;


public class AppResponse {
    private final String action;
    private final String content;

    public AppResponse(String action, String content) {
        this.action = action;
        this.content = content;
    }

    public String getAction() {
        return action;
    }

    public String getContent() {
        return content;
    }
}
