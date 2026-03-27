#!/bin/bash

mvn clean compile package

rm -rf .flatpak-builder
flatpak-builder build-dir me.elordenador.tpv.yml --force-clean --repo=/home/daniel/.repo

flatpak update me.elordenador.tpv -y