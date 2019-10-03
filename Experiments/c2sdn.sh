#!/bin/bash

sleep $((60 - $(date +%s) % 60))
starting=$(date -d "now" +%s)

difference=$(($starting + 3*15*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p basic -c c2 -e 2clientsSyncBasic

difference=$(($starting + 4*15*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p netflix -c c2 -e 2clientsSyncNetflix

difference=$(($starting + 5*15*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p sara -c c2 -e 2clientsSyncSara

difference=$(($starting + 6*15*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p basic -c c2 -e 3clientsSyncBasic

difference=$(($starting + 7*15*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p netflix -c c2 -e 3clientsSyncNetflix

difference=$(($starting + 8*15*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p sara -c c2 -e 3clientsSyncSara

difference=$(($starting + 9*15*60 + 60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p basic -c c2 -e 2clientsaSyncBasic

difference=$(($starting + 10*15*60 + 60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p netflix -c c2 -e 2clientsaSyncNetflix

difference=$(($starting + 11*15*60 + 60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p sara -c c2 -e 2clientsaSyncSara

difference=$(($starting + 12*15*60 + 60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p basic -c c2 -e 3clientsaSyncBasic

difference=$(($starting + 13*15*60 + 60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p netflix -c c2 -e 3clientsaSyncNetflix

difference=$(($starting + 14*15*60 + 60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p sara -c c2 -e 3clientsaSyncSara
