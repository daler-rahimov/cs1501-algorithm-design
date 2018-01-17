

import java.util.Scanner;

/**
 * 10/27/2016
 *
 * @author Daler Rahimov
 */
public class AptTracker {

    private static MultiwayPQ pq = new MultiwayPQ();
    private static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        String input = "";
        do {
            System.out.println("\nEnter: ");
            System.out.println("1 - To add an apartment");
            System.out.println("2 - To update an apartment");
            System.out.println("3 - To remove a specific apartment from consideration");
            System.out.println("4 - To retrieve the lowest price apartment");
            System.out.println("5 - To retrieve the highest square footage apartment");
            System.out.println("6 - To retrieve the lowest price apartment by city");
            System.out.println("7 - To retrieve the highest square footage apartment by city");
            System.out.println("e - To exix");
            input = scan.nextLine();
            switch (input) {
                case "1":
                    add();
                    break;
                case "2":
                    update();
                    break;
                case "3":
                    remove();
                    break;
                case "4":
                    retrieveLowPrice();
                    break;
                case "5":
                    retrieveHighFootage();
                    break;
                case "6":
                    retrieveLowPriceByCity();
                    break;
                case "7":
                    retrieveHighFootageByCity();
                    break;
            }
        } while (!input.equals("e"));
    }

    private static void add() {
        Apt apt = Apt.promptForApt();
        pq.add(apt);
    }

    private static void update() {
        String streetAddress, aptNumber, zip;
        streetAddress = Apt.promptStreetAddress();
        aptNumber = Apt.promptAptNumber();
        zip = Apt.promptZip();
        Apt newApt = new Apt(streetAddress, aptNumber, zip);
        if (pq.isInPQ(newApt)) {
            System.out.println("The apartment is found do you want to update the price? [yes/no]");
            String input = scan.nextLine();
            if (input.toLowerCase().equals("yes")) {
                double price = Apt.promptPrice();
                newApt.setPrice(price);
                pq.updatePrice(newApt);
                System.out.println("The price was updated!");
            } else {
                System.out.println("The price was NOT updated!");
            }
        } else {
            System.out.println("Apt is not found in the colletion!");
        }
    }

    private static void remove() {
        String streetAddress, aptNumber, zip;
        streetAddress = Apt.promptStreetAddress();
        aptNumber = Apt.promptAptNumber();
        zip = Apt.promptZip();
        Apt newApt = new Apt(streetAddress, aptNumber, zip);
        if (pq.isInPQ(newApt)) {
            pq.delete(newApt);
            System.out.println("Apt was removed from the collection!");
        } else {
            System.out.println("Apt was NOT found!");
        }
    }

    private static void retrieveLowPrice() {
        if (!pq.getMinPricePQ().isEmpty()) {
            System.out.println("The lowest price apartment is: ");
            System.out.println(pq.getLowestPriceApt());
        } else {
            System.out.println("No data!");
        }

    }

    private static void retrieveHighFootage() {
        if (!pq.getMinPricePQ().isEmpty()) {
            System.out.println("The highest square footage apartment is");
            System.out.println(pq.getHighestFootage());
        } else {
            System.out.println("No data!");
        }
    }

    private static void retrieveLowPriceByCity() {
        String city = Apt.promptCity();
        Apt aptCity = pq.getLowestPriceAptByCity(city);
        if (aptCity != null) {
            System.out.println("The lowest price apartment for " + city + " is:");
            System.out.println(aptCity);
        } else {
            System.out.println("The is NO such city in the collection!");
        }
    }

    private static void retrieveHighFootageByCity() {
        String city = Apt.promptCity();
        Apt aptCity = pq.getHighestFootageByCity(city);
        if (aptCity != null) {
            System.out.println("The highest square footage apartment for " + city + " is:");
            System.out.println(aptCity);
        } else {
            System.out.println("The is NO such city in the collection!");
        }
    }

}
