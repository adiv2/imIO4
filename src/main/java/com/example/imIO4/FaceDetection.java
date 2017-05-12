package com.example.imIO4;/*
 * imIO5.1
 * Created by Aditya Gholba on 2/5/17.
 */

import com.datatorrent.api.DefaultOutputPort;
import org.opencv.core.*;
import org.opencv.objdetect.CascadeClassifier;

import javax.validation.constraints.NotNull;

import static org.opencv.imgproc.Imgproc.rectangle;

public class FaceDetection extends ToolKit
{
    public final transient DefaultOutputPort<Data> template = new DefaultOutputPort<>();
    public String getXmlPath()
    {
        return xmlPath;
    }

    public void setXmlPath(String xmlPath)
    {
        this.xmlPath = xmlPath;
    }

    @NotNull
    private String xmlPath;

    private void detect(Data data)
    {
        Mat image = readMat(data.bytesImage);
        CascadeClassifier cascadeClassifier = new CascadeClassifier(xmlPath);
        //int f=0;
        //for(Mat image:matFrames)
        MatOfRect faceDetections = new MatOfRect();
        cascadeClassifier.detectMultiScale(image, faceDetections);
        //System.out.println("Rect array length "+faceDetections.toArray().length);
        int rN=0;
        if(faceDetections.toArray().length>=1)
        {
            for (int j = 0; j < 1; j++)
            {
                Rect rect = faceDetections.toArray()[faceDetections.toArray().length - 1];
                rN++;
                Rect rect1 = new Rect(rect.x, rect.y, rect.width, rect.height);
                Mat r = image.submat(rect1);
                if (r.width() > 180)
                {
                    rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                    Data data1 = new Data();
                    data1.bytesImage = writeMat(r);
                    data1.fileName = data.fileName;
                    template.emit(data1);
                }
            }
        }
        data.bytesImage=writeMat(image);
        data.fileName=data.fileName+".png";
        output.emit(data);
    }

    @Override
    void processTuple(Data data)
    {
        detect(data);
    }
}
