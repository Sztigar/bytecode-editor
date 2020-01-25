package app.javassist;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.File;

//głowna klasa obslugi javassist w ktorej jest ClassPool i CtClass ktore ustala sie przy kliknieciu na explorator pakietow gdy cos konczy sie na .class
public class Javassist {

    //ClassPool przechowuje nasze klasy z niego mozemy wyciagac przez .get(nazwaklasy) rozne klasy
    private static ClassPool pool;
    //obiekt reprezentujacy klase ktora bedziemy edytowac
    private static CtClass ctClass;


    public static ClassPool getPool() {
        return pool;
    }

    public static void setPool(File jarFile) throws NotFoundException {
        //getDefault mozna potraktowac jako konstruktor ClassPoola
        pool = ClassPool.getDefault();
        //przy otwieraniu pliku podajemy jego sciezke
        pool.insertClassPath(jarFile.getPath());
    }

    public static CtClass getCtClass() {
        return ctClass;
    }

    //wywoluje sie jak klikniemy na cos co konczy sie na .class
    public static void setCtClass(String className) throws NotFoundException {
        ctClass = pool.get(className);
        Add.setCtClass(ctClass);
        Delete.setCtClass(ctClass);
        Edit.setCtClass(ctClass);
        Get.setCtClass(ctClass);
    }
}
