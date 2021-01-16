# MemoryMash


## MemoryMash by Michael Zeolla

## Table of Contents
1. [Overview](#Overview)
2. [App Evaluation](#App-Evaluation)
3. [Schema](#Schema)
4. [Technology Used](#Technology-Used)
5. [Artistic Credit](#Artistic-Credit)



### Overview
Memory Mash is a creative memory matching app meant to help people with memorization skills, as well as kill time. In this app, the user can create play with a series of default image sets on multiple varying difficulties, as well as create their own game with custom images, that other players can play with. 



<img src='https://i.imgur.com/Krmp2YV.gif' width='' alt='Video Walkthrough' />  <img src='https://i.imgur.com/64KnJmv.gif' width='' alt='Video Walkthrough' />
<img src='https://i.imgur.com/t93jIdw.gif' width='' alt='Video Walkthrough' />  <img src='https://i.imgur.com/fqMHHHV.gif' width='' alt='Video Walkthrough' />



GIF created with [LiceCap](https://www.cockos.com/licecap/).



### App Evaluation
- **Category:** Video Game
- **Mobile:** Mobile is essential for users to easily play the game and create custom games. New custom games are available for download for other users.
- **Story:** Creates a system for any users seeking to improve memory matching/memorization or spend time while waiting.
- **Market:** Anyone who looks to improve their matching skills and enjoys playing mobile games. 
- **Habit:** Users would use the app daily to spend the time they would otherwise waste.


### Schema
Custom Board Object
| Property      | Type | Description |
| ----------- | ----------- | ------------ |
| Board Size | Integer  | Size of the game baord. |
| Difficulty | String  | Difficulty for the specific game type |
| Images | Array<String> | URI's to the images for download if a custom game. |
| Name | String | Name of the custom game mode. |
  

Normal Board Object
|Property |Type |Description|
|---|---|---|
| Board Size | Integer  | Size of the game baord. |
| Difficulty | String  | Difficulty for the specific game type |
| Images | Array<String> | List of the default images to be displayed |
| Category | String | Name of the category for the game mode. |



### Technology Used

Room Persistence Library, Firebase, Google Photos. 



### Artistic Credit

Graphic Icon and SplashScreen Produced by: [Marcela Mayor](https://www.instagram.com/marcy_mayor/)

Category Custom Icons for the Video Games Category made by Freepik from www.flaticon.com" at https://www.flaticon.com/packs/video-game-interface-3?word=video%20games

Category Custom Icons for the Sports Category made by Freepik from www.flaticon.com" at https://www.flaticon.com/packs/sports-151?word=sports

Category Custom Icons for the Animals Category made by Freepik from www.flaticon.com" at https://www.flaticon.com/packs/animals-126?k=1610212110571


