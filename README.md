This is several months old, i plan to return to work on this in a while.  I'll be more interested in returning to it if others show interest in this.  The goal is to make a client that the user can use on their own computer with a progressive web app UI or an app/website where users can create bots that will use a proxy and have one or more bots be utilized through the website.  Via electron and ionic, a native app for both mobile and other os can be made.

This is still a work in progress.  Currently it is only functional through an IDE.

The goal of 3.0 is to allow users to run this from a single jar.  Users would be able to automate only as much as they want to.  

The bot's goal will be two fold, fully automated hive mind of bots or partially automated bot for quality of life for players.

Since this bot will try to replicate a user as much as possible using selenium a web browser testing framework, it is highly unlikely to be discovered as a bot

I plan to seperate this project into two repositories to make it easier to manage.  A client repository and a bot repository.  The client will be a UI for users to interact with the bot and the bot will be the program which will actually perform the actions.  Also would like to support distributed systems with a proxy to control a cluster of bots.

Tasks to be automated will include 
- fleet/resource saving
- automated building/research/ship queue with a scheduler 
- daily item grab
- farming of inactive and active players
- espianoge of players
- farming of recycling debris field
- moon creation by building light fighters (or other ship) and crashing it into a planet
- planet field finder, building colony ships and colonizing a specified planet slot until a field range is discovered
- Add increasing levels of human play replication as to not be discovered as a bot


- - - -
# Ogame BOT #
Once this is done, the server will be hosted on a website which will make a local run not required.

#### Requirements to run locally ####
Java 8 download it [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)<br/>

- - - -
This is the parent folder for the ogame bot.  This is used to fully autonomize the game [Ogame.org](https://en.ogame.gameforge.com/).<br/>

- - - -
### Ogame Bot App ###
This is the bot itself.  This uses the [Selenium Framework](http://www.seleniumhq.org/projects/webdriver/) with a [phantomjs headless browser](http://phantomjs.org/) to automate the game itself.

It also uses [Postgres](https://www.postgresql.org) to store data to be used by all bots in a cluster to communicate with each other.  Postgres is shipped with the code.


When postgres fails to load for whatever reason, it will fall back to [HSQLDB](http://hsqldb.org/) which shouldn't have any issues.  HSQL isn't as good as postgres however due to read/write speeds.

- - - -
### Ogame Bot Server ###
This is the website to control and monitor one or more ogame bots.<br/>
The server consists of a java [Spring Boot](https://spring.io/docs) server that serves an [angularjs](https://angularjs.org/) website as well as holds the api for webrequests made from the website.

The Server has a website that when run is located at [localhost:8080](localhost:8080).  It will then require a log in.  You may create a new one in the register page.
