package org.reluxa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

  public boolean verify(int number) {
    List<Map<String, Object>> list = jdbcTemplatel.queryForList("select * from squares2 order by id");
    if (list.size() != number) {
      logger.error("missing entry from the database");
      return false;
    }
    for(int i=0;i<number;i++) {
      Map<String, Object> map = list.get(i);
      if (!((BigDecimal)map.get("id")).equals(new BigDecimal(i)) ||
      !((BigDecimal)map.get("square")).equals(new BigDecimal(i*i))) {
        logger.error("problem with entry: {} ", i);
        return false;
      }
    }
    logger.info("data was ok in the database!!!");
    return true;
  }


}
