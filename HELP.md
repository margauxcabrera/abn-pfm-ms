# ABN AMRO Clearing Sydney - Processed Future Movement

Go to root ("/") of the project to access Swagger documentation 

### Configurations

Several items can be configured in the project. There are some that can be configured per environment and some that should remain consistent in every environment but can change over time:

* inputMapping.json - this file should be modified if there are changes to the length of the fields in the input file
* ABN_API_KEY - as a temporary security solution, this needs to be configured for each environment to allow for different keys to be used 
* CLIENT_INFORMATION_ATTRIBUTES - should be configured to know the client information key that will be used in the report
* PRODUCT_INFORMATION_ATTRIBUTES - should be configured to know the product information key that will be used in the report
* TRANSACTION_AMOUNT_ATTRIBUTE - should be configured to know the transaction amount key that will be used in the report

### Decisions 

* Decided to sacrifice memory during computation as writing and reading from database will a lot of IO operations which can not only impact the performance of the API but may also database IO operation costs
* Need to add ingress details for IP and hostname and also create secrets for keys for TLS