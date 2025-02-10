# Tender Management System

## Overview

This web-based Tender Management System simplifies the creation, announcement, evaluation, and awarding of tenders. It offers a user-friendly interface for administrators, 
vendors, and bidders, facilitating a transparent and efficient procurement process while ensuring compliance and reducing administrative overhead.

## Features

- **Admin Role**:
    - Create new tenders with details such as title, description, deadline, and budget.
    - Announce tenders to the public or specific vendors/bidders.
    - Evaluate submitted bids based on predefined criteria.
    - Select the winning bid and award the contract.
    - Manage vendors and bidders.

- **Vendor/Bidder Role**:
    - Register on the platform to participate in tenders.
    - Submit bids for tenders.
    - Receive notifications on tender updates and bid outcomes.

- **Key Functionalities**:
    - Tender creation and management.
    - Vendor and bidder registration and profile management.
    - Secure bid submission.
    - Automated bid evaluation process.
    - Notification system for tender updates and outcomes.
    - Contract awarding and management.
    - Feedback and review collection.

## Architecture

The system follows a three-tier architecture:

1.  **Presentation Tier (Client-Side)**:
    -   HTML, CSS, and JavaScript for the user interface.
    -   Bootstrap for responsive design.

2.  **Application Tier (Server-Side)**:
    -   Spring Boot framework for handling business logic and APIs.
    -   RESTful APIs for communication between the client and server.

3.  **Data Tier (Database)**:
    -   MySQL database for storing application data, including tenders, vendors, bidders, and bids.

## Technology Stack

-   **Frontend**:
    -   HTML
    -   CSS
    -   Bootstrap

-   **Backend**:
    -   Spring Boot
    -   Java

-   **Database**:
    -   MySQL

-   **Templating Engine**:
    -   Thymeleaf


## Setup Instructions

1.  **Prerequisites**:
    -   Java Development Kit (JDK)
    -   MySQL Server
    -   Maven (or Gradle)

2.  **Installation**:
    -   Clone the repository:
        ```
        git clone [repository_url]
        ```
    -   Configure the MySQL database:
        -   Create a database named `TenderManagementSystem`.
        -   Update the database connection properties in `application.properties` or `application.yml`.
    -   Build the project using Maven:
        ```
        mvn clean install
        ```
    -   Run the application:
        ```
        mvn spring-boot:run
        ```

3.  **Access**:
    -   Open your web browser and navigate to `http://localhost:8080`.

## Contributing

If you would like to contribute to this project, please follow these steps:

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix.
3.  Make your changes and commit them with descriptive messages.
4.  Submit a pull request.

## License

This project is licensed under the [MIT License](LICENSE).


