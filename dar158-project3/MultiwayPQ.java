/**
 * This class has multiple priority queues that hold different prioritized
 * references of the same object. When any operation is preformed on every
 * priority queues.
 *
 */

import java.util.HashMap;

/**
 *
 * @author Daler
 */
public class MultiwayPQ {

    private class MinPrice extends MaxMinPQIndexTableApt {

        @Override
        public boolean isHigherPriority(Apt a1, Apt a2) {
            return a1.getPrice() < a2.getPrice();
        }

        public MinPrice(int initCapacity) {
            super(initCapacity);
        }
    }

    private class MaxFootage extends MaxMinPQIndexTableApt {

        @Override
        public boolean isHigherPriority(Apt a1, Apt a2) {
            return a1.getSquareFootage() > a2.getSquareFootage();
        }

        public MaxFootage(int initCapacity) {
            super(initCapacity);
        }
    }

    private MaxMinPQIndexTableApt maxFootagePQ = new MaxFootage(10);
    private MaxMinPQIndexTableApt minPricePQ = new MinPrice(10);

    private HashMap<String, MinPrice> minPriceByCity = new HashMap<>();
    private HashMap<String, MaxFootage> maxFootageByCity = new HashMap<>();

    /**
     * Get the lowest priced apartment and returns the instance of Apt class.
     * If city is not in the collection returns null.
     * 
     * @return Apt instance with a lowest price in the collection
     */
    public Apt getLowestPriceApt() {
        return minPricePQ.pick();
    }

    /**
     * Get the lowest priced apartment for given city and returns the instance of Apt class.
     * If city is not in the collection returns null.
     *
     * @param city
     * @return Apt instance with a lowest price in the collection
     */
    public Apt getLowestPriceAptByCity(String city) {
        MinPrice pq = minPriceByCity.get(city.trim().toLowerCase());
        if (pq == null) {
            return null;
        }
        Apt apt = pq.pick();
        return apt;
    }

    /**
     * Get the highest footage apartment and return the instance of Apt class.
     * If city is not in the collection returns null.
     * 
     * @return Apt instance with a lowest price in the collection
     */
    public Apt getHighestFootage() {
        return maxFootagePQ.pick();
    }
    /**
     * Get the highest footage apartment for given city (String) and return the
     * instance of Apt class. If city is not in the collection return null
     *
     * @param city 
     * @return Apt instance with a lowest price in the collection
     */
    public Apt getHighestFootageByCity(String city) {
        MaxFootage pq = maxFootageByCity.get(city.trim().toLowerCase());
        if (pq == null) {
            return null;
        }
        Apt apt = pq.pick();
        return apt;
    }

    /**
     * Check if given apartment's full address is in a queue.
     * @param apt instance of Apt class
     * @return true if it is false if not 
     */
    public boolean isInPQ(Apt apt) {
        return minPricePQ.isInPQ(apt);
    }

    public MaxMinPQIndexTableApt getMaxFootagePQ() {
        return maxFootagePQ;
    }

    public HashMap<String, MinPrice> getMinPriceByCity() {
        return minPriceByCity;
    }

    public HashMap<String, MaxFootage> getMaxFootageByCity() {
        return maxFootageByCity;
    }
    
    public MaxMinPQIndexTableApt getMinPricePQ(){
        return minPricePQ;
    }
    /**
     * Update all prices in all the queues if it exists in the queue. If it does 
     * NOT exists don't do anything.
     * 
     * @param apt 
     */
    public void updatePrice(Apt apt) {
        if (!minPricePQ.isInPQ(apt)) {
            return;
        }
        apt.setCity(minPricePQ.getAptByUnigueIdentifier(apt).getCity());// setting the city so it is posible to look for it in HashMap minPriceByCity
        //Since footage is not being updated only price related queues needs to be updated 
        // to keep the heap property. 
        minPricePQ.updatePriceIfExist(apt);
        minPriceByCity.get(apt.getCity()).updatePriceIfExist(apt);;
    }

    public void delete(Apt apt) {
        if (!minPricePQ.isInPQ(apt)) {
            return;
        }
        apt.setCity(minPricePQ.getAptByUnigueIdentifier(apt).getCity());// setting the city so it is posible to look for it in HashMap minPriceByCity
        minPricePQ.deleteIfExist(apt);
        maxFootagePQ.deleteIfExist(apt);
        minPriceByCity.get(apt.getCity()).deleteIfExist(apt);
        maxFootageByCity.get(apt.getCity()).deleteIfExist(apt);
    }

    /**
     * Add an apartment to all the queues
     *
     * @param apt
     */
    public void add(Apt apt) {
        maxFootagePQ.insert(apt);
        minPricePQ.insert(apt);
        MinPrice cityMinPrice = minPriceByCity.get(apt.getCity());
        if (cityMinPrice == null) {//there no such city in the set yet so add it 
            cityMinPrice = new MinPrice(10);
            cityMinPrice.insert(apt);
            minPriceByCity.put(apt.getCity(), cityMinPrice);
        } else {
            cityMinPrice.insert(apt);
        }

        MaxFootage cityMaxFootage = maxFootageByCity.get(apt.getCity().trim().toLowerCase());
        if (cityMaxFootage == null) {
            cityMaxFootage = new MaxFootage(10);
            cityMaxFootage.insert(apt);
            maxFootageByCity.put(apt.getCity(), cityMaxFootage);
        } else {
            cityMaxFootage.insert(apt);
        }
    }

}
