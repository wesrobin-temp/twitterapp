# Twitter Feed Coding Assignment

## How to run:
* Clone the repo to your local machine.
* Open a terminal and navigate to the repo.
* Build the project by running the following command: `mvn assembly:assembly`.
    * Requires maven be installed on the local machine, get the latest version from [here](https://maven.apache.org/download.cgi).
    * This should build a executable jar-with-dependencies. One has been included in the base project dir of this repo just in case.
* To execute, run the following command: `java -jar twitter-app-1.0.0-jar-with-dependencies.jar <user.txt> <tweet.txt>`.
    * Requires at least [java 8](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) to be installed on the local machine.
    * `user.txt` is the first argument and must be the path to the "user.txt" file containing user information.
    * `tweet.txt` is the second argument and must be the path to the "tweet.txt" file containing users' tweets.


By Wesley Robinson, 21/06/2019