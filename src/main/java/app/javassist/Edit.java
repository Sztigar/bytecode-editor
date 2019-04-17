package app.javassist;

import javassist.*;

//klasa odpowiedzialna za edytowanie rzeczy w edytowanym programie
public abstract class Edit {
    private static CtClass ctClass;

    public static void setCtClass(CtClass ctClass) {
        Edit.ctClass = ctClass;
    }

    public static void method(String selectedItem, String code) throws NotFoundException, CannotCompileException {
        ctClass.getDeclaredMethod(selectedItem).setBody(code);
    }

    public static void constructor(String selectedItem, String code) throws CannotCompileException {
        CtConstructor[] constructors = ctClass.getDeclaredConstructors();
        for(CtConstructor constructor : constructors){
            if(constructor.getLongName().equals(selectedItem)){
                constructor.setBody(code);
                break;
            }
        }
    }

    public static void insertEndMethod(String selectedItem, String text) throws NotFoundException, CannotCompileException {
        CtMethod editedMethod = ctClass.getDeclaredMethod(selectedItem);
        editedMethod.insertAfter(text);
    }

    public static void insertFrontMethod(String selectedItem, String text) throws CannotCompileException, NotFoundException {
        CtMethod editedMethod = ctClass.getDeclaredMethod(selectedItem);
        editedMethod.insertBefore(text);
    }


}
