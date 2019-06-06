package PsuedoJson;

import actives.AbstractActive;
import actives.LoadActives;
import java.io.StringReader;
import java.util.Objects;
import static java.lang.System.out;
import java.lang.reflect.Field;
import java.util.Map.Entry;
import javax.json.*;
import passives.AbstractPassive;
import passives.LoadPassives;

/**
 *
 * @author Matt
 */
public class JsonTest {
    public static void deserialize(String s){
        JsonReader r = Json.createReader(new StringReader(s));
        JsonObject obj = r.readObject();
        
        obj.forEach((String key, JsonValue value)->{
            out.println(key + " : " + value.toString());
            out.println(value.getValueType());
            switch(value.getValueType()){
                
            }
        });
    }
    
    public static void pprint(JsonObject obj, int indentLevel){
        String indent = "";
        for(int i = 0; i < indentLevel; i++){
            indent += " ";
        }
        out.println(indent + "{");
        for(Entry<String, JsonValue> val : obj.entrySet()){
            out.print("    " + indent + val.getKey() + ": ");
            if(val.getValue() instanceof JsonObject){
                pprint((JsonObject)val.getValue(), indentLevel + 4);
            } else if (val.getValue() instanceof JsonArray) {
                out.println("[");
                for(JsonValue j : (JsonArray)val.getValue()){
                    out.println("        " + indent + j);
                }
                out.println("    " + indent + "]");
            } else {
                out.println(val.getValue().toString());
            }
        };
        out.println(indent + "}");
    }
    
    public static void main(String[] args) throws Exception{
        LoadActives.load();
        LoadPassives.load();
        for(AbstractActive aa : AbstractActive.getAll()){
            pprint(aa.serializeJson(), 0);
        }
        for(AbstractPassive ap : AbstractPassive.getAll()){
            pprint(ap.serializeJson(), 0);
        }
    }
}