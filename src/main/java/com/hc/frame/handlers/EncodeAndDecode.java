package com.hc.frame.handlers;

import java.io.UnsupportedEncodingException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class EncodeAndDecode {

	/**
	 * 解码
	 * 将客户端和服务端之间通信的信息进行解码，
	 * 即从ByteBuf -> String 进行转换
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public static String decode(Object msg) throws Exception{
		ByteBuf buf = (ByteBuf)msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);	
		String body = new String(req, "UTF-8"); //防止中文乱码
		return body;
	}
	
	/**
	 * 编码
	 * 从String -> ByteBuf进行转换
	 * @param st
	 * @return
	 */
	public static ByteBuf encode(String st) {
		ByteBuf bf = Unpooled.buffer(st.length());
		byte[] result;
		try {
			result = st.getBytes("UTF-8");  //防止中文乱码
			bf.writeBytes(result);
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}  
		return bf;
	}
	
}
