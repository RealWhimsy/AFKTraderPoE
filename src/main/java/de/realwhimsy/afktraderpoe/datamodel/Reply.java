package de.realwhimsy.afktraderpoe.datamodel;

public class Reply {
    private String whisperTarget;
    private String message;

    public Reply(String whisperTarget, String message) {
        this.whisperTarget = whisperTarget;
        this.message = message;
    }

    public String getWhisperTarget() {
        return whisperTarget;
    }

    public String getMessage() {
        return message;
    }
}
