package org.reluxa.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Transactional
public class DBStore {

  public final JdbcTemplate jdbcTemplatel;

  public DBStore(DataSource dataSource) {
    this.jdbcTemplatel = new JdbcTemplate(dataSource);
  }

  public void deleteAll() {
    jdbcTemplatel.execute("delete from squares");
  }

  public void insert(int number) {
    jdbcTemplatel.execute("insert into squares(id) values("+number+")");
  }

  public void addResult(int number, int result) {
    jdbcTemplatel.execute("update squares set square = "+result+" where id = "+number);
  }


}
