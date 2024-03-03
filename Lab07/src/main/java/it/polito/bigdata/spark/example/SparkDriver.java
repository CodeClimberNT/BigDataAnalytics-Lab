package it.polito.bigdata.spark.example;

import scala.Tuple2;

import org.apache.spark.api.java.*;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;

public class SparkDriver {

	public static void main(String[] args) {

		// The following two lines are used to switch off some verbose log messages
		Logger.getLogger("org").setLevel(Level.OFF);
		Logger.getLogger("akka").setLevel(Level.OFF);

		String inputPath;
		String inputPath2;
		Double threshold;
		String outputFolder;

		inputPath = args[0];
		inputPath2 = args[1];
		threshold = Double.parseDouble(args[2]);
		outputFolder = args[3];

		// Create a configuration object and set the name of the application
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("Spark Lab #7");

		// Create a Spark Context object
		JavaSparkContext sc = new JavaSparkContext(conf);

		// print the application ID
		System.out.println("******************************");
		System.out.println("ApplicationId: " + JavaSparkContext.toSparkContext(sc).applicationId());
		System.out.println("******************************");

		JavaRDD<String> inputRegisterRDD = sc.textFile(inputPath);

		JavaRDD<String> registerNoHeader = inputRegisterRDD.filter(l -> !l.startsWith("station"));

		JavaRDD<String> readingStationFilteredBadDataRDD = registerNoHeader.filter(l -> {
			String[] features = l.split("\t");
			// filter out used slots=0 and free slots=0
			return !(features[2].equals("0") && features[3].equals("0"));
		});

		// System.out.println(readingStationFilteredBadDataRDD.top(10));

		JavaRDD<String> inputStationsRDD = sc.textFile(inputPath2);

		JavaRDD<String> stationsNoHeader = inputStationsRDD.filter(l -> !l.startsWith("id"));

		// System.out.println(stationsNoHeader.top(5));

		// Used long to be sure all value will fit but at this stage i could also assume
		// Integer will suffice, after when grouping all
		JavaPairRDD<String, Long> timeslotFreeSlotPairRDD = readingStationFilteredBadDataRDD.mapToPair(l -> {
			String[] features = l.split("\t");
			String timestamp = features[1];

			// transform date into dayOfWeek
			String dateGMT = timestamp.split(" ")[0];
			String dayOfWeek = DateTool.DayOfTheWeek(dateGMT);

			// to retrieve the house first need to take the time from the timestamp (second
			// part of the string)
			// then need to split "hh:mm:ss" by the ":" and take the first value
			String hour = timestamp.split(" ")[1].split(":")[0];

			// create the timeslot as the request "dayOfWeek - HH"
			String timeslot = dayOfWeek + " - " + hour;
			// create a list with
			Long numFreeSlotsAtRecordTime = Long.parseLong(features[3]);

			return new Tuple2<String, Long>(timeslot, numFreeSlotsAtRecordTime);
		});

		// for all the free slot equals to 0 return 1
		JavaPairRDD<String, Tuple2<Long, Long>> timeslotCriticalOnePairRDD = timeslotFreeSlotPairRDD
				.mapToPair((v) -> new Tuple2<String, Tuple2<Long, Long>>(
						v._1(),
						new Tuple2<Long, Long>(
								v._2() == 0 ? Long.valueOf(1) : Long.valueOf(0),
								Long.valueOf(1))));

		JavaPairRDD<String, Tuple2<Long, Long>> timeslotCriticalPairRDD = timeslotCriticalOnePairRDD
				.reduceByKey((a, b) -> {
					Long numCritical = Long.valueOf(0);
					Long totRecord = Long.valueOf(0);

					numCritical += a._1() + b._1();
					totRecord += a._2() + b._2();
					// return number of criticality and total number of record for that timeslot
					return new Tuple2<Long, Long>(numCritical, totRecord);
				});

		// System.out.println(timeslotCriticalPairRDD.count());
		// System.out.println(timeslotCriticalPairRDD.take(20));

		// assumption that for each key there are at least a value
		JavaPairRDD<String, Double> timeslotCriticalityPairRDD = timeslotCriticalPairRDD.mapToPair(
				// now return for every timeslot the fraction of critical record by the total
				// number of record of that timeslot
				(x) -> new Tuple2<String, Double>(x._1(), (double) x._2()._1() / (double) x._2()._2()))
				.filter(x -> x._2() > threshold);

		// System.out.println(timeslotCriticalityPairRDD.count());
		// System.out.println(timeslotCriticalityPairRDD.take(5));

		JavaPairRDD<Double, String> criticalityTimeslotPairRDD = timeslotCriticalityPairRDD
				.mapToPair(x -> new Tuple2<Double, String>(x._2(), x._1())).sortByKey(false);

		System.out.println(criticalityTimeslotPairRDD.count());
		System.out.println(criticalityTimeslotPairRDD.takeOrdered(5));



		
		// Store in resultKML one String, representing a KML marker, for each station
		// with a critical timeslot
		// JavaRDD<String> resultKML = .....

		// Invoke coalesce(1) to store all data inside one single partition/i.e., in one
		// single output part file
		// resultKML.coalesce(1).saveAsTextFile(outputFolder);

		// Close the Spark context
		sc.close();
	}
}
