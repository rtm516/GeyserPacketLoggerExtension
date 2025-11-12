package org.geyser.extension.packetlogger.web;

import com.google.gson.JsonIOException;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.util.HttpString;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.CloseMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import org.geyser.extension.packetlogger.Constants;
import org.geyser.extension.packetlogger.types.WebSocketMessage;
import org.geysermc.geyser.api.extension.ExtensionLogger;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebApplication {
    final ExtensionLogger logger;
    final Set<WebSocketChannel> channels;

    private final Undertow server;

    public WebApplication(ExtensionLogger logger) {
        this.logger = logger;
        this.channels = ConcurrentHashMap.newKeySet();

        PathHandler pathHandler = new PathHandler();


        // WebSocket handler
        WebSocketConnectionCallback callback = new WebSocketHandler(this);
        pathHandler.addExactPath("/messaging", new WebSocketProtocolHandshakeHandler(callback));

        // Static files
        ResourceHandler resourceHandler = new ResourceHandler(
            new ClassPathResourceManager(WebApplication.class.getClassLoader(), "static/"))
            .setDirectoryListingEnabled(false)
            .setCachable(exchange -> {
                String path = exchange.getRelativePath();
                boolean shouldCache = path.matches(".*\\.(css|js|jpg|jpeg|png|gif|svg|woff2?|ttf|eot)$");

                if (shouldCache) {
                    exchange.getResponseHeaders().add(HttpString.tryFromString("E-Tag"), Integer.toString(path.hashCode()));
                }

                // Cache static assets
                return shouldCache;
            })
            .setCacheTime(3600)
            .setWelcomeFiles("index.html");
        pathHandler.addPrefixPath("/", resourceHandler);

        server = Undertow.builder()
            .addHttpListener(8082, "127.0.0.1")
            .setHandler(pathHandler)
            .build();
    }

    public void start() {
        server.start();
    }

    public void stop() {
        // Close all WebSocket connections
        channels.forEach(channel -> {
            if (channel.isOpen()) {
                WebSockets.sendClose(CloseMessage.GOING_AWAY, "Server shutting down", channel, null);
            }
        });

        server.stop();
    }

    public void broadcastMessage(WebSocketMessage message) {
        try {
            broadcastMessage(Constants.GSON.toJson(message));
        } catch (JsonIOException e) {
            logger.severe("Failed to serialize message for broadcasting: " + e.getMessage());
        }
    }

    public void broadcastMessage(String message) {
        channels.forEach(channel -> {
            if (channel.isOpen()) {
                WebSockets.sendText(message, channel, null);
            }
        });
    }
}
