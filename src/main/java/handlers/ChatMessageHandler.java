package handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;


import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatMessageHandler extends SimpleChannelInboundHandler<String> {

    public static final ConcurrentLinkedQueue<SocketChannel> channels = new ConcurrentLinkedQueue<>();


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected: " + ctx.channel());
        channels.add((SocketChannel) ctx.channel());
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Message from client: " + msg);
        msg = msg.replace("lol", "***");
//        ctx.writeAndFlush(msg);
        String finalMsg = msg;
        channels.forEach(c -> c.writeAndFlush(finalMsg));
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected: " + ctx.channel());
    }
}
