# camel-kie-example


### What is this?

* It's an Apache Camel component (endpoint) that integrates with KIE (Drools).
* It allows you to specify a KIE module (using maven GAV) which is pulling into the route and executed.
* It allows you to specify portions of the message body as facts.


### What isn't this?

* It doesn't use Spring (which means is much more simple to use).
* It does not allow you to start jBPM processes or interact with processes in any way (yet).


### How to use

1) Either start or ensure your maven repository is up and running (ie a Nexus, Artifactory etc..)
2) in "rules", change the pom's distributionManagement to point to your maven repository (I used Nexus on port 9000)
3) in "rules", run this command to build and deploy the pre-compiled KIE module (rules) into your maven repository

```
mvn clean deploy
```
4) Choose from the next two titles, either run using Fuse/Karaf or in your IDE


### Run from within your IDE

10) build the whole project from the root using the following command (it should build the component, domain, routes etc..):
```
mvn clean install
```
11) open "routes" in your IDE and run the test class named MaskNumberRouteTest - it should be green.
12) you're done! You've just run a camel route which loaded rules from your maven repository, executed them which changed the payload by masking out the phone numbers into X's


### Deploy and run on Fuse/Karaf

20) download jboss-fuse or Karaf, unzip and start it up
21) run the installer.karaf script by running the following command
```
source http://your-maven-repo/com.redhat.fuse/camel-kie-features/camel-kie-features-installer.karaf
```
or from your local maven repo by running:
```
source mvn:com.redhat.fuse/camel-kie-features/1.0-SNAPSHOT/karaf/installer
```
22) Wait for features to be deployed (specifically bundle camel-kie-example-routes should be in an active state)
23) to test the service, copy camel-kie-example/example-routes/src/test/resources/in.txt to /tmp/camel-kie-example
24) ensure the file was collected and that an /tmp/camel-kie-example/out.txt was created where the "to" numbers are obfuscated with XXXX characters





