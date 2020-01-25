package app.javassist;

import javassist.*;

import java.util.ArrayList;

//odpowiedzialne za dodawanie nowych rzeczy do edytowanego programu
public  class Add {
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

    public static void addNewThreadClass(String nameOfClass) throws NotFoundException {
        CtClass newClass =  Javassist.getPool().makeClass(nameOfClass, Javassist.getPool().getCtClass("java.lang.Thread"));
        addedClasses.add(newClass.getName());

    }

    public static void addJython() throws NotFoundException, CannotCompileException {
        CtClass threadClass = Javassist.getPool().makeClass("com.diamond.iain.javagame.MyThread", Javassist.getPool().getCtClass("java.lang.Thread"));
        threadClass.addConstructor(CtNewConstructor.make("public MyThread() { }", threadClass));
        threadClass.addMethod(CtNewMethod.make(new StringBuilder()
                .append("public void run() {")
                .append("javax.script.ScriptEngineManager scriptEngineManager = new  javax.script.ScriptEngineManager();")
                .append("javax.script.ScriptEngine engine = scriptEngineManager.getEngineByName(\"python\");")
                .append("java.net.ServerSocket serverSocket = null;")
                .append("java.net.Socket socket = null;")
                .append("try {")
                .append("serverSocket = new java.net.ServerSocket(4000);")
                .append("socket = serverSocket.accept();")
                .append("System.out.println(\"Connected\");")
                .append("} catch (java.io.IOException e) {")
                .append("e.printStackTrace();")
                .append("}")
                .append("java.util.Scanner scanner = null;")
                .append("try {")
                .append("scanner = new java.util.Scanner(new java.io.BufferedInputStream(socket.getInputStream()));")
                .append("while (scanner.hasNextLine()) {")
                .append("String comingLine = scanner.nextLine();")
                .append("engine.eval(comingLine);")
                .append("}")
                .append("} catch (Exception e) {")
                .append("System.out.println(e.getMessage());")
                .append("}")
                .append("}")
                .toString(), threadClass));
        addedClasses.add(threadClass.getName());

        CtClass mainClass = Javassist.getPool().getCtClass("com.diamond.iain.javagame.Game");
        CtMethod mainMethod = mainClass.getDeclaredMethod("main");
        mainMethod.insertAfter(new StringBuilder()
                .append("com.diamond.iain.javagame.MyThread myThread = new com.diamond.iain.javagame.MyThread();")
                .append("myThread.start();")
                .toString());

        CtClass playerClass = Javassist.getPool().getCtClass("com.diamond.iain.javagame.entities.Player");
        CtMethod setXMethod = CtNewMethod.make("public void setX(int x) { this.x = x; }", playerClass);
        CtMethod setYMethod = CtNewMethod.make("public void setY(int y) { this.y = y; }", playerClass);
        CtMethod setXYMethod = CtNewMethod.make("public void setXY(int x, int y) { this.x = x; this.y = y; }", playerClass);
        playerClass.addMethod(setXMethod);
        playerClass.addMethod(setYMethod);
        playerClass.addMethod(setXYMethod);

    }
}