# camel-kie-example


### What is this?

* It's an Apache Camel component (endpoint) that integrates with KIE (Drools).
* It allows you to specify a KIE module (using maven GAV) which is pulled into the route and executed.
* It allows you to specify portions of the message body as facts.


### What this isn't?

* It doesn't use Spring (which means is much more simple to use).
* It does not allow you to start jBPM processes or interact with processes in any way (yet).


### Instruction Manual

#### URI Format
``` xml
drools:ReleaseId[?options]
```
Where ReleaseId is colon delimited Group, Artifact, Version (or GAV) for specifying maven artifacts. For example, "com.your.company:your-artifact:1.0" 

**Example:**
``` xml
<to uri="drools:com.redhat.fuse:camel-kie-example-rules:1.0-SNAPSHOT?facts=${body.phoneCalls}&amp;kieBaseName=KBase1" />
```

#### URI Options
| Name             | Default Value | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
|------------------|---------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| facts            | ${body}       | Mandatory. Using simple language identify what portion of the message body you'd like to be inserted into the Drools Session as facts. If the expression resolves as an Iterable class then this component will iterate through each items inserting them individually. ie. ${body} will insert then entire body, and ${body.phoneCalls} will insert the phone call list, but because a List is Iterable it will insert each PhoneCall object individually. |
| kieBaseName      |               | The name of the Kie Base in the Kie Module specified in the ReleaseId. Leaving this field empty means it will pick the default KieBase in the KieModule.                                                                                                                                                                                                                                                                                                    |
| kieMavenSettings |               | If set, this must point to a maven settings.xml file for Drools to find the artifact with the ReleaseId                                                                                                                                                                                                                                                                                                                                                     |


### Running the Example

* Either start or ensure your maven repository is up and running (ie a Nexus, Artifactory etc..)
* in "rules", change the pom's distributionManagement to point to your maven repository (I used Nexus on port 9000)
* in "rules", run this command to build and deploy the pre-compiled KIE module (rules) into your maven repository

```
mvn clean deploy
```
* Choose from the next two titles, either run in your IDE or using Fuse/Karaf 


### Running the Example - from within your IDE

* build the whole project from the root using the following command (it should build the component, domain, routes etc..):
```
mvn clean install
```
* open "routes" in your IDE and run the test class named MaskNumberRouteTest - it should be green.
* you're done! You've just run a camel route which loaded rules from your maven repository, executed them which changed the payload masking out the phone numbers into X's


### Running the Example - deploy and run on Fuse/Karaf

* download jboss-fuse or Karaf, unzip and start it up
* run the installer.karaf script by running the following command
```
source http://your-maven-repo/com.redhat.fuse/camel-kie-features/camel-kie-features-installer.karaf
```
or from your local maven repo by running:
```
source mvn:com.redhat.fuse/camel-kie-features/1.0-SNAPSHOT/karaf/installer
```
* Wait for features to be deployed (specifically bundle camel-kie-example-routes should be in an active state)
* to test the service, copy camel-kie-example/example-routes/src/test/resources/in.txt to /tmp/camel-kie-example
* ensure the file was collected and that an /tmp/camel-kie-example/out.txt was created where the "to" numbers are obfuscated with XXXX characters
* You're done! You've just run a camel route which loaded rules from a maven repository, executed them and changed the payload masking out the phone numbers into X's

