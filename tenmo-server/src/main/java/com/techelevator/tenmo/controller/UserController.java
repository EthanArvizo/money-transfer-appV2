package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }
    @GetMapping("/{userId}")
    public User getUserByUserId(@PathVariable int userId){
        return userDao.getUserById(userId);
    }
    @GetMapping("/list")
    public List<User> getUsers(Principal principal){
        String currentUser = principal.getName();
        List<User> allUsers = userDao.getUsers();
        allUsers.removeIf(user -> user.getUsername().equals(currentUser));
        return allUsers;
    }

}
