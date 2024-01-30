package Utility;

import java.io.File;

public class utilities {

    public static File getJSONSchemaFile(String JSONFile){
        return new File("src/test/resources/jsonSchema/listUserSchema.json");
    }

}
