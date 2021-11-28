package com.mkuskowski.livecamv2;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.widget.ImageView;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Vector;

public class CustomDetector {

    protected static final int BATCH_SIZE = 1;
    protected static final int PIXEL_SIZE = 3;
    protected static final int NUM_THREADS = 10;
    protected static final int INPUT_SIZE = 416;
    protected static final float MINIMUM_CONFIDENCE = 0.5f; // Tensorflow Confidence
    protected static final String MODEL_FILE = "yolov4-416-fp32.tflite";
    protected static final String LABELS_FILE = "coco.txt";
    protected static final int[] OUTPUT_WIDTH = new int[]{2535, 2535};
    protected static final int[] OUTPUT_WIDTH_FULL = new int[]{10647, 10647};
    protected static final float mNmsThresh = 0.6f;

    private Interpreter tfLite;
    private Vector<String> labels = new Vector<String>();
    private Handler handler = new Handler();

    public CustomDetector(final AssetManager assetManager) throws IOException {
        Interpreter.Options options = (new Interpreter.Options());
        options.setNumThreads(NUM_THREADS);
        loadLabelsFile(assetManager);
        tfLite = new Interpreter(loadModelFile(assetManager, MODEL_FILE), options);
    }

    public void Detect(Bitmap cropBitmap, ImageView imageView, boolean isTiny) {
        new Thread(() -> {
            final List<Recognition> results = this.recognizeImage(cropBitmap, isTiny);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    handleResult(cropBitmap, results, imageView);
                }
            });
        }).start();
    }

    protected void loadLabelsFile(AssetManager assetManager) throws IOException{
        InputStream labelsInput = assetManager.open(LABELS_FILE);
        BufferedReader br = new BufferedReader(new InputStreamReader(labelsInput));
        String line;
        while ((line = br.readLine()) != null) {
            this.labels.add(line);
        }
        br.close();
    }
    protected MappedByteBuffer loadModelFile(AssetManager assets, String modelFilename) throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd(modelFilename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    protected ArrayList<Recognition> getDetectionsForTiny(ByteBuffer byteBuffer, Bitmap bitmap) {
        ArrayList<Recognition> detections = new ArrayList<Recognition>();
        Map<Integer, Object> outputMap = new HashMap<>();
        outputMap.put(0, new float[1][OUTPUT_WIDTH[0]][4]);
        outputMap.put(1, new float[1][OUTPUT_WIDTH[1]][labels.size()]);
        Object[] inputArray = {byteBuffer};
        tfLite.runForMultipleInputsOutputs(inputArray, outputMap);

        int gridWidth = OUTPUT_WIDTH[0];
        float[][][] bboxes = (float [][][]) outputMap.get(0);
        float[][][] out_score = (float[][][]) outputMap.get(1);

        for (int i = 0; i < gridWidth;i++){
            float maxClass = 0;
            int detectedClass = -1;
            final float[] classes = new float[labels.size()];
            for (int c = 0;c< labels.size();c++){
                classes [c] = out_score[0][i][c];
            }
            for (int c = 0;c<labels.size();++c){
                if (classes[c] > maxClass){
                    detectedClass = c;
                    maxClass = classes[c];
                }
            }
            final float score = maxClass;
            if (score > MINIMUM_CONFIDENCE){
                final float xPos = bboxes[0][i][0];
                final float yPos = bboxes[0][i][1];
                final float w = bboxes[0][i][2];
                final float h = bboxes[0][i][3];
                final RectF rectF = new RectF(
                        Math.max(0, xPos - w / 2),
                        Math.max(0, yPos - h / 2),
                        Math.min(bitmap.getWidth() - 1, xPos + w / 2),
                        Math.min(bitmap.getHeight() - 1, yPos + h / 2));
                detections.add(new Recognition("" + i, labels.get(detectedClass),score,rectF,detectedClass ));
            }
        }
        return detections;
    }
    private ArrayList<Recognition> getDetectionsForFull(ByteBuffer byteBuffer, Bitmap bitmap){
        ArrayList<Recognition> detections = new ArrayList<Recognition>();
        Map<Integer, Object> outputMap = new HashMap<>();
        outputMap.put(0, new float[1][OUTPUT_WIDTH_FULL[0]][4]);
        outputMap.put(1, new float[1][OUTPUT_WIDTH_FULL[1]][labels.size()]);
        Object[] inputArray = {byteBuffer};
        tfLite.runForMultipleInputsOutputs(inputArray, outputMap);
        int gridWidth = OUTPUT_WIDTH_FULL[0];
        float[][][] bboxes = (float [][][]) outputMap.get(0);
        float[][][] out_score = (float[][][]) outputMap.get(1);
        for (int i = 0; i < gridWidth;i++){
            float maxClass = 0;
            int detectedClass = -1;
            final float[] classes = new float[labels.size()];
            for (int c = 0;c< labels.size();c++){
                classes [c] = out_score[0][i][c];
            }
            for (int c = 0;c<labels.size();++c){
                if (classes[c] > maxClass){
                    detectedClass = c;
                    maxClass = classes[c];
                }
            }
            final float score = maxClass;
            if (score > mNmsThresh){
                final float xPos = bboxes[0][i][0];
                final float yPos = bboxes[0][i][1];
                final float w = bboxes[0][i][2];
                final float h = bboxes[0][i][3];
                final RectF rectF = new RectF(
                        Math.max(0, xPos - w / 2),
                        Math.max(0, yPos - h / 2),
                        Math.min(bitmap.getWidth() - 1, xPos + w / 2),
                        Math.min(bitmap.getHeight() - 1, yPos + h / 2));
                detections.add(new Recognition("" + i, labels.get(detectedClass),score,rectF,detectedClass ));
            }
        }
        return detections;
    }

    protected ArrayList<Recognition> recognizeImage(Bitmap bitmap, boolean isTiny) {
        ByteBuffer byteBuffer = convertBitmapToByteBuffer(bitmap);
        @NonNull ArrayList<Recognition> detections;
        if(isTiny) {
            detections = getDetectionsForTiny(byteBuffer, bitmap);
        }
        else {
            detections = getDetectionsForFull(byteBuffer, bitmap);
        }
        final ArrayList<Recognition> recognitions = nms(detections);
        return recognitions;
    }
    protected void handleResult(Bitmap bitmap, List<Recognition> results, ImageView imageView) {
        final Canvas canvas = new Canvas(bitmap);

        final Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);

        final Paint fontPaint = new Paint();
        fontPaint.setColor(Color.RED);
        fontPaint.setTextSize(20);

        final List<Recognition> mappedRecognitions = new LinkedList<Recognition>();
        for (final Recognition result : results) {
            final RectF location = result.getLocation();
            if (location != null && result.getConfidence() >= MINIMUM_CONFIDENCE) {
                //paint.breakText(result.getTitle(),0,result.getTitle().length(),);
                canvas.drawRect(location, paint);
                float roundedConfidence = roundTwoDecimals(result.getConfidence());
                canvas.drawText(result.getTitle() +" "+roundedConfidence,(result.getLocation().left),result.getLocation().top - 5,fontPaint);
                //canvas.drawText(result.getTitle() + " " + result.getConfidence(),result.getLocation().left + 1,result.getLocation().top + 1,fontPaint);
            }
        }
        imageView.setImageBitmap(bitmap);
    }

    float roundTwoDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Float.valueOf(twoDForm.format(d));
    }
    protected ArrayList<Recognition> nms(ArrayList<Recognition> list) {
        ArrayList<Recognition> nmsList = new ArrayList<Recognition>();
        for (int k = 0; k < labels.size(); k++) {
            PriorityQueue<Recognition> pq =
                    new PriorityQueue<Recognition>(
                            50,
                            new Comparator<Recognition>() {
                                @Override
                                public int compare(final Recognition lhs, final Recognition rhs) {
                                    // Intentionally reversed to put high confidence at the head of the queue.
                                    return Float.compare(rhs.getConfidence(), lhs.getConfidence());
                                }
                            });

            for (int i = 0; i < list.size(); ++i) {
                if (list.get(i).getDetectedClass() == k) {
                    pq.add(list.get(i));
                }
            }
            while (pq.size() > 0) {
                //insert detection with max confidence
                Recognition[] a = new Recognition[pq.size()];
                Recognition[] detections = pq.toArray(a);
                Recognition max = detections[0];
                nmsList.add(max);
                pq.clear();
                for (int j = 1; j < detections.length; j++) {
                    Recognition detection = detections[j];
                    RectF b = detection.getLocation();
                    if (box_iou(max.getLocation(), b) < mNmsThresh) {
                        pq.add(detection);
                    }
                }
            }
        }
        return nmsList;
    }

    public static float box_iou(RectF a, RectF b) {
        return box_intersection(a, b) / box_union(a, b);
    }
    public static float box_intersection(RectF a, RectF b) {
        float w = overlap((a.left + a.right) / 2, a.right - a.left,
                (b.left + b.right) / 2, b.right - b.left);
        float h = overlap((a.top + a.bottom) / 2, a.bottom - a.top,
                (b.top + b.bottom) / 2, b.bottom - b.top);
        if (w < 0 || h < 0) return 0;
        float area = w * h;
        return area;
    }
    public static float box_union(RectF a, RectF b) {
        float i = box_intersection(a, b);
        float u = (a.right - a.left) * (a.bottom - a.top) + (b.right - b.left) * (b.bottom - b.top) - i;
        return u;
    }
    public static float overlap(float x1, float w1, float x2, float w2) {
        float l1 = x1 - w1 / 2;
        float l2 = x2 - w2 / 2;
        float left = l1 > l2 ? l1 : l2;
        float r1 = x1 + w1 / 2;
        float r2 = x2 + w2 / 2;
        float right = r1 < r2 ? r1 : r2;
        return right - left;
    }
    public static ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer =
                ByteBuffer.allocateDirect(4 * BATCH_SIZE * INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < INPUT_SIZE; ++i) {
            for (int j = 0; j < INPUT_SIZE; ++j) {
                final int val = intValues[pixel++];
                byteBuffer.putFloat(((val >> 16) & 0xFF) / 255.0f);
                byteBuffer.putFloat(((val >> 8) & 0xFF) / 255.0f);
                byteBuffer.putFloat((val & 0xFF) / 255.0f);
            }
        }
        return byteBuffer;
    }
    public static Bitmap processBitmap(Bitmap source, int size){

        int image_height = source.getHeight();
        int image_width = source.getWidth();

        Bitmap croppedBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Matrix frameToCropTransformations = getTransformationMatrix(image_width,image_height,size,size,0,false);
        Matrix cropToFrameTransformations = new Matrix();
        frameToCropTransformations.invert(cropToFrameTransformations);

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(source, frameToCropTransformations, null);

        return croppedBitmap;
    }
    public static Matrix getTransformationMatrix(final int srcWidth,final int srcHeight, final int dstWidth,final int dstHeight, final int applyRotation, final boolean maintainAspectRatio) {
        final Matrix matrix = new Matrix();
        if (applyRotation != 0) {
            matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f);
            matrix.postRotate(applyRotation);
        }
        final boolean transpose = (Math.abs(applyRotation) + 90) % 180 == 0;
        final int inWidth = transpose ? srcHeight : srcWidth;
        final int inHeight = transpose ? srcWidth : srcHeight;
        if (inWidth != dstWidth || inHeight != dstHeight) {
            final float scaleFactorX = dstWidth / (float) inWidth;
            final float scaleFactorY = dstHeight / (float) inHeight;

            if (maintainAspectRatio) {
                final float scaleFactor = Math.max(scaleFactorX, scaleFactorY);
                matrix.postScale(scaleFactor, scaleFactor);
            } else {
                matrix.postScale(scaleFactorX, scaleFactorY);
            }
        }

        if (applyRotation != 0) {
            matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f);
        }

        return matrix;
    }
}
