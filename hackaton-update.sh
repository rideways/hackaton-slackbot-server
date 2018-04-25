#/bin/bash

mvn clean package -DskipTests

scp target/hackaton-slackbot-server-0.0.1-SNAPSHOT.jar slack@51.15.101.171:~/hackaton-slack-bot/hackaton-slackbot-server-0.0.1-SNAPSHOT.jar

# ssh slack@51.15.101.171 'cd ~/hackaton-slack-bot ; ln -sf hackaton-slackbot-server-0.0.1-SNAPSHOT.jar hackaton-slackbot-server.jar'

# ssh slack@51.15.101.171 'pkill -f hackaton-slackbot-server.jar'

# ssh slack@51.15.101.171 'cd ~/hackaton-slack-bot ; ./run.sh'
