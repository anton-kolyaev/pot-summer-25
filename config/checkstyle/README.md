## IntelliJ IDEA Setup

To apply Google Java Style formatting in IntelliJ IDEA:

1. Open IntelliJ IDEA and navigate to:  
   `File` → `Settings/Preferences` →
   `Editor` → `Code Style` → `Java`

2. Click the gear icon (⚙) near the scheme dropdown →  
   `Import Scheme` → `IntelliJ IDEA code style XML`

3. Select the file `projectDir\config\checkstyle\google_checks.xml`.

4. Apply and save changes.


## VS Code Setup

To use Google Java Style formatting in VS Code:

1. Ensure you have the **Java Extension Pack** installed.  
   This extension is **recommended automatically** when opening the project.

2. After installing the Java Extension Pack, code formatting with Checkstyle
   will apply automatically using the configuration in the project.

3. The project includes `.vscode/settings.json` configured to use the Google Eclipse style:

   ```json
   {
     "java.format.settings.url": "./ide/eclipse-java-google-style.xml",
     "java.format.settings.profile": "GoogleStyle",
     "editor.formatOnSave": true
   }
   ```   

## Running Checkstyle
Run Checkstyle on main source code:
`./gradlew checkstyleMain`

Run Checkstyle on test source code:
`./gradlew checkstyleTest`

Run all verification tasks, including Checkstyle:
`./gradlew check`

Checkstyle reports are generated at:
`build/reports/checkstyle/checkstyle.html`

## Important Note
After the first build, Checkstyle violations may not appear
in the reports until you run a clean build.

To ensure violations are properly detected, run:
`./gradlew clean`