package com.example.imIO4;/*
 * imIO5.1
 * Created by Aditya Gholba on 3/4/17.
 */

import com.datatorrent.api.Context;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.lib.io.fs.AbstractFileInputOperator;
import ij.ImagePlus;
import ij.io.FileSaver;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class FileReaderA extends AbstractFileInputOperator<Data>
{
    public static final char START_FILE = '(', FINISH_FILE = ')';
    private static final Logger LOG = LoggerFactory.getLogger(FileReaderA.class);
    private transient BufferedImage br = null;
    byte[] a;
    public  int countImageSent=0;
    private boolean stop;
    private transient int pauseTime;
    private transient Path filePath;
    public String filePathStr;

    public final transient DefaultOutputPort<Data> output  = new DefaultOutputPort<>();

    @Override
    public void setup(Context.OperatorContext context)
    {
        super.setup(context);
        pauseTime = context.getValue(Context.OperatorContext.SPIN_MILLIS);

        if (null != filePathStr)
        {      // restarting from checkpoint
            filePath = new Path(filePathStr);
        }
    }

    @Override
    public void emitTuples()
    {
        if ( ! stop )
        {        // normal processing
            super.emitTuples();
            return;
        }

        // we have end-of-file, so emit no further tuples till next window; relax for a bit
        try
        {
            Thread.sleep(pauseTime);
        }
        catch (InterruptedException e) {LOG.info("Sleep interrupted");}
    }

    @Override
    public void endWindow()
    {
        super.endWindow();
        stop = false;
    }

    @Override
    protected InputStream openFile(Path curPath) throws IOException
    {
        LOG.debug("openFile: curPath = {}", curPath);
        filePath = curPath;
        filePathStr = filePath.toString();
        LOG.info("readOpen "+START_FILE + filePath.getName());
        InputStream is =  super.openFile(filePath);
        if(!filePathStr.contains(".fits"))
        {
            a = IOUtils.toByteArray(is);
        }
        else
        {
            String fitsPath = filePath.getParent().toString()+"/"+filePath.getName();
            if(fitsPath.contains(":"))
            {
                fitsPath=fitsPath.replace("file:","");
            }
            LOG.info("ERR "+filePath.getParent()+"/"+filePath.getName());
            LOG.info("ERR "+fitsPath);
            ImagePlus imagePlus = new ImagePlus(fitsPath);
            a = new FileSaver(imagePlus).serialize();
        }
        return is;
    }

    @Override
    protected void closeFile(InputStream is) throws IOException
    {
        LOG.debug("closeFile: filePath = {}", filePath);
        super.closeFile(is);
        LOG.info("readClose "+filePath.getName() + FINISH_FILE);
        filePath = null;
        stop = true;
    }

    @Override
    protected Data readEntity() throws IOException
    {
        //try{Thread.sleep(500);}catch (Exception e){LOG.info("Read Sleep"+e.getMessage());}
        LOG.info("read entity was called"+currentFile);
        byte[] imageInByte = a;
        if(countImageSent<1)
        {
            countImageSent++;
            //LOG.info("returned Image "+countImageSent+" s"+baos.size()+" br "+currentFile);
            Data data = new Data();
            data.bytesImage=imageInByte;
            data.fileName=filePath.getName().toString();
            return  data;
        }
        LOG.info("readEntity: EOF for {}", filePath);
        countImageSent=0;
        return null;
    }

    @Override
    protected void emit(Data data)
    {
        output.emit(data);
        LOG.info("send data from read");
    }

}

class Data
{
    byte[] bytesImage;
    String fileName;
}