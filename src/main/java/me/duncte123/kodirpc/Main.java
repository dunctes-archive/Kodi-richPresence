package me.duncte123.kodirpc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

import java.util.concurrent.TimeUnit;

public class Main {
    private static final DiscordRPC lib = DiscordRPC.INSTANCE;

    public static void main(String... args) {
        String applicationId = "396990609358979074";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = () -> System.out.println("Connected to discord.");
        lib.Discord_Initialize(applicationId, handlers, true, null);
        DiscordRichPresence presence = new DiscordRichPresence();
        //presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        presence.details = "Idling";
        presence.largeImageKey = "logo";
        lib.Discord_UpdatePresence(presence);
        // in a worker thread
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }, "RPC-Callback-Handler").start();

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0,  TimeUnit.MILLISECONDS)
                .build();
        WebListener webListener = new WebListener();
        Request request = new Request.Builder()
                //You need to enable it in kodi, port can be changed
                .url("ws://localhost:9090/jsonrpc")
                .build();
        WebSocket webSocket = client.newWebSocket(request, webListener);


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            lib.Discord_Shutdown();
            webSocket.close(webListener.NORMAL_CLOSURE_STATUS, null);
            client.dispatcher().executorService().shutdown();
        }, "WebSocket-Close-Thread"));

    }

    public static DiscordRPC getLib() {
        return lib;
    }
}
