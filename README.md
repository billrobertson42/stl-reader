# stl-reader

## goals

A library that can read binary STL files with the following goals

* Written for modern Java (Java 17, module)
* Available in maven central 
* Single purpose library, no extra dependencies
* Clear license (Apache 2.0)

## usage

Create an STLReader and pass a BiConsumer that accepts a long and an STLFace class to readTriangles.

For example, this code:
```
Path p = Paths.get("my.stl");
try (BinarySTLReader b = new BinarySTLReader(p)) {
    b.readTriangles((idx, f) -> System.out.printf("n: %d, tri: %s%n", idx, f));
}
```
Produces the following output:
```
n: 0, tri: normal (0.000000, 0.000000, 1.000000); v1 (-35.000000, 60.000000, 20.000000);v2 (-55.000000, 60.000000, 20.000000);v3 (-35.000000, 40.000000, 20.000000);
n: 1, tri: normal (0.000000, 0.000000, 1.000000); v1 (-35.000000, 40.000000, 20.000000);v2 (-55.000000, 60.000000, 20.000000);v3 (-55.000000, 40.000000, 20.000000);
...
n: 10, tri: normal (1.000000, -0.000000, 0.000000); v1 (-35.000000, 40.000000, 20.000000);v2 (-35.000000, 40.000000, 0.000000);v3 (-35.000000, 60.000000, 20.000000);
n: 11, tri: normal (1.000000, 0.000000, 0.000000); v1 (-35.000000, 60.000000, 20.000000);v2 (-35.000000, 40.000000, 0.000000);v3 (-35.000000, 60.000000, 0.000000);
```

The library uses a callback for this because it does not want to make assumptions about what you need to do with the data.

For another example, please see AsciiIshSTLFormatter.java in the tests for this project.
