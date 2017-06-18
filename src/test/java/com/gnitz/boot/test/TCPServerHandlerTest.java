package com.gnitz.boot.test;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gnitz.boot.tcp.ChannelRepository;
import com.gnitz.boot.tcp.handler.TCPServerHandler;

import java.net.SocketAddress;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TCPServerHandlerTest {

    private TCPServerHandler tcpServerHandler;

    private ChannelHandlerContext channelHandlerContext;

    private Channel channel;

    private SocketAddress remoteAddress;

    @Before
    public void setUp() throws Exception {
        tcpServerHandler = new TCPServerHandler();
        tcpServerHandler.setChannelRepository(new ChannelRepository());

        channelHandlerContext = mock(ChannelHandlerContext.class);
        channel = mock(Channel.class);
        remoteAddress = mock(SocketAddress.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testChannelActive() throws Exception {
        when(channelHandlerContext.channel()).thenReturn(channel);
        when(channelHandlerContext.channel().remoteAddress()).thenReturn(remoteAddress);
        tcpServerHandler.channelActive(channelHandlerContext);
    }

    @Test
    public void testChannelRead() throws Exception {
        when(channelHandlerContext.channel()).thenReturn(channel);
        tcpServerHandler.channelRead(channelHandlerContext, "test message.");
    }

    @Test
    public void testExceptionCaught() throws Exception {

    }
}