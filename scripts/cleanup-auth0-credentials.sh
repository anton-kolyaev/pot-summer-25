#!/bin/bash

# Script to check for Auth0 credentials in the repository
# This script should be run BEFORE the main clean-git-history.sh script

set -e

echo "🔍 Checking for Auth0 credentials in the repository..."
echo ""

# Check for the specific Auth0 credentials mentioned in the issue
echo "Checking for specific Auth0 credentials..."

# Check for domain
if git grep -q "dev-4elhvjoejscooqih.us.auth0.com" 2>/dev/null; then
    echo "⚠️  Found Auth0 domain in repository"
    git grep -n "dev-4elhvjoejscooqih.us.auth0.com"
    echo ""
else
    echo "✅ No Auth0 domain found in current files"
fi

# Check for client ID
if git grep -q "pCf00o062D96Iv6yqjbMNvWmKJQ26bSZ" 2>/dev/null; then
    echo "⚠️  Found Auth0 client ID in repository"
    git grep -n "pCf00o062D96Iv6yqjbMNvWmKJQ26bSZ"
    echo ""
else
    echo "✅ No Auth0 client ID found in current files"
fi

# Check for API token (partial match)
if git grep -q "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkRTa3M4NVFOZGpFa0VGdTFhRmlUSCJ9" 2>/dev/null; then
    echo "⚠️  Found Auth0 API token in repository"
    echo "This is a security risk! Please clean the git history immediately."
    echo ""
else
    echo "✅ No Auth0 API token found in current files"
fi

# Check for client secret
if git grep -q "ZBmHGLPg3mM8Mmdpl8w5-ni59_UtC-nzvd6UwC3R_zktb8x2RSksQlYAyJLFHSWf" 2>/dev/null; then
    echo "⚠️  Found Auth0 client secret in repository"
    echo "This is a security risk! Please clean the git history immediately."
    echo ""
else
    echo "✅ No Auth0 client secret found in current files"
fi

# Check for audience URL
if git grep -q "https://dev-4elhvjoejscooqih.us.auth0.com/api/v2/" 2>/dev/null; then
    echo "⚠️  Found Auth0 audience URL in repository"
    git grep -n "https://dev-4elhvjoejscooqih.us.auth0.com/api/v2/"
    echo ""
else
    echo "✅ No Auth0 audience URL found in current files"
fi

echo ""
echo "📋 Auth0 Credential Cleanup Steps:"
echo "1. Run: ./scripts/clean-git-history.sh"
echo "2. Rotate your Auth0 credentials in the Auth0 dashboard"
echo "3. Update your environment variables with new credentials"
echo "4. Force push the cleaned repository"
echo "5. Notify team members to re-clone"
echo ""
echo "⚠️  CRITICAL: Rotate your Auth0 credentials immediately!"
echo "   - Go to Auth0 Dashboard → Applications → APIs → Auth0 Management API"
echo "   - Rotate the API token"
echo "   - Go to Applications → Your App → Settings → Rotate Client Secret" 