# NodeMetrics

Plugin for server monitoring with a built-in HTTP server. Returns a JSON
report on a protected request using `secret`.

## Features
- HTTP endpoint `/command` with `secret` verification
- Flexible response settings via `config.yml`
- Metrics: online, TPS, MSPT, memory, timestamp, and more
- Compatible with 1.20.1+

## Installation
1. Build the plugin and place the `.jar` into the `plugins` folder.
2. Start the server - `config.yml` will be created.
3. Configure `port`, `secret`, `nodeName`, and the `response` section.

## Config
File: `src/main/resources/config.yml` (after build - `plugins/NodeMetrics/config.yml`)

Example:
```yml
port: 8080
secret: "change-me"
endpoint: "/command"
nodeName: "node-1"
node-region: "eu"

response:
  nodeName: true
  node_region: true
  online: true
  max_online: false
  mspt: true
  tps: true
  total_memory: true
  free_memory: true
  max_memory: true
  timestamp: true
```

## Request
POST `http://<host>:<port><endpoint>`

Request body (JSON):
```json
{
  "secret": "change-me"
}
```

Response (example):
```json
{
  "node": "node-1",
  "node_region": "eu",
  "online": 12,
  "mspt": 19.5,
  "tps": [20.0, 19.98, 19.94],
  "total_memory": 1073741824,
  "free_memory": 536870912,
  "max_memory": 2147483648,
  "timestamp": 1738771200000
}
```

## Notes
- If a field is disabled in `response`, it will be omitted from the JSON.
- Invalid or missing `secret` returns 403.
# NodeMetrics

Plugin for server monitoring with a built-in HTTP server. Returns a JSON
report on a protected request using `secret`.

## Features
- HTTP endpoint `/command` with `secret` verification
- Flexible response settings via `config.yml`
- Metrics: online, TPS, MSPT, memory, timestamp, and more
 - Compatible with 1.20.1+

## Installation
1. Build the plugin and place the `.jar` into the `plugins` folder.
2. Start the server — `config.yml` will be created.
3. Configure `port`, `secret`, `nodeName`, and the `response` section.

## Config
File: `src/main/resources/config.yml` (after build — `plugins/NodeMetrics/config.yml`)

Example:
```yml
port: 8080
secret: "change-me"
endpoint: "/command"
nodeName: "node-1"
node-region: "eu"

response:
  nodeName: true
  node_region: true
  online: true
  max_online: false
  mspt: true
  tps: true
  total_memory: true
  free_memory: true
  max_memory: true
  timestamp: true
```

## Request
POST `http://<host>:<port><endpoint>`

Request body (JSON):
```json
{
  "secret": "change-me"
}
```

Response (example):
```json
{
  "node": "node-1",
  "node_region": "eu",
  "online": 12,
  "mspt": 19.5,
  "tps": [20.0, 19.98, 19.94],
  "total_memory": 1073741824,
  "free_memory": 536870912,
  "max_memory": 2147483648,
  "timestamp": 1738771200000
}
```

## Notes
- If a field is disabled in `response`, it will be omitted from the JSON.
- Invalid or missing `secret` returns 403.
#
