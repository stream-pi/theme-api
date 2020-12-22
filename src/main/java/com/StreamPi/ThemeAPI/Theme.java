/*
Theme.java

Contributor(s): Debayan Sutradhar (@dubbadhar)

Check 'Theme Standard.md' if you want to understand the hierarchy.
This reads a theme folder.
 */


package com.StreamPi.ThemeAPI;


import com.StreamPi.Util.Exception.MinorException;
import com.StreamPi.Util.Version.Version;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;


public class Theme {

    private String fullName, shortName, author, website;
    private Version version, themePlatformVersion;
    private final File path;
    private final XMLConfiguration config;


    public Theme(File path) throws MinorException {
        this.path = path;

        if(!path.isDirectory())
        {
            throw new MinorException("Theme", "Theme path "+path.getName()+" is not a folder.");
        }

        File themeFile = new File(path.getAbsolutePath() + "/theme.xml");
        if(!themeFile.isFile())
        {
            throw new MinorException("Theme", "Theme folder "+path.getName()+" has no theme.xml");
        }


        Configurations configs = new Configurations();
        FileBasedConfigurationBuilder<XMLConfiguration> builder = configs.xmlBuilder(themeFile);

        try {
            config = builder.getConfiguration();
        } catch (ConfigurationException e) {
            e.printStackTrace();
            throw new MinorException("Theme", "ConfigurationException occurred for theme folder "+path.getName());
        }

        loadUpThemeXMLContents();
    }

    private List<String> stylesheets;
    private List<String> fonts;

    public Version getThemePlatformVersion()
    {
        return themePlatformVersion;
    }


    private void loadUpThemeXMLContents() throws MinorException
    {
        //info
        fullName = path.getName();

        try
        {
            themePlatformVersion = new Version(config.getString("theme-platform-version"));
        }
        catch (MinorException e)
        {
            throw new MinorException("Invalid theme-platform-version ("+fullName+")");
        }

        shortName = config.getString("info.short-name", "Unknown");
        author = config.getString("info.author", "Unknown");
        website = config.getString("info.website", "Unknown");

        try
        {
            version = new Version(config.getString("info.version"));
        }
        catch (MinorException e)
        {
            throw new MinorException("Invalid theme-version ("+fullName+")");
        }

        //theme

        stylesheets = config.getList(String.class, "theme.stylesheets.stylesheet");

        fonts = config.getList(String.class, "theme.fonts.font");


        if(stylesheets == null)
        {
            throw new MinorException("No stylesheets found. At least one required. ("+fullName+")");
        }

        for (int i=0;i<stylesheets.size(); i++)
        {
            stylesheets.set(i, Paths.get(path.getAbsolutePath() + "/" + stylesheets.get(i)).toUri().toString());
        }

        if(fonts!=null)
        {
            for (int i=0;i<fonts.size(); i++)
            {
                fonts.set(i, Paths.get( path.getAbsolutePath() + "/" + fonts.get(i)).toUri().toString());
            }
        }
    }

    public List<String> getStylesheets()
    {
        return stylesheets;
    }

    public List<String> getFonts()
    {
        return fonts;
    }

    public String getFullName()
    {
        return fullName;
    }

    public String getShortName()
    {
        return shortName;
    }

    public String getAuthor()
    {
        return author;
    }

    public String getWebsite()
    {
        return website;
    }

    public Version getVersion()
    {
        return version;
    }
}
