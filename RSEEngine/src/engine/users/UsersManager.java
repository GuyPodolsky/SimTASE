package engine.users;

import java.util.Map;
import java.util.TreeMap;

public class UsersManager {
    private static UsersManager UM = new UsersManager();
    private Map<String, User> users;
    private UsersManager(){
       users = new TreeMap<>();
    }

    public static UsersManager getInstance() {
        return UM;
    }

    public User getUser(String name) {
        return users.get(name);
    }

    /**
     * @param name   the name of the new user
     * @param stocks the stocks the user hold shares
     * @throws IllegalArgumentException if there is already a user with the given name
     */


    public void addUser(String name, Map<String, UserHoldings> stocks) throws IllegalArgumentException{
        if (users.containsKey(name))
            throw new IllegalArgumentException("A user with this name " + name + " is already in the system.");

        users.put(name, new User(name, stocks));
    }

    /**
     * @param name the name of the new user
     * @throws IllegalArgumentException if there is already a user with the given name
     */
    public void addUser(String name, boolean isAdmin) throws IllegalArgumentException {
        if (users.containsKey(name))
            throw new IllegalArgumentException("A user with this name " + name + " is already in the system.");

        users.put(name, new User(name, isAdmin));
    }

    public void addUser(String name, User user) throws IllegalArgumentException {
        if (users.containsKey(name))
            throw new IllegalArgumentException("A user with this name " + name + " is already in the system.");
        users.put(name, user);
    }

    public Map<String, User> getUsers() { //TODO:Use DTO
        return users;
    }

    public void setUsers(Map<String, User> newUsers) {
      users = newUsers;
    }

    public boolean isExists(String name){
        return users.containsKey(name);
    }
}
