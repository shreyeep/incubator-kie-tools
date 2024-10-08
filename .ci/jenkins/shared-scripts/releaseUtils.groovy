/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
* Setup the GPG Key to sign release artifacts
*/
def setupSigningKey(String gpgKeyCredentialsId) {
    withCredentials([file(credentialsId: gpgKeyCredentialsId, variable: 'SIGNING_KEY')]) {
        sh """#!/bin/bash -el
        gpg --list-keys
        gpg --batch --pinentry-mode=loopback --import $SIGNING_KEY
        """.trim()
    }
}

/**
* Sign an artifact using GPG
*/
def signArtifact(String artifactFileName) {
    sh """#!/bin/bash -el
    gpg --no-tty --batch --sign --pinentry-mode loopback --output ${artifactFileName}.asc --detach-sig ${artifactFileName}
    shasum -a 512 ${artifactFileName} > ${artifactFileName}.sha512
    """.trim()
}

/**
* Publish release artifacts to a SVN repository
*/
def publishArtifacts(String artifactsDir, String releaseRepository, String releaseVersion, String credentialsId) {
    withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'ASF_USERNAME', passwordVariable: 'ASF_PASSWORD')]) {
        sh """#!/bin/bash -el
        svn co --depth=empty ${releaseRepository}/${releaseVersion} svn-kie
        cp ${artifactsDir}/* svn-kie
        cd svn-kie
        svn add . --force
        svn ci --non-interactive --no-auth-cache --username ${ASF_USERNAME} --password '${ASF_PASSWORD}' -m "Apache KIE ${releaseVersion} artifacts"
        rm -rf svn-kie
        """.trim()
    }
}

/**
* Download release artifacts from a specific release
*/
def downloadReleaseArtifacts(String releaseRepository, String artifactsDir, String releaseVersion) {
    sh """#!/bin/bash -el
    mkdir -p "${artifactsDir}"
    svn co "${releaseRepository}/${releaseVersion}" "${artifactsDir}"
    """.trim()
}

/**
* Return a list of upstream images artifacts
*/
def getUpstreamImagesArtifactsList(String artifactsDir, String releaseVersion) {
    return [
        "${artifactsDir}/incubator-kie-${releaseVersion}-kogito-base-builder-image.tar.gz",
        "${artifactsDir}/incubator-kie-${releaseVersion}-kogito-data-index-ephemeral-image.tar.gz",
        "${artifactsDir}/incubator-kie-${releaseVersion}-kogito-data-index-postgresql-image.tar.gz",
        "${artifactsDir}/incubator-kie-${releaseVersion}-kogito-jit-runner-image.tar.gz",
        "${artifactsDir}/incubator-kie-${releaseVersion}-kogito-jobs-service-allinone-image.tar.gz",
        "${artifactsDir}/incubator-kie-${releaseVersion}-kogito-jobs-service-ephemeral-image.tar.gz",
        "${artifactsDir}/incubator-kie-${releaseVersion}-kogito-jobs-service-postgresql-image.tar.gz"
    ]
}

return this
