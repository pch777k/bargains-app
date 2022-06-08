# Bargains-app
Bargains demo app created with Spring.

## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)
* [Deploy on heroku](#deploy-on-heroku)
* [Swagger Open Api](#swagger-open-api)
* [Database diagram](#database-diagram)
* [Features](#features)
* [Screenshots](#screenshots)

## General info
Using the Bargains app, you can add information about promotions, sales and discounts. Each offer can be rated by other users. Offers can be sorted by the number of votes, the number of comments or the date they were added. Additionally, you can view by category or specific store. The screenshots showing the application's works are presented below. The application can be tested, it is implemented on heroku, and you can test Swagger Open Api. Sample data can be loaded for ease of testing.
	
## Technologies
- Java 8
- Spring Boot
- Thymeleaf
- Lombok
- Swagger
- Maven
- HTML 
- CSS 
- Bootstrap 4
- JavaScript
- PostgreSQL
  
## Setup
Clone this repostory to your desktop. Run applications using Spring Boot. You will then be able to access it at localhost:8080

## Deploy on heroku
https://bargainsdemo.herokuapp.com/

## Swagger Open Api
https://bargainsdemo.herokuapp.com/swagger-ui/index.html

## Database diagram
![diagram](https://user-images.githubusercontent.com/56579554/172560265-5c4019f7-32c0-4e76-bd23-7213d3eda65c.png)

## Features
### User
- [User registration with field validation](#user-registration-with-field-validation)
- [Editing nickname and photo](#edit-nickname-and-photo)
- [Changing password](#change-password)
- [User's bargains](#view-of-bargains-of-a-specific-user)
- [User's activities](#view-of-activities-of-a-specific-user)
- [User's comments](#view-of-comments-of-a-specific-user)
- [User's votes](#view-of-votes-of-a-specific-user)
- Two types of account: USER and ADMIN
- Deleting the user, only ADMIN can do this 
### Bargain
- [Adding a new bargain](#add-a-new-bargain)
- [Validation fields of bargain](#validation-fields-of-bargain)
- [Editing the bargain](#edit-the-bargain)
- Opening the Bargain
- Closing the bargain manually or automatically when the date is exceeded 
- Deleting the bargain
- [All bargains](#main-page)
- [All bargains of specific category](#view-of-bargains-in-the-electronics-category)
- [All bargains of specific shop](#view-of-bargains-of-a-specific-shop)
- [Search engine in the bargains](#view-of-bargains-in-the-electronics-category)
- [Sorting of bargains](#view-of-bargains-in-the-electronics-category)
- [View of a specific bargain](#view-of-a-specific-bargain) 
- [Voting](#voting)
### Comment
- Adding, editing, deleting a comment

## Screenshots
##### User registration with field validation
![signup](https://user-images.githubusercontent.com/56579554/172561803-2ba92548-0d37-4c50-a57a-d0fdb8a8858c.gif)
##### Login Page
![login-screenshot](https://user-images.githubusercontent.com/56579554/172572644-5863441f-ea9e-427d-8700-0071afbd06a1.png)
##### Edit nickname and photo
![profile](https://user-images.githubusercontent.com/56579554/172562108-77a619ed-6d1c-4389-b002-8759db5040c2.gif)
##### Change password
![change-password](https://user-images.githubusercontent.com/56579554/172562506-fc9b8c76-3c01-40d5-8688-deb37f01820e.gif)
##### View of bargains of a specific user 
![bargains-user-screenshot](https://user-images.githubusercontent.com/56579554/172567677-db89734f-9220-4738-b63e-6cab251ac47e.png)
##### View of activities of a specific user 
![user-overview](https://user-images.githubusercontent.com/56579554/172567293-8b35bdf4-11db-423f-890e-f1c2d1ec6f63.png)
##### View of comments of a specific user
![comments-user-screenshot](https://user-images.githubusercontent.com/56579554/172566884-76ba4db0-75df-4d5a-9154-71d38a45710f.png)
##### View of votes of a specific user
![votes-user-screenshot](https://user-images.githubusercontent.com/56579554/172565973-acc3e6ae-36cc-48cd-8926-3c835ea34c1f.png)
##### Add a new bargain
![add-bargain](https://user-images.githubusercontent.com/56579554/172569240-045fba4d-cc94-4d87-9aaa-5c7f93ed08cf.gif)
##### Validation fields of bargain
![validation-bargain-screenshot-highlight](https://user-images.githubusercontent.com/56579554/172569576-36cbc076-8d12-4aa0-9f8f-53b50a85e73b.png)
##### Edit the bargain
![edit-bargain](https://user-images.githubusercontent.com/56579554/152182711-16532e6f-c5b8-43d9-882d-92b4cc829fd8.gif)
##### Main page
![bargains-best-screenshot](https://user-images.githubusercontent.com/56579554/172570581-59e19457-c795-488d-87e8-69bf86d617f4.png)
##### View of bargains in the Electronics category
###### Additionally found by the keyword "acer", sorted by the latest offers
![bargains-category-search-acer](https://user-images.githubusercontent.com/56579554/172570903-559059fa-e729-4b5d-a69a-90379623b768.png)
##### View of bargains, sorted by the most commented
![bargains-commented-screenshot](https://user-images.githubusercontent.com/56579554/172571163-c2c2a7aa-2cda-4563-9499-4c6a11caab80.png)
##### View of bargains of a specific shop
![bargains-shop](https://user-images.githubusercontent.com/56579554/172571395-7663bb46-4120-49f9-9ae4-b727b33fd763.png)
##### View of a specific bargain 
![bargain-screenshot](https://user-images.githubusercontent.com/56579554/172571599-1407e813-c262-43ab-a320-407c047c525d.png)
##### Voting
###### Additionally, it shows how hiding ended offers works
![vote](https://user-images.githubusercontent.com/56579554/152210927-c4d2952d-c066-41a2-a628-0f15e3ce8930.gif)
![screenshot vote 4 options](https://user-images.githubusercontent.com/56579554/172571778-f00e0dd4-816d-416f-9f58-d5998017cd8a.png)
