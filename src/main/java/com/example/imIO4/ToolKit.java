package com.example.imIO4;/*
 * imIO4
 * Created by Aditya Gholba on 9/2/17.
 */

import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.common.util.BaseOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.tools.Tool;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class ToolKit extends BaseOperator
{
    private static final Logger LOG = LoggerFactory.getLogger(ToolKit.class);
    public String filePathStr;
    public String fileType;
    //private transient ArrayList<byte[]> bytesArray = new ArrayList<>();
    public transient BufferedImage bufferedImage = null;
    public final transient DefaultInputPort<String> controlIn = new DefaultInputPort<String>()
    {
        @Override
        public void process(String tuple)
        {
            processControlTuple(tuple);
        }
    };

    protected void processControlTuple(final String tuple)
    {
        filePathStr = tuple;
        if (filePathStr.contains("(") && filePathStr.contains(".png")){fileType="png";}
        if (filePathStr.contains("(") && filePathStr.contains(".jpg")){fileType="jpg";}
        controlOut.emit(tuple);
        LOG.info("emit ctr out "+tuple);
    }

    public final transient DefaultOutputPort<String> controlOut = new DefaultOutputPort<>();
    public final transient DefaultOutputPort<byte[]> output = new DefaultOutputPort<>();
    public final transient DefaultInputPort<byte[]> input = new DefaultInputPort<byte[]>()
    {

        @Override
        public void process(byte[] tuple)
        {
            processTuple(tuple);
        }
    };

    void processTuple(byte[] byteimage)
    {

    }


}
