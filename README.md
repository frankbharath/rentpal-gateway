## Table of contents
* [Introduction](#introduction)
* [Purpose of Gateway](#purpose-of-gateway)
* [Tools and Technologies](#tools-and-technologies)
* [Stateful vs Stateless Authentication](#stateful-vs-stateless-authentication)

## Introduction
The first step to secure a web application is by having authentication and authorization functionality. Authentication checks the user identity, whereas authorization checks whether the user has access to a specific resource. Thanks to Spring, all we have to do is configure security such that it provides authentication and authorization, and we don't have to write the logic for ourselves.

## Purpose of Gateway
When a web application is monolithic, all the services that include authentication service, user service will be served from a single server. Now, the industry is moving away from monolithic to microservice-based architecture. The advantage of microservices are plenty and I will not go into deep. But in short, microservices decouples tightly integrated services that help us to scale and deploy independently. So in my project, I have 3 different spring boot-microservices, 
1) User service
2) Agreement service
3) Notification service

Now the question would be, how do we forward the request to the right microservices? The answer would be a gateway. I have developed my project extensively using spring boot, so I am using Spring cloud gateway. A gateway acts as an interface between a user and microservices. The user will send a request to the gateway and the gateway will route the request to the corresponding microservices. We will have to configure the gateway and provide routing information to it.

### Advantages of Gateway
- We don't have to expose microservices to the internet.
- Gateway can perform authentication for a given request, instead of each microservice performing their authentication and authorization. I am using the gateway to authenticate the request.
- We can have circuit breakers, if a microservice goes down we can forward the request to the landing page that shows service is down.

## Tools and Technologies
- Spring cloud gateway - A non-blocking i/o gateway that routes the request to the microservices.
- Spring eureka - Helps with dynamic IP resolution for microservices registered with eureka.
- OpenID connect - Using Facebook and Google for the sign-in option.
- Slf4j - For logging information.
- Redis - To store session information that can be scaled.
- Language - Java 11

## Stateful vs Stateless Authentication
There was a dilemma of whether to implement a stateful session or stateless session.
- Stateful sessions are traditional sessions where the server generates session cookies and sends them to the browser. And browser in turn sends the session cookies to the server for validating and authenticating the user. The problem with this method is, the sessions should be stored either in a relational database or in-memory database and for every request, there is a lookup in the DB. Also, the sessions should be distributed across the network to avoid a single point of failure.
- Stateless session is achieved using JWT tokens, JWT tokens are special tokens that include user info, associated role and will be signed using the server private key. The client sends the token in the header and the server verifies the token with the public key. There is no session is maintained and can be scaled easily just by sharing the public key to all the horizontal servers. But the problem comes when invalidating the token, the JWT tokens cannot be invalidated once created. To overcome this problem, JWT tokens are made short-lived and a blacklist of JWT tokens must be maintained in the relational DB or in-memory cache. Now once again we are moving towards a session of JWT tokens and defeats the selling point of JWT being sessionless.

In the end, I used stateful sessions and the reasons are
- The selling point of JWT is being stateless and I was not able to achieve complete stateless using JWT. 
- JWTs are exposed to XSS attack in the browser as the tokens will be stored in local storage.
- If I have multiple angular client, then the user will have to login to each client.
