#!/bin/bash

sleep $((60 - $(date +%s) % 60))
starting=$(date -d "now" +%s)

difference=$(($starting + 2*15*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p sand -c c3 -e 3clientsSyncSandqoe -f true

difference=$(($starting + 4*15*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p sand -c c3 -e 3clientsaSyncSandqoe -f true

difference=$(($starting + 7*15*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p sand -c c3 -e 3clientsSyncSandbanddiv

difference=$(($starting + 9*15*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_2s/bbb_2s.mpd -p sand -c c3 -e 3clientsASyncSandbanddiv
