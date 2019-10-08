package com.extjs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * JS压缩合并器
 *
 * @author liuzhiyi
 */
public class JsBuilder {

    private static Logger logger = LoggerFactory.getLogger(JsBuilder.class);

    private static ArrayList<File> outputFiles = new ArrayList<>();

    private static String homeDir;
    private static String projectFile;
    private static String debugSuffix = "-debug";

    private static JSONObject projCfg;
    private static JSONArray pkgs;

    private static Boolean verbose = Boolean.FALSE;

    private static File deployDir;

    private static String projectHome;

    public static void build(String homeDir2, String projectFile2) {
        homeDir = homeDir2;
        projectFile = projectFile2;
        if (homeDir == "") {
            logger.error(
                    "The --homeDir or -d argument is required and was not included in the commandline arguments.");
        }
        if (projectFile == "") {
            logger.error(
                    "The --projectFile or -p argument is required and was not included in the commandline arguments.");
        }
        if (homeDir == "" || projectFile == "") {
            return;
        }
        openProjectFile(projectFile);
        loadPackages();
        mkDeployDir();
        createTargetsWithFileIncludes();
        createTargetsWithDeps();
        copyResources();
        compressOutputFiles();
    }

    private static void openProjectFile(String projectFileName) {
        try {
            File inputFile = new File(projectFileName);
            projectHome = inputFile.getParent();

            /* read the file into a string */
            String s = FileHelper.readFileToString(inputFile);

            /* create json obj from string */
            projCfg = JSON.parseObject(s);
            logger.debug("Loading the '%s' Project%n", projCfg.get("projectName"));
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("Failed to open project file.");
        }
    }

    private static void loadPackages() {
        try {
            pkgs = projCfg.getJSONArray("pkgs");
            logger.debug("Loaded %d Packages%n", pkgs.size());
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("Failed to find \'pkgs\' configuration.");
        }
    }

    private static void mkDeployDir() {
        try {
            deployDir = new File(homeDir + File.separatorChar + projCfg.getString("deployDir"));
            deployDir.mkdirs();
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("Failed to create deploy directory.");
        }
    }

    private static void createTargetsWithFileIncludes() {
        try {
            int len = pkgs.size();
            /* loop over packages for fileIncludes */
            for (int i = 0; i < len; i++) {
                /* Build pkg and include file deps */
                JSONObject pkg = pkgs.getJSONObject(i);
                /* if we don't include dependencies, it must be fileIncludes */
                if (!pkg.getBooleanValue("includeDeps")) {
                    String targFileName = pkg.getString("file");
                    if (targFileName.indexOf(".js") != -1) {
                        targFileName = FileHelper.insertFileSuffix(pkg.getString("file"),
                                debugSuffix);
                    }
                    if (verbose) {
                        logger.debug("Building the '%s' package as '%s'%n", pkg.getString("name"),
                                targFileName);
                    }

                    /* create file and write out header */
                    File targetFile = new File(
                            deployDir.getCanonicalPath() + File.separatorChar + targFileName);
                    outputFiles.add(targetFile);
                    targetFile.getParentFile().mkdirs();
                    FileHelper.writeStringToFile("", targetFile, false);

                    /* get necessary file includes for this specific package */
                    JSONArray fileIncludes = pkg.getJSONArray("fileIncludes");
                    int fileIncludesLen = fileIncludes.size();
                    if (verbose) {
                        logger.debug("- There are %d file include(s).%n", fileIncludesLen);
                    }

                    /* loop over file includes */
                    for (int j = 0; j < fileIncludesLen; j++) {
                        /*
                         * open each file, read into string and append to target
                         */
                        JSONObject fileCfg = fileIncludes.getJSONObject(j);

                        String subFileName = projectHome + File.separatorChar
                                + fileCfg.getString("path") + fileCfg.getString("text");
                        if (verbose) {
                            logger.debug("- - %s%s%n", fileCfg.getString("path"),
                                    fileCfg.getString("text"));
                        }
                        File subFile = new File(subFileName);
                        String tempString = FileHelper.readFileToString(subFile);
                        logger.debug(tempString);
                        FileHelper.writeStringToFile(tempString, targetFile, true);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("Failed to create targets with fileIncludes.");
        }
    }

    private static void createTargetsWithDeps() {
        try {
            int len = pkgs.size();
            for (int i = 0; i < len; i++) {
                /* Build pkg and include file deps */
                JSONObject pkg = pkgs.getJSONObject(i);
                /* if we need to includeDeps, they shoudl already be built. */
                if (pkg.getBooleanValue("includeDeps")) {
                    String targFileName = pkg.getString("file");
                    if (targFileName.indexOf(".js") != -1) {
                        targFileName = FileHelper.insertFileSuffix(pkg.getString("file"),
                                debugSuffix);
                    }
                    if (verbose) {
                        logger.debug("Building the '%s' package as '%s'%n", pkg.getString("name"),
                                targFileName);
                        logger.debug("This package is built by included dependencies.");
                    }

                    /* create file and write out header */
                    File targetFile = new File(
                            deployDir.getCanonicalPath() + File.separatorChar + targFileName);
                    outputFiles.add(targetFile);
                    targetFile.getParentFile().mkdirs();
                    FileHelper.writeStringToFile("", targetFile, false);

                    /* get necessary pkg includes for this specific package */
                    JSONArray pkgDeps = pkg.getJSONArray("pkgDeps");
                    int pkgDepsLen = pkgDeps.size();
                    if (verbose) {
                        logger.debug("- There are %d package include(s).%n", pkgDepsLen);
                    }

                    /* loop over file includes */
                    for (int j = 0; j < pkgDepsLen; j++) {
                        /*
                         * open each file, read into string and append to target
                         */
                        String pkgDep = pkgDeps.getString(j);
                        if (verbose) {
                            logger.debug("- - %s%n", pkgDep);
                        }
                        String nameWithorWithoutSuffix = pkgDep;
                        if (pkgDep.indexOf(".js") != -1) {
                            nameWithorWithoutSuffix = FileHelper.insertFileSuffix(pkgDep,
                                    debugSuffix);
                        }

                        String subFileName = deployDir.getCanonicalPath() + File.separatorChar
                                + nameWithorWithoutSuffix;
                        File subFile = new File(subFileName);
                        String tempString = FileHelper.readFileToString(subFile);
                        FileHelper.writeStringToFile(tempString, targetFile, true);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to create target with package dependencies.", e);
        }
    }

    public static void compressOutputFiles() {
        Reader in = null;
        Writer out = null;
        logger.debug("Compressing output files...");
        for (File f : outputFiles) {
            try {
                if (f.getName().indexOf(".js") != -1) {
                    if (verbose) {
                        logger.debug("- - " + f.getName() + " -> "
                                + f.getName().replace(debugSuffix, ""));
                    }
                    in = new InputStreamReader(new FileInputStream(f), "UTF-8");
                    JavaScriptCompressor compressor = new JavaScriptCompressor(in,
                            new ErrorReporter() {

                                @Override
                                public void warning(String message, String sourceName, int line,
                                        String lineSource, int lineOffset) {
                                    if (line < 0) {
                                        logger.error("\n[WARNING] " + message);
                                    } else {
                                        logger.error("\n[WARNING] " + line + ':' + lineOffset + ':'
                                                + message);
                                    }
                                }

                                @Override
                                public void error(String message, String sourceName, int line,
                                        String lineSource, int lineOffset) {
                                    if (line < 0) {
                                        logger.error("\n[ERROR] " + message);
                                    } else {
                                        logger.error("\n[ERROR] " + line + ':' + lineOffset + ':'
                                                + message);
                                    }
                                }

                                @Override
                                public EvaluatorException runtimeError(String message,
                                        String sourceName, int line, String lineSource,
                                        int lineOffset) {
                                    error(message, sourceName, line, lineSource, lineOffset);
                                    return new EvaluatorException(message);
                                }
                            });

                    // Close the input stream first, and then open the output
                    // stream,
                    // in case the output file should override the input file.
                    in.close();
                    in = null;

                    out = new OutputStreamWriter(
                            new FileOutputStream(f.getAbsolutePath().replace(debugSuffix, "")),
                            "UTF-8");

                    boolean munge = true;
                    boolean preserveAllSemiColons = false;
                    boolean disableOptimizations = false;
                    int linebreakpos = -1;

                    compressor.compress(out, linebreakpos, munge, false, preserveAllSemiColons,
                            disableOptimizations);
                }
            } catch (EvaluatorException e) {

                logger.error(e.getMessage(), e);
                // Return a special error code used specifically by the web
                // front-end.
                System.exit(2);

            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                System.exit(1);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }

                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }

        }
    }

    public static void copyResources() {
        try {
            JSONArray resources = projCfg.getJSONArray("resources");
            int resourceLen = resources.size();

            for (int z = 0; z < resourceLen; z++) {
                JSONObject resourceCfg = resources.getJSONObject(z);
                String filters = resourceCfg.getString("filters");
                File srcDir = new File(
                        projectHome + File.separatorChar + resourceCfg.getString("src"));
                File destDir = new File(deployDir.getCanonicalPath() + File.separatorChar
                        + resourceCfg.getString("dest"));
                FileHelper.copyDirectory(srcDir, destDir, filters);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
