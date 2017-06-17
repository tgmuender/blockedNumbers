package ch.hosenruck;

import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;

import java.nio.charset.Charset;

import static io.netty.buffer.Unpooled.wrappedBuffer;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Handler which returns the provided {@link JsonObject} on every request.
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    private final JsonObject blockedNumbersResponse;
    private final ByteBuf responseBuffer;

    public HttpServerHandler(JsonObject blockedNumbers) {
        blockedNumbersResponse = blockedNumbers;
        responseBuffer = wrappedBuffer(blockedNumbersResponse.toString().getBytes(Charset.forName("UTF-8")));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!isHttpRequest(msg)) {
            return;
        }

        final FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, responseBuffer);
        response.headers().add(CONTENT_TYPE, "application/json");

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private boolean isHttpRequest(Object msg) {
        return msg != null && msg instanceof HttpRequest;
    }
}
