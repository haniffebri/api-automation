package autoApi;

import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.testng.annotations.Test;


import java.io.File;
import java.util.HashMap;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

public class APITest {

    @Test
    public void getListUser(){

        RestAssured
                .given()
                .when()
                .get("https://reqres.in/api/users?page=2") // ( Test get api/users?page=2 total data 6 per page
                .then()
                .log().all() // print semua req ke console
                .assertThat().statusCode(200)  // status code
                .assertThat().body("page", Matchers.equalTo(2)) // access correct page
                .assertThat().body("data.id", Matchers.hasSize(6)); // seluruh data ada 6

    }

    @Test
    public void createNewUserTest(){

        //Create post body with parameter name , job in json format
        String name = "Hanif";
        String job = "student";
        JSONObject bodyObject = new JSONObject(); // Hashmap alternatif
        bodyObject.put("name", name);
        bodyObject.put("job", job);

        //Test
        RestAssured.given()
                .header("Content-type", "application/json")
                .header("Accept", "application/json")
                .body(bodyObject.toString()) // convert json to string format -> { "name":"hanif", "job":"student"}
                .when()
                .post("https://reqres.in/api/users")
                .then().log().all() //.log().all() untuk print semua request ke console
                .assertThat().statusCode(201) //status code 201 ( OK )
                .assertThat().body("name", Matchers.equalTo(name)) // assert response body "name"
                .assertThat().body("job", Matchers.equalTo(job)) // assert response body "job"
                .assertThat().body("$", Matchers.hasKey("id")) // assert response body "id"
                .assertThat().body("$", Matchers.hasKey("createdAt")); // assert response body "createdAt"

    }

    @Test
    public void RegisterTestSuccessful(){

        String email = "eve.holt@reqres.in";
        String password = "pistol";

        JSONObject bodyObject = new JSONObject();

        bodyObject.put("email", email);
        bodyObject.put("password", password);

        //Test
        RestAssured.given()
                .header("Content-type", "application/json")
                .header("Accept", "application/json")
                .body(bodyObject.toString())
                .when()
                .post("https://reqres.in/api/register")
                .then().log().all()
                .assertThat().statusCode(200)
                .assertThat().body("id", Matchers.equalTo(4))
                .assertThat().body("token", Matchers.equalTo("QpwL5tke4Pnpja7X4"));
    }

    @Test
    public void RegisterTestUnsuccesful() {

        String email = "eve.holt@reqres.in";

        JSONObject bodyObject = new JSONObject();

        bodyObject.put("email", email);

        //Test
        RestAssured.given()
                .header("Content-type", "application/json")
                .header("Accept", "application/json")
                .body(bodyObject.toString())
                .when()
                .post("https://reqres.in/api/register")
                .then().log().all()
                .assertThat().statusCode(400);
    }

    @Test
    public void testPutUser(){

        RestAssured.baseURI = "https://reqres.in/";
        //Data to update
        int userId = 2;
        String newName = "Hanif";

        //Test PUT user id 2 -> update first name
        //pertama, get atribut dari id ke dua
        String fname = given().when().get("api/users/"+userId).getBody().jsonPath().get("data.first_name");
        String lname = given().when().get("api/users/"+userId).getBody().jsonPath().get("data.last_name");
        String avatar = given().when().get("api/users/"+userId).getBody().jsonPath().get("data.avatar");
        String email = given().when().get("api/users/"+userId).getBody().jsonPath().get("data.email");
        System.out.println("name before = "+fname);

        //ubah first name ke "Hanif"
        //Create body request with hashmap and convert it to json
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("id", userId);
        bodyMap.put("email", email);
        bodyMap.put("first_name", newName);
        bodyMap.put("last_name", lname);
        bodyMap.put("avatar", avatar);
        JSONObject bodyObject = new JSONObject(bodyMap);

        given().log().all()
                .header("Content-Type", "application/json") // set header to accept json
                .body(bodyObject.toString()) //convert bodyObject to string format
                .put("api/users/"+userId)
                .then().log().all()
                .assertThat().statusCode(200)
                .assertThat().body("first_name", Matchers.equalTo(newName));
    }

    @Test
    public void testPatchUser(){

        RestAssured.baseURI = "https://reqres.in/";
        //Data to update
        int userId = 3;
        String newName = "Hanif";

        //Test Patch user id 3 -> update first name
        //pertama, get firstname dari user id ke 3
        String fname = given().when().get("api/users/"+userId).getBody().jsonPath().get("data.first_name");
        System.out.println("name before = "+fname);

        //ubah first name ke "Hanif"
        //Create body request with hashmap and convert it to json
        HashMap<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("first_name", newName);
        JSONObject bodyObject = new JSONObject(bodyMap);

        given().log().all()
                .header("Content-Type", "application/json") // set header to accept json
                .body(bodyObject.toString()) //convert bodyObject to string format
                .patch("api/users/"+userId)
                .then().log().all()
                .assertThat().statusCode(200)
                .assertThat().body("first_name", Matchers.equalTo(newName));
    }

    @Test
    public void testDeleteUser() {

        RestAssured.baseURI = "https://reqres.in/"; //define base URL
        int userToDelete = 4;
        given().log().all() // print semua req ke console
                .when().delete("api/users/" + userToDelete)
                .then()
                .log().all()
                .assertThat().statusCode(204); // status code 204
    }

    @Test
    public void validateJsonSchemaGetListUser() {

        RestAssured.baseURI = "https://reqres.in/"; //define base URL

        File file = new File("src/test/resources/jsonSchema/listUserSchema.json");

        given().log().all() // print semua req ke console
                .when().get("api/users/")
                .then()
                .log().all()
                .assertThat().statusCode(200) // status code 200
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(file));
    }

}