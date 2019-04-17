package app.save;

import app.javassist.Add;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

import java.io.File;

//odpalany przez nas watek ktory zapisuje zeedytowany program
//Task to klasa JavaFXowa ktora obsluguje wspolbieznosc
public class SaveTask extends Task<Void> {

    private File openedFile;
    private File saveFile;
    private ProgressBar progressBar;

    public SaveTask(File openedFile, File saveFile, ProgressBar progressBar) {
        this.openedFile = openedFile;
        this.saveFile = saveFile;
        this.progressBar = progressBar;
    }

    @Override
    public Void call() throws Exception {
        progressBar.setProgress(0.1);
        FileActions.unpack(openedFile, saveFile); //wypakowuje jara do jakiegos folderu
        progressBar.setProgress(0.40);
        FileActions.saveClasses(openedFile, saveFile); //CtClass.writeFile("sciezka gdzie zapisac");
        progressBar.setProgress(0.50);
        FileActions.saveAddedClasses(saveFile); //iterowanie po liscie addedClasses i zapisywanie ich
        if (Add.isJython()) {
            FileActions.addJython(saveFile);
        }
        progressBar.setProgress(0.55);
        FileActions.createPackages(saveFile); //iterowanie po liscie addedPackages i zapisywanie ich
        progressBar.setProgress(0.65);
        FileActions.pack(saveFile); //pakujesz to do jara
        progressBar.setProgress(0.80);
        FileActions.delete(saveFile); //usuniecie folderu tymczasowego
        progressBar.setProgress(1.0);
        return null;
    }


}

