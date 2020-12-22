Resources: 

https://github.com/stirante/RuneChanger

https://github.com/stirante/lol-client-java-api

https://riot-api-libraries.readthedocs.io/en/latest/lcu.html

https://nickcano.com/reversing-league-of-legends-client/

https://lcu.vivide.re/ - LCU docs 

TODO: 

Hook up runesReforged.json to runes to get names instead of integers (JSON pattern matching)

Spin up Discord API - check if Discord has r/w/x permissions to set runes 


Problems:
LCU wrapper only runs locally - can't be entirely server side 
-create lightweight client that takes care of setting runes 
-might need to write own api that interacts w client, ie: 
>user: !runes $champion 
>server returns runepage 
>client reads response via discord 
>client sets runepage 

