

#ifndef TENSORFLOW_EXAMPLES_ANDROID_JNI_OBJECT_TRACKING_FRAME_PAIR_H_
#define TENSORFLOW_EXAMPLES_ANDROID_JNI_OBJECT_TRACKING_FRAME_PAIR_H_

#include "tensorflow/examples/android/jni/object_tracking/keypoint.h"

namespace tf_tracking {


class FramePair {
 public:
  FramePair()
      : start_time_(0),
        end_time_(0),
        number_of_keypoints_(0) {}

  void Init(const int64_t start_time, const int64_t end_time);

  void AdjustBox(const BoundingBox box,
                 float* const translation_x,
                 float* const translation_y,
                 float* const scale_x,
                 float* const scale_y) const;

 private:

  Point2f GetWeightedMedian(const float* const weights,
                            const Point2f* const deltas) const;

  float GetWeightedMedianScale(const float* const weights,
                               const Point2f* const deltas) const;


  int FillWeights(const BoundingBox& box,
                  float* const weights) const;


  void FillTranslations(Point2f* const translations) const;

  int FillScales(const Point2f& old_center,
                 const Point2f& translation,
                 float* const weights,
                 Point2f* const scales) const;


 public:

  int64_t start_time_;

  int64_t end_time_;


  Keypoint frame1_keypoints_[kMaxKeypoints];

  Keypoint frame2_keypoints_[kMaxKeypoints];

  int number_of_keypoints_;


  bool optical_flow_found_keypoint_[kMaxKeypoints];

 private:
  TF_DISALLOW_COPY_AND_ASSIGN(FramePair);
};

}  // namespace tf_tracking

#endif  // TENSORFLOW_EXAMPLES_ANDROID_JNI_OBJECT_TRACKING_FRAME_PAIR_H_
