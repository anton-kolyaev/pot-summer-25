#!/bin/bash

# Script to clean sensitive data from git history
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

echo "Creating passwords.txt file with sensitive patterns..."

cat > passwords.txt << EOF
insurance_app_password
postgres
test-api-token
test-client-secret
your-api-token
your-client-secret
EOF

echo "Passwords file created. Review passwords.txt and add any additional sensitive data patterns."

read -p "Continue with git history cleaning? (y/N): " -n 1 -r
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

echo "Cleaning repository with BFG..."
bfg --replace-text passwords.txt .git

echo "Cleaning up..."
git reflog expire --expire=now --all
git gc --prune=now --aggressive

echo "✅ Git history cleaned!"
echo ""
echo "Next steps:"
echo "1. Review the changes: git log --oneline"
echo "2. Test your application to ensure it still works"
echo "3. Force push to remote: git push --force --all"
echo "4. Force push tags: git push --force --tags"
echo ""
echo "⚠️  IMPORTANT: Inform all team members to re-clone the repository after you push!" 