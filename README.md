# Book Trading Simulation

## Description
A Java application designed to simulate a marketplace where buyer and seller agents interact for trading books. The simulation demonstrates the interaction between multiple seller agents who list books for sale and a buyer agent who searches for a specific book within a specified budget.

## Features
- **Dynamic Seller Agents:** Seller agents can dynamically add books with their prices to their catalog.
- **Market Analysis:** The buyer agent performs market analysis to find the lowest price for a target book within the budget.
- **Interactive Purchase Decisions:** The buyer is prompted to make a decision if the cheapest book is over the budget.
- **Transaction Handling:** Handles the purchase transaction and displays transaction details.

## Requirements
- Java Runtime Environment (JRE) or Java Development Kit (JDK).

## Installation
1. Clone or download the repository.
2. Navigate to the directory containing the source code.
3. Compile the Java program using your preferred Java compiler.

## Usage
To run the simulation, execute the compiled Java program with two arguments:
- The title of the book the buyer is looking for.
- The maximum price the buyer is willing to pay.

```bash
java BookTradingSimulation <targetBook> <maxPrice>
```
Change targetbook and maxprice with your book and price

## Components
## BookTradingSimulation
The `BookTradingSimulation` class serves as the main driver for the simulation. It is responsible for:
- Initialising seller and buyer agents.
- Performing market analysis.
- Overseeing the transaction process.

## SellerAgent
The `SellerAgent` class represents a seller in the market. Each seller agent:
- Maintains a catalog of books with prices.
- Handles requests and purchase orders from buyer agents.

## BuyerAgent
The `BuyerAgent` class acts as the buyer in the market. It performs the following tasks:
- Sets a target book and a maximum price.
- Conducts market analysis.
- Sends requests to sellers.
- Evaluates offers.
- Completes the purchase process.

## Transaction
The `Transaction` class is responsible for storing the details of a completed transaction. It includes:
- Book title.
- Seller's name.
- Price of the book.

Additionally, the `Transaction` class provides functionality to display transaction details.
