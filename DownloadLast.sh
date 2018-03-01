#!/bin/bash
#This script download the last stable build on jenkins
echo "Branch: "$1
if [[  $1 = "master"  ]]
then
	base_url="https://jenkins.seb6596.ovh/job/Bot%20Discord%20Gradle/lastStableBuild/artifact/"
else
	base_url="https://jenkins.seb6596.ovh/job/Bot%20Discord%20Gradle%20Devel/lastStableBuild/artifact/"
fi

data=$(curl -s -g "https://jenkins.seb6596.ovh/job/Bot%20Discord%20Gradle/lastStableBuild/api/xml?xpath=/freeStyleBuild/artifact&wrapper=artifacts")
relativePath=$(grep -oPm1 "(?<=<relativePath>)[^<]+" <<< "$data")
jarFile=$(grep -oPm1 "(?<=<fileName>)[^<]+" <<< "$data")



wget ${base_url}${relativePath} -O bot.jar -q


