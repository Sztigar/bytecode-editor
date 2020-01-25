package app.javassist;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;

//pobieramy tutaj z klasy metody konstruktory albo fieldy
public class Get {
    private static CtClass ctClass;

    public static void setCtClass(CtClass ctClass) {
        Get.ctClass = ctClass;
    }


    //zwraca nam ObservableList bo taki typ przyjmuje JavaFXowy ListView w ktorym listujemy te rzeczy
    public static ObservableList<String> methods() {
        //getDeclaredMethods zwraca nam tylko zadeklarowane przez programiste metody
        CtMethod[] ctMethods = ctClass.getDeclaredMethods();
        ObservableList<String> methodsList = FXCollections.observableArrayList();
        for (CtMethod m : ctMethods) {
            methodsList.add(m.getName());
        }
        return methodsList;
    }

    public static ObservableList<String> constructors() {
        //getDeclared zwraca nam rowniez prywatne konstruktory
        CtConstructor[] ctConstructors = ctClass.getDeclaredConstructors();
        ObservableList<String> constructorsList = FXCollections.observableArrayList();
        for (CtConstructor c : ctConstructors) {
            constructorsList.add(c.getLongName());
        }
        return constructorsList;
    }

    public static ObservableList<String> fields() {
        CtField[] ctFields = ctClass.getDeclaredFields();
        ObservableList<String> fieldsList = FXCollections.observableArrayList();
        for (CtField f : ctFields) {
            fieldsList.add(f.getName());
        }
        return fieldsList;
    }

}
