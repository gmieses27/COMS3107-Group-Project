package marketplace;


import marketplace.common.*;
import marketplace.data.*;
import marketplace.processor.*;
import marketplace.ui.*;

public class Main {
    public static void main(String[] args) {

        //loads data for user to use
        CSVRepository.configure("data/student_listings.csv",
                "data/book_metadata.csv", "data/market_prices.csv");
        CSVRepository repo = CSVRepository.getInstance();

        //marketplace that will be used
        MarketplaceService service = new MarketplaceService(repo);

        MarketplacePrinter printer = new MarketplacePrinter();
        ConsoleMenu menu = new ConsoleMenu(service, printer);

        menu.start();
    }
    // Note: ISBN normalization and business logic are handled in MarketplaceService.
}

