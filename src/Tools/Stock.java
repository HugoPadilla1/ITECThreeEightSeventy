package Tools;

public class Stock {

    private String symbol;
    private String companyName;
    private double currentPrice;
    private long volume;
    private long marketCap;
    private double dividendYield;

    // Default constructor
    public Stock() {

    }

    // Full constructor
    public Stock(String symbol,
                 String companyName,
                 double currentPrice,
                 long volume,
                 long marketCap,
                 double dividendYield) {

        this.symbol = symbol;
        this.companyName = companyName;
        this.currentPrice = currentPrice;
        this.volume = volume;
        this.marketCap = marketCap;
        this.dividendYield = dividendYield;
    }

    // Getters

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public long getVolume() {
        return volume;
    }

    public long getMarketCap() {
        return marketCap;
    }

    public double getDividendYield() {
        return dividendYield;
    }


    // Setters

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public void setMarketCap(long marketCap) {
        this.marketCap = marketCap;
    }

    public void setDividendYield(double dividendYield) {
        this.dividendYield = dividendYield;
    }


    // Prints the stock in the format required by assignment
    public void printStock() {

        System.out.println(symbol);
        System.out.println(companyName);
        System.out.printf("%.2f%n", currentPrice);
        System.out.println(volume);
        System.out.println(marketCap);
        System.out.println(dividendYield);
        System.out.println();
    }
}