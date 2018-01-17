/*
this class has following information:
A street address (e.g., 4200 Forbes Ave.)
An apartment number (e.g., 3601)
The city the apartment is in (e.g., Pittsburgh)
The apartment's ZIP code (e.g., 15213)
The price to rent (in US dollars per month)
The square footage of the apartment
 */

import java.util.Scanner;

public class Apt {
    private String streetAddress, aptNumber, zip, city;
    private double price, squareFootage;
    private static int aptCount = 0;
    @Override
    public String toString(){
        String s =  "\nStreet address:   " + streetAddress + 
                    "\nApartment number: " + aptNumber+
                    "\nCity:             " + city +
                    "\nZip code:         " + zip +
                    "\nPrice to rent:    " + price+
                    "\nSquare foootage:  " + squareFootage;
        return s ;
    }
    
    /**
     * This method returns unique identifier of an apartment which is 
     * streetAddress + aptNumber + zip 
     * @return 
     */
    public String getUniqueIdentifier(){
       return (streetAddress + aptNumber + zip).toLowerCase().replaceAll("\\s+","");  
    }
    /**
     * This option will prompt the user for a street address, apartment number,
     * and zip code of the apartment. Return ture if they match. 
     * @param streetAddress 
     * @param aptNumber
     * @param zip
     * @return true if they match false otherwise 
     */
    public boolean checkStAddAptNumZip(String streetAddress, String aptNumber, String zip){
        if( !this.streetAddress.toLowerCase().equals(streetAddress)){
            return false;
        }else if (!this.aptNumber.toLowerCase().equals(aptNumber)){
            return false;
        }else if (!this.zip.toLowerCase().equals(zip)){
            return false;
        }else{
            return true;
        }
    } 

    
    // constructors 
    public Apt() {
    }
    public Apt(String streetAddress, String aptNumber, String zip) {
        this.streetAddress = streetAddress;
        this.aptNumber = aptNumber;
        this.zip = zip;
        this.city = "";
        this.price = 0;
        this.squareFootage = 0;
    }
    public Apt(String streetAddress, String aptNumber, String zip, String city, 
            double price, double squareFootage) {
        this.streetAddress = streetAddress;
        this.aptNumber = aptNumber;
        this.zip = zip;
        this.city = city;
        this.price = price;
        this.squareFootage = squareFootage;
        aptCount++;
    }
    
    
       //PROMPS
    /**
     * This method will prompt for all the data needed for this class and 
     * returns and object Apt 
     * @return object of the class Apt
     */
    public static Apt promptForApt(){
        String streetAddress, aptNumber, zip, city;
        double price, squareFootage;
        streetAddress = promptStreetAddress();
        aptNumber = promptAptNumber();
        zip = promptZip();
        city = promptCity();
        price = promptPrice();
        squareFootage = promptSquareFootage();
        return new Apt(streetAddress, aptNumber, zip, city, price, squareFootage);
    }
    /**
     * Prompting the user from some input
     * @return 
     */ 
    public static double promptSquareFootage(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the square footage: ");
        double output = scan.nextDouble();
        return output;
    }
    /**
     * Prompting the user from some input
     * @return 
     */ 
    public static double promptPrice(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the price: ");
        double output = scan.nextDouble();
        return output;
    }
    /**
     * Prompting the user from some input
     * @return 
     */ 
    public static String promptCity(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the city: ");
        String output = scan.nextLine();
        return output;
    }
    /**
     * Prompting the user from some input
     * @return 
     */ 
    public static String promptZip(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the zip code: ");
        String output = scan.nextLine();
        return output;
    }
    /**
     * Prompting the user from some input
     * @return 
     */ 
    public static String promptStreetAddress(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the street address: ");
        String output = scan.nextLine();
        return output;
    }
    /**
     * Prompting the user from some input
     * @return 
     */ 
    public static String promptAptNumber(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the apartment number (leave it empty if there no apt#): ");
        String output = scan.nextLine();
        return output;
    }
    
    // getters & setters
    public static int getAptCount() {
        return aptCount;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getAptNumber() {
        return aptNumber;
    }

    public void setAptNumber(String aptNumber) {
        this.aptNumber = aptNumber;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city.trim().toLowerCase();
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSquareFootage() {
        return squareFootage;
    }

    public void setSquareFootage(double squareFootage) {
        this.squareFootage = squareFootage;
    }
    
    
}
