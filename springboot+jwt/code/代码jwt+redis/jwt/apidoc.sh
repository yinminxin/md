#!/usr/bin/env bash

workspace=$(pwd)
appName=$(basename ${workspace})
logDir=${workspace}/logs
targetDir=${workspace}/target

echo "<<<<<<<<<<<<<<<<<<<<<<<<"
echo "BUILD APIDOC ..."
echo ">>>>>>>>>>>>>>>>>>>>>>>>"

apiName=${appName}

#apidoc -i ${workspace}/src/main/java/com/lk/hm/api -o ${targetDir}/${apiName} -t ../apidoc-contentType-plugin/template/ --parse-parsers apicontenttype=../apidoc-contentType-plugin/api_content_type.js
apidoc -i ${workspace}/src/main/java/com/course/ymx/jwt/controller -o ${targetDir}/${apiName}
echo $(date +%Y%m%d%H%M%S)
echo "BUILD APIDOC SUCCESS"