#include <jni.h>

//For first API key 
JNIEXPORT jstring JNICALL Java_com_d2dreamdevelopers_myapplication_APIKeyLibrary_getAPIKey(JNIEnv *env, jobject instance) { 
return (*env)-> NewStringUTF(env, "RGAPI-e8772642-146c-4ba2-994c-62a477063a05");
}

JNIEXPORT jstring JNICALL
Java_com_dreamsphere_pickem_Activities_MainActivities_SettingsActivity_getKeys(JNIEnv *env,
                                                                               jobject thiz) {
    return (*env)-> NewStringUTF(env, "RGAPI-e8772642-146c-4ba2-994c-62a477063a05");
}

JNIEXPORT jstring JNICALL
Java_com_dreamsphere_pickem_NotificationsService_AlarmReceiver_getKeys(JNIEnv *env, jobject thiz) {
    return (*env)-> NewStringUTF(env, "RGAPI-e8772642-146c-4ba2-994c-62a477063a05");
}

JNIEXPORT jstring JNICALL
Java_com_dreamsphere_pickem_Activities_EloTracker_EloTrackerActivity_getKeys(JNIEnv *env, jobject thiz) {
    return (*env)-> NewStringUTF(env, "RGAPI-e8772642-146c-4ba2-994c-62a477063a05");
    // TODO: implement getKeys()
}

