echo "Checking out file from repo ${CODE_URL}"
git config --global credential.helper '!aws codecommit credential-helper $@'
git config --global credential.UseHttpPath true

# Install git-secrets
cd /tmp/git-secret && PREFIX="/usr/local" make install
cp /tmp/git-secret/git-secrets /usr/bin

# Clone target repo, checkout branch and make it current
cd /tmp/repo
git config --global advice.detachedHead false
git clone ${CODE_URL} /tmp/repo
git switch ${BRANCH}
echo "Checkout of the branch is happening now ${BRANCH}"

# Scan code
git secrets --install
git secrets --register-aws
git secrets --scan -r 2> error.txt
cat error.txt

# Send an SNS message only if errors are found in scan
if ! [ -s "error.txt" ];then
    exit
fi
aws sns publish --topic-arn "${SNS_TOPIC}" --message file://error.txt
echo "Scan complete and results published"