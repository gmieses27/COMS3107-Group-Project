package marketplace.ui;
import marketplace.processor.MarketplaceService; // Or MarketplaceProcessor if you renamed it
import java.util.Scanner;

public class ConsoleMenu {
    private final MarketplaceService processor;
    private final MarketplacePrinter printer;
    private final Scanner scanner;

    public ConsoleMenu(MarketplaceService processor, MarketplacePrinter printer) {
        this.processor = processor;
        this.printer = printer;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printer.printWelcome();
        boolean running = true;
        while (running) {
            printer.printMenu();
            System.out.print("Select an option (0 to exit): ");
            String line = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(line);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Please enter a number between 0 and 7.");
                continue;
            }

            switch (choice) {
                case 0:
                    running = false;
                    System.out.println("Exiting. Goodbye.");
                    break;
                case 1:
                    printer.printTotalCount(processor.getTotalListingsCount());
                    break;
                case 2:
                    printer.printCourseSummaries(processor.averagePriceByCourse());
                    break;
                case 3:
                    printer.printMarketComparison(processor.studentVsMarketComparison());
                    break;
                case 4:
                    printer.printCheapestByCondition(processor.cheapestListingByCondition());
                    break;
                case 5:
                    printer.printFairnessScore(processor.marketFairnessScore());
                    break;
                case 6:
                    printer.printBarterMatches(processor.barterCompatibilityFinder());
                    break;
                case 7:
                    printer.printDemandIndex(processor.demandIndex(10));
                    break;
                default:
                    System.out.println("Unknown option. Please choose 0-7.");
            }
        }
    }
}
