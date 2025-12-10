package marketplace.ui;
import marketplace.processor.LenientStrategy;
import marketplace.processor.MarketplaceService; // Or MarketplaceProcessor if you renamed it
import marketplace.processor.StandardStrategy;

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
                System.out.println("Invalid input. Please enter a number between 0 and 8.");
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
                case 8:
                    // 1. Ask the Printer to display the options
                    printer.printFairnessOptions();
                    int mode = -1;
                    try {
                        mode = Integer.parseInt(scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        mode = -1; // Invalid handling
                    }
                    if (mode == 2) {
                        processor.setStrategy(new LenientStrategy());
                        printer.printStrategyUpdate("Lenient");
                    } else {
                        // Default to Standard for option 1 or invalid input
                        processor.setStrategy(new StandardStrategy());
                        printer.printStrategyUpdate("Standard");
                    }
                    break;
                default:
                    System.out.println("Unknown option. Please choose 0-8.");
            }
        }
    }
}
