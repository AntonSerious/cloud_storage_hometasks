package handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.nio.charset.StandardCharsets;

public class OutputHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        String message = String.valueOf(msg);
        ByteBuf buf = ctx.alloc().directBuffer();
        System.out.println("out: " + message);
        buf.writeBytes(message.getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(msg);
    }
}
