package outlier.detection.spark;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import scala.Tuple2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
//import org.apache.spark.examples.streaming.StreamingExamples;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.hadoop.MongoOutputFormat;
import com.mongodb.util.JSON;

import outlier.detection.dto.OutputMessage;
import outlier.detection.spark.input.InputMessage;
import outlier.detection.spark.input.MessageParser;
import outlier.detection.spark.processor.Processor;

/**
 * Consumes messages from one or more topics in Kafka and does outlier detection.
 *
 * Usage: JavaKafkaInboundProcessor <zkQuorum> <group> <topics> <numThreads>
 *   <zkQuorum> is a list of one or more zookeeper servers that make quorum
 *   <group> is the name of kafka consumer group
 *   <topics> is a list of one or more kafka topics to consume from
 *   <numThreads> is the number of threads the kafka consumer should use
 *
 * To run this example:
 *   `$ bin/spark-submit outlier.detection.spark.JavaKafkaInboundProcessor zoo01,zoo02, \
 *    zoo03 my-consumer-group topic1,topic2 1`
 */

public final class JavaKafkaInboundProcessor {
 
  private static final MessageParser PARSER = new MessageParser();
  private static final Processor PROCESSOR = new Processor();
  private static final ObjectMapper MAPPER = new ObjectMapper();
  
  private static final String MONGO_HOST = "mongo_host";
  
  private JavaKafkaInboundProcessor() {
  }

  public static void main(String[] args) {
    if (args.length < 4) {
      System.err.println("Usage: JavaKafkaInboundProcessor <zkQuorum> <group> <topics> <numThreads>");
      System.exit(1);
    }

//    StreamingExamples.setStreamingLogLevels();
    SparkConf sparkConf = new SparkConf().setAppName("JavaKafkaInboundProcessor")
//    		.setMaster("local[*]")
    		;   
    Configuration outputConfig = new Configuration();
    outputConfig.set("mongo.output.uri", "mongodb://" + MONGO_HOST + ":27017/outliers.output");
    
    // Create the context with 2 seconds batch size
    JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, new Duration(2000));

    int numThreads = Integer.parseInt(args[3]);
    Map<String, Integer> topicMap = new HashMap<String, Integer>();
    String[] topics = args[2].split(",");
    for (String topic: topics) {
      topicMap.put(topic, numThreads);
    }
    
    JavaPairReceiverInputDStream<String, String> messages =
            KafkaUtils.createStream(jssc, args[0], args[1], topicMap);

    JavaDStream<OutputMessage> records = messages.map(new Function<Tuple2<String, String>, InputMessage>() {
      @Override
      public InputMessage call(Tuple2<String, String> tuple2) {
        return PARSER.parse(tuple2._2());
      }
    }).filter(new Function<InputMessage, Boolean>(){
		@Override
		public Boolean call(InputMessage msg) throws Exception {		
			return msg!=null;
		}
    }).map(new Function<InputMessage, OutputMessage>() {
        @Override
        public OutputMessage call(InputMessage input) {
          return PROCESSOR.process(input);
        }
      });

    
    
 // Output contains tuples of (null, BSONObject) - ObjectId will be generated by Mongo driver if null
    JavaPairDStream<Object, BSONObject> save = records.mapToPair(new PairFunction<OutputMessage, Object, BSONObject>() {

		
        @Override
        public Tuple2<Object, BSONObject> call(OutputMessage outputMessage) {
        	
        	BasicDBObject obj = null;
			try {
				String str = MAPPER.writeValueAsString(outputMessage);
				obj = (BasicDBObject)JSON.parse(str);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return null;
			}
            return new Tuple2<>(null, obj);
        }
    }).filter(new Function<Tuple2<Object, BSONObject>, Boolean>(){
		@Override
		public Boolean call(Tuple2<Object, BSONObject> msg) throws Exception {		
			return msg!=null;
		}
    });
    
    // Only MongoOutputFormat and config are relevant
   // save.saveAsNewAPIHadoopFiles("file:///bogus", Object.class, Object.class, MongoOutputFormat.class, config);
    save.saveAsNewAPIHadoopFiles("file:///bogus", "suffix", Object.class, BSONObject.class, MongoOutputFormat.class, outputConfig);
	
	//StreamingContext.stop(true, true)
    
    save.print();
    jssc.start();
    jssc.awaitTermination();
  }
}