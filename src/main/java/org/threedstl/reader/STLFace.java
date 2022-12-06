package org.threedstl.reader;

// Copyright 2022 William Robertson
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

import java.nio.FloatBuffer;
import java.util.Formatter;

/**
 * Object giving access to STL Face data passed to the callback
 * provided to BinarySTLReader#readTriangles
 *
 */
public class STLFace {

    private FloatBuffer data;

    /**
     * Constructor
     * @param data data produced by BinarySTLReader#readTriangles
     */
    STLFace withData(FloatBuffer data) {
        this.data = data;
        return this;
    }

    /**
     * Get the first, second or third normal
     * @param idx a value between [0,3) i.e. 0-2
     * @return a normal coordinate
     */
    public float normal(int idx) {
        if(idx >= 0 && idx < 3) {
            return data.get(idx);
        }
        throw new IllegalArgumentException("Invalid index: " + idx);
    }

    /**
     * Get the first, second or third x coordinate
     * e.g. To get the first coordinate, call x(0), y(0), z(0)
     * @param vertexNumber a value between [0,3) i.e. 0-2
     * @return an x coordinate
     */
    public float x(int vertexNumber) {
        if(vertexNumber >= 0 && vertexNumber < 3) {
            return data.get(3 + vertexNumber * 3);
        }
        throw new IllegalArgumentException("Invalid index: " + vertexNumber);
    }

    /**
     * Get the first, second or third y coordinate
     * e.g. To get the second coordinate, call x(1), y(1), z(1)
     * @param vertexNumber a value between [0,3) i.e. 0-2
     * @return an y coordinate
     */
    public float y(int vertexNumber) {
        if(vertexNumber >= 0 && vertexNumber < 3) {
            return data.get((3 + vertexNumber * 3) + 1);
        }
        throw new IllegalArgumentException("Invalid index: " + vertexNumber);
    }

    /**
     * Get the first, second or third z coordinate
     * e.g. To get the third coordinate, call x(2), y(2), z(2)
     * @param vertexNumber a value between [0,3) i.e. 0-2
     * @return an z coordinate
     */
    public float z(int vertexNumber) {
        if(vertexNumber >= 0 && vertexNumber < 3) {
            return data.get((3 + vertexNumber * 3) + 2);
        }
        throw new IllegalArgumentException("Invalid index: " + vertexNumber);
    }

    public String toString() {
        Formatter f = new Formatter();
        f.format("normal (%f, %f, %f); ", normal(0), normal(1), normal(2));
        f.format("v1 (%f, %f, %f);", x(0), y(0), z(0));
        f.format("v2 (%f, %f, %f);", x(1), y(1), z(1));
        f.format("v3 (%f, %f, %f);", x(2), y(2), z(2));
        return f.toString();
    }

}
