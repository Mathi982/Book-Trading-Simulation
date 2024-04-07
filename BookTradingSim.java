import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class BookTradingSimulation {
   
   
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java BookTradingSimulation <targetBook> <maxPrice>");
            System.exit(1);
        }
    
        String targetBook = args[0];
        double maxPrice = Double.parseDouble(args[1]);
    
        try {
            // Creates the seller agents
            SellerAgent seller1 = new SellerAgent("Seller1");
            seller1.addBooksDynamically();
    
            SellerAgent seller2 = new SellerAgent("Seller2");
            seller2.addBooksDynamically();
    
            List<SellerAgent> sellers = Arrays.asList(seller1, seller2);
    
            // Creates the buyer agents and the market analysis
            BuyerAgent buyer = new BuyerAgent(targetBook, maxPrice);

            buyer.performMarketAnalysis(sellers);
    
            // Buyer sends requests to seller agents
            buyer.sendRequestToSellers(sellers);
    
            // Buyer evaluates offers and makes a purchase
            Transaction transaction = buyer.evaluateOffersAndPurchase();
    
            // Prints the transaction history
            if (transaction != null) {
                transaction.displayTransaction();
            } else {
                System.out.println("No purchase was made.");
            }
        } catch (Exception e) {
            System.err.println("An error occurred in the simulation: " + e.getMessage());
        }
    }
    

    
    
    
    static class Transaction {
        private String bookTitle;
        private String sellerName;
        private double price;

        public Transaction(String bookTitle, String sellerName, double price) {
            this.bookTitle = bookTitle;
            this.sellerName = sellerName;
            this.price = price;
        }

        public void displayTransaction() {
            System.out.println("Transaction Details:");
            System.out.println("Book: " + bookTitle);
            System.out.println("Seller: " + sellerName);
            System.out.println("Price: £" + price);
        }
    }





static class BuyerAgent {
    private String targetBook;
    private double maxPrice;
    private Map<String, Double> receivedOffers = new ConcurrentHashMap<>();
    private boolean hasPurchased;

    public BuyerAgent(String targetBook, double maxPrice) {
        this.targetBook = targetBook;
        this.maxPrice = maxPrice;
        this.hasPurchased = false;
    }

    public void performMarketAnalysis(List<SellerAgent> sellers) {
        double lowestPrice = Double.MAX_VALUE;
        String lowestPriceSeller = null;

        for (SellerAgent seller : sellers) {
            Map<String, Double> catalogue = seller.getCatalogue();
            if (catalogue.containsKey(targetBook) && catalogue.get(targetBook) < lowestPrice) {
                lowestPrice = catalogue.get(targetBook);
                lowestPriceSeller = seller.getName();
            }
        }

        if (lowestPrice <= maxPrice) {
            System.out.println("Found " + targetBook + " within budget from " + lowestPriceSeller + " for £" + lowestPrice);
            sendPurchaseOrder(lowestPriceSeller, targetBook);
        } else if (lowestPriceSeller != null) {
            promptForPurchaseDecision(lowestPriceSeller, targetBook, lowestPrice);
        } else {
            System.out.println("Target book not found in any seller's catalogue.");
        }
    }

    // If the book happens to be over the price they are willing to pay for, the cheapest target book will be shown for the user to buy if they want to
    private void promptForPurchaseDecision(String seller, String book, double price) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Cheapest available " + book + " is £" + price + " from " + seller);
        System.out.println("Do you want to purchase? (yes/no)");
        String decision = scanner.nextLine().trim().toLowerCase();

        if ("yes".equals(decision)) {
            sendPurchaseOrder(seller, book);
        } else {
            System.out.println("Purchase declined.");
        }
    }

    // Sends Purchase order
    private void sendPurchaseOrder(String sellerName, String bookTitle) {
        System.out.println("Purchasing " + bookTitle + " from " + sellerName);
        hasPurchased = true;
        // Here, you can add logic to notify the seller about the purchase
    }


    // Sends requests to seller agents
    public void sendRequestToSellers(List<SellerAgent> sellers) {
        for (SellerAgent seller : sellers) {
            try {
                seller.receiveRequest(this, targetBook);
            } catch (Exception e) {
                System.err.println("Error sending request to seller: " + e.getMessage());
            }
        }
    }

    // Receives offers from seller agents
    public void receiveOffer(String sellerName, double price) {
        if (sellerName == null || price <= 0) {
            return; // Comes out as an  invalid offer
        }
        if (price <= maxPrice) {
            receivedOffers.put(sellerName, price);
            System.out.println("Buyer received an offer from " + sellerName + " for £" + price);
        }
    }

    // Evaluates the offers and makes a purchase
    public Transaction evaluateOffersAndPurchase() {
        if (hasPurchased) {
            System.out.println("Buyer has already purchased a book.");
            return null;
        }

        Map.Entry<String, Double> bestOffer = null;
        for (Map.Entry<String, Double> offer : receivedOffers.entrySet()) {
            if (bestOffer == null || offer.getValue() < bestOffer.getValue()) {
                bestOffer = offer;
          }
        }

        if (bestOffer != null) {
            System.out.println("Purchasing " + targetBook + " from " + bestOffer.getKey() + " for £" + bestOffer.getValue());
            hasPurchased = true;
            return new Transaction(targetBook, bestOffer.getKey(), bestOffer.getValue());
        } else {
            System.out.println("No suitable offers received for " + targetBook);
            return null;
        }
    }
}




static class SellerAgent {
    private String name;
    private Map<String, Double> booksForSale;
    
    // Gets the catalogue of books
    public Map<String, Double> getCatalogue() {
        return new HashMap<>(booksForSale);
    }

    public String getName() {
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }

    public SellerAgent(String name) {
        this.name = name;
        this.booksForSale = new HashMap<>();
    }

    // Add books to the catalogue dynamically
    public void addBooksDynamically() {
        Scanner scanner = new Scanner(System.in);
        int numOfBooks = 0;
    
        while (true) {
            try {
                System.out.println("[" + name + "] Enter the number of books to add:");
                String input = scanner.nextLine();
                numOfBooks = Integer.parseInt(input);
                break; // Exit the loop if a valid integer is provided
            } catch (NumberFormatException e) {
                System.err.println("[" + name + "] Invalid input. Please provide a valid numeric value for the number of books.");
            }
        }
    
        for (int i = 0; i < numOfBooks; i++) {
            String title;
            while (true) {
                System.out.println("[" + name + "] Enter the title for book " + (i + 1) + ":");
                title = scanner.nextLine().trim();
    
                // Checks for empty prompt  
                if (title.isEmpty()) {
                    System.err.println("[" + name + "] Book title cannot be empty.");
                } else if (booksForSale.containsKey(title)) {
                    System.err.println("[" + name + "] Book title '" + title + "' already exists.");
                } else {
                    break;
                }
            }
    
            double price;
            while (true) {
                System.out.println("[" + name + "] Enter the price for " + title + ":");
                String priceInput = scanner.nextLine();
                try {
                    price = Double.parseDouble(priceInput);
                    if (price <= 0) {
                        System.err.println("[" + name + "] Price must be positive.");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("[" + name + "] Invalid price input. Please enter a numeric value.");
                }
            }
    
            addBookToCatalog(title, price);
        }
    }
    
    
    // Adds book to the catalogue
    public void addBookToCatalog(String title, double price) {
        if (title.isEmpty()) {
            System.err.println("[" + name + "] Book title cannot be empty.");
            return;
        }
        if (price <= 0) {
            System.err.println("[" + name + "] Price must be positive.");
            return;
        }
        booksForSale.put(title, price);
        System.out.println("[" + name + "] Added " + title + " with price £" + price);
    }

    // Receives a request for a book from a buyer agent
    public void receiveRequest(BuyerAgent buyer, String bookTitle) {
        if (bookTitle.isEmpty()) {
            System.err.println("[" + name + "] Book title cannot be empty.");
            return;
        }
        if (booksForSale.containsKey(bookTitle)) {
            Double price = booksForSale.get(bookTitle);
            buyer.receiveOffer(name, price);
        }
    }

    // Processes a purchase order from a buyer agent
    public void processPurchaseOrder(String bookTitle) {
        if (!booksForSale.containsKey(bookTitle)) {
            System.err.println("[" + name + "] Book not available for sale: " + bookTitle);
            return;
         }
            Double price = booksForSale.get(bookTitle);
            booksForSale.remove(bookTitle);
            System.out.println("[" + name + "] Sold " + bookTitle);

    // Records the transaction with the stored prices
        Transaction transaction = new Transaction(bookTitle, name, price);
    }

    // Displays the seller's catalogue
    public void displayCatalogue() {
        System.out.println("[" + name + "] Catalogue:");
        for (Entry<String, Double> entry : booksForSale.entrySet()) {
            System.out.println("- " + entry.getKey() + ": £" + entry.getValue());
        }
    }
    

}

}
