package org.threedstl.reader;

//Copyright 2022 William Robertson
//
//    Licensed under the Apache License, Version 2.0 (the "License"); you
//    may not use this file except in compliance with the License.
//
//    You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
//    implied. See the License for the specific language governing
//    permissions and limitations under the License.

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class BinarySTLReaderTest {

    private URL cube = getClass().getResource("/cube.stl");
    private URL asciiCube = getClass().getResource("/cube_ascii.stl");

    @Test
    public void readTestCube() throws Exception {

        AsciiIshSTLFormatter ascii = new AsciiIshSTLFormatter();

        try(BinarySTLReader bsr = new BinarySTLReader(Paths.get(cube.toURI()))) {
            assertEquals(12, bsr.readTriangles(ascii));
            assertEquals(12, bsr.expectedTriangles());
        }

        String expected = Files.readString(Paths.get(asciiCube.toURI()));
        String actual = ascii.toString();
        assertEquals(actual, expected);


    }

    @Test
    public void testReadingFromZipFile() throws Exception {
        URI zip = new URI("jar:"+getClass().getResource("/cubes.zip"));
        FileSystem fs = FileSystems.newFileSystem(zip, Collections.emptyMap());

        String expected = Files.readString(Paths.get(asciiCube.toURI()));

        Path cube1 = fs.getPath("cube1.stl");
        AsciiIshSTLFormatter ascii1 = new AsciiIshSTLFormatter();
        try (BinarySTLReader b = new BinarySTLReader(fs.getPath("cube1.stl"))) {
            b.readTriangles(ascii1);
        }
        String actual1 = ascii1.toString();
        assertEquals(expected, actual1);

        Path cube2 = fs.getPath("cube2.stl");
        AsciiIshSTLFormatter ascii2 = new AsciiIshSTLFormatter();
        try (BinarySTLReader b = new BinarySTLReader(fs.getPath("cube2.stl"))) {
            b.readTriangles(ascii2);
        }
        String actual2 = ascii2.toString();
        assertEquals(expected, actual2);
    }


}
