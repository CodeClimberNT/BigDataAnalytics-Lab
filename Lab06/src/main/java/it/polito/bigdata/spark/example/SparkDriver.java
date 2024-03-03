package it.polito.bigdata.spark.example;

import org.apache.spark.api.java.*;

import scala.Tuple2;

import org.apache.spark.SparkConf;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class SparkDriver {

	public static void main(String[] args) {
		// The following two lines are used to switch off some verbose log messages
		Logger.getLogger("org").setLevel(Level.OFF);
		Logger.getLogger("akka").setLevel(Level.OFF);

		String inputPath;
		String outputPath;

		inputPath = args[0];
		outputPath = args[1];

		// Create a configuration object and set the name of the application
		SparkConf conf = new SparkConf().setAppName("Spark Lab #6").setMaster("local");

		// Use the following command to create the SparkConf object if you want to run
		// your application inside Eclipse.
		// SparkConf conf=new SparkConf().setAppName("Spark Lab #6").setMaster("local");
		// Remember to remove .setMaster("local") before running your application on the
		// cluster

		// Create a Spark Context object
		JavaSparkContext sc = new JavaSparkContext(conf);

		// print the application ID
		System.out.println("******************************");
		System.out.println("ApplicationId: " + JavaSparkContext.toSparkContext(sc).applicationId());
		System.out.println("******************************");

		// Read the content of the input file
		JavaRDD<String> inputRDD = sc.textFile(inputPath).filter(line -> !line.startsWith("Id,"));

		JavaPairRDD<String, String> userProductPairRDD = inputRDD.mapToPair(l -> {
			String[] values = l.split(",");
			return new Tuple2<String, String>(values[2], values[1]);
		}).distinct();

		JavaPairRDD<String, Iterable<String>> userAllProductsPairRDD = userProductPairRDD.groupByKey();

		System.out.println(userAllProductsPairRDD.collect());
		System.out.println(userAllProductsPairRDD.count());

		JavaRDD<Iterable<String>> productReviewedRDD = userAllProductsPairRDD.values();

		JavaPairRDD<String, Integer> pairProductOne = productReviewedRDD.flatMapToPair(products -> {
			List<Tuple2<String, Integer>> result = new ArrayList<>();
			for (String p1 : products)
				for (String p2 : products) {
					if (p1.equals(p2))
						break;

					result.add(new Tuple2<String, Integer>(p1 + "," + p2, 1));
				}
			return result.iterator();
		});

		JavaPairRDD<String, Integer> pairProductFreqRDD = pairProductOne.reduceByKey((a, b) -> a + b);

		JavaPairRDD<String, Integer> pairProductFreqFilteredRDD = pairProductFreqRDD.filter(a -> a._2 > 1);

		JavaPairRDD<Integer, String> pairFreqProductRDD = pairProductFreqFilteredRDD
				.mapToPair(productTuple -> new Tuple2<Integer, String>(productTuple._2, productTuple._1));

				JavaPairRDD<Integer, String> orderedFreqProductDescRDD = pairFreqProductRDD.sortByKey(false);

		System.out.println(pairProductFreqRDD.collect());
		System.out.println(pairProductFreqFilteredRDD.collect());
		System.out.println(orderedFreqProductDescRDD.collect());

		// Store the result in the output folder
		// inputRDD.saveAsTextFile(outputPath);

		// Close the Spark context
		sc.close();
	}
}
