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
        //String path = dag.getValue(Context.DAGContext.LIBRARY_JARS);
        //path = path +";"+"/home/aditya/libopencv.path"
        //FileReaderA reader = dag.addOperator("read",  FileReaderA.class);
        BytesFileWriterA writeOther = dag.addOperator("writeOther", BytesFileWriterA.class);
        //BytesFileWriterA writeHalo = dag.addOperator("writeHalo", BytesFileWriterA.class);
        //BytesFileWriterA writeGlare = dag.addOperator("writeGlare", BytesFileWriterA.class);
        Compress compressor = dag.addOperator("compress", Compress.class);
        //Resize resizer = dag.addOperator("resize",Resize.class);
        //ASASSN asassn1 = dag.addOperator("ASASSN_Halo",ASASSN.class);
        //ASASSN asassn2 = dag.addOperator("ASASSN_Glare",ASASSN.class);
        //FileFormatConverter converter = dag.addOperator("Format_Converter",FileFormatConverter.class);
        //Filters filters = dag.addOperator("filter",Filters.class);
        FrameByFrameVideoReader frameByFrameVideoReader = dag.addOperator("videoReader",FrameByFrameVideoReader.class);
        Template_Match faceRecognition = dag.addOperator("recognition",Template_Match.class);
        //Parallel partitioning not tested
        //Read write
        //dag.addStream("read to write",reader.output,writeOther.input);

        //Compress
        /*
        dag.addStream("read to compressor", reader.output,compressor.input);
        dag.addStream("compressor to write", compressor.output,writer.input);
        */


        //Compress + Resize
        /*
        dag.addStream("read to compressor", reader.output,compressor.input);
        dag.addStream("compressor to resizer", compressor.output,resizer.input);
        dag.addStream("resizer to write", resizer.output,writeOther.input);
        */

        //Open CV

        /*
        dag.addStream("read to asassn1",reader.output,asassn1.input);
        dag.addStream("asassn1 to write halo",asassn1.output1,writeHalo.input);
        dag.addStream("asassn1 to asassn2",asassn1.output,asassn2.input);
        dag.addStream("asassn2 to write glare",asassn2.output1,writeGlare.input);
        dag.addStream("asassn2 to write others",asassn2.output,writeOther.input);
        */

        //File format converter
        /*
        dag.addStream("read to converter",reader.output,converter.input);
        dag.addStream("converter to compress",converter.output,compressor.input);
        dag.addStream("compress to resize",compressor.output,resizer.input);
        dag.addStream("resize to write",resizer.output,writeOther.input);
        */

        //Asassn preProcessing
        /*
        dag.addStream("read to filter",reader.output,filters.input);
        dag.addStream("filter to converter",filters.output,converter.input);
        dag.addStream("converter to write",converter.output,writeOther.input);
        */

        //Video splitter
        dag.addStream("Video to compress",frameByFrameVideoReader.output,compressor.input);
        dag.addStream("Compress to Write",compressor.output,writeOther.input);



    }
}
