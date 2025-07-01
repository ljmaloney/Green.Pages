1. Install Docker Desktop for local setup
2. Use the docker-compose.yml file from Fusion Auth using the instructions found here https://lnkd.in/ecDsBY7Y
3. Once you have your Fusion Auth instance configured, create an application and roles within that application. Note the client id and client secret, they will be required when configuring SpringBoot / Spring Security
4. In FusionAuth on the Application Edit page, go to the OAuth tab and
   a. Set PKCE to required
   b. Enter the redirect URLs, these are the allowed URLs for redirection once FusionAuth has authenticated a user.
   c. For swagger on localhost, this URL is required -> http://localhost:8080/swagger-ui/oauth2-redirect.html
5. Go to Settings -> System and select the CORS tab
6. Set CORS to enabled, allow credentials to enabled
7. Allowed headers should be -> Authorization, Content-Type, X-Requested-With
8. Select the allowed HTTP methods
9. Enter the allowed origin URLs, it must have at least http://localhost:8081/swagger-ui/index.html#/
10. Enter the Exposed headers, it must contain Authorization

In Spring Boot
1. Add Spring security dependencies alongside OpenApi plugin and dependencies
2. Create SecurityFilterChain @Bean, to configure which URLs will require security
3. Create a Converter<Jwt, AbstractAuthenticationToken> @Bean including a supporting JwtAuthenticationToken instance
4. Create a JwtDecoder @Bean using http://localhost:9011/.well-known/jwks.json (or whatever host you have for Fusion Auth)
5. Configure Swagger/Spring doc in application config files with the client id and secret

Once configuration is complete, start spring boot, and the swagger page should now have an "Authorize" button present, which will enable you to authenticate using FusionAuth and call the secured spring boot endpoints.