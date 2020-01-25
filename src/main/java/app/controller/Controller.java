package app.controller;

import app.javassist.*;
import app.other.Alerts;
import app.other.Explore;
import app.other.PackageBuilder;
import app.save.SaveTask;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.io.File;

public class Controller {

    @FXML
    private TreeView<String> packageExplorer;

    @FXML
    private ListView<String> listView;

    @FXML
    private MenuItem saveFileBtn;

    @FXML
    private MenuItem addPackageBtn;

    @FXML
    private MenuItem deletePackage;

    @FXML
    private MenuItem addClassBtn;

    @FXML
    private MenuItem addInterfaceBtn;


    @FXML
    private MenuItem deleteClassBtn;

    @FXML
    private RadioMenuItem addJythonBtn;

    @FXML
    private Button showMethodsBtn;

    @FXML
    private Button showConstructorsBtn;

    @FXML
    private Button showFieldsBtn;

    @FXML
    private Button addClickBtn;

    @FXML
    private Button deleteClickBtn;

    @FXML
    private Button setBodyBtn;

    @FXML
    private Button insertFrontBtn;

    @FXML
    private Button insertEndBtn;

    @FXML
    private TextArea code;

    @FXML
    private ProgressBar progressBar;

    private Explore explore;

    private File openedFile;

    private String path;

    @FXML
    void initialize() {
        PackageBuilder.setPackageExplorer(packageExplorer);
        explore = Explore.METHODS;
    }


    private void setButtons(boolean value) {
        addClickBtn.setDisable(value);
        deleteClickBtn.setDisable(value);
        setBodyBtn.setDisable(value);
        insertFrontBtn.setDisable(value);
        insertEndBtn.setDisable(value);
        showMethodsBtn.setDisable(value);
        showConstructorsBtn.setDisable(value);
        showFieldsBtn.setDisable(value);
    }

    private void setMenuButtons(boolean value) {
        saveFileBtn.setDisable(value);
        addPackageBtn.setDisable(value);
        deletePackage.setDisable(value);
        addClassBtn.setDisable(value);
        addInterfaceBtn.setDisable(value);
        deleteClassBtn.setDisable(value);
        addJythonBtn.setDisable(value);
    }


    @FXML
    void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Jar File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JAR", "*.jar"));
        File tempFile = fileChooser.showOpenDialog(showFieldsBtn.getScene().getWindow());
        if (tempFile != null) {
            openedFile = tempFile;
            try {
                Javassist.setPool(openedFile);
                PackageBuilder.treeViewFromFile(openedFile);
                setMenuButtons(false);
            } catch (NotFoundException e) {
                Alerts.warning("Jar file not found!");
            }
        }
    }

    @FXML
    void packageClick() {
        if (packageExplorer.getSelectionModel().getSelectedItem() != null) {
            path = PackageBuilder.getPath();
            if (path.endsWith(".class")) {
                try {
                    Javassist.setCtClass(path.replace(".class", ""));
                    listView.setItems(Get.methods());
                    setButtons(false);
                } catch (NotFoundException e) {
                    Alerts.warning("Cant find this class!");
                }
            } else {
                setButtons(true);
                listView.setItems(null);
            }
        }
    }

    @FXML
    void addClass() {
        if (packageExplorer.getSelectionModel().getSelectedItem() != null) {
            if (!path.endsWith(".class")) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setHeaderText("Enter class name: ");
                dialog.showAndWait();
                if (!dialog.getEditor().getText().equals("")) {
                    Add.addClass(path + "." + dialog.getEditor().getText());
                    packageExplorer.getSelectionModel().getSelectedItem().getChildren().add(new TreeItem<String>(dialog.getEditor().getText() + ".class"));
                    Alerts.information("Class added!");
                } else
                    Alerts.warning("Name your class!");
            } else
                Alerts.warning("Select a package!");
        } else
            Alerts.warning("Select a package!");
    }

    @FXML
    void addClick() {
        try {
            if (explore == Explore.METHODS) {
                Add.method(code.getText());
                listView.setItems(Get.methods());
                Alerts.information("Method added!");
            }
            if (explore == Explore.CONSTRUCTORS) {
                Add.constructor(code.getText());
                listView.setItems(Get.constructors());
                Alerts.information("Constructor added!");
            }
            if (explore == Explore.FIELDS) {
                Add.field(code.getText());
                listView.setItems(Get.fields());
                Alerts.information("Field added!");
            }
        } catch (CannotCompileException e) {
            Alerts.warning("Cannot compile: " + e.getReason());
        }
    }

    @FXML
    void addInterface() {
        if (packageExplorer.getSelectionModel().getSelectedItem() != null) {
            if (!path.endsWith(".class")) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setHeaderText("Enter interface name: ");
                dialog.showAndWait();
                if (!dialog.getEditor().getText().equals("")) {
                    Add.addInterface(path + "." + dialog.getEditor().getText());
                    packageExplorer.getSelectionModel().getSelectedItem().getChildren().add(new TreeItem<String>(dialog.getEditor().getText() + ".class"));
                    Alerts.information("Interface added!");
                } else
                    Alerts.warning("Name your interface!");
            } else
                Alerts.warning("Select a package!");
        } else
            Alerts.warning("Select a package!");
    }

    @FXML
    void addJython() {
        if(addJythonBtn.isSelected()){
            Add.setJython(true);
            try {
                Add.addJython();
                Alerts.information("Jython added!");
            } catch (NotFoundException e) {
                Alerts.warning(e.getMessage());
            } catch (CannotCompileException e) {
                Alerts.warning(e.getMessage());
            }
        } else{
            Add.setJython(false);
            Alerts.information("Jython disabled");
        }
    }

    @FXML
    void addPackage() {
        if (packageExplorer.getSelectionModel().getSelectedItem() != null) {
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Enter package name: ");
            td.showAndWait();
            if (!PackageBuilder.getPath().endsWith(".class")) {
                if (!td.getEditor().getText().equals("")) {
                    Add.addPackage(PackageBuilder.getPath() + "." + td.getEditor().getText());
                    packageExplorer.getSelectionModel().getSelectedItem().getChildren().add(new TreeItem<String>(td.getEditor().getText()));
                    Alerts.information("Package added!");
                } else
                    Alerts.warning("Name your package!");
            } else
                Alerts.warning("Select a package not a class!");
        } else
            Alerts.warning("Pick a package!");
    }

    @FXML
    void deleteClass() {
        if (packageExplorer.getSelectionModel().getSelectedItem() != null) {
            if (path.endsWith(".class")) {
                try {
                    Delete.deleteClass(path.replace(".class", ""));
                    TreeItem<String> deletedClass = packageExplorer.getSelectionModel().getSelectedItem();
                    deletedClass.getParent().getChildren().remove(deletedClass);
                    Alerts.information("Class deleted!");
                } catch (Exception e) {
                    Alerts.warning("Can only delete added classes!");
                }
            } else
                Alerts.warning("Pick a class not a package!");
        } else
            Alerts.warning("Pick a class!");
    }

    @FXML
    void deleteClick() {
        if (listView.getSelectionModel().getSelectedItem() != null) {
            try {
                if (explore == Explore.METHODS) {
                    Delete.method(listView.getSelectionModel().getSelectedItem());
                    listView.setItems(Get.methods());
                    Alerts.information("Method deleted!");
                }
                if (explore == Explore.CONSTRUCTORS) {
                    Delete.constructor(listView.getSelectionModel().getSelectedItem());
                    listView.setItems(Get.constructors());
                    Alerts.information("Constructor deleted!");
                }
                if (explore == Explore.FIELDS) {
                    Delete.field(listView.getSelectionModel().getSelectedItem());
                    listView.setItems(Get.fields());
                    Alerts.information("Field deleted!");
                }
            } catch (NotFoundException e) {
                Alerts.warning("Item not found!");
            }
        } else Alerts.warning("Pick what to delete!");
    }

    @FXML
    void deletePackage() {
        if (packageExplorer.getSelectionModel().getSelectedItem() != null) {
            if (!path.endsWith(".class")) {
                if(packageExplorer.getSelectionModel().getSelectedItem().getChildren().isEmpty()){
                    try {
                        Delete.deletePackage(path.replace(".", "\\"));
                        TreeItem<String> deletedPackage = packageExplorer.getSelectionModel().getSelectedItem();
                        deletedPackage.getParent().getChildren().remove(deletedPackage);
                        Alerts.information("Package added!");
                    } catch (Exception e) {
                        Alerts.warning("Can only delete added packages!");
                    }
                } else Alerts.warning("Package is not empty, delete children first!");
            } else
                Alerts.warning("Pick a package not a class!");
        } else Alerts.warning("Pick a package!");
    }

    @FXML
    void insertEndClick() {
        if (listView.getSelectionModel().getSelectedItem() != null) {
            try {
                Edit.insertEndMethod(listView.getSelectionModel().getSelectedItem(), code.getText());
                Alerts.information("Method edited!");
            } catch (CannotCompileException e) {
                Alerts.warning("Cannot compile: " + e.getReason());
            } catch (NotFoundException e) {
                Alerts.warning("Method not found!");
            }
        } else Alerts.warning("Pick a method to edit!");
    }

    @FXML
    void insertFrontClick() {
        if (listView.getSelectionModel().getSelectedItem() != null) {
            try {
                Edit.insertFrontMethod(listView.getSelectionModel().getSelectedItem(), code.getText());
                Alerts.information("Method edited!");
            } catch (CannotCompileException e) {
                Alerts.warning("Cannot compile: " + e.getReason());
            } catch (NotFoundException e) {
                Alerts.warning("Method not found!");
            }
        } else Alerts.warning("Pick a method to edit!");
    }

    @FXML
    void saveFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a place to save");
        File saveFile = fileChooser.showSaveDialog(setBodyBtn.getScene().getWindow());
        if (saveFile != null) {
            SaveTask saveTask = new SaveTask(openedFile, saveFile, progressBar);
            saveTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent t) {
                    Alerts.information("File saved!");
                    progressBar.setProgress(0.0);
                }
            });
            Thread th = new Thread(saveTask);
            th.setDaemon(true);
            th.start();
        }
    }

    @FXML
    void setBodyClick() {
        if (listView.getSelectionModel().getSelectedItem() != null) {
            try {
                if (explore == Explore.METHODS) {
                    Edit.method(listView.getSelectionModel().getSelectedItem(), code.getText());
                    Alerts.information("Method body edited!");
                }
                if (explore == Explore.CONSTRUCTORS) {
                    Edit.constructor(listView.getSelectionModel().getSelectedItem(), code.getText());
                    Alerts.information("Constructor body edited!");
                }
            } catch (CannotCompileException e) {
                Alerts.warning("Cannot compile: " + e.getReason());
            } catch (NotFoundException e) {
                Alerts.warning("Method not found!");
            }
        } else Alerts.warning("Pick an item to edit!");
    }

    @FXML
    void showConstructors() {
        explore = Explore.CONSTRUCTORS;
        listView.setItems(Get.constructors());
        insertFrontBtn.setDisable(true);
        insertEndBtn.setDisable(true);
        setBodyBtn.setDisable(false);
    }

    @FXML
    void showFields() {
        explore = Explore.FIELDS;
        listView.setItems(Get.fields());
        insertFrontBtn.setDisable(true);
        insertEndBtn.setDisable(true);
        setBodyBtn.setDisable(true);
    }

    @FXML
    void showMethods() {
        explore = Explore.METHODS;
        listView.setItems(Get.methods());
        setButtons(false);
    }

}