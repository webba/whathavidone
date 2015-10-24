/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whci;
import javassist.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.util.*;


/**
 *
 * @author Webba
 */
public class Whci {

    /**
     * @param args the command line arguments 
     * 
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String location = getJarLocation();
        location = location.substring(1, location.length()-8);
        String settings = null;
        try{
            settings = readFile(location + "settings.txt", StandardCharsets.UTF_8);
            System.out.println(settings);
            JSONParser parser = new JSONParser();
            JSONObject settingsObj = (JSONObject)parser.parse(settings);
            String jarPath = settingsObj.get("jar").toString();
            File modFolder = new File(settingsObj.get("mods").toString());
            if (!modFolder.isDirectory()){
                System.out.println("Mods folder is not directory");
                return;
            }
            for (final File fileEntry : modFolder.listFiles()){
                String modJSON = readFile(fileEntry.getPath(), StandardCharsets.UTF_8);
                Mod tempMod = new Mod(jarPath, modJSON);
                tempMod.InjectAll();
            }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    
    private static void overrideWurmMethod(String className, String methodName, String methodText){
        
    }
    
    static String readFile(String path, Charset encoding) 
  throws IOException 
    {
      byte[] encoded = Files.readAllBytes(Paths.get(path));
      return new String(encoded, encoding);
    }
    
    private static String getJarLocation(){
        return Whci.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }
}
