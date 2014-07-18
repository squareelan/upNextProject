#!/bin/bash
echo "Executing auto-deploy"
echo "======================"
echo ""

echo "Stopping upNext server"
echo "======================"
echo ""
sudo forever stop server.js


echo "Updating upNext project"
echo "======================="
echo ""
cd ~/aws/production/upNext
sudo git pull --rebase

echo "Restarting upNext server"
echo "======================="
echo ""
cd webapp
sudo forever start server.js

echo "Finished updating project."