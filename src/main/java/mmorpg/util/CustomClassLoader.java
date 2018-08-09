/**
 * Copyright (c) 2012 Sean Beecroft, Permission is hereby granted, free of
 * charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * For more information on this project and others, please visit my google code 
 * repository:
 * https://code.google.com/u/seanbeecroft@gmail.com/
 */
package mmorpg.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.TimerTask;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Our custom implementation of the ClassLoader. For any of classes from
 * "javablogging" package it will use its {@link CustomClassLoader#getClass()}
 * method to load it from the specific .class file. For any other class it will
 * use the super.loadClass() method from ClassLoader, which will eventually pass
 * the request to the parent.
 *
 */
public class CustomClassLoader extends ClassLoader {

    String path;

    /**
     * Parent ClassLoader passed to this constructor will be used if this
     * ClassLoader can not resolve a particular class.
     *
     * @param parent Parent ClassLoader (may be from
     * getClass().getClassLoader())
     */
    public CustomClassLoader(ClassLoader parent, String path) {
        super(parent);
        this.path = path;
    }

    /**
     * Loads a given class from .class file just like the default ClassLoader.
     * This method could be changed to load the class over network from some
     * other server or from the database.
     *
     * @param name Full class name
     */
    private Class<?> getClass(String name)
            throws ClassNotFoundException {
        // We are getting a name that looks like
        // javablogging.package.ClassToLoad
        // and we have to convert it into the .class file name
        // like javablogging/package/ClassToLoad.class
        String file = name.replace('.', '/') + ".class";
        byte[] b = null;
        try {
            // This loads the byte code data from the file
            String f = path + file;
            System.out.println("f:" + f);
            b = loadClassData(f);
            // defineClass is inherited from the ClassLoader class
            // and converts the byte array into a Class
            Class<?> c = defineClass(name, b, 0, b.length);
            resolveClass(c);
            return c;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Every request for a class passes through this method. If the requested
     * class is in "javablogging" package, it will load it using the
     * {@link CustomClassLoader#getClass()} method. If not, it will use the
     * super.loadClass() method which in turn will pass the request to the
     * parent.
     *
     * @param name Full class name
     */
    @Override
    public Class<?> loadClass(String name)
            throws ClassNotFoundException {
        System.out.println("loading class '" + name + "'");
        // HACK BELOW - should resolve to actual class path!!!
        Class c = null;
        try {
            c = ClassLoader.getSystemClassLoader().loadClass(name);
        } catch (Exception ex) {
        }

        if (c == null) {
            return getClass(name);
        } else {
            return c;
        }
        //return super.loadClass(name);
    }

    /**
     * Loads a given file (presumably .class) into a byte array. The file should
     * be accessible as a resource, for example it could be located on the
     * classpath.
     *
     * @param name File name to load
     * @return Byte array read from the file
     * @throws IOException Is thrown when there was some problem reading the
     * file
     */
    private byte[] loadClassData(String name) throws IOException {
        // Opening the file
        FileInputStream stream = new FileInputStream(new File(name));
        int size = stream.available();
        byte buff[] = new byte[size];
        DataInputStream in = new DataInputStream(stream);
        // Reading the binary data
        in.readFully(buff);
        in.close();
        return buff;
    }
}