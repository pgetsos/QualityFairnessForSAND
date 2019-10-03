#!/bin/bash

sleep $((60 - $(date +%s) % 60))
starting=$(date -d "now" +%s)

difference=$(($starting + 6*15*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p basic -c c3 -e 3clientsSyncbasic

difference=$(($starting + 7*15*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p netflix -c c3 -e 3clientsSyncNetflix

difference=$(($starting + 8*15*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p sara -c c3 -e 3clientsSyncSara

difference=$(($starting + 12*15*60 + 2*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p basic -c c3 -e 3clientsaSyncbasic

difference=$(($starting + 13*15*60 + 2*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p netflix -c c3 -e 3clientsaSyncNetflix

difference=$(($starting + 14*15*60 + 2*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p sara -c c3 -e 3clientsaSyncSara
