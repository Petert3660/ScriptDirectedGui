package com.thehutgroup;

import com.thehutgroup.createdgui.TestGui;
import com.thehutgroup.guiScriptParser.GuiBuilder;
import com.thehutgroup.guiScriptParser.GuiScriptParser;
import com.thehutgroup.guicomponents.GuiProperties;
import com.thehutgroup.guis.GuiHelper;
import com.thehutgroup.statics.Statics;
import com.thehutgroup.utilities.FileUtilities;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.util.FileCopyUtils;

/**
 * Created by Peter Thomson on 13/04/2018.
 */
@SuppressWarnings("ALL")
@SpringBootApplication
public class Application implements CommandLineRunner {

    public static final String FINAL_GUI_DIR = "C:\\GradleTutorials\\ScriptDirectedGui\\GuiSourceFiles\\";
    public static final String RESOURCES_DIR = "C:\\GradleTutorials\\ScriptDirectedGui\\src\\main\\resources\\";
    public static final String FINAL_GUI_TARGET_DIR = "C:\\GradleTutorials\\ScriptDirectedGui\\src\\main\\java\\com\\thehutgroup\\createdgui\\";

    Scanner scanner = new Scanner(System.in);

    @Autowired
    private GuiProperties guiProperties;

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private static final String PROPS_FILENAME = "application";
    private static final String SERVER_PORT_PROPERTY = "server.port";

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .headless(false)
                .run(args);
    }

    @Override
    public void run(String... strings) throws Exception {

        final String GUI_SCRIPT_FILE = "guiScript";
        final String GUI_SCRIPT_FILE_COPY = "guiScriptLatest";

        System.out.println("strings[0] - " + strings[0]);
        System.out.println("strings[1] - " + strings[1]);

        //breakPoint("BP1");

        try {
            if (!strings[0].contains("testGui") && !StringUtils.isEmpty(strings[1])) {
                if (strings[0].contains("copy")) {
                    System.out.println("ScriptDirectedGui: Change Detected - Creating GUI Properties and Compiling!");
                    createGuiProperties(strings[1], strings[2]);
                    System.out.println("ScriptDirectedGui: Copying compiled GUI in to TestGui class!");
                    String src = RESOURCES_DIR + "TestGui.java.tmp";
                    String target = FINAL_GUI_TARGET_DIR + "TestGui.java";
                    FileCopyUtils.copy(new File(src), new File(target));
                    FileCopyUtils.copy(new File(target), new File(FINAL_GUI_DIR + strings[1] + "\\TestGui.java"));
                    FileUtils.deleteQuietly(new File(src));
                    if (!(new File(FINAL_GUI_DIR + strings[1] + "\\" + strings[2]).exists())) {
                        System.out.println("ScriptDirectedGui: ERROR - The file, " + strings[1] + "\\" + strings[2] + " does not exist in the GUI script source directory - exiting!");
                        System.exit(0);
                    }
                    copyGuiScript(strings[1] + "\\" + strings[2], GUI_SCRIPT_FILE_COPY);
                    //breakPoint("BP2");
                    System.exit(0);
                } else if (strings[0].contains("run")) {
                    System.out.println("ScriptDirectedGui: Testing GUI!");
                    testGui();
                    //breakPoint("BP3");
                }
            } else {
                testGui();
                breakPoint("BP4");
            }

            syncComponents();
            //breakPoint("BP5");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //breakPoint("BPException");
        }
    }

    private void breakPoint(String message) {
        int i = 0;
        System.out.println(message);
        while (i == 0) {
            System.out.print("Please enter a value - ");
            i = scanner.nextInt();
        }
    }

    private void syncComponents() throws IOException {

        //This method updates the template project with the latest versions of the Gui Components

        FileUtilities.copyAllFilesFromSrcDirToTargetDir(Statics.JAVA_PROJECTS_DIR + Statics.SCRIPT_DIRECTED__PROJECT + Statics.RELATIVE_PATH_FOR_COMPONENTS,
                Statics.JAVA_PROJECTS_DIR + Statics.TEMPLATE_PROJECT + Statics.RELATIVE_PATH_FOR_COMPONENTS);
    }

    private boolean guiScriptupdated(String srcFile, String targetFile) throws IOException {

        File file1 = new File(FINAL_GUI_DIR + srcFile);
        File file2 = new File(RESOURCES_DIR + targetFile);

        if (!file2.exists()) {
            RandomAccessFile fout = new RandomAccessFile(file2, "rw");
            fout.close();
        }

        return !FileUtils.contentEquals(file1, file2);
    }

    private void copyGuiScript(String src, String targ) throws IOException {

        File file1 = new File(FINAL_GUI_DIR + src);
        File file2 = new File (RESOURCES_DIR + targ);

        FileCopyUtils.copy(file1, file2);
    }

    private void testGui() {
        TestGui tmg = new TestGui();
        GuiHelper gh = new GuiHelper(guiProperties);
        gh.showFrame(tmg);
    }

    private void createGuiProperties(String projectName, String scriptName) {

        System.out.println("ScriptDirectedGui: This method will run at startup and create the appropriate GUI properties from the input script");
        GuiScriptParser gsp = new GuiScriptParser(guiProperties);
        gsp.readInputScript(projectName, scriptName);
        GuiBuilder gb = new GuiBuilder(guiProperties);
        gb.buildGuiClass();
    }
}