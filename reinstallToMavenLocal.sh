#!/bin/sh

repoDirFoc="${HOME}/.m2/repository/ch/bailu/foc/"
repoDirFocAndroid="${HOME}/.m2/repository/ch/bailu/foc-android/"
echo "\nRemove 'foc' from local Maven repository"
rm -r ${repoDirFoc}

echo "\nRemove 'foc-android' from local Maven repository"
rm -r ${repoDirFocAndroid}


echo "\nInstall to local Maven repository"
./gradlew -q publishToMavenLocal || exit 1
find ${repoDirFoc}
find ${repoDirFocAndroid}
