#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

#include "jni.h"
#include "uart.h"
#include "jserial_kySerialDrv.h"

/*
 * Class:     jserial_kyserial
 * Method:    serial_open
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_jserial_kySerialDrv_serial_1open(JNIEnv *env, jobject obj, jstring name, jstring rate)
{
  const char *dev = (*env)->GetStringUTFChars(env, name, NULL);
  if(dev == NULL) {
    printf("null pointer error!\n");
    (*env)->ReleaseStringUTFChars(env, name, dev);
    (*env)->ReleaseStringUTFChars(env, rate, NULL);
    return -1;
  }

  const char *baud = (*env)->GetStringUTFChars(env, rate, NULL);
  if(baud == NULL) {
    printf("invalid baudrate!\n");
    (*env)->ReleaseStringUTFChars(env, name, dev);
    (*env)->ReleaseStringUTFChars(env, rate, baud);
    return -1;
  }

  if(uart_open(dev, baud) != EXIT_SUCCESS) {
    printf("failed to open device %s.\n", dev);
    (*env)->ReleaseStringUTFChars(env, name, dev);
    (*env)->ReleaseStringUTFChars(env, rate, baud);
    return -1;
  }
  return 0;
}

/*
 * Class:     jserial_kyserial
 * Method:    serial_baud
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_jserial_kySerialDrv_serial_1baud(JNIEnv *env, jobject obj, jstring rate)
{
  int ret = 0;
  const char *baud = (*env)->GetStringUTFChars(env, rate, NULL);
  if(baud == NULL) {
    printf("invalid baudrate!\n");
    ret = -1;
    goto error;
  }

  if(uart_baudrate(baud) != EXIT_SUCCESS) {
    printf("failed to set baudrate.\n");
    ret = -1;
  }

error:
  (*env)->ReleaseStringUTFChars(env, rate, baud);
  return ret;
}

/*
 * Class:     jserial_kyserial
 * Method:    serial_send
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_jserial_kySerialDrv_serial_1send(JNIEnv *env, jobject obj, jbyteArray buf, jint len)
{
  int ret = 0;
  jbyte* p = (*env)->GetByteArrayElements(env, buf, 0);
  jsize size = (*env)->GetArrayLength(env, buf);
  if(len < size) size = len;
  ret = uart_write((char *)p, (size_t)size);
  (*env)->ReleaseByteArrayElements(env, buf, p, 0);
  return ret;
}

/*
 * Class:     jserial_kyserial
 * Method:    serial_read
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_jserial_kySerialDrv_serial_1read(JNIEnv *env, jobject obj, jbyteArray buf, jint len)
{
  int ret = 0;
  jbyte* p = (*env)->GetByteArrayElements(env, buf, NULL);
  ret = uart_read((char *)p, len);
  if(ret > 0) {
    (*env)->SetByteArrayRegion(env, buf, 0, ret, p);
  }
  return ret;
}

/*
 * Class:     jserial_kyserial
 * Method:    serial_block
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_jserial_kySerialDrv_serial_1block(JNIEnv *env, jobject obj, jlong sec, jlong us)
{
  return uart_block_time(sec, us);
}

/*
 * Class:     jserial_kyserial
 * Method:    serial_flush_read
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_jserial_kySerialDrv_serial_1flush_1read(JNIEnv *env, jobject obj)
{
  uart_flush_read();
  return 0;
}

/*
 * Class:     jserial_kyserial
 * Method:    serial_flush_write
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_jserial_kySerialDrv_serial_1flush_1write(JNIEnv *env, jobject obj)
{
  uart_flush_write();
  return 0;
}

/*
 * Class:     jserial_kyserial
 * Method:    serial_close
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_jserial_kySerialDrv_serial_1close(JNIEnv *env, jobject obj)
{
  uart_close();
  return 0;
}

