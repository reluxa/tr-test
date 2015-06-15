package org.reluxa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Transactional
public class DBStore {

  public final JdbcTemplate jdbcTemplatel;
  private Logger logger = LoggerFactory.getLogger(DBStore.class);


  public DBStore(DataSource dataSource) {
    this.jdbcTemplatel = new JdbcTemplate(dataSource);
  }

  public void deleteAll() {
    jdbcTemplatel.execute("delete from squares2");
//    jdbcTemplatel.execute("drop table squares2");
//    jdbcTemplatel.execute("create table squares2 (id integer primary key, square integer)");
    logger.info("All entries were deleted from the database");
  }

  public void insert(int number) {
    jdbcTemplatel.execute("insert into squares2(id) values("+number+")");
    logger.info("{} was inserted to the database", number);

  }

  public void addResult(int number, int result) {
    jdbcTemplatel.execute("update squares2 set square = "+result+" where id = "+number);
    logger.info("{} was updated in the db to {}", number, result);
  }


}
