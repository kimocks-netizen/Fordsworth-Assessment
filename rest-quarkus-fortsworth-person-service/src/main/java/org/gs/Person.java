package org.gs;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;


/**
 * @author Bryne Chikomo
 * @version 1.0.0
 * 
**/
public class Person {

    private Long id;
    private String firstname;
    /*You can add other variables of your choice here like: 
        lastname
        address
        phone number 
    etc*/
   
   /**
    * 
    * @param id
    * @param firstname
    */
    public Person(Long id, String firstname) {
      this.id = id;
      this.firstname = firstname;
    }
   
    /**
     * 
     * @return id
     */
    public Long getId() {
      return id;
    }
   
    public void setId(Long id) {
      this.id = id;
    }
   
    /**
     * 
     * @return firstname
     */
    public String getfirstname() {
      return firstname;
    }
   
    public void setfirstname(String firstname) {
      this.firstname = firstname;
    }
   
    /**
     * 
     * @param client
     * @return Person Object
     */
    public static Multi<Person> findAll(PgPool client) {
      return client
              .query("SELECT id, firstname FROM persons ORDER BY firstname DESC")
              .execute()
              .onItem()
              .transformToMulti(set -> Multi.createFrom().iterable(set))
              .onItem()
              .transform(Person::from);
    }
   
    /**
     * 
     * @param client
     * @param id
     * @return
     */
    public static Uni<Person> findById(PgPool client, Long id) {
      return client
              .preparedQuery("SELECT id, firstname FROM persons WHERE id = $1")
              .execute(Tuple.of(id))
              .onItem()
              .transform(m -> m.iterator().hasNext() ? from(m.iterator().next()) : null);
    }
   
    /**
     * 
     * @param client
     * @param firstname
     * @link Response Class
     * @return
     */
    public Uni<Long> save(PgPool client, String firstname) {
      return client
              .preparedQuery("INSERT INTO persons (firstname) VALUES ($1) RETURNING id")
              .execute(Tuple.of(firstname))
              .onItem()
              .transform(m -> m.iterator().next().getLong("id"));
    }
   
    /**
     * 
     * @param client
     * @param id
     * @return
     */
    public static Uni<Boolean> delete(PgPool client, Long id) {
      return client
              .preparedQuery("DELETE FROM persons WHERE id = $1")
              .execute(Tuple.of(id))
              .onItem()
              .transform(m -> m.rowCount() == 1);
    }
   /**
    * 
    * @param row
    * @return
    */
    private static Person from(Row row) {
      return new Person(row.getLong("id"), row.getString("firstname"));
    }
}
