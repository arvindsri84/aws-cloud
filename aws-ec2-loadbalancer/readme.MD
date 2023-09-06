aws cloudformation create-stack --stack-name hello-world-stack --template-body file://ec2-autohealing-loadbalancer.yml --capabilities CAPABILITY_NAMED_IAM
aws cloudformation describe-stacks --stack-name hello-world-stack
aws cloudformation delete-stack --stack-name hello-world-stack
aws s3 cp  ../demo-java-app/hello-world-springboot/target/hello-world-springboot-0.0.1-SNAPSHOT*.zip s3://proof-of-concepts-zyvika/

