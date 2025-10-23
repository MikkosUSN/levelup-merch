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

/*
 * Loads user credentials and roles from the database.
 * Uses JdbcTemplate to avoid extra repository classes.
 */
@Service
public class DatabaseUserDetailsService implements UserDetailsService {

  private final JdbcTemplate jdbc;

  public DatabaseUserDetailsService(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserRow user = findUser(username);
    List<String> roleNames = findRoles(username);

    var authorities = roleNames.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    boolean enabled = user.enabled != null && user.enabled == 1;

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

  private UserRow findUser(String username) {
    String sql = "SELECT id, username, password, enabled FROM users WHERE username = ?";
    // New overload: (sql, rowMapper, args...)
    List<UserRow> rows = jdbc.query(sql, new UserRowMapper(), username);
    if (rows.isEmpty()) {
      throw new UsernameNotFoundException("User not found");
    }
    return rows.get(0);
  }

  private List<String> findRoles(String username) {
    String sql =
        "SELECT r.name " +
        "FROM roles r " +
        "JOIN user_roles ur ON r.id = ur.role_id " +
        "JOIN users u ON u.id = ur.user_id " +
        "WHERE u.username = ?";
    // New overload: (sql, rowMapper, args...)
    return jdbc.query(sql, (rs, i) -> rs.getString("name"), username);
  }

  private static class UserRow {
    // Keeping fields minimal; remove id to silence “not used” warning
    String username;
    String password;
    Integer enabled;
  }

  private static class UserRowMapper implements RowMapper<UserRow> {
    @Override
    public UserRow mapRow(ResultSet rs, int rowNum) throws SQLException {
      UserRow u = new UserRow();
      // u.id = rs.getLong("id"); // removed since not used anywhere
      u.username = rs.getString("username");
      u.password = rs.getString("password");
      u.enabled = rs.getInt("enabled");
      return u;
    }
  }
}
