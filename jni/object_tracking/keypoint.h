

#ifndef TENSORFLOW_EXAMPLES_ANDROID_JNI_OBJECT_TRACKING_KEYPOINT_H_
#define TENSORFLOW_EXAMPLES_ANDROID_JNI_OBJECT_TRACKING_KEYPOINT_H_

#include "tensorflow/examples/android/jni/object_tracking/geom.h"
#include "tensorflow/examples/android/jni/object_tracking/image-inl.h"
#include "tensorflow/examples/android/jni/object_tracking/image.h"
#include "tensorflow/examples/android/jni/object_tracking/logging.h"
#include "tensorflow/examples/android/jni/object_tracking/time_log.h"
#include "tensorflow/examples/android/jni/object_tracking/utils.h"

#include "tensorflow/examples/android/jni/object_tracking/config.h"

namespace tf_tracking {

// For keeping track of keypoints.
struct Keypoint {
  Keypoint() : pos_(0.0f, 0.0f), score_(0.0f), type_(0) {}
  Keypoint(const float x, const float y)
      : pos_(x, y), score_(0.0f), type_(0) {}

  Point2f pos_;
  float score_;
  uint8_t type_;
};

inline std::ostream& operator<<(std::ostream& stream, const Keypoint keypoint) {
  return stream << "[" << keypoint.pos_ << ", "
      << keypoint.score_ << ", " << keypoint.type_ << "]";
}

}

#endif
