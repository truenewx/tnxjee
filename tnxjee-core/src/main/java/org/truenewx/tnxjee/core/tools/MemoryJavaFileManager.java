/*
 * Copyright (c) 2014, 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.truenewx.tnxjee.core.tools;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import javax.tools.*;
import javax.tools.JavaFileObject.Kind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.tools.StandardLocation.CLASS_PATH;

/**
 * 基于内存的Java文件管理器。从{@link jdk.jshell.MemoryFileManager}中复制改编
 */
public class MemoryJavaFileManager implements JavaFileManager {

    private final StandardJavaFileManager stdFileManager;

    private final Map<String, OutputMemoryJavaFileObject> classObjects = new TreeMap<>();

    private ClassFileCreationListener classListener = null;

    private ClassLoader classLoader;

    private Logger logger = LoggerFactory.getLogger(getClass());

    Iterable<? extends Path> getLocationAsPaths(Location loc) {
        return this.stdFileManager.getLocationAsPaths(loc);
    }

    static abstract class MemoryJavaFileObject extends SimpleJavaFileObject {

        public MemoryJavaFileObject(String name, Kind kind) {
            super(URI.create("string:///" + name.replace('.', '/')
                    + kind.extension), kind);
        }
    }

    class SourceMemoryJavaFileObject extends MemoryJavaFileObject {
        private final String src;
        private final Object origin;

        SourceMemoryJavaFileObject(Object origin, String className, String code) {
            super(className, Kind.SOURCE);
            this.origin = origin;
            this.src = code;
        }

        public Object getOrigin() {
            return this.origin;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return this.src;
        }
    }

    public static class OutputMemoryJavaFileObject extends MemoryJavaFileObject {

        /**
         * Byte code created by the compiler will be stored in this
         * ByteArrayOutputStream.
         */
        private ByteArrayOutputStream bos = new ByteArrayOutputStream();
        private byte[] bytes = null;

        private final String className;

        public OutputMemoryJavaFileObject(String name, Kind kind) {
            super(name, kind);
            this.className = name;
        }

        public byte[] getBytes() {
            if (this.bytes == null) {
                this.bytes = this.bos.toByteArray();
                this.bos = null;
            }
            return this.bytes;
        }

        public void dump() {
            try {
                Path dumpDir = FileSystems.getDefault().getPath("dump");
                if (Files.notExists(dumpDir)) {
                    Files.createDirectory(dumpDir);
                }
                Path file = FileSystems.getDefault().getPath("dump", getName() + ".class");
                Files.write(file, getBytes());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public String getName() {
            return this.className;
        }

        /**
         * Will provide the compiler with an output stream that leads to our
         * byte array.
         */
        @Override
        public OutputStream openOutputStream() throws IOException {
            return this.bos;
        }

        @Override
        public InputStream openInputStream() throws IOException {
            return new ByteArrayInputStream(getBytes());
        }
    }

    public MemoryJavaFileManager(StandardJavaFileManager standardManager) {
        this.stdFileManager = standardManager;
    }

    private Collection<OutputMemoryJavaFileObject> generatedClasses() {
        return this.classObjects.values();
    }

    // For debugging dumps
    public void dumpClasses() {
        for (OutputMemoryJavaFileObject co : generatedClasses()) {
            co.dump();
        }
    }

    public JavaFileObject createSourceFileObject(String name, String code) {
        return new SourceMemoryJavaFileObject(code, name, code);
    }

    /**
     * Returns a class loader for loading plug-ins from the given location. For
     * example, to load annotation processors, a compiler will request a class
     * loader for the {@link
     * StandardLocation#ANNOTATION_PROCESSOR_PATH
     * ANNOTATION_PROCESSOR_PATH} location.
     *
     * @param location a location
     * @return a class loader for the given location; or {@code null}
     * if loading plug-ins from the given location is disabled or if
     * the location is not known
     * @throws SecurityException     if a class loader can not be created
     *                               in the current security context
     * @throws IllegalStateException if {@link #close} has been called
     *                               and this file manager cannot be reopened
     */
    @Override
    public ClassLoader getClassLoader(Location location) {
        this.logger.debug("getClassLoader: {}\n", location);
        if (this.classLoader == null) {
            this.classLoader = new MemoryClassLoader(this.stdFileManager.getClassLoader(location), this.classObjects);
        }
        return this.classLoader;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    /**
     * Lists all file objects matching the given criteria in the given
     * location.  List file objects in "subpackages" if recurse is
     * true.
     *
     * <p>Note: even if the given location is unknown to this file
     * manager, it may not return {@code null}.  Also, an unknown
     * location may not cause an exception.
     *
     * @param location    a location
     * @param packageName a package name
     * @param kinds       return objects only of these kinds
     * @param recurse     if true include "subpackages"
     * @return an Iterable of file objects matching the given criteria
     * @throws IOException           if an I/O error occurred, or if {@link
     *                               #close} has been called and this file manager cannot be
     *                               reopened
     * @throws IllegalStateException if {@link #close} has been called
     *                               and this file manager cannot be reopened
     */
    @Override
    public Iterable<JavaFileObject> list(Location location,
            String packageName,
            Set<Kind> kinds,
            boolean recurse)
            throws IOException {
        Iterable<JavaFileObject> stdList = this.stdFileManager.list(location, packageName, kinds, recurse);
        if (location == CLASS_PATH && packageName.equals("REPL")) {
            // if the desired list is for our JShell package, lazily iterate over
            // first the standard list then any generated classes.
            return () -> new Iterator<JavaFileObject>() {
                boolean stdDone = false;
                Iterator<? extends JavaFileObject> it;

                @Override
                public boolean hasNext() {
                    if (this.it == null) {
                        this.it = stdList.iterator();
                    }
                    if (this.it.hasNext()) {
                        return true;
                    }
                    if (this.stdDone) {
                        return false;
                    } else {
                        this.stdDone = true;
                        this.it = generatedClasses().iterator();
                        return this.it.hasNext();
                    }
                }

                @Override
                public JavaFileObject next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return this.it.next();
                }

            };
        } else {
            return stdList;
        }
    }

    /**
     * Infers a binary name of a file object based on a location.  The
     * binary name returned might not be a valid binary name according to
     * <cite>The Java&trade {        throw new UnsupportedOperationException("Not supported yet.");  } Language Specification</cite>.
     *
     * @param location a location
     * @param file     a file object
     * @return a binary name or {@code null} the file object is not
     * found in the given location
     * @throws IllegalStateException if {@link #close} has been called
     *                               and this file manager cannot be reopened
     */
    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (file instanceof OutputMemoryJavaFileObject) {
            OutputMemoryJavaFileObject ofo = (OutputMemoryJavaFileObject) file;
            this.logger.debug("inferBinaryName {} => {}\n", file, ofo.getName());
            return ofo.getName();
        } else {
            return this.stdFileManager.inferBinaryName(location, file);
        }
    }

    /**
     * Compares two file objects and return true if they represent the
     * same underlying object.
     *
     * @param a a file object
     * @param b a file object
     * @return true if the given file objects represent the same
     * underlying object
     * @throws IllegalArgumentException if either of the arguments
     *                                  were created with another file manager and this file manager
     *                                  does not support foreign file objects
     */
    @Override
    public boolean isSameFile(FileObject a, FileObject b) {
        return this.stdFileManager.isSameFile(b, b);
    }

    /**
     * Determines if the given option is supported and if so, the
     * number of arguments the option takes.
     *
     * @param option an option
     * @return the number of arguments the given option takes or -1 if
     * the option is not supported
     */
    @Override
    public int isSupportedOption(String option) {
        this.logger.debug("isSupportedOption: {}\n", option);
        return this.stdFileManager.isSupportedOption(option);
    }

    /**
     * Handles one option.  If {@code current} is an option to this
     * file manager it will consume any arguments to that option from
     * {@code remaining} and return true, otherwise return false.
     *
     * @param current   current option
     * @param remaining remaining options
     * @return true if this option was handled by this file manager,
     * false otherwise
     * @throws IllegalArgumentException if this option to this file
     *                                  manager is used incorrectly
     * @throws IllegalStateException    if {@link #close} has been called
     *                                  and this file manager cannot be reopened
     */
    @Override
    public boolean handleOption(String current, Iterator<String> remaining) {
        this.logger.debug("handleOption: current: {}\n", current +
                ", remaining: " + remaining);
        return this.stdFileManager.handleOption(current, remaining);
    }

    /**
     * Determines if a location is known to this file manager.
     *
     * @param location a location
     * @return true if the location is known
     */
    @Override
    public boolean hasLocation(Location location) {
        this.logger.debug("hasLocation: location: {}\n", location);
        return this.stdFileManager.hasLocation(location);
    }

    interface ClassFileCreationListener {
        void newClassFile(OutputMemoryJavaFileObject jfo, Location location,
                String className, Kind kind, FileObject sibling);
    }

    void registerClassFileCreationListener(ClassFileCreationListener listen) {
        this.classListener = listen;
    }

    /**
     * Returns a {@linkplain JavaFileObject file object} for input
     * representing the specified class of the specified kind in the
     * given location.
     *
     * @param location  a location
     * @param className the name of a class
     * @param kind      the kind of file, must be one of {@link
     *                  Kind#SOURCE SOURCE} or {@link
     *                  Kind#CLASS CLASS}
     * @return a file object, might return {@code null} if the
     * file does not exist
     * @throws IllegalArgumentException if the location is not known
     *                                  to this file manager and the file manager does not support
     *                                  unknown locations, or if the kind is not valid
     * @throws IOException              if an I/O error occurred, or if {@link
     *                                  #close} has been called and this file manager cannot be
     *                                  reopened
     * @throws IllegalStateException    if {@link #close} has been called
     *                                  and this file manager cannot be reopened
     */
    @Override
    public JavaFileObject getJavaFileForInput(Location location,
            String className,
            Kind kind)
            throws IOException {
        return this.stdFileManager.getJavaFileForInput(location, className, kind);
    }

    /**
     * Returns a {@linkplain JavaFileObject file object} for output
     * representing the specified class of the specified kind in the
     * given location.
     *
     * <p>Optionally, this file manager might consider the sibling as
     * a hint for where to place the output.  The exact semantics of
     * this hint is unspecified.  The JDK compiler, javac, for
     * example, will place class files in the same directories as
     * originating source files unless a class file output directory
     * is provided.  To facilitate this behavior, javac might provide
     * the originating source file as sibling when calling this
     * method.
     *
     * @param location  a location
     * @param className the name of a class
     * @param kind      the kind of file, must be one of {@link
     *                  Kind#SOURCE SOURCE} or {@link
     *                  Kind#CLASS CLASS}
     * @param sibling   a file object to be used as hint for placement;
     *                  might be {@code null}
     * @return a file object for output
     * @throws IllegalArgumentException if sibling is not known to
     *                                  this file manager, or if the location is not known to this file
     *                                  manager and the file manager does not support unknown
     *                                  locations, or if the kind is not valid
     * @throws IOException              if an I/O error occurred, or if {@link
     *                                  #close} has been called and this file manager cannot be
     *                                  reopened
     * @throws IllegalStateException    {@link #close} has been called
     *                                  and this file manager cannot be reopened
     */
    @Override
    public JavaFileObject getJavaFileForOutput(Location location,
            String className, Kind kind, FileObject sibling) throws IOException {

        OutputMemoryJavaFileObject fo;
        fo = new OutputMemoryJavaFileObject(className, kind);
        this.classObjects.put(className, fo);
        this.logger.debug("Set out file: {} = {}\n", className, fo);
        if (this.classListener != null) {
            this.classListener.newClassFile(fo, location, className, kind, sibling);
        }
        return fo;
    }

    /**
     * Returns a {@linkplain FileObject file object} for input
     * representing the specified <a href="JavaFileManager.html#relative_name">relative
     * name</a> in the specified package in the given location.
     *
     * <p>If the returned object represents a {@linkplain
     * Kind#SOURCE source} or {@linkplain
     * Kind#CLASS class} file, it must be an instance
     * of {@link JavaFileObject}.
     *
     * <p>Informally, the file object returned by this method is
     * located in the concatenation of the location, package name, and
     * relative name.  For example, to locate the properties file
     * "resources/compiler.properties" in the package
     * "com.sun.tools.javac" in the {@linkplain
     * StandardLocation#SOURCE_PATH SOURCE_PATH} location, this method
     * might be called like so:
     *
     * <pre>getFileForInput(SOURCE_PATH, "com.sun.tools.javac", "resources/compiler.properties");</pre>
     *
     * <p>If the call was executed on Windows, with SOURCE_PATH set to
     * <code>"C:\Documents&nbsp;and&nbsp;Settings\UncleBob\src\share\classes"</code>,
     * a valid result would be a file object representing the file
     * <code>"C:\Documents&nbsp;and&nbsp;Settings\UncleBob\src\share\classes\com\sun\tools\javac\resources\compiler.properties"</code>.
     *
     * @param location     a location
     * @param packageName  a package name
     * @param relativeName a relative name
     * @return a file object, might return {@code null} if the file
     * does not exist
     * @throws IllegalArgumentException if the location is not known
     *                                  to this file manager and the file manager does not support
     *                                  unknown locations, or if {@code relativeName} is not valid
     * @throws IOException              if an I/O error occurred, or if {@link
     *                                  #close} has been called and this file manager cannot be
     *                                  reopened
     * @throws IllegalStateException    if {@link #close} has been called
     *                                  and this file manager cannot be reopened
     */
    @Override
    public FileObject getFileForInput(Location location,
            String packageName,
            String relativeName)
            throws IOException {
        this.logger.debug("getFileForInput location={} packageName={}\n", location, packageName);
        return this.stdFileManager.getFileForInput(location, packageName, relativeName);
    }

    /**
     * Returns a {@linkplain FileObject file object} for output
     * representing the specified <a href="JavaFileManager.html#relative_name">relative
     * name</a> in the specified package in the given location.
     *
     * <p>Optionally, this file manager might consider the sibling as
     * a hint for where to place the output.  The exact semantics of
     * this hint is unspecified.  The JDK compiler, javac, for
     * example, will place class files in the same directories as
     * originating source files unless a class file output directory
     * is provided.  To facilitate this behavior, javac might provide
     * the originating source file as sibling when calling this
     * method.
     *
     * <p>If the returned object represents a {@linkplain
     * Kind#SOURCE source} or {@linkplain
     * Kind#CLASS class} file, it must be an instance
     * of {@link JavaFileObject}.
     *
     * <p>Informally, the file object returned by this method is
     * located in the concatenation of the location, package name, and
     * relative name or next to the sibling argument.  See {@link
     * #getFileForInput getFileForInput} for an example.
     *
     * @param location     a location
     * @param packageName  a package name
     * @param relativeName a relative name
     * @param sibling      a file object to be used as hint for placement;
     *                     might be {@code null}
     * @return a file object
     * @throws IllegalArgumentException if sibling is not known to
     *                                  this file manager, or if the location is not known to this file
     *                                  manager and the file manager does not support unknown
     *                                  locations, or if {@code relativeName} is not valid
     * @throws IOException              if an I/O error occurred, or if {@link
     *                                  #close} has been called and this file manager cannot be
     *                                  reopened
     * @throws IllegalStateException    if {@link #close} has been called
     *                                  and this file manager cannot be reopened
     */
    @Override
    public FileObject getFileForOutput(Location location,
            String packageName,
            String relativeName,
            FileObject sibling)
            throws IOException {
        return null;
    }

    @Override
    public Location getLocationForModule(Location location, String moduleName) throws IOException {
        return this.stdFileManager.getLocationForModule(location, moduleName);
    }

    @Override
    public Location getLocationForModule(Location location, JavaFileObject fo) throws IOException {
        return this.stdFileManager.getLocationForModule(location, fo);
    }

    @Override
    public String inferModuleName(Location location) throws IOException {
        return this.stdFileManager.inferModuleName(location);
    }

    @Override
    public Iterable<Set<Location>> listLocationsForModules(Location location) throws IOException {
        return this.stdFileManager.listLocationsForModules(location);
    }

    @Override
    public boolean contains(Location location, FileObject file) throws IOException {
        return this.stdFileManager.contains(location, file);
    }

    /**
     * Flushes any resources opened for output by this file manager
     * directly or indirectly.  Flushing a closed file manager has no
     * effect.
     *
     * @throws IOException if an I/O error occurred
     * @see #close
     */
    @Override
    public void flush() throws IOException {
        // Nothing to flush
    }

    /**
     * Releases any resources opened by this file manager directly or
     * indirectly.  This might render this file manager useless and
     * the effect of subsequent calls to methods on this object or any
     * objects obtained through this object is undefined unless
     * explicitly allowed.  However, closing a file manager which has
     * already been closed has no effect.
     *
     * @throws IOException if an I/O error occurred
     * @see #flush
     */
    @Override
    public void close() throws IOException {
        // Nothing to close
    }
}
