# Internet of Things example

Follow the steps in the referece below to test the IOT example, clone the whole project from the github template mentioned , but replace the IOTExample folder with the content in this branch. 
https://github.com/hdinsight/hdinsight-storm-examples

This is an example for IOT (Internet of Things) scenario using Storm under ARM model. The basic idea is to showcase how to use Storm topology to read messages from Azure EventHubs, read document from Azure DocumentDB for data referencing and save data to Azure Storage.

For this example to work end to end, along with the Storm topology (iot), following tools have been provided:

1. EventHubs event generator (eventgen) 
2. DocumentDB document generator (docdbgen)

## Source code
vehiclegen.py: this is a simple python scripts to generate random vehicle VIN number and model number as a text file (vehiclevin.txt).

eventgen: the Azure EventHubs generator, a standalone java program to write messages to Azure EventHubs. It uses the vehicle info to generate engine temperature messages for each vehicle.

docdbgen: The Azure DocumentDB generator, a standalone java program to create DocumentDB collection that stores vehicle VIN to model number map. Later the document DB is used to retrieve model number from the VIN.

iot: The Storm topology, it has the following components:
EventHubSpout: source of stream
TypeConversionBolt: converts EventHubs message in JSON format into fields in a tuple
DataReferenceBolt: enrich data by reading DocumentDB to get models for a given VIN
WasbStoreBolt: write the enriched data points into Azure Blob Storage

## Prerequisites
In order to build and run the example, you need to have:

1. Java 1.7/1.8 SDK.
2. Maven 3.x - If you have M2_HOME configured, the examples should detect your mvn automatically.
3. Latest version of Azure Powershell (0.8.13 or later).
4. An active Azure subscription for deploying Azure services for example execution.

NOTE: The access to Windows Azure Blob Storage requires hadoop-azure jar, which can only be built from Apache hadoop trunk at this point (it will be included in Hadoop 2.7). You can choose to build yourself, or you can use the prebuilt jar included in the lib folder.

## How to build
Use ```IoTExample\build.bat``` to build the example.

## How to run
Use ```IoTExample\run.bat``` to run the example that will create resources in Azure, build and submit the example topology.

```IMPORTANT NOTES:```
The original version of example provided in the technet has many compiling errors, I have fixed them all and composed a version for the latest HDInsight 3.6 and ARM Model, thus you can download and compile directly under Eclipse. To test it , please make sure you have all reprequisites succcessfully prepared. 

* During the process, you will be prompted to login with your Azure credentials and type your subscription name.
* This will first create all dependencies (DocumentDB, EventHubs, Storage) in Azure, then update configuration files, then build all 3 projects.
* This script will open a browser window for the external Storm UI, you need to enter Storm cluster credentials, they are stored at config\configurations.properties
* In the case of failure in creating resources in Azure, you can run ```IoTExample\cleanup.bat``` which will delete resources that have been created.
* As the latest HDInsight does not support windows anymore starting from 3.5, you can only input Linux for HDinsight OS type, and the final toplogy can not be uploaded to Storm automatically, you might want to manually follow the steps talking here to upload the toplogy and submit it. 
Using Winscp to upload the topology if you are developing on VS 2017 under Windows:

    ~/target/iot-1.0.jar USERNAME@CLUSTERNAME-ssh.azurehdinsight.net:.
    storm jar ~/iot-1.0.jar "com.microsoft.hdinsight.storm.examples.IotTopology"  "IotTopology"

## How to clean and delete all resources in Azure and local build artifacts ###
Use ```IoTExample\cleanup.bat``` to delete the resources created from previous step and also any local build generated artifacts.
"# IOTExample" 
