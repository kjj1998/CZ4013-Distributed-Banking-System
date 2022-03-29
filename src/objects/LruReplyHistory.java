package objects;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static utils.Constants.LRU_CACHE_SIZE;


public class LruReplyHistory<K, V>{
    private Map<K, V> responseHistory;

    /**
     * Constructor to create a linked hashmap with lru semantics.
     *
     * @param cacheSize the size of the LRU cache or the maximum number of key value pairs that can be stored
     */
    public LruReplyHistory(int cacheSize) {
        responseHistory = Collections.synchronizedMap(new LinkedHashMap<K, V>(cacheSize, 0.75f, true){ //access order pushes the most recent access to the front of the LRU cache.

            /**
             * Removes the Least Recently Used entry in the response history when the LRU cache reaches its maximum capacity
             * @param eldest the eldest entry in the LinkedHashMap
             * @return boolean true if cache size has exceeded maximum capacity, false if cache size is currently lower than maximum capacity
             */
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LRU_CACHE_SIZE;
            }
        });
    }

    public Map<K, V> getResponseHistory() {
        return responseHistory;
    }

    /**
     * Retrieves a server reply using the request message id as the key. Pushes the most recently retrieved reply to the front of the linked map
     *
     * @param key
     * @return Optional of type datagrampacket or byte[]
     */
    public Optional<V> getReply(K key) {
        return Optional.ofNullable(responseHistory.get(key));
    }

    /**
     * Inserts a reply message into the lru cache
     *
     * @param key the request identifier from the client as a string
     * @param value the reply message as a datagram packet or a byte[]
     */
    public void putReply(K key, V value){
        responseHistory.put(key, value);
    }
}
