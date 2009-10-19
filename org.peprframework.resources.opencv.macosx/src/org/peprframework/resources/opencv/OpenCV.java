/**
 * Copyright 2009 pepr Framework
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.peprframework.resources.opencv;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.peprframework.resources.opencv.OpenCVCore.CvArr;
import org.peprframework.resources.opencv.OpenCVCore.CvMemStorage;
import org.peprframework.resources.opencv.OpenCVCore.CvPoint2D32f;
import org.peprframework.resources.opencv.OpenCVCore.CvRect;
import org.peprframework.resources.opencv.OpenCVCore.CvSeq;
import org.peprframework.resources.opencv.OpenCVCore.CvSize;
import org.peprframework.resources.opencv.OpenCVCore.CvTermCriteria;
import org.peprframework.resources.opencv.internal.LibraryHelper;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author Volker Fritzsch
 * @version 1.0
 * 
 */
public interface OpenCV extends Library {

	public OpenCV INSTANCE = LibraryHelper.loadLibrary("/lib/OpenCV", OpenCV.class);

	public static class CvHaarClassifier extends Structure {

		public CvHaarClassifier() {
			super();
		}

		public CvHaarClassifier(com.sun.jna.Pointer pointer, int offset) {
			super();
			useMemory(pointer, offset);
			read();
		}

		public CvHaarClassifier(CvHaarClassifier struct) {
			this(struct.getPointer(), 0);
		}

		public static class ByReference extends CvHaarClassifier implements
				Structure.ByReference {

			public ByReference() {
				super();
			}

			public ByReference(CvHaarClassifier struct) {
				super(struct.getPointer(), 0);
			}

		}

		public static class ByValue extends CvHaarClassifier implements
				Structure.ByValue {

			public ByValue() {
				super();
			}

			public ByValue(CvHaarClassifier struct) {
				super(struct.getPointer(), 0);
			}

		}

		public int count;

		public CvHaarFeature.ByReference haar_feature;

		public com.sun.jna.ptr.FloatByReference threshold;

		public com.sun.jna.ptr.IntByReference left;

		public com.sun.jna.ptr.IntByReference right;

		public com.sun.jna.ptr.FloatByReference alpha;

	}

	public static class CvHaarClassifierCascade extends Structure {

		public CvHaarClassifierCascade() {
			super();
		}

		public CvHaarClassifierCascade(Pointer pointer, int offset) {
			super();
			useMemory(pointer, offset);
			read();
		}

		public CvHaarClassifierCascade(CvHaarClassifierCascade struct) {
			this(struct.getPointer(), 0);
		}

		public static class ByReference extends CvHaarClassifierCascade
				implements Structure.ByReference {

			public ByReference() {
				super();
			}

			public ByReference(CvHaarClassifierCascade struct) {
				super(struct.getPointer(), 0);
			}
		}

		public static class ByValue extends CvHaarClassifierCascade implements
				Structure.ByValue {

			public ByValue() {
				super();
			}

			public ByValue(CvHaarClassifierCascade struct) {
				super(struct.getPointer(), 0);
			}
		}

		public int flags;

		public int count;

		public CvSize.ByValue orig_window_size;

		public CvSize.ByValue real_window_size;

		public double scale;

		public CvHaarStageClassifier.ByReference stage_classifier;

		public Pointer hid_cascade;

	}

	public static class CvHaarFeature extends Structure {

		public CvHaarFeature() {
			super();
		}

		public CvHaarFeature(Pointer pointer, int offset) {
			super();
			useMemory(pointer, offset);
			read();
		}

		public CvHaarFeature(CvHaarFeature struct) {
			this(struct.getPointer(), 0);
		}

		public static class ByReference extends CvHaarFeature implements
				Structure.ByReference {

			public ByReference() {
				super();
			}

			public ByReference(CvHaarFeature struct) {
				super(struct.getPointer(), 0);
			}

		}

		public static class ByValue extends CvHaarFeature implements
				Structure.ByValue {

			public ByValue() {
				super();
			}

			public ByValue(CvHaarFeature struct) {
				super(struct.getPointer(), 0);
			}

		}

		public int tilted;

		public rect_struct.ByReference[] rect = new rect_struct.ByReference[(3)];

		public static class rect_struct extends com.sun.jna.Structure {

			public rect_struct() {
				super();
			}

			public rect_struct(com.sun.jna.Pointer pointer, int offset) {
				super();
				useMemory(pointer, offset);
				read();
			}

			public rect_struct(rect_struct struct) {
				this(struct.getPointer(), 0);
			}

			public static class ByReference extends rect_struct implements
					Structure.ByReference {

				public ByReference() {
					super();
				}

				public ByReference(rect_struct struct) {
					super(struct.getPointer(), 0);
				}

			}

			public static class ByValue extends rect_struct implements
					Structure.ByValue {

				public ByValue() {
					super();
				}

				public ByValue(rect_struct struct) {
					super(struct.getPointer(), 0);
				}

			}

			public CvRect.ByValue r;

			public float weight;

		}

	}

	public static class CvHaarStageClassifier extends Structure {

		public CvHaarStageClassifier() {
			super();
		}

		public CvHaarStageClassifier(Pointer pointer, int offset) {
			super();
			useMemory(pointer, offset);
			read();
		}

		public CvHaarStageClassifier(CvHaarStageClassifier struct) {
			this(struct.getPointer(), 0);
		}

		public static class ByReference extends CvHaarStageClassifier implements
				Structure.ByReference {

			public ByReference() {
				super();
			}

			public ByReference(CvHaarStageClassifier struct) {
				super(struct.getPointer(), 0);
			}

		}

		public static class ByValue extends CvHaarStageClassifier implements
				Structure.ByValue {

			public ByValue() {
				super();
			}

			public ByValue(CvHaarStageClassifier struct) {
				super(struct.getPointer(), 0);
			}

		}

		public int count;

		public float threshold;

		public CvHaarClassifier.ByReference classifier;

		public int next;

		public int child;

		public int parent;

	}

	public static final int CV_CAP_ANY = 0;

	public static final int CV_CAP_PROP_BRIGHTNESS = 10;

	public static final int CV_CAP_PROP_CONTRAST = 11;

	public static final int CV_CAP_PROP_CONVERT_RGB = 15;

	public static final int CV_CAP_PROP_FORMAT = 8;

	public static final int CV_CAP_PROP_FOURCC = 6;

	public static final int CV_CAP_PROP_FPS = 5;

	public static final int CV_CAP_PROP_FRAME_COUNT = 7;

	public static final int CV_CAP_PROP_FRAME_HEIGHT = 4;

	public static final int CV_CAP_PROP_FRAME_WIDTH = 3;

	public static final int CV_CAP_PROP_GAIN = 14;

	public static final int CV_CAP_PROP_HUE = 13;

	public static final int CV_CAP_PROP_MODE = 9;

	public static final int CV_CAP_PROP_POS_AVI_RATIO = 2;

	public static final int CV_CAP_PROP_POS_FRAMES = 1;

	public static final int CV_CAP_PROP_POS_MSEC = 0;

	public static final int CV_CAP_PROP_SATURATION = 12;

	public static final int CV_INTER_AREA = 3;

	public static final int CV_INTER_CUBIC = 2;

	public static final int CV_INTER_LINEAR = 1;

	public static final int CV_INTER_NN = 0;

	public static final int IPL_DEPTH_8U = 8;

	public static final int CV_TERMCRIT_ITER = 1;

	public static final int CV_TERMCRIT_EPS = 2;

	public static final int IPL_DEPTH_32F = 32;

	public static final int CV_CVTIMG_FLIP = 1;

	public static final int CV_AA = 16;

	public void cvReleaseImage(PointerByReference image);

	public void cvResize(CvArr src, CvArr dst, int interpolation);

	public CvSeq cvHaarDetectObjects(CvArr image, /* CvHaarClassifierCascade */
	Pointer cascade, CvMemStorage storage, double scale_factor,
			int min_neighbors, int flags, CvSize.ByValue min_size);

	public void cvGoodFeaturesToTrack(CvArr image, CvArr eig_image,
			CvArr temp_image, CvPoint2D32f[] corners, IntBuffer corner_count,
			double quality_level, double min_distance, CvArr mask,
			int block_size, int use_harris, double k);

	public void cvCalcOpticalFlowPyrLK(CvArr prev, CvArr curr, CvArr prev_pyr,
			CvArr curr_pyr, CvPoint2D32f[] prev_features,
			CvPoint2D32f[] curr_features, int count, CvSize.ByValue win_size,
			int level, ByteBuffer status, FloatBuffer track_error,
			CvTermCriteria.ByValue criteria, int flags);

}
