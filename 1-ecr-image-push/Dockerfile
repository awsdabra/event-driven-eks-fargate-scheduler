FROM amazonlinux:2

RUN yum update -y

RUN yum install git -y
RUN yum install make -y
RUN yum install aws-cli -y
RUN mkdir -p /tmp/repo
RUN git clone https://github.com/awslabs/git-secrets.git /tmp/git-secret
ADD encode.sh /tmp/
RUN chmod -R 777 /tmp
ENTRYPOINT [ "sh","/tmp/encode.sh" ]