## IntelliJ IDEA Setup

To apply Google Java Style formatting in IntelliJ IDEA:

1. Open IntelliJ IDEA and navigate to:  
   `File` → `Settings/Preferences` →
   `Editor` → `Code Style` → `Java`

2. Click the gear icon (⚙) near the scheme dropdown →  
   `Import Scheme` → `IntelliJ IDEA code style XML`

3. Select the file `projectDir\config\ide\intelij-java-google-style.xml`.

4. Apply and save changes.

## VS Code Setup

To use Google Java Style formatting in VS Code:

1. Install one of the following formatter extensions:
   **Google Java Formatter (Liangcheng Juves)**
   or
   **Google Java Format for VS Code (Jose V Sebastian)**

2. Add the following configuration to your .vscode/settings.json file
   to enable automatic formatting on save using the installed formater:

   ```json
   {
    "editor.formatOnSave": true,
    "[java]": {
      "editor.defaultFormatter": "liangcheng.javes-google-java-formatter" 
    }
   }
   ```
   or
   ```json
   {
    "editor.formatOnSave": true,
    "[java]": {
      "editor.defaultFormatter": "liangcheng.javes-google-java-formatter"
    }
   }
   ```


3. Now, whenever you save Java files in VS Code, they will be automatically formatted 
   according to the Google Java Style via the chosen plugin.

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