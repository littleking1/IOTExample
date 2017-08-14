package com.microsoft.hdinsight.storm.examples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

//import com.microsoft.eventhubs.client.ConnectionStringBuilder;
//import com.microsoft.eventhubs.client.EventHubSender;
//import com.microsoft.eventhubs.client.EventHubClient;
import com.microsoft.azure.eventhubs.*;
import com.microsoft.azure.servicebus.*;
//import com.microsoft.eventhubs.spout.EventHubSpoutConfig;

public class IotEventGen
{
  List<CompletableFuture<PartitionSender>> senders;
  List<String> vins;
  List<EventData> events;
  
  public IotEventGen(String propFile, String vinFile) {
    //read properties file and construct EventHubs senders
    senders = new ArrayList<CompletableFuture<PartitionSender>>();
    try {
      Properties properties = new Properties();
      properties.load(new FileReader(propFile));
      
      String username = properties.getProperty("eventhubs.username");
      String password = properties.getProperty("eventhubs.password");
      String namespace = properties.getProperty("eventhubs.namespace");
      String entitypath = properties.getProperty("eventhubs.entitypath");
      String sasKeyName = properties.getProperty("eventhubs.keyname");

      String targetFqnAddress = properties.getProperty("eventhubs.targetfqnaddress");
      int partitionCount = Integer.parseInt(properties.getProperty("eventhubs.partitions.count"));
      
      if(targetFqnAddress == null || targetFqnAddress.length()==0) {
        targetFqnAddress = "servicebus.windows.net";
      }
     // String connectionString = new ConnectionStringBuilder(username, password,
    	//	namespace, targetFqnAddress)
      ConnectionStringBuilder connStr = new ConnectionStringBuilder(namespace, entitypath, sasKeyName, password);
      EventHubClient client = EventHubClient.createFromConnectionStringSync(connStr.toString());
     // EventHubClient client = EventHubClient.create(connectionString, entitypath);
    
      for(int i=0; i<partitionCount; ++i) {
    	  //PartitionSender part = client.createPartitionSender(""+i);
        senders.add(client.createPartitionSender(""+i));
      //  senders.add(client.createPartitionSender("test"));
        
      }
    }
    catch(Exception e) {
      System.out.println("Exception in creating senders: " + e.getMessage());
    }
    
    //read VIN number document and save all VIN numbers to list
    vins = new ArrayList<String>();
    try {
      BufferedReader in = new BufferedReader(new FileReader(vinFile));
      while(in.ready()) {
        String line = in.readLine();
        String[] parts = line.split(",");
        vins.add(parts[0]);
      }
      in.close();
    }
    catch(Exception e) {
      System.out.println("Exception in reading vins: " + e.getMessage());
    }
  }
  
  /**
   * map the VIN number to a partition ID
   * @param vin
   * @return
   */
  public static int mapVinToPartition(String vin, int partitionCount) {
    //let's sum the first 4 character and mod partitionCount
    int sum = 0;
    for(int i=0; i<4; ++i) {
      if(Character.isDigit(vin.charAt(i))) {
        sum += vin.charAt(i) - '0';
      }
      else {
        sum += vin.charAt(i) - 'A';
      }
    }
    return sum % partitionCount;
  }
    private void generate() {
    Random r = new Random();
    try {
      for(String vin: vins) {
        IotEvent ie = new IotEvent(vin);
        ie.randomize(r);
        int pid = mapVinToPartition(vin, senders.size());
        //System.out.println("sending to partition " + pid);
       // events.add(ie.serialize().getBytes("UTF-8")) ;
        senders.get(pid).get().send(new EventData (ie.serialize().getBytes("UTF-8")));
      
        //byte [] payload= "test".getBytes();
      //  EventData test = new EventData(payload);
 
    //    senders.get(pid).send
      }
    }
    catch(Exception e) {
      System.out.println("Exception in sending to EventHubs: " + e.getMessage());
    }
  }
  
  public void generate(int num) {
    for(int i=0; i<num; ++i) {
      System.out.println("sending events for round " + i);
      generate();
    }
  }
  
  /**
   * Generate EventHubs events:
   *   args[0]: eventhubs configuration properties
   *   args[1]: vehicle database file
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    if(args.length < 3) {
      System.out.println("usage: ");
      System.out.println("\tioteventgen eventhubs.properties vehiclevin.txt <events_per_vihecle>");
      return;
    }
    
    IotEventGen ieg = new IotEventGen(args[0], args[1]);
    int num = Integer.parseInt(args[2]);
    ieg.generate(num);
  }
}
