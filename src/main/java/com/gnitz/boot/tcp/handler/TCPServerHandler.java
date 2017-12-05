package com.gnitz.boot.tcp.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.gnitz.boot.tcp.ChannelRepository;

@Component
@Qualifier("tcpServerHandler")
@ChannelHandler.Sharable
public class TCPServerHandler extends ChannelInboundHandlerAdapter {

	@Autowired
	private ChannelRepository channelRepository;

	private static Logger logger = Logger.getLogger(TCPServerHandler.class.getName());

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Assert.notNull(this.channelRepository,
				"[Assertion failed] - ChannelRepository is required; it must not be null");

		ctx.fireChannelActive();
		logger.debug(ctx.channel().remoteAddress());
		String channelKey = ctx.channel().remoteAddress().toString();
		channelRepository.put(channelKey, ctx.channel());

		ctx.writeAndFlush("Your channel key is " + channelKey + "\n\r");

		logger.debug("Binded Channel Count is " + this.channelRepository.size());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		/*
		 * String stringMessage = (String) msg;
		 * 
		 * logger.debug(stringMessage);
		 * 
		 * String[] splitMessage = stringMessage.split("::");
		 * 
		 * if ( splitMessage.length != 2 ) { ctx.channel().writeAndFlush(stringMessage +
		 * "\n\r"); return; }
		 * 
		 * if ( channelRepository.get(splitMessage[0]) != null ) {
		 * channelRepository.get(splitMessage[0]).writeAndFlush(splitMessage[1] +
		 * "\n\r"); }
		 */

		ByteBuf byteBuf = (ByteBuf) msg;
		while (byteBuf.isReadable()) {
			int byLen = byteBuf.readableBytes();
			byte[] hexByte = new byte[byLen];
			byteBuf.readBytes(hexByte);
			String hexStr = Hex.encodeHexString(hexByte).toUpperCase();
			logger.debug("Received Response: " + hexStr);
			ctx.writeAndFlush(Unpooled.wrappedBuffer(hexByte));
		}

		byteBuf.release();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error(cause.getMessage(), cause);
		// ctx.close();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		Assert.notNull(this.channelRepository,
				"[Assertion failed] - ChannelRepository is required; it must not be null");
		Assert.notNull(ctx, "[Assertion failed] - ChannelHandlerContext is required; it must not be null");

		String channelKey = ctx.channel().remoteAddress().toString();
		this.channelRepository.remove(channelKey);

		logger.debug("Binded Channel Count is " + this.channelRepository.size());
	}

	public void setChannelRepository(ChannelRepository channelRepository) {
		this.channelRepository = channelRepository;
	}
}
