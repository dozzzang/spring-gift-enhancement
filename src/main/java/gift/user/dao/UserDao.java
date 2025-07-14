package gift.user.dao;

import gift.exception.UserNotFoundException;
import gift.user.domain.Role;
import gift.user.domain.User;
import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

  private final JdbcClient jdbcClient;

  public UserDao(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  public User saveUser(String email, String encodedPassword) {
    String sql = "insert into users (email,password) values (?, ?)";

    jdbcClient.sql(sql)
        .param(email)
        .param(encodedPassword)
        .update();

    String sqlForFind = "SELECT memberId, email, password, role FROM users"
                        + " WHERE email = ?";
    return jdbcClient.sql(sqlForFind)
        .param(email)
        .query((result, rowNum) -> {
          User user = new User(
              result.getLong("memberId"),
              result.getString("email"),
              result.getString("password"),
              Role.valueOf(result.getString("role"))
          );
          return user;
        })
        .single();
  }

  public User findByEmail(String email) {
    String sql = "select memberId, email, password, role from users where email = ?";

    return jdbcClient.sql(sql)
        .param(email)
        .query((result, rowNum) -> {
          User user = new User(
              result.getLong("memberId"),
              result.getString("email"),
              result.getString("password"),
              Role.valueOf(result.getString("role"))
          );
          return user;
        })
        .single();
  }

  public User findById(Long id) {
    String sql = "select memberId, email, password, role from users where memberId = ?";

    return jdbcClient.sql(sql)
        .param(id)
        .query((result, rowNum) -> {
          User user = new User(
              result.getLong("memberId"),
              result.getString("email"),
              result.getString("password"),
              Role.valueOf(result.getString("role"))
          );
          return user;
        })
        .single();
  }

  public List<User> findAllUsers() {
    String sql = "select memberId, email, password, role from users";
    return jdbcClient.sql(sql)
        .query((resultSet, rowNum) -> {
              User user = new User(
                  resultSet.getLong("memberId"),
                  resultSet.getString("email"),
                  resultSet.getString("password"),
                  Role.valueOf(resultSet.getString("role"))
              );
              return user;
            }
        ).list();
  }

  public User updateUser(Long userId, String email, String encodedPassword) {
    String sql = "update users set email = ?, password = ? where memberId = ?";
    int updatedRow = jdbcClient.sql(sql)
        .param(email)
        .param(encodedPassword)
        .param(userId)
        .update();
    if (updatedRow == 0) {
      throw new UserNotFoundException();
    }
    return findById(userId);
  }

  public void deleteById(Long userId) {
    String sql = "delete from users where memberId = ?";

    int deletedRows = jdbcClient.sql(sql)
        .param(userId)
        .update();

    if (deletedRows == 0) {
      throw new UserNotFoundException();
    }
  }

}
