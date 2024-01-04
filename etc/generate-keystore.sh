#!/bin/sh

keytool -genkeypair -keyalg Ed25519 -dname "CN=Test" -alias sign -validity 36500 -storepass test1! -keystore test.p12
keytool -genkeypair -keyalg X25519 -dname "CN=Test" -alias dh -signer sign -validity 36500 -storepass test1! -keystore test.p12