## Table of contents
* [Introduction](#introduction)
* [Purpose of Gateway](#purpose-of-gateway)
* [Tools and Technologies](#tools-and-technologies)
* [Stateful vs Stateless Authentication](#statefule-vs-stateless-authentication)

## Introduction
The first step to secure a web application is by having authentication and authorization functionality. Authentication checks the identity of the user whereas authorization checks whether user have access to specific resource. Thanks to Spring, all we have to do is configure security such that it provides authentication and authorization and we don't have to write logic for ourselves.

## Purpose of Gateway
When a web application is monolothic, all the services (authentication service, notification service and so on) will be served from a single server. Now, the industry is moving away from monolithic to microservice based architecture. The advantage of microservices are plenty and I will not go into deep but in short microservices decouples tightly integrated services that helps us to scale and deploy independently.
So in my project I have 3 different microservices that were developed using spring boot.

Now the question would be, how do we forward the request to right microservices? The answer would be obviously a gateway. I have developed my project extensively using spring boot, so I am using Spring cloud gateway. A gateway acts as an interface between a user and microservices. The user will send a request to the gateway and gateway will route the request to the corresponding microservices. We will have to configure gateway and provide routing information to it.

### Advantages of Gateway
- We don't have to expose microservices to the internet.
- Gateway can perform authentication for a given request, instead of each microservice performing their own authentication and authorization. I am using gateway to authenticate the request.
- We can have circuit breakers, if a microservice goes down we can forward the request to landing page that shows service is down.

## Tools and Technologies
- Spring cloud gateway - A non blocking i/o gateway that routes request to the microservices.
- Spring eureka - Helps with dynamic ip resolution for microservices registered with eureka.
- OpenID connect - Using facebook and google for sign in option.
- Redis - To store session information that can be scaled.
- Language - Java 11

## Stateful vs Stateless Authentication
There was a dilemma whether to implement a stateful session or stateless session.
- Stateful session are traditional session where server generates session cookies and sends it to the browser. And browser in turn sends the session cookies to server for validating and authenticating the user. The problem with this method is, the sessions should be stored either in relational database or in-memory database and for every request there is a lookup in the db. Also, the sessions should be distributed across the network to avoid single point of failure.
- Stateless session are acheived using JWT tokens, JWT tokens are special tokens that includes user info, associated role and will be signed using server private key. Client sends the token in the header and server verfies the token with public key. There is no session is maintained and can be scaled easily just by sharing the public key to all the horizontal servers. But the problems comes when invalidating the token, the JWT tokens cannot be invalidated once created. To overcome this problem, JWT tokens are made short lived and a black list of JWT token must maintained in the relational db or in-memory cache. Now once again we are moving towards a session of JWT tokens and defeats the selling point of JWT being a sessionless.

In the end, I used stateful sessions and the reasons are
- The selling point of JWT are being stateless and I was not able to achieve complete stateless using JWT. 
- JWTs are exposed to XSS attack in the browser as the tokens will be stored in local storage.
