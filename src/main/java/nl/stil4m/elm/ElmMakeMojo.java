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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mojo(name = "make")
public class ElmMakeMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "make.elmMakeExecutable", defaultValue = "/usr/local/bin/elm-make")
    private String executablePath;

    @Parameter(property = "make.inputFiles")
    private String[] inputFiles;

    @Parameter(property = "make.outputFile")
    private String outputFile;

    public void execute() throws MojoExecutionException {
        List<File> inputFs = Stream.of(inputFiles).map(f -> {
            File file = new File(f);
            if (!file.isAbsolute()) {
                return new File(project.getBasedir(), f);
            } else {
                return file;
            }
        }).collect(Collectors.toList());


        File outputF = new File(outputFile);
        if (!outputF.isAbsolute()) {
            outputF = new File(project.getBasedir(), outputFile);
        }

        inputFs.forEach(inputF -> {
            if (!inputF.exists()) {
                throw new IllegalArgumentException("Input file '" + inputF.getAbsolutePath() + "' does not exist");
            }
        });
        try {
            Files.createDirectories(outputF.getParentFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<String> commands = new ArrayList<>();
        commands.add(executablePath);
        commands.add("--yes");
        commands.addAll(inputFs.stream().map(File::getAbsolutePath).collect(Collectors.toList()));
        commands.add("--output");
        commands.add(outputF.getAbsolutePath());

        getLog().info("Executing elm-make command: '" + Arrays.toString(commands.toArray()) + "'");

        try {
            Process process = new ProcessBuilder(commands)
                    .directory(project.getBasedir())
                    .redirectErrorStream(true)
                    .start();

            BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = inputStream.readLine()) != null) {
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
