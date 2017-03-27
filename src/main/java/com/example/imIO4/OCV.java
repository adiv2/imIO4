package com.example.imIO4;
/*
 * imIO4
 * Created by Aditya Gholba on 23/3/17.
 */

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;


public class OCV extends ToolKit
{
    private static final Logger LOG = LoggerFactory.getLogger(OCV.class);
    private void ovcFunc(byte[] byteImage)
    {
        try
        {
            LOG.info("ocv start");
            LOG.info("bytesImage size"+byteImage.length);
            String path = "/home/aditya/opencv-3.2.0/build/lib/libopencv_java320.so";
            System.load(path);
            InputStream in = new ByteArrayInputStream(byteImage);
            BufferedImage bufferedImage= ImageIO.read(in);
            int type = bufferedImage.getType();
            Mat source = Imgcodecs.imdecode(new MatOfByte(byteImage), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
            LOG.info("source size"+source.size());
            Mat destination;
            destination= source.clone();

            BufferedImage bufferedImage1 = new BufferedImage(destination.width(),destination.height(),type);
            byte[] data = new byte[ ((int) destination.total() * destination.channels()) ];
            destination.get(0, 0, data);
            byte b;
            for(int i=0; i<data.length; i=i+3)
            {
                b = data[i];
                data[i] = data[i+2];
                data[i+2] = b;
            }
            bufferedImage1.getRaster().setDataElements(0,0,destination.cols(),destination.rows(),data);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage1, "jpg", byteArrayOutputStream);
            byte[] imageInBytes = byteArrayOutputStream.toByteArray();
            output.emit(imageInBytes);
        }
        catch (Exception e)
        {
            LOG.info("error:ocv " + e.getMessage());
        }

    }

    @Override
    void processTuple(byte[] byteArray)
    {
        ovcFunc(byteArray);
    }

}
