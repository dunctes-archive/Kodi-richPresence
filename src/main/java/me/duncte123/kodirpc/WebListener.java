package me.duncte123.kodirpc;

import club.minnced.discord.rpc.DiscordRichPresence;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.json.JSONObject;


public class WebListener extends WebSocketListener {
    private final DiscordRichPresence presence = new DiscordRichPresence();
    public final int NORMAL_CLOSURE_STATUS = 1000;

    @Override public void onOpen(WebSocket webSocket, Response response) {
        System.out.println("Connected to kodi");
        presence.largeImageKey = "logo";
        //webSocket.close(1000, "Goodbye, World!");
    }

    @Override public void onMessage(WebSocket webSocket, String text) {
        //System.out.println("MESSAGE: " + text);
        JSONObject jsonObject = new JSONObject(text);
        presence.details = "Idling";
        if(jsonObject.optString("method") != null) {
            switch (jsonObject.getString("method")) {
                case "Player.OnPlay":
                    JSONObject item = jsonObject.optJSONObject("params").optJSONObject("data").optJSONObject("item");
                    presence.details = "Show: " +  item.optString("showtitle");
                    presence.state = "Episode: " + item.getString("title");
                    break;
                case "Player.OnPause":
                    presence.details = "Player paused";
                    presence.state = "Show: " + jsonObject.optJSONObject("params").optJSONObject("data").optJSONObject("item").getString("showtitle");
                    break;
                case "Player.OnStop":
                    presence.details = "No show";
                    presence.state = "";
                    break;
                case "System.OnQuit":
                    System.exit(0);
                    break;
                default:
                    System.out.println(jsonObject.toString(4));
                    break;
            }
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
        System.out.println("Failed to connect: " + t.getMessage());
        System.exit(0);
        //t.printStackTrace();
    }


}
