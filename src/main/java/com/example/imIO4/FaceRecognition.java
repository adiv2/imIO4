package com.example.imIO4;/*
 * imIO5.1
 * Created by Aditya Gholba on 10/5/17.
 */

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import static org.opencv.imgproc.Imgproc.rectangle;

public class FaceRecognition extends Template_Match
{
    ArrayList<Faces> facesList = new ArrayList<>();
    ArrayList<Faces> facesCopyList = new ArrayList<>();

    public double getThreshold()
    {
        return matchThreshold;
    }

    public void setThreshold(double threshold)
    {
        this.matchThreshold = threshold;
    }

    double matchThreshold;

    public double getUniqueThreshold()
    {
        return uniqueThreshold;
    }

    public void setUniqueThreshold(double uniqueThreshold)
    {
        this.uniqueThreshold = uniqueThreshold;
    }

    double uniqueThreshold;

    protected static final Logger LOG = LoggerFactory.getLogger(FaceRecognition.class);
    int persons=0;
    protected void recognizeFace(Data data)
    {

        Mat source = readMat(data.bytesImage);
        if(facesList.size()==0)
        {
            persons++;
            Faces faces2 = new Faces();
            faces2.mat=source.clone();
            faces2.name="person_"+Integer.toString(persons);
            facesList.add(faces2);
            data.fileName=faces2.name+"_"+data.fileName+".png";
            data.bytesImage=writeMat(source);
            output.emit(data);
        }
        else
        {
            double maxvals[]=new double[facesList.size()];
            int numVals=0;
            Boolean newFace = false;
            for (Faces facesObj : facesList)
            {

                Mat template = facesObj.mat;
                Mat result = new Mat();
                Imgproc.matchTemplate(source, template, result, Imgproc.TM_CCOEFF_NORMED);                Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
                Point matchLoc;
                matchLoc = mmr.maxLoc;
                //System.out.println("Max val " + mmr.maxVal);
                if (mmr.maxVal >= matchThreshold)
                {
                    rectangle(source, matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows()), new Scalar(0, 255, 0), 10);
                    //writeMat(source,"/home/aditya/Desktop/watchFound.jpg");
                    LOG.info("Max val " + mmr.maxVal);
                    LOG.info("Found at " + matchLoc.x);
                    data.fileName=facesObj.name+"_"+data.fileName+".png";
                    data.bytesImage=writeMat(source);
                    output.emit(data);
                    //imwrite("/home/aditya/Desktop/Person/" + facesObj.name + "_"+numVals+"_"+ ".png", source);
                    LOG.info("Face is "+data.fileName);

                }
                if(mmr.maxVal<uniqueThreshold){numVals++;}
                if(numVals==(facesList.size())){newFace=true;}
            }
            if(newFace)
            {
                persons++;
                LOG.info("Smallest max vals are "+maxvals[0]);//+" "+maxvals[1]+" "+maxvals[2]+" ");
                LOG.info("New face person value "+persons);
                Faces faces1 = new Faces();
                faces1.mat=source.clone();
                faces1.name="person_"+Integer.toString(persons);
                facesCopyList.add(faces1);
                data.fileName=faces1.name+"_"+data.fileName+".png";
                data.bytesImage=writeMat(source);
                output.emit(data);
            }

            for (Faces facesObj:facesCopyList)
            {
                if(!facesList.contains(facesObj))
                {
                    facesList.add(facesObj);
                }
            }
            facesCopyList.clear();
        }
        LOG.info("");
    }


    @Override
    void processTuple(Data data)
    {
        recognizeFace(data);
    }
}
class Faces extends FaceRecognition
{
    String name;
    Mat mat;

}
