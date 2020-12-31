# My_data_analyser
Analyser of 'my data' on several site such as Facebook or spotify

This goal is to extract interesting information from your personnal data, that can be accessed from sites/apps.
**There aren't no internet use, or share of your data.**

## Environement
I use IntelliJ IDEA, but it also work with Android Studio

Developpement is in **Kotlin**

## Doing
Folders/Files in analyse progress
* messages 
  * \[\*\] > Photos+Messages... 
* posts 
  * your_posts > Every posts + date
* comments
  * comments.json > Contains every comments that I posted

## TODOS

Folders/files that I want to analyse:
* events
  * your_event_responses.json > Contains joined+declined+interested events
  * Display a chrat abouot number of joined events
  * Allow browsing throught joined, declined and intrested events
* friends
  * friends.json > name + date of add 
  * Just need to display a chart showing the number of added friends
* groups
  * your_posts....json > every posts (including photos) + comments in groups
  * your..membership....json > groups joined and left + dates
  * Browse through groups, while showing the number of interactions
  * Display details about posts in a group, and joining date
* interactions
  * groups.json > groups + number of interactions with
  * I don't know if it is useful... The information is also in "groups" folder
* photos_and_videso
  * contains every albums + posted photos (with dates)
  * ..?
  
 ### Spotify !
 * Evolution of stream counts
 * Evolution of stream time
 * Best artists (in stream time)
   * Best artists of a period
 * Best songs (in stream time)
   * Best songs of a period
 * Search data ?
   * Evolution
   * History 
   * Bests ?

## Libraries 
I use [MPAndroidChart](https://weeklycoding.com/mpandroidchart/) for displaying graphs ([Javadoc](https://javadoc.jitpack.io/com/github/PhilJay/MPAndroidChart/v3.1.0/javadoc/)).
