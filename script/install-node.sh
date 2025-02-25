#!/usr/bin/bash
#
# The MIT License (MIT)
#
# Copyright (c) 2019 Code Technology Studio
#
# Permission is hereby granted, free of charge, to any person obtaining a copy of
# this software and associated documentation files (the "Software"), to deal in
# the Software without restriction, including without limitation the rights to
# use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
# the Software, and to permit persons to whom the Software is furnished to do so,
# subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
# FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
# COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
# IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
# CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#

# shellcheck disable=SC2016,SC2119,SC2155,SC2206,SC2207,SC2254

node_version=18.19.0

ARCH="$(dpkg --print-architecture)"
case "${ARCH}" in
aarch64 | arm64)
	BINARY_ARCH='arm64'
	;;
amd64 | x86_64)
	BINARY_ARCH='x64'
	;;
*)
	echo "Unsupported arch: ${ARCH}"
	exit 1
	;;
esac

curl -LfsSo /opt/node-v${node_version}-linux-${BINARY_ARCH}.tar.gz https://npmmirror.com/mirrors/node/v${node_version}/node-v${node_version}-linux-${BINARY_ARCH}.tar.gz
tar -zxvf /opt/node-v${node_version}-linux-${BINARY_ARCH}.tar.gz -C /opt/


cat >"/etc/profile" <<EOL
# node js
export NODE_HOME="/opt/node-v${node_version}-linux-${BINARY_ARCH}/"
export PATH=$PATH:$NODE_HOME/bin
EOL

source /etc/profile

node -v
