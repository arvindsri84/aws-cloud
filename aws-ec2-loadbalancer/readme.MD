aws cloudformation create-stack --stack-name hello-world-stack --template-body file://ec2-autohealing-loadbalancer.yml --capabilities CAPABILITY_NAMED_IAM
aws cloudformation describe-stacks --stack-name hello-world-stack
aws cloudformation delete-stack --stack-name hello-world-stack