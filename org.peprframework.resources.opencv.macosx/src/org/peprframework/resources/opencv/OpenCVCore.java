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

import org.peprframework.resources.opencv.internal.LibraryHelper;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * @author Volker Fritzsch
 * @version 1.0
 * 
 */
public interface OpenCVCore extends Library {

	public OpenCVCore INSTANCE = LibraryHelper.loadLibrary("/lib/OpenCV", OpenCVCore.class);

	public static class CvArr extends PointerType {

		public CvArr() {
			super();
		}

		public CvArr(Pointer p) {
			super(p);
		}

	}

	public static class CvMemBlock extends Structure {

		public CvMemBlock() {
			super();
		}

		public CvMemBlock(Pointer pointer, int offset) {
			super();
			useMemory(pointer, offset);
			read();
		}

		public CvMemBlock(CvMemBlock struct) {
			this(struct.getPointer(), 0);
		}

		public static class ByReference extends CvMemBlock implements
				Structure.ByReference {

			public ByReference() {
				super();
			}

			public ByReference(CvMemBlock struct) {
				super(struct.getPointer(), 0);
			}

		}

		public static class ByValue extends CvMemBlock implements
				Structure.ByValue {

			public ByValue() {
				super();
			}

			public ByValue(CvMemBlock struct) {
				super(struct.getPointer(), 0);
			}

		}

		public CvMemBlock.ByReference prev;

		public CvMemBlock.ByReference next;

	}

	public static class CvMemStorage extends Structure {

		public CvMemStorage() {
			super();
		}

		public CvMemStorage(Pointer pointer, int offset) {
			super();
			useMemory(pointer, offset);
			read();
		}

		public CvMemStorage(CvMemStorage struct) {
			this(struct.getPointer(), 0);
		}

		public static class ByReference extends CvMemStorage implements
				Structure.ByReference {

			public ByReference() {
				super();
			}

			public ByReference(CvMemStorage struct) {
				super(struct.getPointer(), 0);
			}

		}

		public static class ByValue extends CvMemStorage implements
				Structure.ByValue {

			public ByValue() {
				super();
			}

			public ByValue(CvMemStorage struct) {
				super(struct.getPointer(), 0);
			}

		}

		public int signature;
		/*
		 * First allocated block.
		 */
		public CvMemBlock.ByReference bottom;

		/*
		 * Current memory block - top of the stack.
		 */
		public CvMemBlock.ByReference top;

		/*
		 * We get new blocks from parent as needed.
		 */
		public CvMemStorage.ByReference parent;

		/*
		 * Block size.
		 */
		public int block_size;

		/*
		 * Remaining free space in current block.
		 */
		public int free_space;

	}

	public static class CvRect extends Structure {

		public CvRect() {
			super();
		}

		public CvRect(com.sun.jna.Pointer pointer, int offset) {
			super();
			useMemory(pointer, offset);
			read();
		}

		public CvRect(CvRect struct) {
			this(struct.getPointer(), 0);
		}

		public static class ByReference extends CvRect implements
				Structure.ByReference {

			public ByReference() {
				super();
			}

			public ByReference(CvRect struct) {
				super(struct.getPointer(), 0);
			}

		}

		public static class ByValue extends CvRect implements Structure.ByValue {

			public ByValue() {
				super();
			}

			public ByValue(CvRect struct) {
				super(struct.getPointer(), 0);
			}
		}

		public int x;

		public int y;

		public int width;

		public int height;

	}

	public static class CvSeq extends Structure {

		public CvSeq() {
			super();
		}

		public CvSeq(Pointer pointer, int offset) {
			super();
			useMemory(pointer, offset);
			read();
		}

		public CvSeq(CvSeq struct) {
			this(struct.getPointer(), 0);
		}

		public static class ByReference extends CvSeq implements
				Structure.ByReference {

			public ByReference() {
				super();
			}

			public ByReference(CvSeq struct) {
				super(struct.getPointer(), 0);
			}

		}

		public static class ByValue extends CvSeq implements Structure.ByValue {

			public ByValue() {
				super();
			}

			public ByValue(CvSeq struct) {
				super(struct.getPointer(), 0);
			}

		}

		public int flags;

		public int header_size;

		public CvSeq.ByReference h_prev;

		public CvSeq.ByReference h_next;

		public CvSeq.ByReference v_prev;

		public CvSeq.ByReference v_next;

		public int total;

		public int elem_size;

		public ByteByReference block_max;

		public ByteByReference ptr;

		public int delta_elems;

		public CvMemStorage.ByReference storage;

		public CvSeqBlock.ByReference free_blocks;

		public CvSeqBlock.ByReference first;

	}

	public static class CvSeqBlock extends Structure {

		public CvSeqBlock() {
			super();
		}

		public CvSeqBlock(Pointer pointer, int offset) {
			super();
			useMemory(pointer, offset);
			read();
		}

		public CvSeqBlock(CvSeqBlock struct) {
			this(struct.getPointer(), 0);
		}

		public static class ByReference extends CvSeqBlock implements
				Structure.ByReference {

			public ByReference() {
				super();
			}

			public ByReference(CvSeqBlock struct) {
				super(struct.getPointer(), 0);
			}

		}

		public static class ByValue extends CvSeqBlock implements
				Structure.ByValue {

			public ByValue() {
				super();
			}

			public ByValue(CvSeqBlock struct) {
				super(struct.getPointer(), 0);
			}

		}

		/*
		 * Previous sequence block.
		 */
		public CvSeqBlock.ByReference prev;

		/*
		 * Next sequence block.
		 */
		public CvSeqBlock.ByReference next;

		/*
		 * Index of the first element in the block +
		 */
		public int start_index;

		/*
		 * sequence->first->start_index.<br> Number of elements in the block.
		 */
		public int count;

		/*
		 * Pointer to the first element of the block.
		 */
		public com.sun.jna.ptr.ByteByReference data;

	}

	public static class CvSize extends Structure {

		public static class ByValue extends CvSize implements Structure.ByValue {
		}

		public int width;

		public int height;

	}

	public static class IplImage extends Structure {

		/*
		 * sizeof(IplImage)
		 */
		public int nSize;

		/*
		 * version (=0)
		 */
		public int ID;

		/*
		 * Most of OpenCV functions support 1,2,3 or 4 channels
		 */
		public int nChannels;

		/*
		 * ignored by OpenCV
		 */
		public int alphaChannel;

		/*
		 * pixel depth in bits: IPL_DEPTH_8U, IPL_DEPTH_8S, IPL_DEPTH_16S,
		 * IPL_DEPTH_32S, IPL_DEPTH_32F and IPL_DEPTH_64F are supported
		 */
		public int depth;

		/*
		 * ignored by OpenCV
		 */
		public byte[] colorModel = new byte[4];

		/*
		 * ignored by OpenCV
		 */
		public byte[] channelSeq = new byte[4];

		/*
		 * 0 - interleaved color channels, 1 - separate color channels,
		 * cvCreateImage can only create interleaved images
		 */
		public int dataOrder;

		/*
		 * 0 - top-left origin 1 - bottom-left origin (Windows bitmaps style)
		 */
		public int origin;

		/*
		 * Alignment of image rows (4 or 8). OpenCV ignores it and uses
		 * widthStep instead
		 */
		public int align;

		/*
		 * image width in pixels
		 */
		public int width;

		/*
		 * image height in pixels
		 */
		public int height;

		/*
		 * image ROI. if Null, the whole image is selected
		 */
		public Pointer roi;

		/*
		 * must be NULL
		 */
		public Pointer maskROI;

		/*
		 * must be NULL
		 */
		public Pointer imageId;

		/*
		 * must be NULL
		 */
		public Pointer tileInfo;

		/*
		 * image data size in bytes (== image->height * image->widthStep in case
		 * of interleaved data)
		 */
		public int imageSize;

		/*
		 * pointer to aligned image data
		 */
		public ByteByReference imageData;

		/*
		 * size of aligned image row in bytes
		 */
		public int widthStep;

		/*
		 * ignored by OpenCV
		 */
		public int[] BorderMode = new int[4];

		/*
		 * ignored by OpenCV
		 */
		public int[] BorderConst = new int[4];

		/*
		 * pointer to very origin of image data (not necessarily aligned) -
		 * needed for correct deallocation
		 */
		public Pointer imageDataOrigin;

	}

	public static class CvPoint2D32f extends Structure {
		public float x;
		public float y;
	}

	public static class CvPoint extends Structure {

		public static class ByValue extends CvPoint implements
				Structure.ByValue {
		}

		public int x;
		public int y;
	}

	public static class CvTermCriteria extends Structure {

		public static class ByValue extends CvTermCriteria implements
				Structure.ByValue {
		}

		public int type;
		public int max_iter;
		public double epsilon;
	}

	public static class CvScalar extends com.sun.jna.Structure {

		public static class ByValue extends CvScalar implements
				Structure.ByValue {
		}

		public double[] val = new double[4];
	}

	public static class CvFont extends com.sun.jna.Structure {

		public CvFont() {
			super();
		}

		public CvFont(com.sun.jna.Pointer pointer, int offset) {
			super();
			useMemory(pointer, offset);
			read();
		}

		public CvFont(CvFont struct) {
			this(struct.getPointer(), 0);
		}

		public static class ByReference extends CvFont implements
				Structure.ByReference {

			public ByReference() {
				super();
			}

			public ByReference(CvFont struct) {
				super(struct.getPointer(), 0);
			}
		}

		public static class ByValue extends CvFont implements Structure.ByValue {

			public ByValue() {
				super();
			}

			public ByValue(CvFont struct) {
				super(struct.getPointer(), 0);
			}
		}

		public int font_face;

		public float hscale;
		public float vscale;

		public float shear;

		public int thickness;

		public float dx;
		public int line_type;
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

	public static final int CV_FONT_HERSHEY_PLAIN = 1;

	public static final int CV_TERMCRIT_ITER = 1;

	public static final int CV_TERMCRIT_EPS = 2;

	public static final int IPL_DEPTH_32F = 32;

	public static final int CV_CVTIMG_FLIP = 1;

	public static final int CV_AA = 16;

	public void cvCopy(CvArr src, CvArr dst, CvArr mask);

	public IplImage cvCreateImage(CvSize.ByValue cvSize, int depth, int channels);

	public Pointer cvLoad(byte[] filename, CvMemStorage memstorage,
			byte[] name, String real_name);

	public CvMemStorage cvCreateMemStorage(int block_size);

	public void cvReleaseMemStorage(PointerByReference storage);

	public ByteByReference cvGetSeqElem(CvSeq seq, int index);

	public void cvResetImageROI(IplImage image);

	public void cvSetImageROI(IplImage image, CvRect.ByValue rect);

	public void cvClearMemStorage(CvMemStorage storage);

	public void cvLine(CvArr image, CvPoint.ByValue point1,
			CvPoint.ByValue point2, CvScalar.ByValue color, int thickness,
			int line_type, int shift);

	public void cvInitFont(CvFont font, int font_face, double hscale,
			double vscale, double shear, int thickness, int line_type);

	public void cvPutText(CvArr img, String text, CvPoint.ByValue org,
			CvFont font, CvScalar.ByValue color);
}
