package com.clc.levelup.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Loads user credentials and roles directly from the database using {@link JdbcTemplate}.
 * <p>
 * This implementation avoids additional repository classes by running lightweight SQL queries
 * for authentication and authorization. It returns a Spring Security {@link UserDetails}
 * object with the username, hashed password, and authorities populated.
 * </p>
 */
@Service
public class DatabaseUserDetailsService implements UserDetailsService {

  private final JdbcTemplate jdbc;

  /**
   * Inject a configured {@link JdbcTemplate} for database access.
   * @param jdbc JDBC template
   */
  public DatabaseUserDetailsService(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  /**
   * Load user details by username.  
   * Queries both the {@code users} and {@code roles} tables to build a complete user profile.
   * @param username the username to look up
   * @return fully populated {@link UserDetails} object
   * @throws UsernameNotFoundException if the username does not exist
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // Fetch the user record
    UserRow user = findUser(username);

    // Fetch all roles for this user
    List<String> roleNames = findRoles(username);

    // Convert each role name to a GrantedAuthority
    var authorities = roleNames.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    boolean enabled = user.enabled != null && user.enabled == 1;

    // Return a standard Spring Security UserDetails object
    return org.springframework.security.core.userdetails.User
        .withUsername(user.username)
        .password(user.password)
        .authorities(authorities)
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(!enabled)
        .build();
  }

  /**
   * Query a single user record from the database by username.
   * @param username the username to find
   * @return populated {@link UserRow} record
   * @throws UsernameNotFoundException if no matching user is found
   */
  private UserRow findUser(String username) {
    String sql = "SELECT id, username, password, enabled FROM users WHERE username = ?";

    // Execute the query and map results
    List<UserRow> rows = jdbc.query(sql, new UserRowMapper(), username);

    if (rows.isEmpty()) {
      throw new UsernameNotFoundException("User not found");
    }
    return rows.get(0);
  }

  /**
   * Query all role names associated with a given username.
   * @param username the username for which roles should be loaded
   * @return list of role names (e.g., ROLE_USER, ROLE_ADMIN)
   */
  private List<String> findRoles(String username) {
    String sql =
        "SELECT r.name " +
        "FROM roles r " +
        "JOIN user_roles ur ON r.id = ur.role_id " +
        "JOIN users u ON u.id = ur.user_id " +
        "WHERE u.username = ?";

    // Return list of role names
    return jdbc.query(sql, (rs, i) -> rs.getString("name"), username);
  }

  /**
   * Lightweight holder for a user record retrieved from the database.
   * Used internally by the row mapper.
   */
  private static class UserRow {
    String username;
    String password;
    Integer enabled;
  }

  /**
   * Maps result set rows to {@link UserRow} instances.
   */
  private static class UserRowMapper implements RowMapper<UserRow> {
    @Override
    public UserRow mapRow(ResultSet rs, int rowNum) throws SQLException {
      UserRow u = new UserRow();
      u.username = rs.getString("username");
      u.password = rs.getString("password");
      u.enabled = rs.getInt("enabled");
      return u;
    }
  }
}
