#!/usr/bin/env bash

set -euxo pipefail

curl -v http://localhost:8080 | python -m json.tool
