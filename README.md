# Textbook Marketplace Application

## Three Data Sources
1. **Student Listings CSV** - Student textbook submissions
2. **Market Price Data CSV** - External pricing reference
3. **Book Metadata CSV** - Validated book information

## Seven Core Operations
1. Total Listings Count
2. Average Price by Course
3. Student vs. Market Price Comparison
4. Cheapest by Condition
5. Market Fairness Score
6. Barter Compatibility Finder
7. Book Demand Index

## Architecture (3-tier)

This project follows a simple 3-tier architecture:

- Presentation (CLI): `src/main/Main.java` — user interaction and program menu
- Business / Service layer: `src/main/MarketplaceService.java` — core logic and calculations
- Data / Persistence layer: `src/main/CSVFileReader.java` — CSV parsing and data loading

This separation keeps the code clean and easier to test and extend.

## Build & Run (local)

You need a Java JDK on your PATH (javac/java). From the project root run:

```powershell
# compile
javac -d out src\main\*.java

# run (interactive menu)
java -cp out main.Main
```

If you prefer to run a sequence of commands (non-interactive) you can pipe choices on PowerShell:

```powershell
"1`n2`n3`n4`n5`n6`n7`n0`n" | java -cp out main.Main
```

If `javac` or `java` are not found, install a JDK and add it to your PATH before proceeding.
# Textbook Marketplace Application

## Three Data Sources
1. **Student Listings CSV** - Student textbook submissions
2. **Market Price Data CSV** - External pricing reference
3. **Book Metadata CSV** - Validated book information

## Five Core Operations
1. Total Listings Count
2. Average Price by Course
3. Student vs. Market Price Comparison
4. Cheapest by Condition
5. Market Fairness Score
6. Barter Compatibility Finder
7. Book Demand Index