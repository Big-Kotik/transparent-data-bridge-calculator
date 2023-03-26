# Calculator masked spy app

Android app for inconspicuous recording, photo taking, media storage and emergency communication

[![License MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)


## How it works and why it is needed

What you can do:
```
✔ record audio 
✔ take pictures
✔ contact emergency contact in Telegram
✔ Wipe your storage if needed
```

How it works:
```
After file is created (photo taken or audio record) it will be encrypted and send through proxy to your local storage
```

Why it is needed:
```
1. during rallies - if police will take your phone, they won\'t be able to delete any of audio you recorded during the rally
2. to inconspicuous recording during interactions with state representatives
3. to cantant your lawyer in case of arrest
4. to remoted storage wiping during police search
```


## Guide

### How to deploy the app:
```bash
git clone https://github.com/Big-Kotik/transparentb-data-bridge-relay

sudo docker build -t relay .

sudo docker run -d -p 10000:10000 relay
```
please, consult with a test specialist deploy a relay server, to be sure, you are doing everything right

### How to use the app:

#### Install and get started:
```
1. download the app
2. Give the app all permissions it needs
3. Press "=" to see current state of the app
4. If something is not set, see the alert and specify the needed option
5. Repeat steps 3 and 4 until the alert "All set up"
```


#### Options to be set by user:
```
 - SERVER_INDEX - unique server id
 - PREFIX - prefix for every command
 - TELEGRAM_CHAT_ID - chat id to send message to
```
#### Commands to use
```
It is essential to write chosen prefix before every command you use, so try to choose short prefix
Commands:
 - 1 - start recording
 - 2 - stop recording (file will be saved to your server)
 - ( - enable camera 
 - 0 - take a picture (will be saved to your server)
 - ) - disable camera
 - 3 - implement telegram bot for alerts

So, if your prefix is "44", to take a picture you need to write 44(44044)
Or, if your prefix is "8", to make a recording your need to write 8182
```


## App Screen

<img src="https://github.com/Big-Kotik/transparent-data-bridge-calculator/blob/master/ART/Calculator.png" width="270" height="585">

## Future plans:

- implement storage wiping
- implement video recording
- add better encryption


### Contributing 💡
If you want to contribute to this project and make it better with new ideas, your pull request is very welcomed.
If you find any issue just put it in the repository issue section, thank you.


##### Thanks to ![JahidHasanCO](https://github.com/JahidHasanCO) for cover calculator app