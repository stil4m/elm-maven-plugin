# elm-maven-plugin

Maven plugin to build elm sources within a maven build.

## Repository

The plugin is hosted on my own maven repository (hosted on Github). To depend on the plugin you should add the following snippet to your `pom.xml`:

```
...
<pluginRepositories>
	<pluginRepository>
		<id>stil4m-releases</id>
		<name>stil4m-releases</name>
		<url>https://github.com/stil4m/maven-repository/raw/master/releases/</url>
	</pluginRepository>
</pluginRepositories>
...
```

## Example

An example configuration for the plugin would be as shown below.

```
<plugin>
    <groupId>nl.stil4m</groupId>
    <artifactId>elm-maven-plugin</artifactId>
    <version>2.0.0</version>
    <executions>
        <execution>
            <id>Make Elm Source</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>make</goal>
            </goals>
            <configuration>
                <inputFiles>src/App.elm</inputFiles>
                <outputFile>generated/main.js</outputFile>
                <executablePath>/path/to/elm-make</executablePath><!-- default value is /usr/local/bin/elm-make -->
            </configuration>
        </execution>
    </executions>
</plugin>

```
