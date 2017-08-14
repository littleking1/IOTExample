package com.microsoft.hdinsight.storm.samples;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.microsoft.azure.eventhubs.*;
import com.microsoft.azure.servicebus.*;
import com.microsoft.hdinsight.storm.examples.IotEvent;
import com.microsoft.hdinsight.storm.examples.IotEventGen;


public class AppTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testVinMap() {
    assertEquals(0, IotEventGen.mapVinToPartition("AAAABBBB", 32));
    assertEquals(1, IotEventGen.mapVinToPartition("A100BADADFAFDSF", 32));
    assertEquals(0, IotEventGen.mapVinToPartition("8888BADADFAFDSF", 32));
    assertEquals(26, IotEventGen.mapVinToPartition("888CBADADFAFDSF", 32));
  }
  
  @Test
  public void testIotEvent() {
    IotEvent ie = new IotEvent("ABCDEFGHIJKLMNOP");
    ie.engineTemperature = 250;
    ie.outsideTemperature = 78;
    ie.speed = 55;
    ie.timestamp = 123456789;
    assertEquals("{vin:\"ABCDEFGHIJKLMNOP\",outsideTemperature:78,engineTemperature:250,speed:55,timestamp:123456789}",
        ie.serialize());
    
    ie.deserialize("{\"vin\":\"12345\",\"outsideTemperature\":101,\"engineTemperature\":222,\"speed\":43,\"timestamp\":987654321}");
    assertEquals("12345", ie.vin);
    assertEquals(101, ie.outsideTemperature);
    assertEquals(222, ie.engineTemperature);
    assertEquals(43, ie.speed);
    assertEquals(987654321L, ie.timestamp);
  }


public static void main(String[] args) throws Exception {

    
    IotEventGen ieg = new IotEventGen(args[0], args[1]);
    int num = Integer.parseInt(args[2]);
    ieg.generate(num);
  }
}
