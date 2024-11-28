#include <jni.h>

JNIEXPORT jdouble JNICALL
Java_com_example_specequipmentapp_util_NativeUtils_calculateTotalPrice(
        JNIEnv *env, jobject thiz, jintArray quantities, jdoubleArray prices) {
    // Получаем длину массива
    jsize length = (*env)->GetArrayLength(env, quantities);

    // Получаем элементы массивов
    jint *q = (*env)->GetIntArrayElements(env, quantities, NULL);
    jdouble *p = (*env)->GetDoubleArrayElements(env, prices, NULL);

    jdouble total = 0;

    for (jsize i = 0; i < length; i++) {
        total += q[i] * p[i];
    }

    // Освобождаем массивы
    (*env)->ReleaseIntArrayElements(env, quantities, q, 0);
    (*env)->ReleaseDoubleArrayElements(env, prices, p, 0);

    return total;
}
