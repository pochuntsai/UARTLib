#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <errno.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <string.h>
#include <stdint.h>
#include <termios.h>
#include <android/log.h>
#include <sys/ioctl.h>

#undef	TCSAFLUSH
#define	TCSAFLUSH	TCSETSF
#ifndef	_TERMIOS_H_
#define	_TERMIOS_H_
#endif

int fd;
struct termios newtio, oldtio;

#define LOG_TAG "Brian"
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, fmt, ##args)


JNIEXPORT jint JNICALL Java_idv_android_hellouart_Uart2C_openUart(JNIEnv *env,jobject mc, jstring portName)
{
	const char *str1 = "/dev/";
	char *str2 = (char*)(*env)->GetStringUTFChars(env , portName, NULL);
	char *sall = (char*) malloc(strlen(str1) + strlen(str2) + 1);

	strcpy(sall, str1);
	strcat(sall, str2);

	fd = open(sall, O_RDWR | O_NOCTTY | O_NDELAY);
	LOGI("JNI open uart port device node = %s , fdnum=%d \n",sall,fd);
	(*env)->ReleaseStringUTFChars(env , portName, str2);
	free(sall);

	return fd;
}

JNIEXPORT void JNICALL Java_idv_android_hellouart_Uart2C_closeUart(JNIEnv *env,jobject mc, jint i)
{
	LOGI("******Brian:close*****");
	close(i);
}

JNIEXPORT jint JNICALL Java_idv_android_hellouart_Uart2C_setUart(JNIEnv *env,jobject mc, jint i)
{
	int Baud_rate[] = { B9600, B115200 , B19200, B230400};


	LOGI("JNI UART Set baudrate is %d",Baud_rate[i]);
	tcgetattr(fd, &oldtio);
	tcgetattr(fd, &newtio);
	cfsetispeed(&newtio, Baud_rate[i]);
	cfsetospeed(&newtio, Baud_rate[i]);

	newtio.c_lflag = 0;
	newtio.c_cflag = Baud_rate[i] | CS8 | CREAD | CLOCAL;
	//newtio.c_cflag = B9600 | CS8 | CREAD | CLOCAL;
	//newtio.c_iflag = BRKINT | IGNPAR | IXON | IXOFF | IXANY; //Brian+: org: enable soft flow control
	newtio.c_iflag = BRKINT | IGNPAR;
	newtio.c_oflag = 02;
	newtio.c_line = 0;
	newtio.c_cc[7] = 255;
	newtio.c_cc[4] = 0;
	newtio.c_cc[5] = 0;

	if (tcsetattr(fd, TCSANOW, &newtio) < 0)
	{
		printf("tcsetattr2 fail !\n");
		exit(1);
	}
	return fd;
}

JNIEXPORT jint JNICALL Java_idv_android_hellouart_Uart2C_sendMsgUart(JNIEnv *env,jobject mc, jint fd_num, jbyteArray inByte)
{
	int len;
	jboolean isCopy;
	jbyte *a = (*env)->GetByteArrayElements(env, inByte, &isCopy);
	len = (*env)->GetArrayLength(env, inByte);
	char *buf = (char*)a;

	//buf = (*env)->GetStringUTFChars(env, str, NULL);
	//len = (*env)->GetStringLength(env, str);
	//LOGI("*** sendMsgUart buf = %s",buf);
	fd = fd_num;
	write(fd, buf, len);
	(*env)->ReleaseByteArrayElements(env, inByte, a, 0);
	return 0;
}
JNIEXPORT jstring JNICALL Java_idv_android_hellouart_Uart2C_receiveMsgUart(JNIEnv *env,jobject mc, jint fd_num)
{
	char buffer[100];
	int len, size=100;
	memset(buffer, 0, sizeof(buffer));
	fd = fd_num;
	len = read(fd, buffer, size);

	if (len > 0)
	{
		//LOGI("*** receiveMsgUart %s",buffer);
		//buf[len]='\0';
		return (*env)->NewStringUTF(env, buffer);
	} else
		return NULL;
}

JNIEXPORT jbyteArray JNICALL Java_idv_android_hellouart_Uart2C_receiveMsgUartByte(JNIEnv *env,jobject mc, jint fd_num)
{
	char buffer[100];

	int len, size=100;
	memset(buffer, 0, sizeof(buffer));

	fd = fd_num;
	len = read(fd, buffer, size);
	jbyteArray result=(*env)->NewByteArray(env, size);

	if (len > 0)
	{
		//LOGI("*** receiveMsgUart %s",buffer);
		//buf[len]='\0';

		(*env)->SetByteArrayRegion(env, result, 0, size, buffer);
		return result;
		//return (*env)->NewStringUTF(env, buffer);
	} else
		return NULL;
}
