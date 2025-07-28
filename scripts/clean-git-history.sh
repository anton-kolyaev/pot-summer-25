#!/bin/bash

# Script to clean Auth0 credentials from git history
# WARNING: This will rewrite git history. Make sure to backup your repository first!

set -e

echo "⚠️  WARNING: This script will rewrite git history!"
echo "Make sure you have backed up your repository before proceeding."
echo ""

read -p "Have you backed up your repository? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Please backup your repository first, then run this script again."
    exit 1
fi

echo "Creating auth0-credentials.txt file with Auth0-specific patterns..."

cat > auth0-credentials.txt << EOF
# Auth0 credentials (specific to this repository)
dev-4elhvjoejscooqih.us.auth0.com
pCf00o062D96Iv6yqjbMNvWmKJQ26bSZ
eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkRTa3M4NVFOZGpFa0VGdTFhRmlUSCJ9.eyJpc3MiOiJodHRwczovL2Rldi00ZWxodmpvZWpzY29vcWloLnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJwQ2YwMG8wNjJEOTZJdjZ5cWpiTU52V21LSlEyNmJTWkBjbGllbnRzIiwiYXVkIjoiaHR0cHM6Ly9kZXYtNGVsaHZqb2Vqc2Nvb3FpaC51cy5hdXRoMC5jb20vYXBpL3YyLyIsImlhdCI6MTc1MzM3NjI4NCwiZXhwIjoxNzUzNDYyNjg0LCJzY29wZSI6InJlYWQ6dXNlcnMgdXBkYXRlOnVzZXJzIGRlbGV0ZTp1c2VycyBjcmVhdGU6dXNlcnMiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMiLCJhenAiOiJwQ2YwMG8wNjJEOTZJdjZ5cWpiTU52V21LSlEyNmJTWiJ9.DWKvN8i8GAHY2TPUDAEiEmx-Io2bnYmb13ryr9OHf-YCe6cgsdLyoTiLOULkmvzVviRKV9d7vS6Eh2xnLf1TuU5IBpoLFGCqkEJfP-qTX-AX2u-yzZG2ahIA4lrzY_Cyx4lOyYF1YXIYtb6j6JHetYcBOeFH4htcV_M_a6pBS3b_5qPubCFHSJraFGPN6kv3ke_7ct619iaFzzWOpiOsLZvN5LuGjt6ni6JjbjPa2Iy7OwEXxCSRweyIi-QopSYhHzEqR9iwkcctL8N0h4OLk8iVI1pFpzp73j2a7qQxH5tjDrCxM2mPNDBjoXzsfSTRX35ZGYmDvEnJmiK_Z_QMfg
ZBmHGLPg3mM8Mmdpl8w5-ni59_UtC-nzvd6UwC3R_zktb8x2RSksQlYAyJLFHSWf
https://dev-4elhvjoejscooqih.us.auth0.com/api/v2/
EOF

echo "Auth0 credentials file created. Review auth0-credentials.txt and add any additional Auth0-specific patterns."

read -p "Continue with Auth0 git history cleaning? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Aborted."
    exit 1
fi

# Check if BFG is installed
if ! command -v bfg &> /dev/null; then
    echo "BFG Repo-Cleaner not found. Installing..."
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        brew install bfg
    else
        echo "Please install BFG manually: https://rtyley.github.io/bfg-repo-cleaner/"
        echo "Or use the alternative git filter-branch method below."
        exit 1
    fi
fi

echo "Cleaning Auth0 credentials from repository with BFG..."
bfg --replace-text auth0-credentials.txt .git

echo "Cleaning up..."
git reflog expire --expire=now --all
git gc --prune=now --aggressive

echo "✅ Auth0 credentials cleaned from git history!"
echo ""
echo "Next steps:"
echo "1. Review the changes: git log --oneline"
echo "2. Test your application to ensure it still works"
echo "3. Force push to remote: git push --force --all"
echo "4. Force push tags: git push --force --tags"
echo ""
echo "⚠️  IMPORTANT: Inform all team members to re-clone the repository after you push!"
echo "⚠️  IMPORTANT: Rotate your Auth0 credentials immediately!" 