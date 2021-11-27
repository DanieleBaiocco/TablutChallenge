#!/bin/bash
parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd $parent_path
java -jar TablutINiegghie.jar "$1" "$2" "$3"
