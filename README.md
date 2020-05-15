# A custom Keycloak User Storage Provider

Implements a Keycloak User Storage Provider for
ALMA's User Registry.  

The actual storage provider is in the `jar-module` project; the `ear-module`
packages that and its dependencies in an 
[EAR file](https://en.wikipedia.org/wiki/EAR_(file_format))
suitable for deployment, thus allowing
the use of custom dependencies that are not part of the
Keycloak module space .

Based on 
[A custom Keycloak User Storage Provider](https://github.com/thomasdarimont/keycloak-user-storage-provider-demo) 
by [Thomas Darimont](https://github.com/thomasdarimont).

## Build

Check out keycloak-user-storage-provider (**NOTE**: 
currently hosted on GitHub at 
git@github.com:amchavan/keycloak-user-storage-provider.git).

Build the EAR file:

    mvn clean package

**NOTE** The build runs a set of unit tests that require 
a valid set of properties in _$ACSDATA/config/archiveConfig.properties_.

The EAR file will be generated as _keycloak-user-storage-provider/ear-module/target/user-storage-provider-bundle-&lt;version&gt;.ear_

## Install

Installation is as simple as copying the EAR file to the Keycloak
_deployments_ directory: `.../keycloak-<version>/standalone/deployments`  
The Keycloak server may be already running and doesn't need to be restarted.

**NOTE** The versions of the Keycloak server and the user storage provider should remain aligned.
