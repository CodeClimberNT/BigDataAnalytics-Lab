package it.polito.bigdata.hadoop.lab;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/*
 * Lab - Mapper
 */

/* Set the proper data types for the (key,value) pairs */
class MapperBigData extends Mapper<LongWritable, // Input key type
        Text, // Input value type
        Text, // Output key type
        Text> {// Output value type

    protected void map(
            LongWritable key, // Input key type
            Text value, // Input value type
            Context context) throws IOException, InterruptedException {
            
        String filter = context.getConfiguration().get("filter");

        String[] values = value.toString().split("\t");
        String[] words = values[0].split("\\s+");
        if (words[0].equals(filter) || words[1].equals(filter)) {
            context.write(new Text(values[0]), new Text(values[1]));
        }
    }
}
