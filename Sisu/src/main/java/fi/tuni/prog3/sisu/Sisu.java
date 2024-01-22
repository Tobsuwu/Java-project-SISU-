package fi.tuni.prog3.sisu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * JavaFX Sisu
 * Reads the course data from sisu API, asks a new student to create account and
 * choose a degree program, or an existing student to log in, and creates and
 * updates the ui for the user student's course data.
 * @author antti
 */
public class Sisu extends Application {
    private final int WIDTH = 1300;
    private final int HEIGHT = 700;

    private static ArrayList<Student> students = new ArrayList<>();
    private static TreeSet<DegreeProgram> degreePrograms;
    static DegreeProgram userDp;
    private static ReadAPI r = new ReadAPI();
    private static ReadAndWriteStudentData studentReaderAndWriter = 
            new ReadAndWriteStudentData();
    private String logoPath = "Resources" + File.separator + "Logo.png";
    private String eikuPath = "Resources" + File.separator + "eiku.jpg";
    
    // Common nodes for the UI
    Stage uiStage;
    Scene startScene;
    Scene mainScene;
    Student activeStudent;
    ImageView logoView;
    ImageView eikuView;
    Font emphFont = Font.font("Monospace", FontWeight.BOLD, FontPosture.REGULAR,
            16);
    
    // Start scene nodes:
    GridPane startGrid = new GridPane();
    Label nameLbl = new Label("Name (Firstname Lastname):");
    Label studentIdLbl = new Label("Student id:");
    Label startError = new Label();
    TextField nameField = new TextField();
    TextField studentIdField = new TextField();
    Button signInButton = new Button("Sign in/create account");
    
    // Main scene nodes:
    TabPane tPane = new TabPane();
    Tab infoTab = new Tab("Degree programs");
    Tab structureTab = new Tab("Program structure");
    
    // DegreeProgram tab:
    VBox dpBox = new VBox();
    ScrollPane programScroll = new ScrollPane();
    VBox programBox = new VBox();
    Button confirmProgram = new Button("Confirm selection");
    ToggleGroup programGroup = new ToggleGroup();
    Label selectProgramLbl = new Label("Select degree program:");
    Label infoError = new Label();
    
    // Structure tab:
    GridPane structureGrid = new GridPane();
    Button saveAndExit = new Button("Save and exit");
    Label saveErrorLbl = new Label();
    // Structure field
    VBox structureBox = new VBox();
    ScrollPane structureScroll = new ScrollPane();
    TitledPane dpPane = new TitledPane();
    Accordion smAccord = new Accordion();
    // Selection field
    VBox selectionBox = new VBox();
    ScrollPane selectionScroll = new ScrollPane();
    Button selectionConfirm = new Button("Confirm selection");
    ArrayList<CheckBox> selectionList = new ArrayList<>();
    String openedPaneName;
    
    
    @Override
    public void start(Stage stage) {
        uiStage = stage;
        
        // Get the images
        try{
            FileInputStream logoStream = new FileInputStream(logoPath);
            Image logo = new Image(logoStream);
            logoView = new ImageView(logo);
            logoView.setFitHeight(200);
            logoView.setPreserveRatio(true);
            
            FileInputStream eikuStream = new FileInputStream(eikuPath);
            Image eiku = new Image(eikuStream);
            eikuView = new ImageView(eiku);
            eikuView.setFitHeight(30);
            eikuView.setPreserveRatio(true);
        } catch (FileNotFoundException e) {
            System.out.println("IMAGE NOT FOUND");
        }
        
        // Start the program
        stage.setTitle("SISU");
        startScene = setStartScene();
        stage.setScene(startScene);
        
        startScene.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                signStudentIn();
            }
        });
        
        signInButton.setOnAction((event) -> {
            signStudentIn();
        });

        confirmProgram.setOnAction((event) -> {
            confirmProgramSelection();
        });
        
        dpPane.setOnMouseClicked((event) -> {
            dpPane.setExpanded(true);
            openSelection(dpPane);
        });
        
        selectionConfirm.setOnAction((event) -> {
            updateStudentData();
            updateStructure();
        });
        
        saveAndExit.setOnAction((event) -> {
            try {
                for (StudyModule sm : activeStudent.getDegreeProgram()
                        .getModules()) {
                    if (sm.getCourses().isEmpty()) {
                        throw new IOException();
                    }
                }
                studentReaderAndWriter.writeToFile(activeStudent);
                stage.close();
            } catch (IOException e) {
                saveErrorLbl.setText("Please select courses for all study "
                        + "modules");
                saveErrorLbl.setTextFill(Color.RED);
            }
        });
        
        stage.show();
    }
    
    /**
     * Sets up the start scene where a student inserts their personal
     * information and signs in.
     * @return The start scene.
     */
    public Scene setStartScene() {
        startScene = new Scene(startGrid, WIDTH-600, HEIGHT-500);
        startGrid.add(nameLbl, 1, 0);
        startGrid.add(studentIdLbl, 1, 1);
        nameField.setPrefWidth(150);
        studentIdField.setPrefWidth(150);
        startGrid.add(nameField, 2, 0);
        startGrid.add(studentIdField, 2, 1);
        startGrid.add(startError, 1, 3, 2, 1);
        startGrid.add(logoView, 0, 0, 1, 4);
        startGrid.add(signInButton, 1, 2);
        
        return startScene;
    }
    
    /**
     * Sets up the student view for user student in mainScene. Returns the
     * scene.
     * @return The scene with student's course information.
     */
    public Scene setStudentView() {
        mainScene = new Scene(tPane, WIDTH, HEIGHT);
        tPane.getTabs().add(infoTab);
        tPane.getTabs().add(structureTab);
        infoTab.setContent(dpBox);
        structureTab.setContent(structureGrid);
        
        // Build degree program view
        for (DegreeProgram dp : degreePrograms) {
            RadioButton newRb = new RadioButton(dp.getName());
            newRb.setToggleGroup(programGroup);
            newRb.setUserData(dp);
            newRb.setPrefWidth(WIDTH);
            programBox.getChildren().add(newRb);
        }
        programScroll.setPrefSize(WIDTH-50, HEIGHT-100);
        programScroll.setContent(programBox);
        selectProgramLbl.setFont(emphFont);
        dpBox.getChildren().add(selectProgramLbl);
        dpBox.getChildren().add(programScroll);
        dpBox.getChildren().add(confirmProgram);
        dpBox.getChildren().add(infoError);
        dpBox.setPrefSize(WIDTH, HEIGHT);
        
        // Build structure view
        structureBox.setPrefSize(0.6*WIDTH, HEIGHT);
        structureScroll.setContent(structureBox);
        selectionBox.setPrefSize(0.4*WIDTH, HEIGHT);
        selectionBox.setBackground(new Background(new BackgroundFill(
                                                      Color.PURPLE,
                                                      CornerRadii.EMPTY,
                                                      Insets.EMPTY)));
        selectionScroll.setContent(selectionBox);
        
        structureGrid.add(structureScroll, 0, 0, 2, 1);
        structureGrid.add(selectionScroll, 2, 0);
        structureGrid.add(saveAndExit, 0, 1);
        structureGrid.add(saveErrorLbl, 1, 1);
        return mainScene;
    }
    
    /**
     * Tries to sign student in and open the student view. Shows error messages
     * if signing in is not successful.
     */
    public void signStudentIn() {
        startError.setText("");
        
        // Check that the inputs are valid
        if (studentIdField.getText().equals("") ||
                nameField.getText().equals("") ||
                nameField.getText().split(" ").length != 2) {
            startError.setText("Invalid input");
            startError.setTextFill(Color.RED);
            return;
        }

        // Find student if they already exist
        if (students.isEmpty() == false) {
            for (Student s : students) {
                if (s.getStudentNumber()
                    .equals(studentIdField.getText())) {
                    if (!s.getFirstName()
                            .equals(nameField.getText().split(" ")[0]) || 
                            !s.getLastName()
                                .equals(nameField.getText().split(" ")[1])) {
                        startError.setText("Name and student number do not "
                                + "match");
                        startError.setTextFill(Color.RED);
                        return;
                    }
                    
                    // Sign in as an existing user
                    activeStudent = s;
                    for (var dp : degreePrograms) {
                        if (dp.getName().equals(activeStudent.getDegreeProgram()
                                .getName())) {
                            try {
                                userDp = r.parseModules(dp);
                            } catch (MalformedURLException ex) {
                            }
                        }
                    }
                    mainScene = setStudentView();
                    uiStage.setScene(mainScene);
                    initializeStructure();
                    return;
                }
            }
        }
        
        // Sign in as a new student
        activeStudent = new Student(nameField.getText(),
                                    studentIdField.getText());
        mainScene = setStudentView();
        uiStage.setScene(mainScene);
    }
    
    
    /**
     * Confirm the student's program direction and assign it for the student.
     * Calls function initializeStructure to create initial program structure.
     */
    public void confirmProgramSelection() {
        if (programGroup.getSelectedToggle() != null) {
            infoError.setText("");
            try {
                userDp = r.parseModules((DegreeProgram) programGroup
                        .getSelectedToggle().getUserData());
            } catch (MalformedURLException ex) {
            }

            activeStudent.setDegreeProgram(new DegreeProgram(userDp));
            initializeStructure();
        } else {
            infoError.setText("Pick a study program");
            infoError.setTextFill(Color.RED);
        }
    }
    
    /**
     * Initializes the view of the student's course information in ui structure 
     * tab.
     */
    public void initializeStructure() {
        tPane.getSelectionModel().select(structureTab);
        structureBox.getChildren().clear();
        selectionBox.getChildren().clear();

        smAccord = new Accordion();
        updateStructure();
        
        dpPane.setFont(emphFont);
        dpPane.setContent(smAccord);
        dpPane.setExpanded(false);
        structureBox.getChildren().add(dpPane);
    }
    
    /**
     * Updates the student's course information in ui structure tab based on
     * the information in activeStudent object. This function should be called
     * after making any modifications to the student's course data.
     */
    public void updateStructure() {
        saveErrorLbl.setText("");
        dpPane.setText(String.format("%s (credits %d/%d)", userDp.getName(),
                activeStudent.getTotalCredits(), userDp.getMinCredits()));
        smAccord.getPanes().clear();
        // Update StudyModules
        for (StudyModule sm : activeStudent.getDegreeProgram().getModules()) {
            TitledPane smPane = new TitledPane();
            smPane.setText(sm.getName());
            smPane.setContent(new Label());
            smAccord.getPanes().add(smPane);

            VBox smCourses = new VBox();
            // Update Courses
            for (Course c : sm.getCourses()) {
                Button courseButton = new Button(String.format(
                        "%s (credits: %d)", c.getName(), c.getMinCredits()));
                if (activeStudent.getAttainment(c.getId()) != null) {
                    courseButton.setText(String
                            .format("%s (credits: %d, Grade: %d)",
                                c.name, c.getMinCredits(), activeStudent
                                    .getAttainment(c.getId()).getGrade()));
                    if (activeStudent.getAttainment(c.getId()).getGrade() > 0) {
                        courseButton.setTextFill(Color.GREEN);
                    } else {
                        courseButton.setTextFill(Color.RED);
                    }
                }
                smCourses.getChildren().add(courseButton);
                courseButton.setOnAction((event) -> {
                    openCourseAttainment(c);
                });
                
            }
            smPane.setContent(smCourses);
        }
        // Event handlers for all of the newly added studymodule panes
        for (TitledPane smPane : smAccord.getPanes()) {
            smPane.setOnMouseClicked((event) -> {
                openSelection(smPane);
            });
        }

    }
    
    /**
     * Opens the studymodule or course selection boxes in the selection field.
     * @param tp The TitledPane the user chose to open for selection.
     */
    public void openSelection(TitledPane tp) {
        selectionBox.getChildren().clear();
        selectionList.clear();
        openedPaneName = tp.getText();
        
        for (StudyModule sm : userDp.getModules()) {
            // Check if tp is a DegreeProgram, then add StudyModules under it
            if (tp.getContent() instanceof Accordion) {
                CheckBox smBox = new CheckBox(sm.getName());
                smBox.setTextFill(Color.LIGHTGRAY);
                if (activeStudent.getDegreeProgram().getModules().contains(sm)){
                    smBox.setSelected(true);
                }
                smBox.setUserData(sm);
                selectionList.add(smBox);
                selectionBox.getChildren().add(smBox);
            }
            // Else tp is a StudyModule, then add Courses under it
            else {
                if (sm.getName().equals(openedPaneName)) {
                    for (Course c : userDp.getModule(openedPaneName)
                            .getCourses()) {
                        CheckBox cBox = new CheckBox(c.getName());
                        cBox.setTextFill(Color.LIGHTGRAY);
                        if (activeStudent.getDegreeProgram()
                                         .getModule(openedPaneName)
                                         .getCourses().contains(c)) {
                            cBox.setSelected(true);
                        }
                        cBox.setUserData(c);
                        selectionList.add(cBox);
                        selectionBox.getChildren().add(cBox);
                    }
                }
            }
        }
        
        selectionBox.getChildren().add(selectionConfirm);
    }
    
    
    /**
     * Updates the data for activeStudent based on user's decisions.
     */
    public void updateStudentData() {
        // Update StudyModules
        if (selectionList.get(0).getUserData() instanceof StudyModule) {
            TreeSet<StudyModule> previousSms = activeStudent.getDegreeProgram()
                    .getModules();
            TreeSet<StudyModule> newSms = new TreeSet<>();
            for (CheckBox entryBox : selectionList) {
                var entry = entryBox.getUserData();
                if (entryBox.isSelected()) {
                    newSms.add(new StudyModule((StudyModule) entry));
                }
            }
            
            // Remove the unselected StudyModules and add the new selected ones.
            List<String> newIds = newSms.stream().map((sm) -> sm.getGroupId())
                    .collect(Collectors.toList());
            List<String> prevIds = previousSms.stream()
                    .map((sm) -> sm.getGroupId())
                    .collect(Collectors.toList());
            
            TreeSet<StudyModule> addedSms = newSms
                    .stream().filter((sm) -> !prevIds.contains(sm.getGroupId()))
                    .collect(Collectors.toCollection(TreeSet::new));
            List<String> removedIds = prevIds.stream()
                    .filter((id) -> !newIds.contains(id))
                    .collect(Collectors.toList());
            
            activeStudent.getDegreeProgram().addModules(addedSms);
            activeStudent.getDegreeProgram().removeModules(removedIds);
        }
        // Update Courses
        else {
            StudyModule updatedSm = activeStudent.getDegreeProgram()
                                                 .getModule(openedPaneName);
            updatedSm.getCourses().clear();
            for (CheckBox entryBox : selectionList) {
                var entry = entryBox.getUserData();
                if (entryBox.isSelected()) {
                    updatedSm.addCourse((Course) entry);
                }
            }
        }
    }
    
    /**
     * Opens a window where the user can see the learning outcomes of course c 
     * and assign a grade they got on that course.
     * @param c The course which information is shown.
     */
    public void openCourseAttainment(Course c) {
        // Course attainment scene elements:
        GridPane courseGrid = new GridPane();
        VBox attBox = new VBox();
        Spinner<Integer> gradeSpinner = new Spinner<>(0, 5, 0, 1);
        Button confirmAttainment = new Button("Confirm");
        Button eikuAttainment = new Button();
        eikuAttainment.setGraphic(eikuView);
        VBox descriptionBox = new VBox();
        Label descriptionLbl = new Label();
        descriptionLbl.setText(c.getOutcomes());
        descriptionLbl.setWrapText(true);
        Label courseHeader = new Label();
        courseHeader.setText(c.getName());
        courseHeader.setFont(emphFont);
        
        attBox.prefWidthProperty().bind(courseGrid.widthProperty()
                .multiply(0.20));
        attBox.getChildren().add(new Label("Your grade:"));
        attBox.getChildren().add(gradeSpinner);
        attBox.getChildren().add(confirmAttainment);
        attBox.getChildren().add(eikuAttainment);
        
        descriptionBox.prefWidthProperty().bind(courseGrid.widthProperty()
                .multiply(0.80));
        descriptionBox.getChildren().add(new Label("Learning outcomes:"));
        descriptionBox.getChildren().add(descriptionLbl);
        
        courseGrid.add(courseHeader, 0, 0, 2, 1);
        courseGrid.add(attBox, 0, 1);
        courseGrid.add(descriptionBox, 1, 1);
        
        Scene attScene = new Scene(courseGrid, WIDTH, HEIGHT-200);
        uiStage.setScene(attScene);
        
        confirmAttainment.setOnAction((event) -> {
            if (activeStudent.getAttainment(c.getId()) == null) {
                Student.Attainment a = activeStudent.new Attainment(gradeSpinner
                                                                .getValue(), c);
                activeStudent.addAttainment(a);
            } else {
                activeStudent.getAttainment(c.getId()).setGrade(gradeSpinner
                        .getValue());
            }
            uiStage.setScene(mainScene);
            updateStructure();
        });
        
        eikuAttainment.setOnAction((event) -> {
            uiStage.setScene(mainScene);
        });
    }

    public static void main(String[] args) throws IOException {
        // Read data
        degreePrograms = r.parseDegreePrograms();
        students = studentReaderAndWriter.readFromFile();
        
        launch();
    }
}