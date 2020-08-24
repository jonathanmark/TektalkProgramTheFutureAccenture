
#ifndef TENSORFLOW_EXAMPLES_ANDROID_JNI_OBJECT_TRACKING_GL_UTILS_H_
#define TENSORFLOW_EXAMPLES_ANDROID_JNI_OBJECT_TRACKING_GL_UTILS_H_

#include <GLES/gl.h>
#include <GLES/glext.h>

#include "tensorflow/examples/android/jni/object_tracking/geom.h"

namespace tf_tracking {

// Draws a box at the given position.
inline static void DrawBox(const BoundingBox& bounding_box) {
  const GLfloat line[] = {
      bounding_box.left_, bounding_box.bottom_,
      bounding_box.left_, bounding_box.top_,
      bounding_box.left_, bounding_box.top_,
      bounding_box.right_, bounding_box.top_,
      bounding_box.right_, bounding_box.top_,
      bounding_box.right_, bounding_box.bottom_,
      bounding_box.right_, bounding_box.bottom_,
      bounding_box.left_, bounding_box.bottom_
  };

  glVertexPointer(2, GL_FLOAT, 0, line);
  glEnableClientState(GL_VERTEX_ARRAY);

  glDrawArrays(GL_LINES, 0, 8);
}



inline static void MapWorldSquareToUnitSquare(const BoundingSquare& square) {
  glScalef(square.size_, square.size_, 1.0f);
  glTranslatef(square.x_ / square.size_, square.y_ / square.size_, 0.0f);
}

}

#endif
