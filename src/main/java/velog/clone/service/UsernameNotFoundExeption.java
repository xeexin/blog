package velog.clone.service;

public class UsernameNotFoundExeption extends RuntimeException {
    public UsernameNotFoundExeption(String userNotFound) {
        super(userNotFound);

    }
}
