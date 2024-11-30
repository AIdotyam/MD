#!/bin/bash

git add .

read -r -p "Enter commit message: " commit_message
git commit -m "$commit_message"

git push -u origin master
git push -u fork master

echo "Changes pushed to both remote repositories."

read -n 1 -s -r -p "Press any key to continue"