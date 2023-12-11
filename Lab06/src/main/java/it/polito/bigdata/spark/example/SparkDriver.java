package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.*;
import org.apache.spark.SparkConf;
	
public class SparkDriver {
	
	public static void main(String[] args) {

		String inputPath;
		String outputPath;
		
		inputPath=args[0];
		outputPath=args[1];

	
		// Create a configuration object and set the name of the application
		SparkConf conf=new SparkConf().setAppName("Spark Lab #6");
		
		// Use the following command to create the SparkConf object if you want to run
		// your application inside Eclipse.
		// SparkConf conf=new SparkConf().setAppName("Spark Lab #6").setMaster("local");
		// Remember to remove .setMaster("local") before running your application on the cluster
		
		// Create a Spark Context object
		JavaSparkContext sc = new JavaSparkContext(conf);

		// print the application ID
		System.out.println("******************************");
		System.out.println("ApplicationId: "+JavaSparkContext.toSparkContext(sc).applicationId());
		System.out.println("******************************");
		
		// Read the content of the input file
		// JavaRDD<String> inputRDD = sc.textFile(inputPath);

		// TODO .......
		
		// Store the result in the output folder
		// resultRDD.saveAsTextFile(outputPath);

		// Close the Spark context
		sc.close();
	}
}
