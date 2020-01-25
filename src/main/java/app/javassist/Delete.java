package app.javassist;

import javassist.*;

import java.util.ArrayList;



//klasa odpowiedzialna za usuwanie rzeczy z edytowanego programu
public class Delete {
    private static CtClass ctClass;
    private static ArrayList<String> addedClasses = new ArrayList<String>();
    private static ArrayList<String> addedPackages = new ArrayList<String>();

    public static void setCtClass(CtClass ctClass) {
        Delete.ctClass = ctClass;
    }

    public static void setAddedClasses(ArrayList<String> addedClasses) {
        Delete.addedClasses = addedClasses;
    }

    public static void setAddedPackages(ArrayList<String> addedPackages) {
        Delete.addedPackages = addedPackages;
    }

    public static ArrayList<String> getAddedClasses() {
        return addedClasses;
    }

    public static ArrayList<String> getAddedPackages() {
        return addedPackages;
    }


    public static void field(String fieldName) throws NotFoundException {
        ctClass.removeField(ctClass.getField(fieldName));
    }

    public static void method(String methodName) throws  NotFoundException {
        ctClass.removeMethod(ctClass.getDeclaredMethod(methodName));
    }

    public static void constructor(String constructorName) throws NotFoundException {
        CtConstructor [] constructors = ctClass.getDeclaredConstructors();
        for(CtConstructor constructor : constructors){
            if(constructor.getLongName().equals(constructorName)){
                ctClass.removeConstructor(constructor);
                break;
            }
        }
    }

    public static void deletePackage(String nameOfPackage) throws Exception {
        if(addedPackages.contains(nameOfPackage)){
            addedPackages.remove(nameOfPackage);
            Add.setAddedPackages(addedPackages);
        } else throw new Exception("Can only delete added packages!");
    }


    public static void deleteClass(String nameOfClass) throws Exception {
        if(addedClasses.contains(nameOfClass)){
            addedClasses.remove(nameOfClass);
            Add.setAddedClasses(addedClasses);
        } else throw new Exception("Can only delete added classes!");
    }
}
