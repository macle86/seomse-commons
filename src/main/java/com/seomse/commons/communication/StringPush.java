




package com.seomse.commons.communication;

import com.seomse.commons.handler.ExceptionHandler;
import com.seomse.commons.utils.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * <pre>
 *  파 일 명 : StringPush.java
 *  설    명 : (문자열) 푸시
 *
 *  작 성 자 : macle
 *  작 성 일 : 2018.04
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 * @author Copyrights 2018 by ㈜섬세한사람들. All right reserved.
 */
public class StringPush {
	private static final Logger logger = LoggerFactory.getLogger(StringPush.class);

	private String hostAddress;
	private int port;
	
	private Socket socket;			
	private OutputStreamWriter send;

	private ExceptionHandler exceptionHandler = null;
	/**
	 * 생성자
	 * @param socket socket
	 */
	public StringPush(Socket socket) throws IOException{
		this.socket = socket; 
		send = new OutputStreamWriter(socket.getOutputStream(), CommunicationDefault.CHAR_SET);
		this.hostAddress = socket.getInetAddress().getHostAddress();
		this.port = socket.getPort();
		
	}
	
	/**
	 * 생성자
	 * @param hostAddress hostAddress
	 * @param port port
	 */
	public StringPush(String hostAddress, int port){
		this.hostAddress = hostAddress;
		this.port = port;
	}

	/**
	 * 예외 핸들러 설정
	 * @param exceptionHandler exceptionHandler
	 */
	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	/**
	 * 서버와 연결한다.
	 * 문자열 전송이 완료되면 반드시 disconnet() 메소드를 호출해야 한다.
	 * @return 연결 성공실패 여부
	 */
	public boolean connect(){
		if(socket == null || socket.isClosed()){
			try{
				socket = new Socket(hostAddress, port);
				send =  new OutputStreamWriter(socket.getOutputStream(), CommunicationDefault.CHAR_SET);
			}catch(Exception e){
				ExceptionUtil.exception(e,logger, exceptionHandler);
				return false;
			}		
		}

		return true;

	}
	
	/**
	 * 연결을 해제한다. 다시 연결할때는 connect()메소드를 호출한다.
	 */
	public void disConnect(){
		
		try{
			if(send != null ){
				send.close();	
			}
		}catch(Exception e){
			ExceptionUtil.exception(e,logger, exceptionHandler);
		}
		send = null;
		try{
			if(socket != null && !socket.isClosed()){
				socket.close();		
			
			}
		}catch(Exception e){
			ExceptionUtil.exception(e,logger, exceptionHandler);
		}
		socket = null;
	}
	
	/**
	 * 서버에 문자열 형태의 메시지를 전송한다.
	 * @param msg sendMessage
	 * @return 메시지 전송 성공실패여부
	 */
	public boolean sendMessage(String msg){	
		try{
			send.write(msg);
			send.flush();
		
			return true;
		}catch(Exception e){
			ExceptionUtil.exception(e,logger, exceptionHandler);
			return false;
		}
	}
	
}