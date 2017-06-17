package company.com;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {

    private static final String PN_FILE_LOCATION = "blockedNumbersFileLocation";

    public static void main(String[] args) throws Exception {
        startHttpServer(8080);
    }

    private static void startHttpServer(int port) throws InterruptedException {
        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            final ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(getHttpInitializer())
                    .bind(port)
                    .sync()
                    .channel()
                    .closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static ChannelInitializer<SocketChannel> getHttpInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                final ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new HttpServerHandler(getBlockedNumbers()));
            }
        };
    }

    private static JsonObject getBlockedNumbers() throws FileNotFoundException {
        final String name = System.getProperty(PN_FILE_LOCATION);
        final FileReader reader = new FileReader(name);
        return  (JsonObject) new JsonParser().parse(reader);
    }

}
