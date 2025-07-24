#!/bin/bash

# Auth0 Environment Variables Setup Script
# Replace the placeholder values below with your actual Auth0 credentials

echo "Setting up Auth0 environment variables..."

# Replace these values with your actual Auth0 credentials
export AUTH0_ENABLED=true
export AUTH0_DOMAIN="dev-4elhvjoejscooqih.us.auth0.com"  # Replace with your Auth0 domain
export AUTH0_API_TOKEN="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkRTa3M4NVFOZGpFa0VGdTFhRmlUSCJ9.eyJpc3MiOiJodHRwczovL2Rldi00ZWxodmpvZWpzY29vcWloLnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJwQ2YwMG8wNjJEOTZJdjZ5cWpiTU52V21LSlEyNmJTWkBjbGllbnRzIiwiYXVkIjoiaHR0cHM6Ly9kZXYtNGVsaHZqb2Vqc2Nvb3FpaC51cy5hdXRoMC5jb20vYXBpL3YyLyIsImlhdCI6MTc1MzM3NjI4NCwiZXhwIjoxNzUzNDYyNjg0LCJzY29wZSI6InJlYWQ6dXNlcnMgdXBkYXRlOnVzZXJzIGRlbGV0ZTp1c2VycyBjcmVhdGU6dXNlcnMiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMiLCJhenAiOiJwQ2YwMG8wNjJEOTZJdjZ5cWpiTU52V21LSlEyNmJTWiJ9.DWKvN8i8GAHY2TPUDAEiEmx-Io2bnYmb13ryr9OHf-YCe6cgsdLyoTiLOULkmvzVviRKV9d7vS6Eh2xnLf1TuU5IBpoLFGCqkEJfP-qTX-AX2u-yzZG2ahIA4lrzY_Cyx4lOyYF1YXIYtb6j6JHetYcBOeFH4htcV_M_a6pBS3b_5qPubCFHSJraFGPN6kv3ke_7ct619iaFzzWOpiOsLZvN5LuGjt6ni6JjbjPa2Iy7OwEXxCSRweyIi-QopSYhHzEqR9iwkcctL8N0h4OLk8iVI1pFpzp73j2a7qQxH5tjDrCxM2mPNDBjoXzsfSTRX35ZGYmDvEnJmiK_Z_QMfg"
export AUTH0_CLIENT_ID="pCf00o062D96Iv6yqjbMNvWmKJQ26bSZ"  # Replace with your application client ID
export AUTH0_CLIENT_SECRET="ZBmHGLPg3mM8Mmdpl8w5-ni59_UtC-nzvd6UwC3R_zktb8x2RSksQlYAyJLFHSWf"  # Replace with your application client secret
export AUTH0_AUDIENCE="https://dev-4elhvjoejscooqih.us.auth0.com/api/v2/"  # Replace with your API audience
export AUTH0_TIMEOUT=10000

echo "Auth0 environment variables set:"
echo "AUTH0_ENABLED: $AUTH0_ENABLED"
echo "AUTH0_DOMAIN: $AUTH0_DOMAIN"
echo "AUTH0_API_TOKEN: [HIDDEN]"
echo "AUTH0_CLIENT_ID: $AUTH0_CLIENT_ID"
echo "AUTH0_CLIENT_SECRET: [HIDDEN]"
echo "AUTH0_AUDIENCE: $AUTH0_AUDIENCE"
echo "AUTH0_TIMEOUT: $AUTH0_TIMEOUT"

echo ""
echo "To make these permanent, add them to your ~/.bashrc or ~/.zshrc file"
echo "Or run this script before starting the application" 