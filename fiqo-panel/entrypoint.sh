#!/bin/sh
cat <<EOF > /usr/share/nginx/html/env.js
window.env = {
  SPRING_BASE_URL: "${SPRING_BASE_URL}"
};
EOF

exec "$@"
