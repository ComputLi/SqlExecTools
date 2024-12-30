Environment Requirements: Java 11
Usage: java -jar SqlExecTools-1.0-SNAPSHOT-jar-with-dependencies.jar
Notes:
SQL input must end with a semicolon (;).CopyCopy
To execute an SQL script, use @fileName. The current version uses semicolons as SQL terminators; therefore, it does not support functions and stored procedures, only basic CRUD operations.CopyCopy
Parameter files are located within the JAR package. By default, it loads the JDBC package from the current path, supporting Oracle, MySQL, DB2, and DM databases. If you need to add other databases, you must modify the configuration file.
Steps:
1. Extract the configuration file: jar xf SqlExecTools.jar Configuration.json (If Configuration.json exists in the current path, it will use the external JSON.)
2. Configure the desired databases and the JAR package path in the JSON file. After making changes, save and exit.
3. When adding a database, the wildcard in the URL should correspond to the prompt names and quantities in parameterName to avoid confusion in the interface.Copy
4. jarPath is the storage path for the JDBC package; by default, it is empty, which means the current JAR execution path. Note that in Windows paths, you need to use double backslashes (\\).CopyCopy
5. If multiple JAR packages need to be added for the database, the value for jarPackage should be ["xxxx1.jar", "xxxx2.jar"].
