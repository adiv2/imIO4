/**
 * Put your copyright and license info here.
 */
package com.example.imIO4;

import org.apache.hadoop.conf.Configuration;

import com.datatorrent.api.annotation.ApplicationAnnotation;
import com.datatorrent.api.StreamingApplication;
import com.datatorrent.api.DAG;

@ApplicationAnnotation(name="imIO4")
public class Application implements StreamingApplication
{
    @Override
    public void populateDAG(DAG dag, Configuration conf)
    {
        // create operators
        FileReader reader = dag.addOperator("read",  FileReader.class);
        BytesFileWriter writer = dag.addOperator("write", BytesFileWriter.class);
        //Compress compressor = dag.addOperator("compress", Compress.class);
        //ToJPEG jpgConverter = dag.addOperator("to jpg ", ToJPEG.class);
        //Resize resizer = dag.addOperator("resize",Resize.class);
        OCV  ovc = dag.addOperator("OpenCV",OCV.class);

        //Parallel partitioning not tested

        //Compress
        /*
        dag.addStream("ctrl1", reader.control, compressor.controlIn);
        dag.addStream("ctrl2", compressor.controlOut, writer.control);
        dag.addStream("read to compressor", reader.output,compressor.input);
        dag.addStream("compressor to write", compressor.output,writer.input);
        */

        //To JPEG
        /*
        dag.addStream("ctrl1", reader.control, jpgConverter.controlIn);
        dag.addStream("ctrl2", jpgConverter.controlOut, writer.control);
        dag.addStream("read to jpgConverter", reader.output,jpgConverter.input);
        dag.addStream("jpgConverter to write", jpgConverter.output,writer.input);
        */

        //Compress + Resize
        /*
        dag.addStream("ctrl1", reader.control, compressor.controlIn);
        dag.addStream("ctrl2", compressor.controlOut, resizer.controlIn);
        dag.addStream("ctrl3", resizer.controlOut, writer.control);
        dag.addStream("read to compressor", reader.output,compressor.input);
        dag.addStream("compressor to resizer", compressor.output,resizer.input);
        dag.addStream("resizer to write", resizer.output,writer.input);
        */

        dag.addStream("ctrl1", reader.control, ovc.controlIn);
        dag.addStream("ctrl2", ovc.controlOut, writer.control);
        dag.addStream("read to OpenCV", reader.output,ovc.input);
        dag.addStream("OpenCV to write", ovc.output,writer.input);
    }
}
