#!/usr/bin/env bash
bowerList=(
    'jquery'
    'angular'
    'angular-animate'
    'angular-aria'
    'angular-cookies'
    'angular-material'
    'angular-material-icons'
    'angular-messages'
    'angular-route'
    'angular-ui-router'
);

for i in "${bowerList[@]}"; do
    bower install $i;
done