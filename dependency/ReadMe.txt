In order to package the plugin you need to add Aspose.Words library to the local Maven repository.

Use the following command:

mvn install:install-file -Dfile=i18-js-generator-maven-plugin-1.0.jar -DgroupId=ru.mail.jira.plugins -DartifactId=i18-js-generator-maven-plugin -Dversion=1.0 -Dpackaging=maven-plugin