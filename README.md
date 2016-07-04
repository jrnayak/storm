# storm
Storm Deployment on AWS EC2
Running Apache Storm cluster on EC2 is greatly simplified with the open source project storm-deploy-alternative. With this handy project, you can define your Storm cluster in a simple configuration file and create the running instances as well as installing and running all necessary software components such as Zookeeper, Storm, and Nimbus is all handled for you.
This guide aims to help you get up and running with Apache Storm on Amazon EC2 as fast as possible.
Features
Runs Storm and Zookeeper daemons under supervision (automatically restarted in case of failure)
Only fetch and compile what is needed (can deploy on prepared images in a few minutes)
Supports executing user-defined commands both pre-config and post-config
Automatically sets up Ganglia, making it easy to monitor performance
Automatically sets up Amazon EC2 AMI Tools on new nodes
Set up AWS user
Create a AWS user and download the security credentials: Access Key Id and Secret Access Key
set up git
yum install git
Set up maven
set up maven by following this link.
download location:
https://maven.apache.org/download.cgi
How to install:
https://maven.apache.org/install.html

Using storm-deploy-alternative
clone the storm-deploy-alternative using git.
git clone https://github.com/timmolter/storm-deploy-alternative
Configuration
This tool, requires two configuration files: storm-deploy-alternative-local/conf/credential.yaml and storm-deploy-alternative-local/conf/configuration.yaml. Put your credentials into the file conf/credential.yaml. It's required that you have generated an SSH key-pair on your local machine in ~/.ssh with an empty pass phrase.
Below is an example of a single cluster configuration, for conf/configuration.yaml
#
# Amazon EC2 example cluster configuration
#
storm-poc:
    - image "eu-west-1/ami-be5cf7cd"            # Ubuntu 14.04 LTS AMI
    - region "eu-west-1"                                        # Region
    - m1.xlarge {ZK, WORKER, MASTER, UI}        # Request service
    - m1.medium {ZK, WORKER}                            # Request service
    - m1.medium {ZK, WORKER}                            # Request service
    - m1.medium {WORKER}                                        # Request service
    - remote-exec-preconfig {cd ~, echo hey > hey.txt}
    - remote-exec-postconfig {}
    - storm-deploy-alternative-cloud-jar-url "s3://ctm-streaming-poc/storm-deploy-alternative-cloud.jar"
    - storm-tar-gz-url "http://apache.mirror.anlx.net/storm/apache-storm-0.10.1/apache-storm-0.10.1.tar.gz" # https://storm.apache.org/downloads.html
    - zk-tar-gz-url "http://apache.mirror.anlx.net/zookeeper/stable/zookeeper-3.4.8.tar.gz" # https://www.apache.org/dyn/closer.cgi/zookeeper/
MASTER is the Storm Nimbus daemon
WORKER is the Storm Supervisor daemon
UI is the Storm and Ganglia User-Interface
LOGVIEWER is the Storm Logviewer daemon
DRPC is the Storm DRPC daemon
ZK is the Zookeeper daemon
Below is an example of a single cluster configuration, for conf/credential.yaml
##
## Amazon AWS Credentials
##
ec2-identity: "AKIAIS4P535GZK34EIHA"
ec2-credential: "skvs55/pNPo9wxRQq4PIP0hzQY/wrweImEW4o1Qy"
Usage
After cloning the repo via git, build the project with Maven with the command: mvn clean package. Two jars will be produced: storm-deploy-alternative-local/target/storm-deploy-alternative-local.jar and storm-deploy-alternative-cloud/target/storm-deploy-alternative-cloud.jar. You need to upload storm-deploy-alternative-cloud/target/storm-deploy-alternative-cloud.jar to some location on the web accessible by your cluster instances via wget. Your own S3 bucket would be a logical location. Update the configuration.yaml entry, storm-deploy-alternative-cloud-jar-url, accordingly.
Deploy
Execute java -jar storm-deploy-alternative-local/target/storm-deploy-alternative-local.jar deploy CLUSTER_NAME
Deploys all nodes belonging in the cluster with name CLUSTER_NAME.
This time, if every things works out OK, the Storm cluster will be deployed. Expect to wait about 5 to 10 minutes before it’s fully deployed. After deployment, you will see the relevant cluster information including the StormUI and Ganglia web application URLs.
563835 [main] INFO dk.kaspergsm.stormdeploy.commands.Deploy - User: ubuntu
563835 [main] INFO dk.kaspergsm.stormdeploy.commands.Deploy - Started:
563835 [main] INFO dk.kaspergsm.stormdeploy.commands.Deploy - 	52.208.118.244	[ZK, WORKER, MASTER, UI]
563835 [main] INFO dk.kaspergsm.stormdeploy.commands.Deploy - 	52.18.8.151	[ZK, WORKER]
563835 [main] INFO dk.kaspergsm.stormdeploy.commands.Deploy - 	52.30.206.104	[ZK, WORKER]
563835 [main] INFO dk.kaspergsm.stormdeploy.commands.Deploy - 	52.51.5.29	[WORKER]
563835 [main] INFO dk.kaspergsm.stormdeploy.commands.Deploy - Storm UI: http://52.208.118.244:8080
563835 [main] INFO dk.kaspergsm.stormdeploy.commands.Deploy - Ganglia UI: http://52.208.118.244/ganglia
Issues
storm java.net.ConnectException: Connection refused
This exception has one meaning only: nothing was listening at the IP:port you tried to connect to. Either that was wrong or you didn't start the server.
Resolution
Go to Zookeeper Installation directory.
cd to bin.
Give the command ./zkServer stop in all zookeeper nodes.
Again give the command ./zkServer start in all zookeeper nodes.
Start all the storm processes(Nimbus and Supervisor).
Now you will not get this exception in Zookeeper cluster.
SSH Into the MASTER Node
Just to test that you can SSH into the MASTER node, run the following command, replacing ec2 with whatever your SSH key name is that you used above.
ssh -i "jclouds#storm-poc.pem" ubuntu@ec2-52-208-118-244.eu-west-1.compute.amazonaws.com
Run a Test Topology
SSH’ed into the MASTER node of the cluster, run the following command, which launches a test storm topology that comes bundled with Storm.
 storm jar "/home/ubuntu/storm/examples/storm-starter/storm-starter-topologies-0.10.1.jar" storm.starter.WordCountTopology wordcount
The result will look something like this:
Running: java -client -Ddaemon.name= -Dstorm.options= -Dstorm.home=/home/ubuntu/storm -Dstorm.log.dir=/home/ubuntu/storm/logs -Djava.library.path=/usr/local/lib:/opt/local/lib:/usr/lib -Dstorm.conf.file= -cp /home/ubuntu/storm/lib/disruptor-2.10.4.jar:/home/ubuntu/storm/lib/log4j-core-2.1.jar:/home/ubuntu/storm/lib/storm-core-0.10.1.jar:/home/ubuntu/storm/lib/slf4j-api-1.7.7.jar:/home/ubuntu/storm/lib/log4j-over-slf4j-1.6.6.jar:/home/ubuntu/storm/lib/clojure-1.6.0.jar:/home/ubuntu/storm/lib/reflectasm-1.07-shaded.jar:/home/ubuntu/storm/lib/minlog-1.2.jar:/home/ubuntu/storm/lib/log4j-api-2.1.jar:/home/ubuntu/storm/lib/log4j-slf4j-impl-2.1.jar:/home/ubuntu/storm/lib/asm-4.0.jar:/home/ubuntu/storm/lib/kryo-2.21.jar:/home/ubuntu/storm/lib/servlet-api-2.5.jar:/home/ubuntu/storm/lib/hadoop-auth-2.4.0.jar:/home/ubuntu/storm/examples/storm-starter/storm-starter-topologies-0.10.1.jar:/home/ubuntu/storm/conf:/home/ubuntu/storm/bin -Dstorm.jar=/home/ubuntu/storm/examples/storm-starter/storm-starter-topologies-0.10.1.jar storm.starter.WordCountTopology wordcount
761  [main] INFO  b.s.u.Utils - Using defaults.yaml from resources
876  [main] INFO  b.s.u.Utils - Using storm.yaml from resources
956  [main] INFO  b.s.u.Utils - Using defaults.yaml from resources
975  [main] INFO  b.s.u.Utils - Using storm.yaml from resources
979  [main] INFO  b.s.StormSubmitter - Generated ZooKeeper secret payload for MD5-digest: -8573928005882740913:-7117292764002903545
981  [main] INFO  b.s.s.a.AuthUtils - Got AutoCreds []
998  [main] INFO  b.s.u.StormBoundedExponentialBackoffRetry - The baseSleepTimeMs [2000] the maxSleepTimeMs [60000] the maxRetries [5]
1011 [main] INFO  b.s.u.StormBoundedExponentialBackoffRetry - The baseSleepTimeMs [2000] the maxSleepTimeMs [60000] the maxRetries [5]
1052 [main] INFO  b.s.u.StormBoundedExponentialBackoffRetry - The baseSleepTimeMs [2000] the maxSleepTimeMs [60000] the maxRetries [5]
1066 [main] INFO  b.s.StormSubmitter - Uploading topology jar /home/ubuntu/storm/examples/storm-starter/storm-starter-topologies-0.10.1.jar to assigned location: /home/ubuntu/storm/storm-local/nimbus/inbox/stormjar-a64a40f9-cc98-4c3e-8163-3ed8e2c4f727.jar
Start uploading file '/home/ubuntu/storm/examples/storm-starter/storm-starter-topologies-0.10.1.jar' to '/home/ubuntu/storm/storm-local/nimbus/inbox/stormjar-a64a40f9-cc98-4c3e-8163-3ed8e2c4f727.jar' (3305726 bytes)
[==================================================] 3305726 / 3305726
File '/home/ubuntu/storm/examples/storm-starter/storm-starter-topologies-0.10.1.jar' uploaded to '/home/ubuntu/storm/storm-local/nimbus/inbox/stormjar-a64a40f9-cc98-4c3e-8163-3ed8e2c4f727.jar' (3305726 bytes)
1158 [main] INFO  b.s.StormSubmitter - Successfully uploaded topology jar to assigned location: /home/ubuntu/storm/storm-local/nimbus/inbox/stormjar-a64a40f9-cc98-4c3e-8163-3ed8e2c4f727.jar
1158 [main] INFO  b.s.StormSubmitter - Submitting topology wordcount in distributed mode with conf {"storm.zookeeper.topology.auth.scheme":"digest","storm.zookeeper.topology.auth.payload":"-8573928005882740913:-7117292764002903545","topology.workers":3,"topology.debug":true}
1458 [main] INFO  b.s.StormSubmitter - Finished submitting topology: wordcount

Furthermore, you can check out the StormUI at the URL given above after the cluster deploy.



Kill
Execute java -jar storm-deploy-alternative-local/target/storm-deploy-alternative-local.jar kill CLUSTER_NAME
Kills all nodes belonging in the cluster with name CLUSTER_NAME.
Limitations
Only deploying to Ubuntu AMIs on Amazon EC2 is supported.
