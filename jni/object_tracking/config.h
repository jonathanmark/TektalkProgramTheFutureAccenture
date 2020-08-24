
#ifndef TENSORFLOW_EXAMPLES_ANDROID_JNI_OBJECT_TRACKING_CONFIG_H_
#define TENSORFLOW_EXAMPLES_ANDROID_JNI_OBJECT_TRACKING_CONFIG_H_

#include <math.h>

#include "tensorflow/examples/android/jni/object_tracking/geom.h"

namespace tf_tracking {


enum KeypointType {
  KEYPOINT_TYPE_DEFAULT = 0,
  KEYPOINT_TYPE_FAST = 1,
  KEYPOINT_TYPE_INTEREST = 2
};

struct MatchScore {
  explicit MatchScore(double val) : value(val) {}
  MatchScore() { value = 0.0; }

  double value;

  MatchScore& operator+(const MatchScore& rhs) {
    value += rhs.value;
    return *this;
  }

  friend std::ostream& operator<<(std::ostream& stream,
                                  const MatchScore& detection) {
    stream << detection.value;
    return stream;
  }
};
inline bool operator< (const MatchScore& cC1, const MatchScore& cC2) {
    return cC1.value < cC2.value;
}
inline bool operator> (const MatchScore& cC1, const MatchScore& cC2) {
    return cC1.value > cC2.value;
}
inline bool operator>= (const MatchScore& cC1, const MatchScore& cC2) {
    return cC1.value >= cC2.value;
}
inline bool operator<= (const MatchScore& cC1, const MatchScore& cC2) {
    return cC1.value <= cC2.value;
}

// Fixed seed used for all random number generators.
static const int kRandomNumberSeed = 11111;

// TODO(andrewharp): Move as many of these settings as possible into a settings
// object which can be passed in from Java at runtime.

// Whether or not to use ESM instead of LK flow.
static const bool kUseEsm = false;

// This constant gets added to the diagonal of the Hessian
// before solving for translation in 2dof ESM.
// It ensures better behavior especially in the absence of
// strong texture.
static const int kEsmRegularizer = 20;

// Do we want to brightness-normalize each keypoint patch when we compute
// its flow using ESM?
static const bool kDoBrightnessNormalize = true;


#define USE_FIXED_POINT_FLOW 1


#define NORMALIZE 1

// Number of keypoints to store per frame.
static const int kMaxKeypoints = 76;

// Keypoint detection.
static const int kMaxTempKeypoints = 1024;

// Number of floats each keypoint takes up when exporting to an array.
static const int kKeypointStep = 7;

// Number of frame deltas to keep around in the circular queue.
static const int kNumFrames = 512;

// Number of iterations to do tracking on each keypoint at each pyramid level.
static const int kNumIterations = 3;

// The number of bins (on a side) to divide each bin from the previous
// cache level into.  Higher numbers will decrease performance by increasing
// cache misses, but mean that cache hits are more locally relevant.
static const int kCacheBranchFactor = 2;

// Number of levels to put in the cache.
// Each level of the cache is a square grid of bins, length:
// branch_factor^(level - 1) on each side.
//
// This may be greater than kNumPyramidLevels. Setting it to 0 means no
// caching is enabled.
static const int kNumCacheLevels = 3;

// The level at which the cache pyramid gets cut off and replaced by a matrix
// transform if such a matrix has been provided to the cache.
static const int kCacheCutoff = 1;

static const int kNumPyramidLevels = 4;


static const int kMaxKeypointsForObject = 16;


static const int kMinNumPyramidLevelsToUseForAdjustment = 1;


static const int kFlowIntegrationWindowSize = 3;


static const int kFlowArraySize =
    (2 * kFlowIntegrationWindowSize + 1) * (2 * kFlowIntegrationWindowSize + 1);


static const float kTrackingAbortThreshold = 0.03f;


static const float kNumDeviations = 2.0f;

static const float kMaxForwardBackwardErrorAllowed = 0.5f;

// Threshold for pixels to be considered different.
static const int kFastDiffAmount = 10;

// How far from edge of frame to stop looking for FAST keypoints.
static const int kFastBorderBuffer = 10;


static const bool kAddArbitraryKeypoints = true;


static const int kNumToAddAsCandidates = 1;


static const float kClosestPercent = 0.6f;


static const int kMinNumConnectedForFastKeypoint = 8;


static const int kHarrisWindowSize = 2;



static const MatchScore kMatchScoreBuffer(0.01f);


static const MatchScore kMinimumMatchScore(0.5f);

static const float kMinimumCorrelationForTracking = 0.4f;

static const MatchScore kMatchScoreForImmediateTermination(0.0f);


static const int kDetectEveryNFrames = 4;


static const int kFeaturesPerFeatureSet = 10;


static const int kNumFeatureSets = 7;


static const int kNumFeatureSetsForCandidate = 2;


static const int kNormalizedThumbnailSize = 11;


static const float kPositionOverlapThreshold = 0.6f;


static const int kMaxNumDetectionFailures = 4;



static const float kScanMinSquareSize = 16.0f;


static const float kScanMaxSquareSize = 64.0f;


static const float kScanScaleFactor = sqrtf(2.0f);


static const int kScanStepSize = 10;



static const float kLockedScaleFactor = 1 / sqrtf(2.0f);


static const float kUnlockedScaleFactor = 1 / 2.0f;


static const float kLastKnownPositionScaleFactor = 1.0f / sqrtf(2.0f);


static const float kMinCorrelationForNewExample = 0.75f;
static const float kMaxCorrelationForNewExample = 0.99f;



static const int kFreeTries = 5;


static const int kFalsePositivePenalty = 5;

struct ObjectDetectorConfig {
  const Size image_size;

  explicit ObjectDetectorConfig(const Size& image_size)
      : image_size(image_size) {}
  virtual ~ObjectDetectorConfig() = default;
};

struct KeypointDetectorConfig {
  const Size image_size;

  bool detect_skin;

  explicit KeypointDetectorConfig(const Size& image_size)
      : image_size(image_size),
        detect_skin(false) {}
};


struct OpticalFlowConfig {
  const Size image_size;

  explicit OpticalFlowConfig(const Size& image_size)
      : image_size(image_size) {}
};

struct TrackerConfig {
  const Size image_size;
  KeypointDetectorConfig keypoint_detector_config;
  OpticalFlowConfig flow_config;
  bool always_track;

  float object_box_scale_factor_for_features;

  explicit TrackerConfig(const Size& image_size)
      : image_size(image_size),
        keypoint_detector_config(image_size),
        flow_config(image_size),
        always_track(false),
        object_box_scale_factor_for_features(1.0f) {}
};

}

#endif
