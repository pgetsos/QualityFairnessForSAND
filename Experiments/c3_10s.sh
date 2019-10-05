#!/bin/bash

sleep $((60 - $(date +%s) % 60))
starting=$(date -d "now" +%s)

difference=$(($starting + 8*15*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_10s/bbb_10s.mpd -p basic -f true -c c3 -e 3clientsSyncbasic

difference=$(($starting + 9*15*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_10s/bbb_10s.mpd -p netflix -c c3 -e 3clientsSyncNetflix

difference=$(($starting + 10*15*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_10s/bbb_10s.mpd -p sara -c c3 -e 3clientsSyncSara

difference=$(($starting + 11*15*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_10s/bbb_10s.mpd -p sand -c c3 -e 3clientsSyncSandqoe -f true

difference=$(($starting + 16*15*60 + 2*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_10s/bbb_10s.mpd -p basic -f true -c c3 -e 3clientsaSyncbasic

difference=$(($starting + 17*15*60 + 2*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_10s/bbb_10s.mpd -p netflix -c c3 -e 3clientsaSyncNetflix

difference=$(($starting + 18*15*60 + 2*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_10s/bbb_10s.mpd -p sara -c c3 -e 3clientsaSyncSara

difference=$(($starting + 19*15*60 + 2*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_10s/bbb_10s.mpd -p sand -c c3 -e 3clientsaSyncSandqoe -f true

difference=$(($starting + 22*15*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_10s/bbb_10s.mpd -p sand -c c3 -e 3clientsSyncSandbanddiv

difference=$(($starting + 24*15*60 + 2*60 - $(date +%s)))
sleep $difference

python3 ../Client/client/dash_client.py -m http://10.0.0.7:8006/bunny_10s/bbb_10s.mpd -p sand -c c3 -e 3clientsASyncSandbanddiv
