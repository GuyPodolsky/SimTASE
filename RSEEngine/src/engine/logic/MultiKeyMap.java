package engine.logic;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * A data structure of a map that supports multi keys.
 * @param <K> the class of the key (both keys should be from the same class).
 * @param <T> the class of the values.
 */
public class MultiKeyMap<K,T> {

    /**
     * The structure contains two tree maps.
     */
    private Map<K,T> mapByKeyOne = new TreeMap<>();
    private Map<K,T> mapByKeyTwo = new TreeMap<>();

    /**
     * A method that insets a date.
     * @param keyOne the first key
     * @param keyTwo the second key
     * @param value the value.
     */
    public void put(K keyOne,K keyTwo,T value){
        mapByKeyOne.put(keyOne,value);
        mapByKeyTwo.put(keyTwo,value);
    }

    /**
     * A methods that gets a data by a key.
     * @param key the key of the desired data.
     * @return the data that is referring to this key.
     */
    public T get(K key) {
       if( mapByKeyOne.containsKey(key))
           return mapByKeyOne.get(key);
       else if(mapByKeyTwo.containsKey(key))
           return mapByKeyTwo.get(key);
       else
          return null;
    }

    /**
     * A methods that checks if there is a key specific in the map.
     * @param key the key that we would like to check.
     * @return true if the key was found in the map, and false otherwise.
     */
    public boolean containsKey(K key){
        if (mapByKeyOne.containsKey(key))
            return true;
        else if (mapByKeyTwo.containsKey(key))
            return true;
        else
            return false;
    }

    /**
     * A method that gives access to the stored values
     * @return a collection with all the values that are stored in the map.
     */
    public Collection<T> values(){
        return mapByKeyOne.values();
    }

    /**
     * A getter of the size of the map
     * @return the size of the map.
     */
    public int size(){
        return mapByKeyOne.size();
    }

    /**
     * A method that clears the map.
     */
    public void clear(){
        mapByKeyOne.clear();
        mapByKeyTwo.clear();
    }
}
