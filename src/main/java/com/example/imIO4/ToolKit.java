package com.example.imIO4;/*
 * imIO4
 * Created by Aditya Gholba on 9/2/17.
 */

import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.common.util.BaseOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.image.BufferedImage;


public class ToolKit extends BaseOperator
{
    private static final Logger LOG = LoggerFactory.getLogger(ToolKit.class);
    public String filePath;
    public static String fileType;
    //private transient ArrayList<byte[]> bytesArray = new ArrayList<>();
    public transient BufferedImage bufferedImage = null;

    public final transient DefaultOutputPort<Data> output = new DefaultOutputPort<>();
    public final transient DefaultOutputPort<Data> output1 = new DefaultOutputPort<>();
    public final transient DefaultInputPort<Data> input = new DefaultInputPort<Data>()
    {

        @Override
        public void process(Data tuple)
        {
            filePath = tuple.fileName;
            if ( filePath.contains(".png")){fileType="png";}
            if (filePath.contains(".jpg")){fileType="jpg";}
            if (filePath.contains(".jpeg")){fileType="jpg";}
            if (filePath.contains(".fits")){fileType="fits";}
            if (filePath.contains(".gif")){fileType="gif";}
            if (filePath.contains(".tif")){fileType="tif";}
            LOG.info("file type"+fileType);
            processTuple(tuple);
        }
    };

    void processTuple(Data data)
    {

    }


}
