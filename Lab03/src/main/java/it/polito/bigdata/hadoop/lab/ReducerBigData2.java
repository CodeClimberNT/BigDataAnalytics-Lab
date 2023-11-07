package it.polito.bigdata.hadoop.lab;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Lab - Reducer
 */

/* Set the proper data types for the (key,value) pairs */
class ReducerBigData2 extends Reducer<
        NullWritable,           // Input key type
        TopKVector<WordCountWritable>,    // Input value type
        Text,           // Output key type
        IntWritable> {  // Output value type

    @Override
    protected void reduce(
            NullWritable key, // Input key type
            Iterable<TopKVector<WordCountWritable>> values, // Input value type
            Context context) throws IOException, InterruptedException {

        /* Implement the reduce method */

    }
}
