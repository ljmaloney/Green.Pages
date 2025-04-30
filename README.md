# Code supporting subscriber portal for GreenYP.com
## Subprojects
### green.yp.api
  DTOs and other objects shared between backend and [Vaadin flow](https://vaadin.com/flow) frontend. 

### green.yp.backend
  A spring boot service containing the backend busines logic supporting subscribers

### usps.address.api
  A client utilizing USPS address validation service as a source of truth
  
## Project setup for a development system
Caveat: This is for a development workstation ONLY. The secrets are handled in a secure manner when deployed
1. If not installed, install Docker Desktop
2. Run docker compose using fusionauth-docker-compose.yml to set up FusionAuth 
   1. https://fusionauth.io/docs/get-started/download-and-install/docker
   2. https://fusionauth.io/docs/get-started/start-here
3. Run docker compose using mysql-docker-compose.yml to create MySQL Docker VM
4. Open a browser and login to the local instance of FusionAuth to create the following parameters 
   1. fusionauth.green.yp.client.id
   2. fusionauth.green.yp.client.secret 
   3. fusionauth.green.yp.app.id
   4. fusionauth.green.yp.tenant
5. Initialize the MySQL tables using greenyp_ddl.sql
6. Start the backend using gradle bootRun

## Open API / Swagger
Open API / Swagger is supported using http://localhost:8081/swagger-ui/index.html
## Initial Data Setup
A couple of tables need to be populated before attempting to use <em>Account - Create New Account</em> from the Postman collection, listed below:

Import the collection Green YP.postman_collection.json into Postman
### Create a Subscription 
Use <em>Subscription - Create new Subscription</em> to create an initial subscription
### Create a Line of Business
Use <em>Reference - Create Line of Business</em> to create the initial line of business
