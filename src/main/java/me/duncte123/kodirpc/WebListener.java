package me.duncte123.kodirpc;

import club.minnced.discord.rpc.DiscordRichPresence;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.json.JSONObject;


public class WebListener extends WebSocketListener {

    public final int NORMAL_CLOSURE_STATUS = 1000;

    @Override public void onOpen(WebSocket webSocket, Response response) {
        System.out.println("Connected");
        //webSocket.close(1000, "Goodbye, World!");
    }

    @Override public void onMessage(WebSocket webSocket, String text) {
        //System.out.println("MESSAGE: " + text);
        JSONObject jsonObject = new JSONObject(text);
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.details = "Idling";
        presence.largeImageKey = "logo";
        if(jsonObject.optString("method") != null && jsonObject.optString("method").equals("System.OnQuit")) {
            System.exit(0);
            return;
        }
        if(jsonObject.optString("method") != null && jsonObject.optString("method").equals("Player.OnPlay")) {
            JSONObject item = jsonObject.optJSONObject("params").optJSONObject("data").optJSONObject("item");
            presence.details = "Watching: " +  item.optString("showtitle");
            presence.state = "Episode: " + item.getString("title");
        }
        Main.getLib().Discord_UpdatePresence(presence);
    }

    @Override public void onMessage(WebSocket webSocket, ByteString bytes) {
        System.out.println("MESSAGE: " + bytes.hex());
    }

    @Override public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        System.out.println("CLOSE: " + code + " " + reason);
    }

    @Override public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
    }


}
