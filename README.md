# Programming 3 Sisu Unraveled

- The program is supposed to be run on Linux. Windows also works, but ä, ö and å do not show properly. 
- The working directory where the program should be run is group2491/Sisu (otherwise the images can't be found)
- The program is run through Sisu.java, located in Sisu/src/main/java/fi/tuni/prog3/sisu/Sisu.java
- Folder documents contains class diagram, työnjako.txt and apidocs (javadocs).
- Folder Sisu/Resources contains images Logo.png and eiku.jpg used by the program
- Folder Sisu/studentData stores the Student data saved by the program. One example student Teemu Teekkari is in the repo.

# User instructions:
Creating an account:
- Create account by entering your first name, last name, and student id, and confirm by pressing either enter or "sign in/create account" button.
- Select degree program from the Degree programs tab and confirm selection. (Takes you to Program structure tab)
- Click on the degree program name to open selection boxes on the right hand side. Make your selections and confirm them.
- Study modules appear, click on them and add or remove courses similarly, from the right hand side panel.
- Courses are shown as buttons under the study modules, click on them to open a window where you can see the learning outcomes of the course, and create an attainment by entering your grade.
- Attainments can be edited, but not removed.
- You can see your total credits and required credits after the name of the degree program.
- Once you have selected the study modules and courses you want, (no empty study modules) click save and exit.

Signing in as an existing student:
- Sign in with your name and student id. (Name and id should match to the saved data)
- Program structure tab opens with your previously saved course and module data.
- Edit your study modules, courses, and attainments like instructed before.
- Once finished, save and exit.
 
# List of additional features:

- Student can save their progress by clicking save and exit button. They can continue where they left of by login in with their name and student number. The data is stored in the folder studentData in Json-form.
- Choosing a course displays a window with learning outcomes, before selecting it as an Attainment.
- Student has Attainments that can be added by selecting a course. Attainments can be given grades and shown in different StudyModules.
- "EIKU"-button implemented if Student does not want to add the course and SISU-logo in the login window implemented.
