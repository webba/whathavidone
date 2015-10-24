/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whci;
import java.util.ArrayList;
import javassist.*;
import org.json.simple.*;
import org.json.simple.parser.*;
/**
 *
 * @author Webba
 */
public class Mod {
    private ArrayList<CodeChange> codeChanges;
    private String jarPath;
    
    public Mod(String _jarPath, String modJSON){
        jarPath = _jarPath;
        codeChanges = new ArrayList<CodeChange>();
        try
        {
            JSONParser parser = new JSONParser();
            JSONArray modCode = (JSONArray)parser.parse(modJSON);
            for (int i = 0; i < modCode.size(); i++) {
                JSONObject temp = (JSONObject)modCode.get(i);
                String _codeBlock = temp.get("codeBlock").toString();
                String _methodName = temp.get("methodName").toString();
                String _className = temp.get("className").toString();
                String _changeType = temp.get("changeType").toString(); 
                codeChanges.add(new CodeChange(_codeBlock, _methodName, _className, _changeType));
            }
        }
        catch (Exception ex){
            System.out.println("Error probably from json shit");
        }
    }
    
    public void InjectAll(){
        for(int i = 0; i < codeChanges.size(); i++){
            try{
                injectMethod(codeChanges.get(i));
            }
            catch(Exception ex){
                CodeChange codeChange = codeChanges.get(i);
                System.out.println("Error in all writing code to " + codeChange.className + ", method: "+ codeChange.methodName);
            }
        }
    }
    
    private void injectMethod(CodeChange codeChange) throws InvalidChangeTypeException {
        if (codeChange.changeType.equals("insertbefore")){
            try{
                ClassPool pool = ClassPool.getDefault();
                ClassPath cp = pool.insertClassPath( jarPath );
                CtClass cc = pool.get(codeChange.className);
                CtMethod cm = cc.getDeclaredMethod(codeChange.methodName, new CtClass[0]);
                cm.insertBefore(codeChange.codeBlock);
                byte[] b = cc.toBytecode(); // convert the new class to bytecode.
                pool.removeClassPath(cp);   // need to remove the classpath to release connection to JAR file so we can update it.
                JarHandler jarHandler = new JarHandler();
                jarHandler.replaceJarFile(jarPath, b, codeChange.className.replace('.', '/') + ".class");
            }
            catch(NotFoundException eex){
                System.out.println(eex.getMessage());
            }
            catch(CannotCompileException exx){
                System.out.println(exx.getReason());
            }
            catch (Exception ex){
                System.out.println("Error writing code to " + codeChange.className + ", method: "+ codeChange.methodName);
            }
        }
        else if (codeChange.changeType.equals("insertafter")){
            try{
                ClassPool pool = ClassPool.getDefault();
                ClassPath cp = pool.insertClassPath( jarPath );
                CtClass cc = pool.get(codeChange.className); 
                CtMethod cm = cc.getDeclaredMethod(codeChange.methodName, new CtClass[0]);
                cm.insertAfter(codeChange.codeBlock);

                byte[] b = cc.toBytecode(); // convert the new class to bytecode.
                pool.removeClassPath(cp);   // need to remove the classpath to release connection to JAR file so we can update it.
                JarHandler jarHandler = new JarHandler();
                jarHandler.replaceJarFile(jarPath, b, codeChange.className.replace('.', '/') + ".class");
            }
            catch (Exception ex){
                System.out.println("Error writing code to " + codeChange.className + ", method: "+ codeChange.methodName);
            }
        }
        else if (codeChange.changeType.equals("replace")){
            try{
                ClassPool pool = ClassPool.getDefault();
                ClassPath cp = pool.insertClassPath( jarPath );
                CtClass cc = pool.get(codeChange.className); 
                CtMethod cm = cc.getDeclaredMethod(codeChange.methodName, new CtClass[0]);
                cm.setBody(codeChange.codeBlock);

                byte[] b = cc.toBytecode(); // convert the new class to bytecode.
                pool.removeClassPath(cp);   // need to remove the classpath to release connection to JAR file so we can update it.
                JarHandler jarHandler = new JarHandler();
                jarHandler.replaceJarFile(jarPath, b, codeChange.className.replace('.', '/') + ".class");
            }
            catch (Exception ex){
                System.out.println("Error writing code to " + codeChange.className + ", method: "+ codeChange.methodName);
            }
        }
        else
        {
            throw new InvalidChangeTypeException();
        }
    }
    
}
