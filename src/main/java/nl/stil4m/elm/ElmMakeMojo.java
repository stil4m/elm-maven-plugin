package nl.stil4m.elm;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;

@Mojo(name = "make")
public class ElmMakeMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "make.elmMakeExecutable", defaultValue = "/usr/local/bin/elm-make")
    private String executablePath;

    @Parameter(property = "make.inputFile")
    private String inputFile;

    @Parameter(property = "make.outputFile")
    private String outputFile;

    public void execute() throws MojoExecutionException {
        File inputF = new File(inputFile);
        if (!inputF.isAbsolute()) {
            inputF = new File(project.getBasedir(), inputFile);
        }

        File outputF = new File(outputFile);
        if (!outputF.isAbsolute()) {
            outputF = new File(project.getBasedir(), outputFile);
        }

        if (!inputF.exists()) {
            throw new IllegalArgumentException("Input file '" + inputF.getAbsolutePath() + "' does not exist");
        }
        try {
            Files.createDirectories(outputF.getParentFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String command = executablePath +
                " --yes " +
                inputF.getAbsolutePath() +
                " --output " +
                outputF.getAbsolutePath();
        getLog().info("Executing elm-make command: '" + command + "'");

        try {
            Runtime rt = Runtime.getRuntime();
            Process process = new ProcessBuilder(command)
                    .directory(project.getBasedir())
                    .redirectErrorStream(true)
                    .start();

            BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while((line = inputStream.readLine()) != null){
                getLog().info("OUTPUT: " + line);
            }

            int exitValue = process.waitFor();
            if (exitValue != 0) {
                throw new IllegalArgumentException("Elm make did not finish with 0 status code. Instead: " + exitValue);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
