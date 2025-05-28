package dataaccess;

import model.UserData;

import java.util.HashSet;

public class MemoryUserDAO implements UserDAO{
    private HashSet<UserData> db;

    public MemoryUserDAO(){
        db = HashSet.newHashSet(16);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for(UserData user: db){
            if(user.username().equals(username)){
                return user;
            }
        }
        throw new DataAccessException("User: " + username + " not found");
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try{
            getUser(user.username());
        } catch (DataAccessException error){
            db.add(user);
            return;
        }
        throw new DataAccessException("User: " + user.username() + " already exists");
    }

    @Override
    public boolean authUser(String username, String password) throws DataAccessException {
        boolean userExists = false;
        for(UserData user: db){
            if(user.username().equals(username)){
                userExists = true;
            }
            if(user.username().equals(username) && user.password().equals(password)){
                return true;
            }
        }
        if(userExists){
            return false;
        }
        else{
            throw new DataAccessException("User: " + username + " does not exist");
        }
    }

    @Override
    public void clear() {
        db = HashSet.newHashSet(16);
    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        try {
            getUser(username);
        }
        catch (DataAccessException e) {
            db.add(new UserData(username, password, email));
            return;
        }

        throw new DataAccessException("User already exists: " + username);

    }
}
