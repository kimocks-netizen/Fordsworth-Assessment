package org.gs;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bryne Chikomo
 * @version 1.0.0
 * 
**/

@Path("/persons")
public class personService {
    //public static List<Person> persons = new ArrayList<>();
     private static List<Person> persons = new ArrayList<>();

    @Inject
    PgPool client;
    
    @PostConstruct
    void config() {
        initdb();
    }
    /**
     * 
     * @return
     */
    @GET
    public Multi<Person> getAll() {
        return Person.findAll(client);
    }
    
    /**
     * 
     * @param id
     * @return
     */
    @GET
    @Path("{id}")
    public Uni<Response> get(@PathParam("id") Long id) {
        return Person.findById(client, id)
                .onItem()
                .transform(person -> person != null ? Response.ok(person) : Response.status(Response.Status.NOT_FOUND))
                .onItem()
                .transform(Response.ResponseBuilder::build);
    }
    
    /**
     * 
     * @param person
     * @return Obj
     * @see Save
     */
    @POST
    public Uni<Response> create(Person person) {
        return person.save(client, person.getfirstname())
                .onItem()
                .transform(id -> URI.create("/persons/" + id))
                .onItem()
                .transform(uri -> Response.created(uri).build());
    }

    /**
     * 
     * @param id
     * @param p
     * @return
     */
    @PUT
    @Path("{id}")

    public Uni <Response> updatePerson(
        @PathParam("id") Long id, Person updatedPerson){
        
        persons = persons.stream().map(person -> {
            if(person.getId().equals(id)){
                person.setfirstname(updatedPerson.getfirstname());
                /*
                You may add other fields here like:
                    lastname
                    address
                    phone number .etc.
                 */ 
            }
            return person;
            
        }).collect(Collectors.toList());

    
        return Uni.createFrom().item(Response.ok(persons).build());
    }

    /**
     * 
     * @param id
     * @return
     */
    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") Long id) {
        return Person.delete(client, id)
                .onItem()
                .transform(deleted -> deleted ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
                .onItem()
                .transform(status -> Response.status(status).build());
    }
    
    private void initdb() {
        client.query("DROP TABLE IF EXISTS persons").execute()
                .flatMap(m-> client.query("CREATE TABLE persons (id SERIAL PRIMARY KEY, " +
                        "firstname TEXT NOT NULL)").execute())
                .flatMap(m -> client.query("INSERT INTO persons (firstname) VALUES('Fordsworth Associates')").execute())
                .flatMap(m -> client.query("INSERT INTO persons (firstname) VALUES('Kenneth Chamisa')").execute())
                .await()
                .indefinitely();
    }
    
}
