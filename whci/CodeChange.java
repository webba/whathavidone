/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whci;

/**
 *
 * @author Webba
 */
public class CodeChange {
    public String codeBlock;
    public String methodName;
    public String className;
    public String changeType;
    public CodeChange(String _codeBlock, String _methodName, String _className, String _changeType) {
        this.codeBlock = _codeBlock;
        this.methodName = _methodName;
        this.className = _className;
        this.changeType = _changeType;
    }
}
