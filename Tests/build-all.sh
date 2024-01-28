#!/bin/bash

echo ".2_,.-*,.4*;.-_,.6*,.-*;.3*,.-*,.-_" > file.txt
./playMosaic.sh A < file.txt
rm file.txt
