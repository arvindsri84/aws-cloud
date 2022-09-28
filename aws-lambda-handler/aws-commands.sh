

aws s3 mb s3://arvindsri82-lambda-functions
aws s3 cp .\handler-function-v3.zip s3://arvindsri82-lambda-functions/handler-function-v3.zip


aws cloudformation create-stack --stack-name hello-lambda-stack --template-body file://lambda_deploy.yml --capabilities CAPABILITY_NAMED_IAM
aws cloudformation delete-stack --stack-name hello-lambda-stack
aws cloudformation delete-stack --stack-name hello-lambda-stack