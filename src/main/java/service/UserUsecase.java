package service;

import DBController.UserDbSql;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserUsecase {

    @Autowired
    private UserDbSql userDbSql;

    public void createUser(User user) {
        userDbSql.createUser(user);
    }

    public void updateUser(User user) {
        userDbSql.updateUser(user);
    }

    public Optional<User> findUserByID(Long id) {
        return userDbSql.findUserByID(id);
    }


    public void deleteUser(Long id) {
        userDbSql.deleteUser(id);
    }

    public User findLogin(String username, String password) {
        return userDbSql.findLogin(username, password);
    }
}