# WebProtégé with XDP Installation Guide

## Prerequisites

All of the below need be installed on the machine where the project is built:

* Apache Maven
* Gradle
* Git
* MongoDB (default installation of MongoDB is fine, no particular configuration needs be done).
* Java 8

## Introduction

Building and running WebProtégé with the XDP plugin is a five step process, each of which is described in more detail further down:

1. Building and installing Semantic Vectors 5.9. Release versions of Semantic Vectors are even numbered (5.4, 5.6, 5.8 …) and are available through Maven Central from which they can be automatically downloaded and built as required by Maven. However, due to a dependency version conflict we need to use the as yet unreleased version 5.9, which has to be manually downloaded, built and installed into your local Maven repository.

2. Building and installing the XdpShared package. This package contains shared data classes that are used for communication between the XDP WebProtégé plugin and the back-end XdpServices indexing and search service.

3. Building and running the XdpServices back-end service. Optionally this service can also be installed into some init script or maintenance daemon system to ensure that it is started up as needed by the system - that is outside of scope of this guide.

4. Initiate XdpServices indexing over a set of ODPs. Optionally, a pre-built index can be used if one is available.

5. Building and running WebProtégé with XDP. This is very similar to building standard WebProtégé.

## Installing Semantic Vectors

1. Clone Semantic Vectors repository:

git clone https://github.com/semanticvectors/semanticvectors.git

2. Step into directory

cd semanticvectors

3. Install into local Maven repository:

mvn install

## Installing XdpShared

1. Clone GitHub repository:

https://github.com/hammar/XdpServices.git

2. Step into directory

cd XdpServices

3. Install into local Maven repository:

gradle install

## Running XdpServices

1. Clone GitHub repository:

git clone https://github.com/hammar/XdpServices.git

2. Step into directory:

cd XdpServices

3. Start service:

gradle bootRun

4. OPTIONALLY, build a redistributable JAR archive that can be installed into some init script or maintenance daemon system:

gradle jar
gradle bootRepackage

## Perform initial indexing

XdpServices expects the following paths to exist and be writable by the user running XdpServices (either the current user if run from console, or the Tomcat service account if run from there):

* /data/xdpservices/search/LuceneIndex/ - Initially empty directory where the Lucene index is created.
* /data/xdpservices/search/SemanticVectors/ - Initially empty directory where Semantic Vectors index is created.
* /data/xdpservices/search/ODPs/ - Holds the ODPs that are to be indexed.
* /data/xdpservices/search/Wordnet/ - Holds a WordNet release that works with JWI (http://projects.csail.mit.edu/jwi/)

Once these paths and their contents exist on the building system, indexing is initiated by accessing http://localhost:7777/index/rebuildIndex

## Build and run WebProtégé with XDP

Before building, ensure that a data directory exists which is writable by the WebProtégé user (either the current user if run directly from console, or the Tomcat system account if run from Tomcat). By default the data directory which WebProtégé expects to exist is /data/webprotege/. However this path can be changed at build time (see below).

1. Clone GitHub repository:

git clone https://github.com/hammar/webprotege.git

2. Step into directory:

cd webprotege

3. Run GWT Dev Mode:

mvn gwt:run

4. Or optionally, if your data directory is NOT /data/webprotege, run GWT Dev Mode with a parameter indicating where that directory is located:

mvn -Ddata.directory=/mypath/mydirectory gwt:run

5. Within the GWT Development Mode console that is brought up on your screen, click ”Launch Default Browser”, allow the Java to JavaScript compilation to take place (can take a little while) and then use WebProtégé as expected.


