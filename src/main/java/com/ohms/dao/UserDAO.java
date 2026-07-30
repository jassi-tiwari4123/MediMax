package com.ohms.dao;

import com.ohms.exception.DatabaseException;
import com.ohms.model.User;
import java.util.List;
import java.util.Optional;

/**
 * UserDAO — Data Access Object interface for the users table.
 *
 * INTERVIEW POINTS:
 *   Interface      — defines the contract; implementation can be swapped
 *                    (JDBC today, JPA tomorrow) without touching service layer.
 *   Abstraction    — callers only see what operations are available, not HOW.
 *   Optional<T>    — Generics; avoids NullPointerException on single lookups.
 *
 * All methods declare throws DatabaseException so callers handle DB errors
 * explicitly rather than catching raw SQLExceptions.
 */
public interface UserDAO {

    /**
     * Inserts a new user and returns the generated primary key.
     */
    int save(User user) throws DatabaseException;

    /**
     * Updates an existing user record.
     */
    void update(User user) throws DatabaseException;

    /**
     * Soft-deletes a user (sets is_active = 0).
     */
    void deactivate(int userId) throws DatabaseException;

    /**
     * Finds a user by primary key.
     */
    Optional<User> findById(int id) throws DatabaseException;

    /**
     * Finds a user by email — used during login and duplicate checks.
     */
    Optional<User> findByEmail(String email) throws DatabaseException;

    /**
     * Finds a user by phone — used for duplicate checks.
     */
    Optional<User> findByPhone(String phone) throws DatabaseException;

    /**
     * Returns all users with the given role.
     */
    List<User> findByRole(String role) throws DatabaseException;

    /**
     * Updates only the password hash — used in password reset flow.
     */
    void updatePassword(int userId, String newPasswordHash) throws DatabaseException;

    /**
     * Returns total count of active users — used on admin dashboard.
     */
    int countActiveUsers() throws DatabaseException;
}
