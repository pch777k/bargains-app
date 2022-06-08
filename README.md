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
![login-screenshot](https://user-images.githubusercontent.com/56579554/172560798-b3569062-6bd5-4b8c-a163-95f2c71fd3d0.png)
##### Edit nickname and photo
![profile](https://user-images.githubusercontent.com/56579554/172562108-77a619ed-6d1c-4389-b002-8759db5040c2.gif)
##### Change password
![change-password](https://user-images.githubusercontent.com/56579554/172562506-fc9b8c76-3c01-40d5-8688-deb37f01820e.gif)
##### View of bargains of a specific user 
<span><img src="https://i.postimg.cc/SKbYtR57/bargains-user-screenshot.png" alt="bargains of a specific user" width="800"/></span>
##### View of activities of a specific user 
<span><img src="https://i.postimg.cc/L5VvjLDw/user-overview.png" alt="activities of a specific user" width="800"/></span>
##### View of comments of a specific user
<span><img src="https://i.postimg.cc/W41pHdrx/comments-user-screenshot.png" alt="comments of a specific user" width="800"/></span>
##### View of votes of a specific user
<span><img src="https://i.postimg.cc/9MBQhjmS/votes-user-screenshot.png" alt="votes of a specific user" width="800"/></span>
##### Add a new bargain
[![add-bargain](https://i.postimg.cc/MG0HdJd5/add-bargain.gif)](https://postimg.cc/XZJnYmMB)
##### Validation fields of bargain
<span><img src="https://i.postimg.cc/5ycN1m4T/validation-bargain-screenshot-highlight.png" alt="edit the new bargain" width="597"/></span>
##### Edit the bargain
![edit-bargain](https://user-images.githubusercontent.com/56579554/152182711-16532e6f-c5b8-43d9-882d-92b4cc829fd8.gif)
##### Main page
<span><img src="https://i.postimg.cc/tgwkdNJ8/bargains-best-screenshot-description.png" alt="main page" width="800"/></span>
##### View of bargains in the Electronics category
###### Additionally found by the keyword "acer", sorted by the latest offers
<span><img src="https://i.postimg.cc/4x9xWPCD/bargains-category-search-acer.png" alt="sorted by the newest offers" width="800"/></span>
##### View of bargains, sorted by the most commented
<span><img src="https://i.postimg.cc/brwvLj8g/bargains-commented-screenshot.png" alt="sorted by the most commented" width="800"/></span>
##### View of bargains of a specific shop
<span><img src="https://i.postimg.cc/QMZxw7hH/bargains-shop.png" alt="bargains of a specific shop" width="800"/></span>
##### View of a specific bargain 
<span><img src="https://i.postimg.cc/6pXkwvRx/bargain-screenshot.png" alt="view of a specific bargain" width="800"/></span>
##### Voting
###### Additionally, it shows how hiding ended offers works
![vote](https://user-images.githubusercontent.com/56579554/152210927-c4d2952d-c066-41a2-a628-0f15e3ce8930.gif)
[![screenshot-vote-4-options.png](https://i.postimg.cc/C5rPnpyp/screenshot-vote-4-options.png)](https://postimg.cc/wRJcnPTw)
