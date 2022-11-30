package org.threed.stl;

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

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.function.BiConsumer;

class AsciiIshSTLFormatter implements BiConsumer<Long, STLFace> {

    List<Long> i = new ArrayList<>();

    List<Float> n1 = new ArrayList<>();
    List<Float> n2 = new ArrayList<>();
    List<Float> n3 = new ArrayList<>();

    List<Float> x = new ArrayList<>();
    List<Float> y = new ArrayList<>();
    List<Float> z = new ArrayList<>();

    @Override
    public void accept(Long idx, STLFace f) {
        i.add(idx);
        n1.add(f.normal(0));
        n2.add(f.normal(1));
        n3.add(f.normal(2));

        x.add(f.x(0));
        y.add(f.y(0));
        z.add(f.z(0));

        x.add(f.x(1));
        y.add(f.y(1));
        z.add(f.z(1));

        x.add(f.x(2));
        y.add(f.y(2));
        z.add(f.z(2));
    }

    @Override
    public String toString() {
        try {
            Formatter f = new Formatter();
            f.format("solid untitled\n");

            for (int c = 0; c < i.size(); ++c) {
                f.format("facet normal %.1f %.1f %.1f\n", n1.get(c), n2.get(c), n3.get(c));
                f.format("  outer loop\n");

                int idx = c * 3;
                f.format("    vertex %.1f %.1f %.1f\n", x.get(idx), y.get(idx), z.get(idx));
                f.format("    vertex %.1f %.1f %.1f\n", x.get(idx + 1), y.get(idx + 1), z.get(idx + 1));
                f.format("    vertex %.1f %.1f %.1f\n", x.get(idx + 2), y.get(idx + 2), z.get(idx + 2));

                f.format("  endloop\n");
                f.format("endfacet\n");
            }

            f.format("endsolid untitled\n");

            return f.toString();
        } catch (IndexOutOfBoundsException oob) {
            oob.printStackTrace(System.out);
            throw oob;
        }
    }
}
