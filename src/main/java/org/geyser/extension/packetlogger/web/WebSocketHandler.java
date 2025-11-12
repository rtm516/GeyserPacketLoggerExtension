package org.geyser.extension.packetlogger.web;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.CloseMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;

public class WebSocketHandler implements WebSocketConnectionCallback {
    private final WebApplication application;

    public WebSocketHandler(WebApplication application) {
        this.application = application;
    }

    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        application.channels.add(channel);
        application.logger.info("Client connected: " + channel.getPeerAddress());

        // Setup message receiver
        channel.getReceiveSetter().set(new AbstractReceiveListener() {
            @Override
            protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                String receivedMessage = message.getData();
                application.logger.info("Received message: " + receivedMessage);
            }

            @Override
            protected void onError(WebSocketChannel channel, Throwable error) {
                application.logger.severe("WebSocket error: " + error.getMessage());
                application.channels.remove(channel);
            }

            @Override
            protected void onCloseMessage(CloseMessage cm, WebSocketChannel channel) {
                application.channels.remove(channel);
                application.logger.info("Client disconnected: " + channel.getPeerAddress());
            }
        });

        channel.resumeReceives();
    }
}
