/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class javax_microedition_m3g_MorphingMesh */

#ifndef _Included_javax_microedition_m3g_MorphingMesh
#define _Included_javax_microedition_m3g_MorphingMesh
#ifdef __cplusplus
extern "C" {
#endif
#undef javax_microedition_m3g_MorphingMesh_NONE
#define javax_microedition_m3g_MorphingMesh_NONE 144L
#undef javax_microedition_m3g_MorphingMesh_ORIGIN
#define javax_microedition_m3g_MorphingMesh_ORIGIN 145L
#undef javax_microedition_m3g_MorphingMesh_X_AXIS
#define javax_microedition_m3g_MorphingMesh_X_AXIS 146L
#undef javax_microedition_m3g_MorphingMesh_Y_AXIS
#define javax_microedition_m3g_MorphingMesh_Y_AXIS 147L
#undef javax_microedition_m3g_MorphingMesh_Z_AXIS
#define javax_microedition_m3g_MorphingMesh_Z_AXIS 148L
/*
 * Class:     javax_microedition_m3g_MorphingMesh
 * Method:    _ctor
 * Signature: (II[I[I[I)I
 */
JNIEXPORT jint JNICALL Java_javax_microedition_m3g_MorphingMesh__1ctor
  (JNIEnv *, jclass, jint, jint, jintArray, jintArray, jintArray);

/*
 * Class:     javax_microedition_m3g_MorphingMesh
 * Method:    _setWeights
 * Signature: (I[F)V
 */
JNIEXPORT void JNICALL Java_javax_microedition_m3g_MorphingMesh__1setWeights
  (JNIEnv *, jclass, jint, jfloatArray);

/*
 * Class:     javax_microedition_m3g_MorphingMesh
 * Method:    _getWeights
 * Signature: (I[F)V
 */
JNIEXPORT void JNICALL Java_javax_microedition_m3g_MorphingMesh__1getWeights
  (JNIEnv *, jclass, jint, jfloatArray);

/*
 * Class:     javax_microedition_m3g_MorphingMesh
 * Method:    _getMorphTarget
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_javax_microedition_m3g_MorphingMesh__1getMorphTarget
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     javax_microedition_m3g_MorphingMesh
 * Method:    _getMorphTargetCount
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_javax_microedition_m3g_MorphingMesh__1getMorphTargetCount
  (JNIEnv *, jclass, jint);

#ifdef __cplusplus
}
#endif
#endif
