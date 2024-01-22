/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package fi.tuni.prog3.sisu;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Interface with methods to read from a file and write to a file.
 */
public interface iReadAndWriteToFile {
    /**
     * Reads JSON from the given file.
     * @return true if the read was successful, otherwise false.
     * @throws FileNotFoundException if the file cannot be accessed. 
     */
    ArrayList<Student> readFromFile() throws IOException; 
    
    /**
     * Write the student progress as JSON into the given file.
     * @param student Student whose data we wish to save.
     * @return true if the write was successful, otherwise false.
     * @throws IOException if it cannot write to a file.
     */
    boolean writeToFile(Student student) throws IOException;
}
