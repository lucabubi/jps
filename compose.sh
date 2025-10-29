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

echo "Avvio completato."
