package de.realwhimsy.afktraderpoe.datamodel.TypeAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import de.realwhimsy.afktraderpoe.datamodel.Reply;


public class ReplyAdapter extends TypeAdapter<Reply> {
    @Override
    public void write(JsonWriter out, Reply reply) throws IOException {
        out.beginObject();
        out.name("whisperTarget").value(reply.getWhisperTarget());
        out.name("message").value(reply.getMessage());
        out.endObject();
    }

    @Override
    public Reply read(JsonReader in) throws IOException {
        String whisperTarget = null;
        String message = null;

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            if (name.equals("whisperTarget")) {
                whisperTarget = in.nextString();
            } else if (name.equals("message")) {
                message = in.nextString();
            } else {
                in.skipValue();
            }
        }
        in.endObject();
        return new Reply(whisperTarget, message);
    }
}