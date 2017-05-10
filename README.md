This is still a work in progress, once it's in a useable state, i will merge it with the master branch.  Currently ogame_1.0 branch is usable in the ide only.  This project will use that branch as a refernce point.
- - - -
# Ogame BOT #
Once this is done, the server will be hosted on a website which will make a local run not required.

#### Requirements to run locally ####
Java 8 download it [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)<br/>
maven  download it [here](https://maven.apache.org/download.cgi)<br/>
Postgres download it [here](https://www.postgresql.org/download/)

- - - -

Once postgres is downloaded run the the following file as postgres user:<br/>
`./jarExecutables/jarResources/database_config/postgres_commands.sql`

This is the parent folder for the ogame bot.  This is used to fully autonomize the game [Ogame.org](https://en.ogame.gameforge.com/).<br/>
Since I can't store jars larger than 100MB on git, you'll have to build the jar yourself.<br/>
Simply run in the root directory:<br/>
`mvn clean install -U`<br/><br/>

This will create and copy the jars for both projects into the `jarResources` directory.<br/><br/>
To Run the jars type:<br/>
`java -jar {{jarfile}}` where `{{jarfile}}` is the name of jar file you want to run.<br/>
You should get these jars in that directory:<br/>
`ogamebot-app-1.0-SNAPSHOT.jar` <br/>
`ogamebot-server-1.0-SNAPSHOT.jar`<br/><br/>
The description of each is below.

- - - -
### Ogame Bot App ###
This is the bot itself.  This uses the [Selenium Framework](http://www.seleniumhq.org/projects/webdriver/) with a [phantomjs headless browser](http://phantomjs.org/) to automate the game itself.

It also uses [Postgres](https://www.postgresql.org) to store data to be used by all bots in a cluster to communicate with each other.

- - - -
### Ogame Bot Server ###
This is the website to control and monitor one or more ogame bots.<br/>
The server consists of a java [Spring Boot](https://spring.io/docs) server that serves an [angularjs](https://angularjs.org/) website as well as holds the api for webrequests made from the website.

To run the server in an IDE, you must first download the bower components, run the following command at `/ogamebotserver/src/main/resources/static/` folder.

`bower install`