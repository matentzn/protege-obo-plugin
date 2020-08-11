#!/usr/bin/env bash
ws=~/ws/protege-obo-plugin/
protege_plugin_dir="/Applications/Protégé.app/Contents/Java/plugins"
plugin="obo-annotations-plugin-0.4.0.jar"

set -e

cd $ws
mvn clean package
cp target/$plugin $protege_plugin_dir