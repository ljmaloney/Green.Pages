### OpenCageGeocodeServiceImpl - uses opencage geocoding services to resolve lat / long

URL to Open Cage - https://opencagedata.com/dashboard#geocoding

1. Sign up and get an api key
2. The feign client files are in opencage/feign

### AwsGeocodeServiceImpl - uses Amazon geocode services to determine lat/long

1. Requires creation of place-index on the aws account
   <code>aws location create-place-index --index-name GreenypPlaceIndex --data-source Here --pricing-plan
   RequestBasedUsage</code>
2. Requires appropriate permissions for the ecsTaskRole - IAM policy
   <code>{
   "Version": "2012-10-17",
   "Statement": [
   {
   "Effect": "Allow",
   "Action": [
   "geo:SearchPlaceIndexForText"
   ],
   "Resource": "arn:aws:geo:YOUR_REGION:YOUR_ACCOUNT_ID:place-index/GreenypPlaceIndex"
   }
   ]
   }
   </code>
3. Attach policy to ecs task role
   <code>aws iam attach-role-policy --role-name YourEcsTaskRoleName --policy-arn arn:aws:iam::YOUR_ACCOUNT_ID:
   policy/GeoLocationSearchPolicy</code>
4. 