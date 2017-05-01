This is still a work in progress, once it's in a useable state, i will merge it with the master branch.  Currently ogame_1.0 branch is usable in the ide only.  This project will use that branch as a refernce point.
- - - -
# Ogame BOT #
This is the parent folder for the ogame bot.  This is used to fully autonomize the game [Ogame.org](https://en.ogame.gameforge.com/).

- - - -
### Ogame Bot App ###
This is the bot itself.  This uses the [Selenium Framework](http://www.seleniumhq.org/projects/webdriver/) with a [phantomjs headless browser](http://phantomjs.org/) to automate the game itself.

- - - -
### Ogame Bot Server ###
This is the website to control and monitor one or more ogame bots.<br/>
The server consists of a java [Spring Boot](https://spring.io/docs) server that serves an [angularjs](https://angularjs.org/) website as well as holds the api for webrequests made from the website.
