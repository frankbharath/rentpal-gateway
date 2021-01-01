## Table of contents
* [Introduction](#introduction)
* [Purpose of Gateway](#purpose-of-gateway)
* [Architecture](#architecture)
* [Tools and Technologies](#tools-and-technologies)

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
- Gateway can perform authentication for a given request, instead of each microservice performing their authentication and authorization. This promotes microservices to be stateless.
- We can have circuit breakers, if a microservice goes down we can forward the request to the landing page that shows service is down.

## Architecture
![gateway](https://user-images.githubusercontent.com/49817583/103141094-9cfebb80-46ef-11eb-889e-d32b4556cf74.png)

## Tools and Technologies
- Spring cloud gateway - A non-blocking i/o gateway that routes the request to the microservices.
- Spring eureka - Helps with dynamic IP resolution for microservices registered with eureka.
- OpenID connect - Using Facebook and Google for the sign-in option.
- Slf4j - For logging information.
- Redis - To store session information that can be scaled.
- Language - Java 11
