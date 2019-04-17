package app.javassist;

import javassist.*;

import java.util.ArrayList;

//odpowiedzialne za dodawanie nowych rzeczy do edytowanego programu
public abstract class Add {
    private static CtClass ctClass;
    private static ArrayList<String> addedClasses = new ArrayList<String>();
    private static ArrayList<String> addedPackages = new ArrayList<String>();
    //czy dodajemy jythona
    private static boolean jython = false;

    public static void setCtClass(CtClass ctClass) {
        Add.ctClass = ctClass;
    }

    public static void setAddedClasses(ArrayList<String> addedClasses) {
        Add.addedClasses = addedClasses;
    }

    public static void setAddedPackages(ArrayList<String> addedPackages) {
        Add.addedPackages = addedPackages;
    }

    public static ArrayList<String> getAddedClasses() {
        return addedClasses;
    }

    public static ArrayList<String> getAddedPackages() {
        return addedPackages;
    }

    public static boolean isJython() {
        return jython;
    }

    public static void setJython(boolean value) {
        Add.jython = value;
    }
    //tworzy nowego fielda
    public static void field(String code) throws CannotCompileException {
        CtField newField = CtField.make(code, ctClass);
        ctClass.addField(newField);
    }

    public static void method(String code) throws CannotCompileException {
        CtMethod newMethod = CtNewMethod.make(code, ctClass);
        ctClass.addMethod(newMethod);
    }

    public static void constructor(String code) throws CannotCompileException {
        CtConstructor newConstructor = CtNewConstructor.make(code, ctClass);
        ctClass.addConstructor(newConstructor);
    }

    public static void addPackage(String nameOfPackage) {
        addedPackages.add(nameOfPackage.replace(".", "\\"));
        Delete.setAddedPackages(addedPackages);
    }

    public static void addClass(String nameOfClass) {
        CtClass newClass =  Javassist.getPool().makeClass(nameOfClass);
        addedClasses.add(newClass.getName());
        Delete.setAddedClasses(addedClasses);
    }

    public static void addInterface(String interfaceName) {
        CtClass newClass = Javassist.getPool().makeInterface(interfaceName);
        addedClasses.add(newClass.getName());
        Delete.setAddedClasses(addedClasses);
    }
}