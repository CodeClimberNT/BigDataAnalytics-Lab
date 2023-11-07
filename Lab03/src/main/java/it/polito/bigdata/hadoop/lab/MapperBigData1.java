package it.polito.bigdata.hadoop.lab;

import java.io.IOException;

import java.util.Arrays;


import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Lab  - Mapper
 */

/* Set the proper data types for the (key,value) pairs */
class MapperBigData1 extends Mapper<
        LongWritable, // Input key type
        Text,         // Input value type
        Text,         // Output key type
        IntWritable> {// Output value type

    protected void map(
            LongWritable key,   // Input key type
            Text value,         // Input value type
            Context context) throws IOException, InterruptedException {
        // from line A,B,B,B split by , and then extract from the second element to the last one (just the bought products)
        String[] boughtTogether = Arrays.copyOfRange(value.toString().split(","), 1, value.toString().split(",").length);

        for (String first : boughtTogether) {
            for (String second : boughtTogether) {
                if (first.equals(second))
                    continue;
                String[] combination = {first, second};
                Arrays.sort(combination);
                context.write(new Text(combination[0] + "," + combination[1]), new IntWritable(1));
            }
        }
    }
}
