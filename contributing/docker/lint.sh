#! /bin/bash

cd $BFD_MOUNT_POINT/apps;

cd $BFD_MOUNT_POINT/apps/$1

mvn com.spotify.fmt:fmt-maven-plugin:check
