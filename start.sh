#!/bin/bash

set -euo pipefail

PROJECT_ROOT=$(cd "$(dirname "$0")" && pwd)
cd "$PROJECT_ROOT"

# Optional: normalize ownership (uncomment if needed)
# echo "Normalizzo ownership dei file (se necessario)..."
# sudo chown -R "$(id -un)":"$(id -gn)" . || true

GRADLE_BUILD() {
  local dir=$1
  local image=$2
  echo "[BUILD] $dir -> $image"
  (cd "$dir" && ./gradlew --no-daemon bootBuildImage --imageName="$image")
}

# Build backend images (no sudo)
GRADLE_BUILD gatewayAPI g19/gateway_api
GRADLE_BUILD CRM g19/crm
GRADLE_BUILD document_store g19/document_store
GRADLE_BUILD communication_manager g19/communication_manager
GRADLE_BUILD analytics_crm g19/analytics_crm

# Frontend build
echo "[BUILD] user-interface (JobPlacementServices) -> g19/user-interface"
(cd user-interface/JobPlacementServices && docker build -t g19/user-interface .)

# Ensure network exists
if ! docker network inspect jps-net >/dev/null 2>&1; then
  echo "[NET] Creo la rete jps-net"
  docker network create jps-net
fi

# Compose up (aggregated)
COMPOSE_FILES=( \
  gatewayAPI/compose_mac.yaml \
  CRM/compose.yaml \
  analytics_crm/compose.yaml \
  communication_manager/compose.yaml \
  document_store/compose.yaml \
  user-interface/JobPlacementServices/compose.yaml \
)

COMPOSE_ARGS=()
for f in "${COMPOSE_FILES[@]}"; do
  COMPOSE_ARGS+=( -f "$f" )
  if [[ ! -f $f ]]; then
    echo "[WARN] File compose mancante: $f" >&2
  fi
done

echo "[UP] Avvio stack Docker..."
docker compose -p jps "${COMPOSE_ARGS[@]}" up -d

echo "[DONE] Build e avvio completati."
