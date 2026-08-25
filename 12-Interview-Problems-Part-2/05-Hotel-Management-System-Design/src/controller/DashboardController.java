package controller;

import domain.Booking;
import service.UserService;

import java.util.List;

public class DashboardController {
    private final UserService userService;

    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    public List<Booking> listUserBookings(String userId) {
        return userService.listUserBookings(userId);
    }
}
