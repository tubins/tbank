#bin/bash
echo "pushing all images....."
docker push docker.io/tubins/cards
docker push docker.io/tubins/loans
docker push docker.io/tubins/accounts
docker push docker.io/tubins/configserver
docker push docker.io/tubins/gatewayserver
docker push docker.io/tubins/eurekaserver
echo "all images pushed successfully"
