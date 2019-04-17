package app.other;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

//klasa ktora buduje nam drzewo pakietow po otwarciu jara
public class PackageBuilder {
    private static TreeView<String> packageExplorer;

    public static void setPackageExplorer(TreeView<String> packageExplorer) {
        PackageBuilder.packageExplorer = packageExplorer;
    }

    //tworzy nam sciezke do kliknietej rzeczy na na exploratorze pakietow polaczoną kropkami
    public static String getPath(){
        StringBuilder pathBuilder = new StringBuilder();
        if(packageExplorer.getSelectionModel().getSelectedItem() != null) {
            for (TreeItem<String> item = packageExplorer.getSelectionModel().getSelectedItem();
                 item != null && !item.getValue().equals("JAR"); item = item.getParent()) {
                if(item == packageExplorer.getRoot()) break;
                pathBuilder.insert(0, item.getValue());
                pathBuilder.insert(0, ".");
            }
            return pathBuilder.toString().substring(1);
        } return "";
    }

    public static void treeViewFromFile(File file) {
        TreeItem<String> root = new TreeItem<String>(file.getName());
        packageExplorer.setRoot(root);
        packageExplorer.setShowRoot(false);
        root.setExpanded(true);

        try {
            buildTreeView(root, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void buildTreeView(TreeItem<String> root, File file) throws IOException {
        List<String> paths = new ArrayList<String>();
        JarInputStream jarInputStream = new JarInputStream(new FileInputStream(file.getPath()));
        JarEntry entry = jarInputStream.getNextJarEntry();
        while (entry != null) {
            if (entry.getName().endsWith(".class")) {
                paths.add(entry.getName());
            }
            entry = jarInputStream.getNextJarEntry();
        }
        for (String path : paths) {
            TreeItem<String> current = root;
            for (String component : path.split("/")) {
                current = getOrCreateChild(current, component);
            }
        }
    }

    private static TreeItem<String> getOrCreateChild(TreeItem<String> parent, String value) {
        TreeItem<String> newChild;
        for (TreeItem<String> child : parent.getChildren()) {
            if (value.equals(child.getValue())) {
                return child;
            }
        }
        newChild = new TreeItem<String>(value);
        parent.getChildren().add(newChild);
        return newChild;
    }
}
