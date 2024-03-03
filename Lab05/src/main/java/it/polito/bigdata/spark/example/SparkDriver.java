package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.*;
import org.omg.CORBA.SystemException;
import org.apache.spark.SparkConf;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class SparkDriver {

        public static void main(String[] args) {

                // The following two lines are used to switch off some verbose log messages
                Logger.getLogger("org").setLevel(Level.OFF);
                Logger.getLogger("akka").setLevel(Level.OFF);

                String inputPath;
                String outputPath;
                String prefix;

                inputPath = args[0];
                outputPath = args[1];
                prefix = args[2];

                // Create a configuration object and set the name of the application
                SparkConf conf = new SparkConf().setAppName("Spark Lab #5")
                                .setMaster("local");

                // Use the following command to create the SparkConf object if you want to run
                // your application inside Eclipse.
                // Remember to remove .setMaster("local") before running your application on the
                // cluster
                // SparkConf conf=new SparkConf().setAppName("Spark Lab #5").setMaster("local");

                // Create a Spark Context object
                JavaSparkContext sc = new JavaSparkContext(conf);

                // print the application ID
                System.out.println("******************************");
                System.out.println("ApplicationId: " + JavaSparkContext.toSparkContext(sc).applicationId());
                System.out.println("******************************");

                // Read the content of the input file/folder
                // Each element/string of wordFreqRDD corresponds to one line of the input data
                // (i.e, one pair "word\tfreq")
                JavaRDD<String> wordFreqRDD = sc.textFile(inputPath);

                /*
                 * Task 1
                 * .......
                 * .......
                 */
                System.out.println("Task 1");


                JavaRDD<String> filteredWordsRDD = wordFreqRDD.filter(lines ->{
                        String word = lines.split("\t")[0];
                        return word.startsWith(prefix);
                });  
                long filteredLines = filteredWordsRDD.count();
                System.out.println("Num of lines after filter: " + filteredLines);

                Long maxFreq = filteredWordsRDD.map(a -> Long.valueOf(a.split("\t")[1]))
                                        .reduce((a,b) -> a>b? a:b);

                System.out.println("Max frequency value: " + maxFreq);

                


                System.out.println("================");
                /*
                 * Task 2
                 * .......
                 * .......
                 */
                System.out.println("Task 2");

                Double freqThreshold = 0.8 * maxFreq;

                JavaRDD<String> freqFilteredWordsRDD = filteredWordsRDD.filter(a -> Double.parseDouble(a.split("\t")[1])>freqThreshold);

                Long freqFilteredLines = freqFilteredWordsRDD.count();
                System.out.println("Number of lines after frequency filter: " + freqFilteredLines);

                System.out.println(freqFilteredWordsRDD.collect());


                freqFilteredWordsRDD.saveAsTextFile(outputPath);

                // Close the Spark context
                sc.close();
        }
}
