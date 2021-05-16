package com.stream_pi.theme_api;


import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;
import com.stream_pi.util.version.Version;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Themes {
    private final List<Theme> themeList;
    private final ArrayList<MinorException> errors;
    private boolean isBadThemeTheCurrentOne = false;

    Logger logger = Logger.getLogger(Themes.class.getName());

    private String themePath;
    private String defaultThemeName;
    private Version minThemeSupportPlatform;

    private boolean isDefaultThemePresent = false;

    public Themes(String themePath, String defaultThemeName, Version minThemeSupportPlatform) throws SevereException {
        this.themePath = themePath;
        this.defaultThemeName = defaultThemeName;
        this.minThemeSupportPlatform = minThemeSupportPlatform;

        themeList = new ArrayList<>();
        errors = new ArrayList<>();

        loadThemes();
    }

    public boolean isDefaultThemePresent()
    {
        return isDefaultThemePresent;
    }

    public void loadThemes() throws SevereException {
        File themeFolder =  new File(themePath);
        if(!themeFolder.isDirectory())
        {
            throw new SevereException("Themes","Theme folder doesn't exist! Cant continue.");
        }


        File[] themeFolders = themeFolder.listFiles();
        if(themeFolders == null)
        {
            throw new SevereException("Themes","themefolders returned null. Cant continue!");
        }

        for(File eachFolder : themeFolders)
        {
            if(eachFolder.isDirectory())
            {
                File themeXML = new File(eachFolder.getAbsolutePath()+"/theme.xml");
                if(themeXML.exists() && themeXML.isFile())
                {
                    try {
                        Theme t = new Theme(eachFolder);


                        if (minThemeSupportPlatform.isBiggerThan(t.getThemePlatformVersion()))
                        {
                            throw new MinorException("Theme version doesn't match minimum theme support level ("+ minThemeSupportPlatform + ") ("+t.getFullName()+")");
                        }

                        if(!isDefaultThemePresent())
                        {
                            if(t.getFullName().equals(defaultThemeName))
                            {
                                isDefaultThemePresent = true;
                            }
                        }


                        themeList.add(t);
                        logger.info("Added "+eachFolder.getName()+" to themes");
                    } catch (MinorException e) {
                        logger.severe("Error adding "+eachFolder.getName()+" to themes");

                        if(eachFolder.getName().equals(defaultThemeName))
                            setBadThemeCurrentOne();

                        errors.add(e);

                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public boolean getIsBadThemeTheCurrentOne()
    {
        return isBadThemeTheCurrentOne;
    }

    private void setBadThemeCurrentOne()
    {
        isBadThemeTheCurrentOne = true;
    }

    public ArrayList<MinorException> getErrors()
    {
        return errors;
    }

    public void refreshThemeList() throws SevereException {
        logger.info("Refreshing themes ...");
        loadThemes();
        logger.info("... Done!");
    }

    public List<Theme> getThemeList()
    {
        return themeList;
    }
}
