package idv.android.hellouart;

import android.util.Log;

public class Uart2C {
	static
	{
		try
		{
			System.loadLibrary("hello-uart");
			Log.i("JNI", "Trying to load libhello-uart.so");
		}
		catch(UnsatisfiedLinkError ule)
		{
			Log.i("JNI", "WARNING: could not to load libhello-uart.so");
		}
	}

	public static native int openUart(String portName);
	public static native void closeUart(int i);
	public static native int setUart(int i);
	public static native int sendMsgUart(int fd_num, byte[] inByte);
	public static native String receiveMsgUart(int fd_num);
	public static native byte[] receiveMsgUartByte(int fd_num);
}